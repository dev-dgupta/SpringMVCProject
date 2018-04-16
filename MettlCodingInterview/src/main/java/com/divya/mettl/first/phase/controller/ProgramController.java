package com.divya.mettl.first.phase.controller;

import com.divya.mettl.first.phase.bean.InputBean;
import com.divya.mettl.first.phase.bean.TransactionBean;
import com.divya.mettl.first.phase.entity.Customer;
import com.divya.mettl.first.phase.service.ProgramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Created by Divya.Gupta on 16-04-2018.
 */

@RestController
public class ProgramController {

    @Autowired
    ProgramService programService;




    /*
    API:localhost:6060/mettl/printTransactionList

    Response:
    [
    {
        "custName": "divyaa",
        "custEmail": "t1122@gmail.com",
        "custLoyaltyPoints": "646157",
        "custClass": "GOLD_CLASS",
        "custTransactions": [
            {
                "loyaltyCardUsed": true,
                "purchaseAmt": 64543431,
                "purchaseDate": "21-06-2012 11:23",
                "transactionId": "23423423"
            },
            {
                "loyaltyCardUsed": true,
                "purchaseAmt": 62310,
                "purchaseDate": "26-06-2012 11:23",
                "transactionId": "2348723"
            }
        ]
    },
    {
        "custName": "derta",
        "custEmail": "t12gg@gmail.com",
        "custLoyaltyPoints": "100",
        "custClass": "NORMAL_CLASS",
        "custTransactions": [
            {
                "loyaltyCardUsed": true,
                "purchaseAmt": 61,
                "purchaseDate": "21-06-2012 11:23",
                "transactionId": "23423423"
            },
            {
                "loyaltyCardUsed": true,
                "purchaseAmt": 6690,
                "purchaseDate": "26-06-2012 11:23",
                "transactionId": "2348723"
            }
        ]
    }
]

     */
    @RequestMapping(value = "/printTransactionList", method = RequestMethod.GET, produces = "application/json")
    public List<TransactionBean> printTransactionList(HttpServletRequest request, HttpServletResponse response) {
        return programService.print();
    }


    /*
    API: localhost:6060/mettl/makeTransaction/
    POST JSON:
    {
  "custName": "divyaa",
  "custEmail": "t1122@gmail.com",
  "loyaltyCardNum": "101111",
  "transList": [
    {
      "purchaseAmt": "64543431",
      "purchaseDate": "21-06-2012 11:23",
      "transID": "23423423"
    },
    {
      "purchaseAmt": "62310",
      "purchaseDate": "26-06-2012 11:23",
      "transID": "2348723"
    }
  ]
}

Response: Transaction Successful!
     */
    @RequestMapping(value = "/makeTransaction", method = RequestMethod.POST, produces = "application/json")
    public String makeTransaction(@RequestBody InputBean inputBean) {
        return programService.makeTransaction(inputBean);
    }


}
