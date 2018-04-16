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

    @RequestMapping(value = "/printTansactionList", method = RequestMethod.GET, produces = "application/json")
    public List<TransactionBean> printTransactionList(HttpServletRequest request, HttpServletResponse response) {
        return programService.print();
    }

    @RequestMapping(value = "/makeTransaction", method = RequestMethod.POST, produces = "application/json")
    public String makeTransaction(@RequestBody InputBean inputBean) {
        return programService.makeTransaction(inputBean);
    }


}
