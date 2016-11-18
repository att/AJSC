/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.beans;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;

public class PropertiesMapBean {
	public static String getProperty(String propFileName, String propertyKey)
	{
		return AJSCPropertiesMap.getProperty(propFileName, propertyKey);
	}

}
