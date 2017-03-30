/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package com.att.ajsc.filemonitor;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AJSCPropertiesMap 
{
	private static HashMap<String, HashMap<String, String>> mapOfMaps = new HashMap<String, HashMap<String, String>>();
	static final Logger logger = LoggerFactory.getLogger(AJSCPropertiesMap.class);
	
	public static void refresh(File file) throws Exception
	{
		try
		{
			logger.info("Loading properties - " + (file != null?file.getName():""));
			
			//Store .json & .properties files into map of maps
			String filePath = file.getPath();
			
			if(filePath.lastIndexOf(".json")>0){
				
				ObjectMapper om = new ObjectMapper();
				TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {};
				HashMap<String, String> propMap = om.readValue(file, typeRef);
				HashMap<String, String> lcasePropMap = new HashMap<String, String>();
				for (String key : propMap.keySet() )
				{
					String lcaseKey = ifNullThenEmpty(key);
					lcasePropMap.put(lcaseKey, propMap.get(key));
				}
				
				mapOfMaps.put(file.getName(), lcasePropMap);
				
				
			}else if(filePath.lastIndexOf(".properties")>0){
				Properties prop = new Properties();
				FileInputStream fis = new FileInputStream(file);
				prop.load(fis);
				
				@SuppressWarnings("unchecked")
				HashMap<String, String> propMap = new HashMap<String, String>((Map)prop);
				
				mapOfMaps.put(file.getName(), propMap);
				
				fis.close();
			}

			logger.info("File - " + file.getName() + " is loaded into the map and the corresponding system properties have been refreshed");
		}
		catch (Exception e)
		{
			logger.error("File " + (file != null?file.getName():"") + " cannot be loaded into the map ", e);
			throw new Exception("Error reading map file " + (file != null?file.getName():""), e);
		}
	}
	
	public static String getProperty(String fileName, String propertyKey)
	{
		HashMap<String, String> propMap = mapOfMaps.get(fileName);
		return propMap!=null?propMap.get(ifNullThenEmpty(propertyKey)):"";
	}
	
	public static HashMap<String, String> getProperties(String fileName){
		return mapOfMaps.get(fileName);
	}
	
	private static String ifNullThenEmpty(String key) {
		if (key == null) {
			return "";
		} else {					
			return key;
		}		
	}

}
