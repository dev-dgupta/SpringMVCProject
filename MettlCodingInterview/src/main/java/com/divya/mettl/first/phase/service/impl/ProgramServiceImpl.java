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
    public String makeTransaction(InputBean inputBean) {

        if (!validRequest(inputBean))
            return /*CommonUtility.errorResponse()*/"Transaction could not be made. Details could not be verified!";
        Customer customer = new Customer();
        getCustomerFromInputBean(inputBean, customer);
        if (null == customer.getLoyaltyCard()) {
            getNewCustomerFields(customer);
        } else {
            List<Transaction> transactions = customer.getTransactionList();
            double custPurchaseAmt = transactions.get(0).getPurchaseAmt();
            if (CacheService.foundInUserCacheMap(inputBean.getLoyaltyCardNum())) {
                Customer customerFoundInMap = (Customer) CacheService.getFromUserCacheMap(customer.getLoyaltyCard().getCardNum());
                double custTotalPurchase = customerFoundInMap.getTotalPurchaseMade() + custPurchaseAmt;
                getLoyaltyCustomerFields(customer, customerFoundInMap, custPurchaseAmt, custTotalPurchase);
                transactions.addAll(customerFoundInMap.getTransactionList());
                customer.setTransactionList(transactions);
            } else {
                customer.setEnrolled(true);
                customer.setTotalPurchaseMade(custPurchaseAmt);
                customer.setCustLoyaltyPoints(Integer.parseInt(env.getProperty("new.cust.point")));
            }
        }
        CacheService.putInUserCacheMap(customer.getLoyaltyCard().getCardNum(), customer);
        return "Transaction successful!";
    }

    private void getLoyaltyCustomerFields(Customer customer, Customer customerFoundInMap, double custPurchaseAmt, double custTotalPurchase) {
        switch (Constants.ProgramClass.getProgramClassFromVal(customerFoundInMap.getCustClass())) {
            case GOLD_CLASS:
                getGoldClassCustomerFields(customer, customerFoundInMap, custPurchaseAmt, custTotalPurchase);
                break;
            case SILVER_CLASS:
                getSilverClassCustomerFields(customer, customerFoundInMap, custPurchaseAmt, custTotalPurchase);
                break;
            case NORMAL_CLASS:
                double allCustPoint = Integer.parseInt(env.getProperty("all.cust.point"));
                customer.setCustLoyaltyPoints((int) (customerFoundInMap.getCustLoyaltyPoints() + (custPurchaseAmt * allCustPoint)));
                break;
            default:
                break;

        }
        customer.setTotalPurchaseMade(custTotalPurchase);
        customer.setEnrolled(true);
    }

    private void getGoldClassCustomerFields(Customer customer, Customer customerFoundInMap, double custPurchaseAmt, double custTotalPurchase) {
        int minPurchase = 0;
        int totalSpent = 0;
        int pointsAdded = 0;
        double totalLoyaltyPointsToBeAdded = 0.0D;
        minPurchase = Integer.parseInt(env.getProperty("gold.class.cust.min.purchase"));
        totalSpent = Integer.parseInt(env.getProperty("gold.class.total.spent.for.point"));
        pointsAdded = Integer.parseInt(env.getProperty("gold.class.point"));
        if (custTotalPurchase > minPurchase) {
            customer.setCustClass(Constants.ProgramClass.GOLD_CLASS.getVal());
            if (custTotalPurchase - minPurchase > custPurchaseAmt) {
                totalLoyaltyPointsToBeAdded = (totalSpent / pointsAdded) * (custPurchaseAmt);
            } else {
                totalLoyaltyPointsToBeAdded = (totalSpent / pointsAdded) * (custTotalPurchase - minPurchase);
            }
            customer.setCustLoyaltyPoints((int) (customerFoundInMap.getCustLoyaltyPoints() + totalLoyaltyPointsToBeAdded));
        } else {
            customer.setCustLoyaltyPoints(customerFoundInMap.getCustLoyaltyPoints() + Integer.parseInt(env.getProperty("all.cust.point")));
        }
    }

    private void getSilverClassCustomerFields(Customer customer, Customer customerFoundInMap, double custPurchaseAmt, double custTotalPurchase) {
        int minPurchase = 0;
        int totalSpent = 0;
        int pointsAdded = 0;
        double totalLoyaltyPointsToBeAdded = 0.0D;
        minPurchase = Integer.parseInt(env.getProperty("silver.class.cust.min.purchase"));
        totalSpent = Integer.parseInt(env.getProperty("silver.class.total.spent.for.point"));
        pointsAdded = Integer.parseInt(env.getProperty("silver.class.point"));
        if (custTotalPurchase > minPurchase) {
            customer.setCustClass(Constants.ProgramClass.SILVER_CLASS.getVal());
            if (custTotalPurchase - minPurchase > custPurchaseAmt) {
                totalLoyaltyPointsToBeAdded = (totalSpent / pointsAdded) * (custPurchaseAmt);
            } else {
                totalLoyaltyPointsToBeAdded = (totalSpent / pointsAdded) * (custTotalPurchase - minPurchase);
            }
            customer.setCustLoyaltyPoints((int) (customerFoundInMap.getCustLoyaltyPoints() + totalLoyaltyPointsToBeAdded));
        } else {
            customer.setCustLoyaltyPoints(customerFoundInMap.getCustLoyaltyPoints() + Integer.parseInt(env.getProperty("all.cust.point")));
        }
    }

    private void getNewCustomerFields(Customer customer) {
        customer.setActive(true);
        customer.setEnrolled(false);
        customer.setCustLoyaltyPoints(0);
        customer.setTotalPurchaseMade(customer.getTransactionList().get(0).getPurchaseAmt());
    }

    private void getCustomerFromInputBean(InputBean inputBean, Customer customer) {
        customer.setCustId(CommonUtility.randomNumber());
        customer.setCustName(inputBean.getCustName());
        customer.setCustEmail(inputBean.getCustEmail());
        if (inputBean.getLoyaltyCardNum() != null) {
            LoyaltyCard loyaltyCard = new LoyaltyCard();
            loyaltyCard.setCardNum(inputBean.getLoyaltyCardNum());
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
