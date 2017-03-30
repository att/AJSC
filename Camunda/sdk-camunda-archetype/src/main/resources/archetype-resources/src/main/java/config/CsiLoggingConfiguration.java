/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
#set($symbol_pound='#')#set($symbol_dollar='$')#set($symbol_escape='\')package ${package}.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.att.ajsc.csilogging.common.AsyncSupport;
import com.att.ajsc.csilogging.interceptors.CsiLoggingCamelPostInterceptor;
import com.att.ajsc.csilogging.interceptors.CsiLoggingCamelPreInterceptor;

@Configuration
public class CsiLoggingConfiguration {
	   @Bean
	    public CsiLoggingCamelPreInterceptor csiLoggingCamelPreInterceptor() {
	        return new CsiLoggingCamelPreInterceptor();
	    }

	    @Bean
	    public CsiLoggingCamelPostInterceptor csiLoggingCamelPostInterceptor() {
	        return new CsiLoggingCamelPostInterceptor();
	    }	     

	    @Bean
	    public AsyncSupport asyncsupport() {
	        return new AsyncSupport();
	    }
}
