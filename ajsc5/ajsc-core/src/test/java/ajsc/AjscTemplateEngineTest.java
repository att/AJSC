/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.spring.SpringCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;

import ajsc.util.AjscTemplateEngine;
public class AjscTemplateEngineTest extends BaseTestCase {
	
	@Before
	public void setUp() {
		super.setUp(); 
	}
	
	@Test
	public void propertyReplaceTest() throws Exception{
		AjscTemplateEngine ate=new AjscTemplateEngine();
		File f=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"RestError.txt");
		if(f.exists())System.out.println("pie");
		Map<String, String>map=new HashMap<String, String>();
		
		String s="{\"key\"\r:{{ \"status\":\"500\"\n, \"MessageId\":\"3\",\"Message\":\"message\"}{}";
		String a1=(String) ate.propertyReplace(s, map);
		s="{\"key\"\r:{{ \"status\":\"500\"\n, \"MessageId\":\"3\",\"Message\":\"message\"}{}";
		String a2=(String) ate.propertyReplace(s, map);
		s="{\"key\"\r:{ \"status\":\"500\"\n, \"MessageId\":\"3\",\"Message\":\"message\"}";
		String a3=(String) ate.propertyReplace(s, map);
		
	}
	
	
}
