/*
 * Copyright (c) 2008 Morgan Stanley & Co. Incorporated, All Rights Reserved
 *
 * Unpublished copyright.  All rights reserved.  This material contains
 * proprietary information that shall be used or copied only within 
 * Morgan Stanley, except with written permission of Morgan Stanley.
 */
package com.ms.msqe.tdms.failure.controller;


import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ms.msqe.tdms.failure.dao.RefreshHistoryDao;
import com.ms.msqe.tdms.failure.domain.RequestHistory;

@Controller
@RequestMapping("/alcazar/refresh")
public class RefreshController {       
    private static List<RequestHistory> requestHistory = new ArrayList<RequestHistory>();
    
    @Autowired
    RefreshHistoryDao refreshHistoryDao;

    static{
        RequestHistory req = new RequestHistory();
        req.setName("fakeRequest");
        req.setUpdateDatetime("2013-09-10");
        req.setSourceDbName("fakeSrcDb");
        req.setTargetDbName("fakeTgtDb");
        req.setStatus("");
        requestHistory.add(req);
        
    }
    
    @RequestMapping(value = "/get/refresh/history/request/{requestName}", method = RequestMethod.GET)
    public @ResponseBody List<RequestHistory> getRequestHistoryByName(@PathVariable String requestName) {
        return refreshHistoryDao.getByRequestName(requestName);
        //return requestHistory;
    }
    
    @RequestMapping(value = "/get/refresh/history/source/server/{serverName}/dbname/{dbName}", method = RequestMethod.GET)
    public @ResponseBody List<RequestHistory> getRequestHistoryBySrcDbName(@PathVariable String serverName, @PathVariable String dbName) {
        return refreshHistoryDao.getBySrcDbName(serverName+"."+dbName);
        //return requestHistory;
    }
}
