package com.divya.mettl.first.phase.bean;

import com.divya.mettl.first.phase.entity.Transaction;
import lombok.Data;

import java.util.List;

/**
 * Created by Divya.Gupta on 16-04-2018.
 */
@Data
public class TransactionBean {

    private String custName;
    private String custEmail;
    private String custLoyaltyPoints;
    private String custClass;
    private List<Transaction> custTransactions;

    public TransactionBean() {
    }


}
