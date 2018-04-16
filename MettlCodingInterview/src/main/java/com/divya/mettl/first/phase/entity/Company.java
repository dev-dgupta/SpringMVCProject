package com.divya.mettl.first.phase.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Divya.Gupta on 16-04-2018.
 */
@Data
public class Company implements Serializable{

    private String companyName;
    private String companyAddress;

    public Company() {
    }
}
