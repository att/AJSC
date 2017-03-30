/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package com.att.ajsc.camunda.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;
import org.camunda.bpm.engine.impl.history.event.HistoryEvent;
import org.camunda.bpm.engine.impl.history.handler.DbHistoryEventHandler;
import org.camunda.bpm.engine.impl.history.handler.HistoryEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.att.ajsc.camunda.core.AttCamundaHistoryEvent;
import com.att.ajsc.camunda.core.AttCamundaService;
import com.google.gson.Gson;


public class CamundaHistoryEventHandler extends DbHistoryEventHandler implements HistoryEventHandler {

    @Autowired
    EmbeddedWebApplicationContext context;
    
    @Value("${camunda.core.jaxsservice.path:jaxrsservices}")
    String restCallforPerformance;

    
	List<HistoricActivityInstanceEventEntity> historyEventList = new ArrayList<HistoricActivityInstanceEventEntity>();
	  
	  @Override
	  public void handleEvent(HistoryEvent historyEvent) 
	  {
	    // create db entry
	    super.handleEvent(historyEvent);
	    
	    // Create log Entry
	    HistoricActivityInstanceEventEntity activityEvent = null;
	    
	    Date startDate = null;
    	Date endDate = null;
	    if(historyEvent != null && historyEvent instanceof HistoricActivityInstanceEventEntity )
	    {
	    	activityEvent = (HistoricActivityInstanceEventEntity) historyEvent;
	    	 if(activityEvent != null)
	 	    {
	 	    	 startDate = activityEvent.getStartTime();
	 	    	 endDate = activityEvent.getEndTime();
	 	    	
	 	    	 if(activityEvent.getActivityType() != null && (activityEvent.getActivityType().equalsIgnoreCase("manualTask")
	 	    			  || activityEvent.getActivityType().equalsIgnoreCase("userTask")))
	 	    	 {
	 	    		historyEventList.clear();
	 	    	 }
	 	    	if(startDate != null && endDate != null)
		 	    {
	 	    		System.out.println(activityEvent + ":" + activityEvent.getProcessInstanceId()+ ":" +  activityEvent.getActivityType() );
		 	    	if(activityEvent != null && activityEvent.getProcessInstanceId() != null)
		 	    	{
		 	    		historyEventList.add(activityEvent);
	 	    			if( activityEvent.getActivityType() != null && activityEvent.getActivityType().equalsIgnoreCase("noneEndEvent"))
	 	    			{
	 	    				if(AttCamundaService.getHttpRequest() != null)
 			 	    		{
 	    					  for(HistoricActivityInstanceEventEntity actiEvent : historyEventList)
			 	    		    {
 	    						 int activityInstanceState = actiEvent.getActivityInstanceState();
		 	    		    	 String activityState = getActivityInstanceState(activityInstanceState);
 	    			 	    	 //  resolve null pointer exception if actiEvent.getActivityName()
 	    			 	    	 String serviceName = actiEvent.getActivityName();
 	    			 	    	 if ( serviceName == null ) {
 	    			 	    		serviceName = "UNKNOWN";
 	    			 	    	 } 	    						
			 	    		    }
 			 	    			System.out.println("Call performacne is success");
 			 	    		}
	 	    				else
 			 	    		{
			 	    		    RestTemplate restTemplate = new RestTemplate();
			 	    		    String histEvents = "";
			 	    		    for(HistoricActivityInstanceEventEntity actiEvent : historyEventList)
			 	    		    {
			 	    		    	 int activityInstanceState = actiEvent.getActivityInstanceState();
			 	    		    	 String activityState = getActivityInstanceState(activityInstanceState);
			 	    		    	histEvents = histEvents + actiEvent.getActivityName() + ":" + String.valueOf(actiEvent.getStartTime().getTime() )+ ":" 
			 	    		    			+ String.valueOf(actiEvent.getEndTime().getTime()) + ":" + activityState + "," ;
			 	    		    	System.out.println(actiEvent);
			 	    		    }
			 	    			Map<String,String> restValues = new HashMap<String,String>();
			 	    			restValues.put("procInstId", activityEvent.getProcessInstanceId());
			 	    			restValues.put("histEventList",histEvents);
			 	    			try
			 	    			{
                                    String port = String.valueOf(context.getEmbeddedServletContainer().getPort());

                                    String url = "http://localhost:" + port + "/"+restCallforPerformance+"/log/postLogHist";
			 	    				// create request body
			 	    	 			AttCamundaHistoryEvent attCamundaHistoryEvent = new AttCamundaHistoryEvent();
			 	    	 			attCamundaHistoryEvent.setHistoryEventList(historyEventList);
			 	    	 			attCamundaHistoryEvent.setProcInstId(activityEvent.getProcessInstanceId());
			 	    	 		
			 	    	 			Gson gson = new Gson();
			 	    	 			String json = gson.toJson(attCamundaHistoryEvent, AttCamundaHistoryEvent.class);
			 	    				
			 	    				// set headers
			 	    				HttpHeaders headers = new HttpHeaders();
			 	    				headers.setContentType(MediaType.APPLICATION_JSON);
			 	    				HttpEntity<String> entity = new HttpEntity<String>(json, headers);

			 	    				// send request and parse result
			 	    				ResponseEntity<String> loginResponse = restTemplate
			 	    				  .exchange(url, HttpMethod.POST, entity, String.class);
			 	    				System.out.println(loginResponse);
			 	    			}
			 	    			catch(Exception e)
			 	    			{
			 	    				System.out.println("Not able to call restService");
			 	    			}
 			 	    		}
	 	    				historyEventList.clear();
	 	    			}
		 	    	}
		 	    }
	 	    }
	    }
	  }


	  private String getActivityInstanceState(int activityInstanceState)
	  {
		   String activityState = "Default";
		  if(activityInstanceState == 1)
	    	 {
	    		 activityState = "Complete";
	    	 }
	    	 else if(activityInstanceState == 2)
	    	 {
	    		 activityState = "Cancelled";
	    	 }
		  return activityState;
	  }

	}