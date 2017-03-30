/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package com.att.ajsc.camunda.core;

import javax.servlet.ServletContext;

import org.camunda.bpm.application.ProcessApplicationInfo;
import org.camunda.bpm.engine.spring.application.SpringServletProcessApplication;
import org.springframework.web.context.ServletContextAware;

/**
 * Override org.camunda.bpm.engine.spring.application.SpringServletProcessApplication.
 * 
 * When the PROP_SERVLET_CONTEXT_PATH property is empty (""), the process forms do 
 * not resolve.  For example, for Form Key app:example-log-message.xhtml, Tasklist 
 * generates a URL starting with: unsafe:app://example-log-message.xhtml...,
 * rather than http://localhost:64597/example-log-message.xhtml...
 * 
 * Note that ServletContext.getContextPath() states that "" will be returned for the
 * default (root) context.  Given this is the expected return, it appears that Camunda
 * is not coded properly to handle this result.
 * See: https://docs.oracle.com/javaee/6/api/javax/servlet/ServletContext.html#getContextPath%28%29
 * 
 */
public class AjscServletProcessApplication extends
		SpringServletProcessApplication implements ServletContextAware {

	/**
	 * Just invoke super version of method...
	 */
	@Override
	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);
	}
	
	/**
	 * Invoke super just in case Camunda adds some additional functionality to their method.
	 * But then override PROP_SERVLET_CONTEXT_PATH. 
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		String contextPath = servletContext.getContextPath();
		if ( contextPath == null || contextPath.length() == 0 ) {
			contextPath = "/";
		}
		properties.put(ProcessApplicationInfo.PROP_SERVLET_CONTEXT_PATH, contextPath);
	}

}
