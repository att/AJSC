/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.mockito.Mockito.*;

import java.io.File;

import javax.servlet.FilterChain;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

import ajsc.BaseTestCase;
import ajsc.common.CommonNames;
import ajsc.filters.InterceptorFilter;

import com.att.ajsc.csi.restmethodmap.RefresheableSimpleRouteMatcher;
import com.att.ajsc.filemonitor.AJSCPropertiesMap;

public class InterceptorFilterTest extends BaseTestCase{

	@Test
	public void testFilter() throws Exception{ 
		InterceptorFilter filter = new InterceptorFilter();
		HttpServletRequest mockRequest = mock(HttpServletRequest.class);
		ServletResponse mockResponse = mock(HttpServletResponse.class);
		File propertiesFile = new File(TEST_RSC_DIR+"PreProcessorInterceptors.properties");
		File post=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"appprops"+File.separator+"PostProcessorInterceptors.properties");
		AJSCPropertiesMap.refresh(propertiesFile);
		AJSCPropertiesMap.refresh(post);
		AJSCPropertiesMap.getProperties("PreProcessorInterceptors.properties");
		
		File file = new File(TEST_RSC_DIR+"/appprops/methodMapper.properties");
		RefresheableSimpleRouteMatcher.refresh(file);
		
		when(mockRequest.getMethod()).thenReturn("GET");
		when(mockRequest.getRequestURI()).thenReturn("/rest/test1/v1/helloWorld");
		when(mockRequest.getPathInfo()).thenReturn("pathinfo");
		
		FilterChain chain = mock(FilterChain.class);
		
		filter.doFilter(mockRequest, mockResponse, chain);
		verify(mockRequest).setAttribute(eq(CommonNames.ATTR_START_TIME), anyString());
	}
}
