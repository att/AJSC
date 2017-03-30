/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package com.att.ajsc.camunda.core;
import java.util.ArrayList;
import java.util.List;

import org.camunda.bpm.engine.impl.history.event.HistoricActivityInstanceEventEntity;


public class AttCamundaHistoryEvent {
	
	private List<HistoricActivityInstanceEventEntity> historyEventList = new ArrayList<HistoricActivityInstanceEventEntity>();
	private String procInstId;
	public List<HistoricActivityInstanceEventEntity> getHistoryEventList() {
		return historyEventList;
	}
	public void setHistoryEventList(
			List<HistoricActivityInstanceEventEntity> historyEventList) {
		this.historyEventList = historyEventList;
	}
	public String getProcInstId() {
		return procInstId;
	}
	public void setProcInstId(String procInstId) {
		this.procInstId = procInstId;
	}
	
}