/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.csi.restmethodmap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonParser;

public class RefresheableSimpleRouteMatcher 
{
	private static AtomicReference<RouteMatcher> sm = new AtomicReference<RouteMatcher>();
	private static Logger logger = LoggerFactory.getLogger(RefresheableSimpleRouteMatcher.class);
	
	public static void refresh(File file) throws Exception
	{
			logger.info("Refresh of service spec from file " + file.getAbsolutePath());
			
			ObjectMapper om = new ObjectMapper().configure(JsonParser.Feature.ALLOW_COMMENTS, true);
			TypeReference<HashMap<String, ArrayList<HashMap<String, String>>>> typeRef = new TypeReference<HashMap<String, ArrayList<HashMap<String, String>>>>() {};
			
			logger.info("Reading file...");
			HashMap<String, ArrayList<HashMap<String, String>>> serviceSpec = om.readValue(file, typeRef);
			
			logger.info("Creating new matcher...");
			SimpleRouteMatcher newMatcher = new SimpleRouteMatcher();
			
 			for ( Entry<String, ArrayList<HashMap<String, String>>> e : serviceSpec.entrySet() )
 			{
 				String service = e.getKey();
 				logger.debug("Processing service - " + service);
 				for ( HashMap<String, String> h : e.getValue() )
 				{
 					String method = h.get("method");
 					String url = h.get("url");
 					String logicalName = service + "-" + h.get("logicalName");
 					String dme2url= h.get("dme2url");
					String type = h.get("type");
					String serviceName = h.get("serviceName");
					String passThroughRespCode = h.get("passThroughRespCode");
					logger.debug("Adding method " + method + " URL: " + url + " logicalName: " + logicalName + " dme2url: " + dme2url + " type: "+ type +" serviceName: " + serviceName);
					newMatcher.parseValidateAddRoute(service, method, url, logicalName, dme2url, type, serviceName,passThroughRespCode);
 				}
 			}			
			sm.set(newMatcher);
		}
	
	public static RouteMatcher getRouteMatcher()
	{
		return sm.get();
	}
}
