/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
#set($symbol_pound='#')#set($symbol_dollar='$')#set($symbol_escape='\')package ${package}.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;


/**
 * Log message.
 * Invoked by the log-message-wf example Camunda workflow/bpmn. 
 * 
 */
public class LogMessageDelegate implements JavaDelegate {
	

	/**
	 * Perform activity.  Log message from running process and set a variable in the running process.
	 * 
	 * @param execution
	 */
	public void execute(DelegateExecution execution) throws Exception {
		String logMessageText = (String)execution.getVariable("logMessageText");
	
		//LOGGER.info("Invoked from processDefinitionId=" + execution.getProcessDefinitionId() +  ", processInstanceId=" + execution.getProcessInstanceId() +  ", activityInstanceId=" + execution.getActivityInstanceId() + ": logMessageText=" + logMessageText);
		System.out.println("Invoked from processDefinitionId=" + execution.getProcessDefinitionId() +  ", processInstanceId=" + execution.getProcessInstanceId() +  ", activityInstanceId=" + execution.getActivityInstanceId() + ": logMessageText=" + logMessageText);
		execution.setVariable("isMessageLogComplete", true);
	}
}
