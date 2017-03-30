/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package com.att.ajsc.camunda.core;

public enum AttCamundaResourceEnum 
{
	  APPLICATION(0),
	  USER( 1),
	  GROUP( 2),
	  GROUP_MEMBERSHIP(3),
	  AUTHORIZATION(4),
	  FILTER(5),
	  PROCESS_DEFINITION(6),
	  TASK( 7),
	  PROCESS_INSTANCE( 8),
	  DEPLOYMENT( 9);
	
	private int resourceValue;
	
	private AttCamundaResourceEnum(int resourceValue)
	{
		this.resourceValue = resourceValue;
	}
	
	public int getResourceValue()
	{
		return this.resourceValue;
	}
}
