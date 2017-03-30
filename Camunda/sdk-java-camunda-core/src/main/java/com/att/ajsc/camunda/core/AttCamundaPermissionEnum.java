/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package com.att.ajsc.camunda.core;

import org.camunda.bpm.engine.authorization.Permissions;
public enum AttCamundaPermissionEnum 
{
	  NONE(Permissions.NONE),
	  ALL( Permissions.ALL),
	  READ( Permissions.READ),
	  UPDATE(Permissions.UPDATE),
	  CREATE(Permissions.CREATE),
	  DELETE(Permissions.DELETE),
	  ACCESS(Permissions.ACCESS);
	
	private Permissions permissions;
	
	private AttCamundaPermissionEnum(Permissions permissions)
	{
		this.permissions = permissions;
	}
	
	public Permissions getPermissions()
	{
		return this.permissions;
	}

}
