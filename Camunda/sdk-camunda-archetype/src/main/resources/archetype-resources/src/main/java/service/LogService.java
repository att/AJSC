/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
#set($symbol_pound='#')#set($symbol_dollar='$')#set($symbol_escape='\')package ${package}.service;

public interface LogService {   
   
    public String logMessage(String logMessageText, String javamail, String springmail, String commonsmail);
        
    public String postLogMessage(String  histEventList);

    public String createLogMessage(String startTime, String endTime, String serviceName);
    
    public String createLogMessageUsingHistory(String procInstId, String  histEventList);
    
    public String CreateHistLog(String procInstId);
    

}
