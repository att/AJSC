/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.beans.interceptors;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HelloWorldLogger implements AjscInterceptor {
	
	static final Logger logger = LoggerFactory.getLogger(HelloWorldLogger.class);
	
	private static HelloWorldLogger classInstance = null;
	private static boolean allow=true;
	
	public static HelloWorldLogger getInstance() {
		if(classInstance == null){
			classInstance = new HelloWorldLogger();
		}
		return classInstance;
	}
	
	public boolean allowOrReject(HttpServletRequest req, HttpServletResponse resp, Map<?,?> paramMap) throws Exception{
		logger.info("Interceptor:: HelloWorldLogger called");
		System.out.println("Interceptor:: HelloWorldLogger called");
		log(req, resp);
		
		return allow;
	}
	
	private void log(HttpServletRequest req, HttpServletResponse resp) {
		if(allow){
			logger.info("URI path = "+req.getPathInfo());
			logger.info("Request URI = "+req.getRequestURI());
		}
	}
	
}
