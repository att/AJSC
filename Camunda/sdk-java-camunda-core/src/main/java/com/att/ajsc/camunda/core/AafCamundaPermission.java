/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package com.att.ajsc.camunda.core;

import java.io.Serializable;

public class  AafCamundaPermission implements Serializable
{
private static final long serialVersionUID = 1L;

   private String type;
   
   private String instance;
   
   private String action;

public String getType() {
	return type;
}

public void setType(String type) {
	this.type = type;
}

public String getInstance() {
	return instance;
}

public void setInstance(String instance) {
	this.instance = instance;
}

public String getAction() {
	return action;
}

public void setAction(String action) {
	this.action = action;
}
}