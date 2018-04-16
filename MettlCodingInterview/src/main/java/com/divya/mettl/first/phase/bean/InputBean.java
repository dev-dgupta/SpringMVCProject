package com.divya.mettl.first.phase.bean;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by Divya.Gupta on 16-04-2018.
 */
@Data
public class InputBean {

    private String custName;
    private String custEmail;
    private String loyaltyCardNum;
    List<TransInputBean> transList;

    public InputBean() {
    }


}
