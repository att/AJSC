/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.csi.writeablerequestfilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class WriteableRequestWrapper extends HttpServletRequestWrapper 
{
	// Using a tree map - generally headers are not many, so
	// the performance of a tree map vs a hashmap is roughly equivalent
	// The advantage is the case-insensitive comparison. This
	// is required when comparing header and query params since
	// all control aspects of the http payload, headers and uri
	// are case-insensitive.
	private Map<String, ArrayList<String>> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	private Map<String, ArrayList<String>> qparams = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	
	public WriteableRequestWrapper(HttpServletRequest request) 
	{
		super(request);
	}
	
	public void addHeader(String key, String value)
	{
		if ( headers.containsKey(key) )
		{
			headers.get(key).add(value);
		}
		else
		{
			Enumeration<String> oldHeaders = super.getHeaders(key);
			ArrayList<String> oldHeadersList;
			if ( oldHeaders != null )
				oldHeadersList = new ArrayList<String>(Collections.list(oldHeaders));
			else
				oldHeadersList = new ArrayList<String>();
			
			oldHeadersList.add(value);
			headers.put(key, oldHeadersList);
		}
	}
	
	@Override
	public String getHeader(String key)
	{
		if ( headers.containsKey(key) )
			return headers.get(key).get(0);
		else
			return super.getHeader(key);
	}
	
	@Override
	public Enumeration<String> getHeaderNames()
	{
		List<String> names = Collections.list(super.getHeaderNames());
		for ( String headerName : headers.keySet() )
			names.add(headerName);
		return Collections.enumeration(names);
	}
	
	@Override
	public Enumeration<String> getHeaders(String name)
	{
		if ( headers.containsKey(name) )
			return Collections.enumeration(new HashSet<String>(headers.get(name)));
		else
			return super.getHeaders(name);
	}
	
	public void addQueryParam(String param, String value)
	{
		if ( qparams.containsKey(param) )
		{
			qparams.get(param).add(value);
		}
		else
		{
			String oldValues[] = super.getParameterValues(param);
			ArrayList<String> plist = null;
			if ( oldValues != null)
				plist = new ArrayList<String>(Arrays.asList(super.getParameterValues(param)));
			else
				plist = new ArrayList<String>();
			plist.add(value);
			qparams.put(param, plist);
		}
	}
	
	@Override
	public String getParameter(String param)
	{
		if ( qparams.containsKey(param) )
			return qparams.get(param).get(0);
		else
			return super.getParameter(param);
	}
	
	@Override
	public Enumeration<String> getParameterNames()
	{
		List<String> values = Collections.list(super.getParameterNames());
		for ( String pname : qparams.keySet() )
			values.add(pname);
		return Collections.enumeration(values);
	}
	
	@Override
	public String[] getParameterValues(String param)
	{
		if ( qparams.containsKey(param) )
		{
			String values[] = new String[qparams.get(param).size()];
			return qparams.get(param).toArray(values);
		}
		else
		{
			return super.getParameterValues(param);
		}
	}
}
