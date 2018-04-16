package com.divya.mettl.first.phase.service;

import com.divya.mettl.first.phase.bean.InputBean;
import com.divya.mettl.first.phase.bean.TransactionBean;
import com.divya.mettl.first.phase.entity.Customer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by Divya.Gupta on 16-04-2018.
 */
public interface ProgramService {
    List<TransactionBean> print();

    String makeTransaction(InputBean inputBean);
}
