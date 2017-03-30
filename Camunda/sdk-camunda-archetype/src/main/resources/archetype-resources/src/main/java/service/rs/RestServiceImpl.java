/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
#set($symbol_pound='#')#set($symbol_dollar='$')#set($symbol_escape='\')package ${package}.service.rs;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.att.ajsc.common.Tracable;
import ${package}.common.LogMessages;
import com.att.ajsc.common.AjscService;

import ${package}.model.HelloWorld;
import ${package}.service.SpringService;
import com.att.ajsc.logging.AjscEelfManager;
import com.att.eelf.configuration.EELFLogger;

@AjscService
public class RestServiceImpl implements RestService {	
	private static EELFLogger log = AjscEelfManager.getInstance().getLogger(RestServiceImpl.class);

	@Autowired
	private SpringService service;

	public RestServiceImpl() {
		// needed for autowiring
	}

	@Override
	@Tracable(message = "invoking quick hello")
	public HelloWorld getQuickHello(String name) {	
		log.info(LogMessages.RESTSERVICE_HELLO);
		log.debug(LogMessages.RESTSERVICE_HELLO_NAME, name);
		return service.getQuickHello(name);
	}

}
