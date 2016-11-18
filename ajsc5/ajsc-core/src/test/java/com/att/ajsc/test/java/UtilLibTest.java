/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.test.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import ajsc.common.CommonNames;
import ajsc.utils.UtilLib;

public class UtilLibTest {
	HttpServletRequest request = mock(HttpServletRequest.class);

	@Test
	public void shouldReturnServiceName() {
		when(request.getHeader("X-CSI-MethodName")).thenReturn("helloservice");
		assertEquals("helloservice", UtilLib.getServiceName(request));
	}
		
	@Test
	public void shouldReturnTrue() {
		assertTrue(UtilLib.compareValues("start ", "[end"));
	}
	
	@Test
	public void shouldReturnNullOrEmpty() {
		assertTrue(UtilLib.isNullOrEmpty(null));
	}
	
	@Test
	public void shouldReturnEmpty() {
		assertTrue(UtilLib.ifNullThenEmpty(null).isEmpty());
	}
	
	@Test
	public void shouldReturnJsonWrapper() {
		assertNotNull(UtilLib.jsonpWrapper("jsonpFunction", "json"));
	}
	
	@Test
	public void shouldReturnUCaseFirstLetter() {
		assertEquals("First",UtilLib.uCaseFirstLetter("first"));
	}
	
	@Test
	public void shouldReturnEpochToXMLGC() {
		XMLGregorianCalendar gcal;
		gcal = UtilLib.epochToXmlGC(100000);
		assertEquals(1,gcal.getDay());
	}
	
	@Test
	public void shouldReturnClientApp() {
		assertTrue(UtilLib.getClientApp().contains("ajsc-csi-restful~"));
	}
	
	@Test
	public void shouldReturnDme2(){
		String uri = UtilLib.dme2UriToLocaldme2Uri("dme2Uri/local/test", "version", "envContext", "routeOffer");
		assertEquals("dme2Uri/local/test",uri);
	}
	
	@Test
	public void shouldReturnErrorResponse(){
		assertEquals(CommonNames.BODY_TYPE_JSON,UtilLib.getErrorResponseBodyType("key:value;key2:value2"));
	}
	
	@Test
	public void shouldReturnStartTime(){
		assertTrue(UtilLib.getStartTimestamp("100").contains("1969-12-31"));
	}
	
	@Test
	public void shouldReturnService(){
		System.setProperty("APP_SERVLET_URL_PATTERN","servlet:/*");
		System.setProperty("APP_RESTLET_URL_PATTERN","restlet://*");
						
		UtilLib.getServiceName("services", "pathinfo");
	}
}
