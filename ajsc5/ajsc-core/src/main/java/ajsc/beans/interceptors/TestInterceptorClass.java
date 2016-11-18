/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.beans.interceptors;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestInterceptorClass implements AjscInterceptor {
	static final Logger logger = LoggerFactory.getLogger(TestInterceptorClass.class);
	
	private static TestInterceptorClass classInstance = null;
	private static boolean allow=false;
	
	public static TestInterceptorClass getInstance() {
		if(classInstance == null){
			classInstance = new TestInterceptorClass();
		}
		return classInstance;
	}
	
	public boolean allowOrReject(HttpServletRequest req, HttpServletResponse resp, Map<?,?> paramMap) throws Exception{
		logger.info("Interceptor:: TestInterceptorClass called");
		log(req, resp);
		
		return allow;
	}
	
	private void log(HttpServletRequest req, HttpServletResponse resp){
		if(allow){
			logger.info("URI path = "+req.getPathInfo());
			logger.info("Request URI = "+req.getRequestURI());
		}
	}
}
