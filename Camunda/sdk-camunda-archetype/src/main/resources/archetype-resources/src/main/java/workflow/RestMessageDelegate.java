/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
#set($symbol_pound='#')#set($symbol_dollar='$')#set($symbol_escape='\')package ${package}.workflow;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Log message.
 * Invoked by the log-message-wf example Camunda workflow/bpmn. 
 * 
 */
public class RestMessageDelegate implements JavaDelegate {
	// currently uses the java.util.logging.Logger like the Camunda engine
	private final Logger LOGGER = Logger.getLogger(RestMessageDelegate.class.getName());

	/**
	 * Perform activity.  Log message from running process and set a variable in the running process.
	 * 
	 * @param execution
	 */
	public void execute(DelegateExecution execution) throws Exception {
		String logMessageText = (String)execution.getVariable("logMessageText");
		RestTemplate restTemplate = new RestTemplate();
		Map<String,String> restValues = new HashMap<String,String>();
		restValues.put("procInstId", execution.getProcessInstanceId());
		LOGGER.info("Invoked from processDefinitionId=" + execution.getProcessDefinitionId() +  ", processInstanceId=" + execution.getProcessInstanceId() +  ", activityInstanceId=" + execution.getActivityInstanceId() + ": logMessageText=" + logMessageText);
		String port = System.getProperty("server.port");
		ResponseEntity<String> resp = restTemplate.getForEntity("http://localhost:" + port + "/services/CamundaExample/v1/jaxrsExample/log/histLog/{procInstId}", String.class, restValues);
			System.out.println("value of resp:" + resp);
		execution.setVariable("isMessageLogComplete", true);
	}
}
