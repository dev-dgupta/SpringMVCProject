package com.divya.mettl.first.phase.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Divya.Gupta on 16-04-2018.
 */
@Data
public class LoyaltyCard implements Serializable {

    private String cardNum;
    private Date cardExpiryDate;
    private Date cardStartDate;

    public LoyaltyCard() {
    }
}
