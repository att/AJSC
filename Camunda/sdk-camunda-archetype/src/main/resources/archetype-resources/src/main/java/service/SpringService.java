/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
#set($symbol_pound='#')#set($symbol_dollar='$')#set($symbol_escape='\')package ${package}.service;

import ${package}.model.HelloWorld;

public interface SpringService {
	public HelloWorld getQuickHello(String name);
}
