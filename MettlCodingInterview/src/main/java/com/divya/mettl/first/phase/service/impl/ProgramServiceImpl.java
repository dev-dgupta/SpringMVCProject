package com.divya.mettl.first.phase.service.impl;

import com.divya.mettl.first.phase.bean.InputBean;
import com.divya.mettl.first.phase.bean.TransInputBean;
import com.divya.mettl.first.phase.bean.TransactionBean;
import com.divya.mettl.first.phase.entity.Customer;
import com.divya.mettl.first.phase.entity.LoyaltyCard;
import com.divya.mettl.first.phase.entity.Transaction;
import com.divya.mettl.first.phase.service.CacheService;
import com.divya.mettl.first.phase.service.ProgramService;
import com.divya.mettl.first.phase.util.CommonUtility;
import com.divya.mettl.first.phase.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Divya.Gupta on 16-04-2018.
 */
@Service
@PropertySource("classpath:loyaltyProgram.properties")
public class ProgramServiceImpl implements ProgramService {

    @Autowired
    private Environment env;

    //    @Override
//    @HystrixCommand(fallbackMethod = "printEmpty")
    @ResponseBody
    public List<TransactionBean> print() {

        Map<String, Object> userMap = CacheService.getUserCacheMap();
        if (null == userMap) {
            return null;
        }
        List<TransactionBean> transactionBeanList = new ArrayList<TransactionBean>(userMap.size());
        TransactionBean transactionBean;
        Customer customer;
        for (String custLoyaltyCardNum : userMap.keySet()) {
            customer = (Customer) userMap.get(custLoyaltyCardNum);
            transactionBean = new TransactionBean();
            transactionBean.setCustName(customer.getCustName());
            transactionBean.setCustEmail(customer.getCustEmail());
            transactionBean.setCustLoyaltyPoints(String.valueOf(customer.getCustLoyaltyPoints()));
            transactionBean.setCustClass(String.valueOf(Constants.ProgramClass.getProgramClassFromVal(customer.getCustClass())));
            transactionBean.setCustTransactions(customer.getTransactionList());
            transactionBeanList.add(transactionBean);
        }
        return transactionBeanList;
    }

    //    @Override
//    @HystrixCommand(fallbackMethod = "makeEmptyTransaction")
    @ResponseBody
    public String makeTransaction(InputBean inputBean) {

        if (!validRequest(inputBean))
            return /*CommonUtility.errorResponse()*/"Transaction could not be made. Details could not be verified!";
        Customer customer = new Customer();
        getCustomerFromInputBean(inputBean, customer);
        if (null == customer.getLoyaltyCard()) {
            getNewCustomerFields(customer);
        } else {
            List<Transaction> transactions = customer.getTransactionList();
            double custPurchaseAmt = 0.0D;
            for (Transaction transaction : transactions) {
                custPurchaseAmt = transaction.getPurchaseAmt() + custPurchaseAmt;
            }
            int goldMinPurchase = Integer.parseInt(env.getProperty("gold.class.cust.min.purchase"));
            int silverMinPurchase = Integer.parseInt(env.getProperty("silver.class.cust.min.purchase"));
            if (CacheService.foundInUserCacheMap(inputBean.getLoyaltyCardNum())) {
                Customer customerFoundInMap = (Customer) CacheService.getFromUserCacheMap(customer.getLoyaltyCard().getCardNum());
                double custTotalPurchase = customerFoundInMap.getTotalPurchaseMade() + custPurchaseAmt;
                switch (Constants.ProgramClass.getProgramClassFromVal(customerFoundInMap.getCustClass())) {
                    case GOLD_CLASS:
                        getGoldClassCustomerFields(customer, customerFoundInMap.getCustLoyaltyPoints(), custPurchaseAmt, custTotalPurchase, goldMinPurchase);
                        break;
                    case SILVER_CLASS:
                        getSilverClassCustomerFields(customer, customerFoundInMap.getCustLoyaltyPoints(), custPurchaseAmt, custTotalPurchase, silverMinPurchase);
                        break;
                    case NORMAL_CLASS:
                        customer.setCustClass(Constants.ProgramClass.NORMAL_CLASS.getVal());
                        setAllCustPoints(customer, custPurchaseAmt, customerFoundInMap.getCustLoyaltyPoints());
                        break;
                    default:
                        break;
                }
                customer.setTotalPurchaseMade(custTotalPurchase);
                customer.setEnrolled(true);
                transactions.addAll(customerFoundInMap.getTransactionList());
                customer.setTransactionList(transactions);
            } else {
                customer.setEnrolled(true);
                customer.setTotalPurchaseMade(custPurchaseAmt);
                customer.setCustLoyaltyPoints(Integer.parseInt(env.getProperty("new.cust.point")));
                if (custPurchaseAmt > goldMinPurchase) {
                    getGoldClassCustomerFields(customer, customer.getCustLoyaltyPoints(), custPurchaseAmt, 0, goldMinPurchase);
                } else if (custPurchaseAmt <= goldMinPurchase && custPurchaseAmt > silverMinPurchase) {
                    getSilverClassCustomerFields(customer, customer.getCustLoyaltyPoints(), custPurchaseAmt, 0, silverMinPurchase);
                } else {
                    customer.setCustClass(Constants.ProgramClass.NORMAL_CLASS.getVal());
                }
            }
        }
        // saving only those customers who have a loyalty card
        if (null != customer.getLoyaltyCard())
            CacheService.putInUserCacheMap(customer.getLoyaltyCard().getCardNum(), customer);
        return "Transaction successful!";
    }

