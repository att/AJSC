/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package com.att.ajsc.camunda.core;

import java.util.List;
public class  AafCamundaRole 
{
   private String name;
   
   private List<AafCamundaPermission> perms;
   
   public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public List<AafCamundaPermission> getPerms() {
	return perms;
}

public void setPerms(List<AafCamundaPermission> perms) {
	this.perms = perms;
}

}
