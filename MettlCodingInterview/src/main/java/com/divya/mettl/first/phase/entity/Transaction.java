package com.divya.mettl.first.phase.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Divya.Gupta on 16-04-2018.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction implements Serializable {

    private boolean loyaltyCardUsed;
    private double purchaseAmt;
    private String purchaseDate;
    private String transactionId;
    private String custId;

    public Transaction() {
    }
}
