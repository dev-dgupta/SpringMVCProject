package com.divya.mettl.first.phase.bean;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Divya.Gupta on 16-04-2018.
 */
@Data
public class TransInputBean implements Serializable {

    private String purchaseAmt;
    private String purchaseDate;
    private String transID;


    public TransInputBean() {
    }
}
