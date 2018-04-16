package com.divya.mettl.first.phase.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Divya.Gupta on 16-04-2018.
 */

@Data
public class Customer implements Serializable {

    private int custId;
    private String custName;
    private String custEmail;
    private String custAddress;

    private String custMob;
    private int custClass;
    private boolean isEnrolled;
    private boolean isActive;
    private int custLoyaltyPoints;
    private double totalPurchaseMade;
    private LoyaltyCard loyaltyCard;
    private List<Transaction> transactionList;


    public Customer() {
    }
}
