package com.divya.mettl.first.phase.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Divya.Gupta on 16-04-2018.
 */
@Data
public class Program implements Serializable {

    private String programName;
    private boolean isActive;

    public Program() {
    }
}