    private void setAllCustPoints(Customer customer, double custPurchaseAmt, int origLoyaltyPoints) {
        double allCustPoint = Double.parseDouble(env.getProperty("all.cust.point"));
        customer.setCustLoyaltyPoints((int) (origLoyaltyPoints + (custPurchaseAmt * allCustPoint)));
    }

    private void getGoldClassCustomerFields(Customer customer, int origCustLoyaltyPoints, double custPurchaseAmt, double custTotalPurchase, int minPurchase) {
        int totalSpent = Integer.parseInt(env.getProperty("gold.class.total.spent.for.point"));
        int pointsAdded = Integer.parseInt(env.getProperty("gold.class.point"));

        customer.setCustClass(Constants.ProgramClass.GOLD_CLASS.getVal());
        if (custTotalPurchase > minPurchase) {
            getCustomerClass(customer, origCustLoyaltyPoints, custPurchaseAmt, custTotalPurchase, minPurchase, totalSpent, pointsAdded);
        } else {
            setAllCustPoints(customer, custPurchaseAmt, origCustLoyaltyPoints);
        }
    }

    private void getCustomerClass(Customer customer, int origCustLoyaltyPoints, double custPurchaseAmt, double custTotalPurchase, int minPurchase, int totalSpent, int pointsAdded) {
        double totalLoyaltyPointsToBeAdded;

        if (custTotalPurchase - minPurchase > custPurchaseAmt) {
            totalLoyaltyPointsToBeAdded = (totalSpent / pointsAdded) * (custPurchaseAmt);
        } else {
            totalLoyaltyPointsToBeAdded = (totalSpent / pointsAdded) * (custTotalPurchase - minPurchase);
        }
        customer.setCustLoyaltyPoints((int) (origCustLoyaltyPoints + totalLoyaltyPointsToBeAdded));
    }

    private void getSilverClassCustomerFields(Customer customer, int origCustLoyaltyPoints, double custPurchaseAmt, double custTotalPurchase, int minPurchase) {
        int totalSpent = Integer.parseInt(env.getProperty("silver.class.total.spent.for.point"));
        int pointsAdded = Integer.parseInt(env.getProperty("silver.class.point"));

        customer.setCustClass(Constants.ProgramClass.SILVER_CLASS.getVal());
        if (custTotalPurchase > minPurchase) {
            getCustomerClass(customer, origCustLoyaltyPoints, custPurchaseAmt, custTotalPurchase, minPurchase, totalSpent, pointsAdded);
        } else {
            setAllCustPoints(customer, custPurchaseAmt, origCustLoyaltyPoints);
        }
    }

    private void getNewCustomerFields(Customer customer) {
        customer.setEnrolled(false);
        customer.setCustLoyaltyPoints(0);
        customer.setTotalPurchaseMade(customer.getTransactionList().get(0).getPurchaseAmt());
    }

    private void getCustomerFromInputBean(InputBean inputBean, Customer customer) {
        customer.setCustId(CommonUtility.randomNumber());
        customer.setCustName(inputBean.getCustName());
        customer.setCustEmail(inputBean.getCustEmail());
        customer.setActive(true);
        if (!CommonUtility.chkNull(inputBean.getLoyaltyCardNum())) {
            LoyaltyCard loyaltyCard = new LoyaltyCard();
            loyaltyCard.setCardNum(inputBean.getLoyaltyCardNum());
            customer.setLoyaltyCard(loyaltyCard);
        }
        List<Transaction> transactionList = new ArrayList<Transaction>(inputBean.getTransList().size());
        Transaction transaction;
        for (TransInputBean transInputBean : inputBean.getTransList()) {
            transaction = new Transaction();
            transaction.setLoyaltyCardUsed(inputBean.getLoyaltyCardNum() != null);
            transaction.setTransactionId(transInputBean.getTransID());
            transaction.setPurchaseAmt(Double.parseDouble(transInputBean.getPurchaseAmt()));
            transaction.setPurchaseDate(transInputBean.getPurchaseDate());
            transactionList.add(transaction);
        }
        customer.setTransactionList(transactionList);
    }

    private boolean validRequest(InputBean inputBean) {

        boolean resp = true;
        if (null == inputBean) {
            resp = false;
        } else {
            if (CommonUtility.chkNull(inputBean.getCustName()) || CommonUtility.chkNull(inputBean.getCustEmail()) || CommonUtility.chkNull(inputBean.getTransList()))
                resp = false;
        }

        return resp;
    }
}
