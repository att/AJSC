/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/

#set($symbol_pound='#')#set($symbol_dollar='$')#set($symbol_escape='\')package ${package}.workflow;


import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class ProcessRequestDelegate implements JavaDelegate {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessRequestDelegate.class);

  //@Override
  public void execute(DelegateExecution execution) throws Exception {
    LOGGER.info("Processing request by '"+execution.getVariable("customerId")+"'...");
    LOGGER.info("Processing request by '"+execution.getVariable("amount")+"'...");
  }

}
