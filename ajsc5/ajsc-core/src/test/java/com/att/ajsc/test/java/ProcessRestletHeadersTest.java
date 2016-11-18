/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.restlet.RestletConstants;
import org.junit.Test;
import org.restlet.data.Form;
import org.restlet.engine.adapter.HttpRequest;

import ajsc.restlet.ProcessRestletHeaders;



public class ProcessRestletHeadersTest {
	
	@Test
	public void shouldProcessHeaders() throws Exception{
		Exchange mockExchange = mock(Exchange.class);
		Message message = mock(Message.class);
		HttpRequest mockRequest = mock(HttpRequest.class);
		when(mockExchange.getIn()).thenReturn(message);
		when(mockExchange.getOut()).thenReturn(mock(Message.class));
		when(message.getHeader("org.restlet.http.headers")).thenReturn(message);
		when(message.getHeader("CamelHttpQuery")).thenReturn("non null value");
		when(message.getHeader("CamelHttpQuery",String.class)).thenReturn("key=value1&key2=value2");
		when(message.getHeader(RestletConstants.RESTLET_REQUEST, HttpRequest.class)).thenReturn(mockRequest);
		ConcurrentMap<String,Object> concurrentMap =new ConcurrentHashMap<>();
		
		Form form = new Form();
		form.set("name", "value");
		
		concurrentMap.put("org.restlet.http.headers", form);
		when(mockRequest.getAttributes()).thenReturn(concurrentMap);
		
		ProcessRestletHeaders.processHeaders(mockExchange);
		verify(mockExchange,times(5)).getIn();
		verify(mockExchange.getIn()).getHeader("org.restlet.http.headers");
		verify(mockExchange.getIn()).getHeader(RestletConstants.RESTLET_REQUEST,HttpRequest.class);
		//verify(mockExchange.getIn()).getHeader("CamelHttpQuery");
	}

}
