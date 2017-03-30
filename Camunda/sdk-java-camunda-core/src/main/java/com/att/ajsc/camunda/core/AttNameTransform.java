/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package com.att.ajsc.camunda.core;

import java.io.Serializable;

public class AttNameTransform implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String attuid;
	private String firstName;
	private String lastName;
	private String middleName;
	private String searchString;
	
	public String getAttuid() {
		return attuid;
	}
	public void setAttuid(String attuid) {
		this.attuid = attuid;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getMiddleName() {
		return middleName;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

}
