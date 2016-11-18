/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.net.SyslogAppender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy;
import ch.qos.logback.classic.Logger;

import com.att.aft.dme2.api.DME2Manager;

import ajsc.BaseTestCase;
import ajsc.common.CommonNames;
import ajsc.utils.DME2Helper;
import static org.junit.Assert.*;

import java.lang.reflect.Field;
public class loggingConfigurationTest extends BaseTestCase{

	@SuppressWarnings("unchecked")
	@Before
	public void setUp(){
		super.setUp();
		File r=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"logback.xml");
		File d=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"rollingpolicy.log");
		//System.out.println(r.getAbsolutePath());
		this.copyFiles(r, new File(System.getProperty("AJSC_HOME")+File.separator+"logs"+File.separator+"logback.xml"));
		this.copyFiles(d, new File(System.getProperty("AJSC_HOME")+File.separator+"logs"+File.separator+"rollingpolicy.log"));
	
		
	}

	@Test
	public void initTest() throws IOException{
		LoggingConfigurationService l=new LoggingConfigurationService();
		//configureloggingHome variables
		Map<String,String>newenv=new HashMap<String,String>();

		//extra s to make path not exist
		System.setProperty("LOGGING_HOME", System.getProperty("AJSC_HOME")+File.separator+"logss");
		System.setProperty("AUTO_NS_LOGGING_DISABLED", "false");
		newenv.put("AJSC_HOME", System.getProperty("AJSC_HOME"));
		//extra s to make path not exist
		newenv.put("LOGGING_HOME", System.getProperty("AJSC_HOME")+File.separator+"logss");
		
		//configureNsAutoLogging variables
		System.setProperty("AUTO_NS_LOGGING_DISABLED", "false");
		newenv.put("AUTO_NS_LOGGING_DISABLED", "false");
		System.setProperty("AUTO_NS_LOGGING_ADDITIVE", "false");
		newenv.put("AUTO_NS_LOGGING_ADDITIVE", "false");
		System.setProperty("AUTO_NS_APPENDERS_DISABLED", "false");
		newenv.put("AUTO_NS_APPENDERS_DISABLED", "false");
		
		System.setProperty("AUTO_NS_LOGGING_APPENDER_TYPE", "false");
		newenv.put("AUTO_NS_LOGGING_APPENDER_TYPE", "false");
		
		System.setProperty("AUTO_NS_APPENDERS_USE_SUBDIRS", "false");
		newenv.put("AUTO_NS_APPENDERS_USE_SUBDIRS", "false");
		
		setEnv(newenv);
		l.init();
		
		
		System.setProperty("LOGGING_HOME", System.getProperty("AJSC_HOME")+File.separator+"logs");
		System.setProperty("AUTO_NS_LOGGING_DISABLED", "true");
		newenv.put("myvar", "works");
		newenv.put("AJSC_HOME", System.getProperty("AJSC_HOME"));
		newenv.put("LOGGING_HOME", System.getProperty("AJSC_HOME")+File.separator+"logs");
		
		//configureNsAutoLogging variables
		System.setProperty("AUTO_NS_LOGGING_DISABLED", "true");
		newenv.put("AUTO_NS_LOGGING_DISABLED", "true");
		System.setProperty("AUTO_NS_LOGGING_ADDITIVE", "true");
		newenv.put("AUTO_NS_LOGGING_ADDITIVE", "true");
		System.setProperty("AUTO_NS_APPENDERS_DISABLED", "true");
		newenv.put("AUTO_NS_APPENDERS_DISABLED", "true");
		
		System.setProperty("AUTO_NS_LOGGING_APPENDER_TYPE", "true");
		newenv.put("AUTO_NS_LOGGING_APPENDER_TYPE", "true");
		
		System.setProperty("AUTO_NS_APPENDERS_USE_SUBDIRS", "true");
		newenv.put("AUTO_NS_APPENDERS_USE_SUBDIRS", "true");
		
		setEnv(newenv);
		
		
		l.init();	
		
		l.stop();
		l.shutdown();
		l.start();
		//System.out.println(System.getenv("myvar"));
		
	}
	
	@Test
	public void invokeConfigurator(){
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		System.setProperty("logback.configurationFile", System.getProperty("AJSC_HOME")+File.separator+"logs"+File.separator+"logback.xml");
		//l.invokeConfigurator();
		l.invokeConfigurator(null, new LoggerContext());
	}
	
	@Test
	public void getPatternLayoutInstance(){
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		l.getPatternLayoutInstance(null);
		//l.getPatternLayoutInstance("stuff");
	}
	
	@Test
	public void getEncoderInstanceTest(){
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		PatternLayoutEncoder ple=(PatternLayoutEncoder) l.getEncoderInstance(null);
		//System.out.println(ple.getClass().getName());
		assertEquals("ch.qos.logback.classic.encoder.PatternLayoutEncoder", ple.getClass().getName());
	}
	
	@Test
	public void getFilterInstanceTest(){
		System.out.println("Running getFilterInstanceTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		ThresholdFilter d=(ThresholdFilter)l.getFilterInstance(null);
		
		assertEquals("ch.qos.logback.classic.filter.ThresholdFilter", d.getClass().getName());
	}
	
	@Test
	public void getThresholdFilterInstanceTest(){
		System.out.println("Running getThresholdFilterInstanceTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		
		ThresholdFilter tf=(ThresholdFilter)l.getThresholdFilterInstance();
		assertEquals("ch.qos.logback.classic.filter.ThresholdFilter", tf.getClass().getName());
	}
	
	@Test
	public void getTriggeringPolicyInstanceTest(){
		System.out.println("Running getTriggeringPolicyInstanceTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		
		System.out.println(l.getTriggeringPolicyInstance(null).getClass());
		SizeBasedTriggeringPolicy<String> tp=(SizeBasedTriggeringPolicy)l.getTriggeringPolicyInstance(null);
		assertEquals("ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy", tp.getClass().getName());
	}
	
	@Test
	public void getRollingFileAppenderInstanceTest(){
		
		System.out.println("Running getRollingFileAppenderInstanceTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		
		Map<String,Object>map=new HashMap<String,Object>();
		map.put("appenderName", "appenderName");
		map.put("rfFileName",System.getProperty("AJSC_HOME")+File.separator+"logs"+File.separator+"rollingpolicy.log");
		//map.put("rfFiltersConf", value);
		@SuppressWarnings("rawtypes")
		RollingFileAppender rf=(RollingFileAppender)l.getRollingFileAppenderInstance(map);
		rf.setName("Name.log");
		assertEquals("Name.log", rf.getName());
		
	}
	
	@Test
	public void getFixedWindowRollingPolicyInstance(){
		System.out.println("Running getTriggeringPolicyInstanceTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		
		System.out.println(l.getFixedWindowRollingPolicyInstance(null, new RollingFileAppender<>()));
	}
	
	
	@Test
	public void getTimeBasedRollingPolicyInstanceTest(){
		System.out.println("Running getTimeBasedRollingPolicyInstanceTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		Map<String,Object>map=new HashMap<String,Object>();
		map.put("appenderName", "appenderName");
		map.put("rfFileName",System.getProperty("AJSC_HOME")+File.separator+"logs"+File.separator+"rollingpolicy.log");
		//map.put("rfFiltersConf", value);
		@SuppressWarnings("rawtypes")
		RollingFileAppender rf=(RollingFileAppender)l.getRollingFileAppenderInstance(map);
		rf.setName("Name.log");
		Map<String,Object>mapP=new HashMap<String,Object>();
		mapP.put("maxHistory",9);
		l.getTimeBasedRollingPolicyInstance(mapP, rf);
	}
	
	@Test
	public void getSyslogAppenderInstanceTest(){
		System.out.println("Running getSyslogAppenderInstanceTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		Map<String,Object>map=new HashMap<String,Object>();
		map.put("appenderName", "appenderName");
		map.put("rfFileName",System.getProperty("AJSC_HOME")+File.separator+"logs"+File.separator+"rollingpolicy.log");
		//map.put("rfFiltersConf", value);
		//map.put(key, value)
		SyslogAppender sa=(SyslogAppender)l.getSyslogAppenderInstance(map);
		assertEquals("ch.qos.logback.classic.net.SyslogAppender", sa.getClass().getName());
	}
	
	@Test
	public void getLoggersTest(){
		System.out.println("Running getLoggersTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		Map<String,Object>map=new HashMap<String,Object>();
		map.put("loggerName", "true");
		map.put("setAdditive", "false");
		ArrayList<String> arr=(ArrayList)l.getLoggers(map);
		assertEquals("java.util.ArrayList", arr.getClass().getName());
		
		l.getLoggers(null);
	}
	
	@Test
	public void getFiltersTest(){
		System.out.println("Running getFiltersTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		l.getFilters(null);
		
		
		Map<String,Object>map=new HashMap<String,Object>();
		
		map.put("next","next" );
		l.getFilters(map);
		LinkedHashSet<Map> arr=new LinkedHashSet<Map>();
		map.put("filters", arr);
		System.out.println(l.getFilters(map).getClass());
		LinkedHashSet tf=(LinkedHashSet)l.getFilters(map);
		System.out.println();
		assertEquals("java.util.LinkedHashSet",tf.getClass().getName());
	}
	
	@Test
	public void getAppendersTest(){
		System.out.println("Running getAppendersTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		Map<String,Object>map=new HashMap<String,Object>();
		map.put("appenderName", "appenderName");
		l.getAppenders(map);
		ArrayList al=(ArrayList)l.getAppenders(null);
		assertEquals("java.util.ArrayList", al.getClass().getName());
	}
	@Ignore("Need to finish")
	@Test
	public void attachAppenderToLoggerTest(){
		System.out.println("Running attachAppenderToLoggerTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		Logger log;
	}
	
	@Ignore("Need to finish")
	@Test
	public void detachAppenderFromLogger(){
		
	}
	
	@Test //need to finish
	public void attachAppenderToLoggers(){
		System.out.println("Running attachAppenderToLoggersTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		Map<String,Object>map=new HashMap<String, Object>();
		map.put("loggerName","ALL_LOGGERS");
		map.put("appender", "appender");
		l.attachAppenderToLoggers(map);
	}
	
	@Test //need to finish
	public void detachAppenderFromLoggersTest(){
		System.out.println("Running attachAppenderToLoggersTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		Map<String,Object>map=new HashMap<String, Object>();
		map.put("loggerName","ALL_LOGGERS");
		map.put("appender", "appender");
		l.detachAppenderFromLoggers(null);
	}
	
	@Test
	public void configureNamespaceLoggingSupportTest(){
		System.out.println("Running configureNamespaceLoggingSupportTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		l.configureNamespaceLoggingSupport("ajsc1", "1.1.0");
		
		System.setProperty("AUTO_NS_APPENDERS_USE_SUBDIRS", "true");
		System.setProperty("AUTO_NS_LOGGING_DISABLED","false");
		l.init();
		l.configureNamespaceLoggingSupport("ajsc2", "1.1.0");
		
		System.setProperty("AUTO_NS_LOGGING_APPENDER_TYPE", "syslogappender");
		l.init();
		l.configureNamespaceLoggingSupport("ajsc3", "1.1.0");
		

		
	}
	
	@Test
	public void startResetStopResistantTest(){
		System.out.println("Running startResetLevelChangeResistantTest");
		LoggingConfigurationService l=new LoggingConfigurationService();
		l.init();
		boolean b=l.isResetResistant();
		assertTrue(b);
		
		l.onStart(new LoggerContext());
		l.onStop(new LoggerContext());
	}
	
	//This method will change the environment variable only in the jvm
	protected static void setEnv(Map<String, String> newenv)
	{
	  try
	    {
	        Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
	        Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
	        theEnvironmentField.setAccessible(true);
	        Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
	        env.putAll(newenv);
	        Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
	        theCaseInsensitiveEnvironmentField.setAccessible(true);
	        Map<String, String> cienv = (Map<String, String>)     theCaseInsensitiveEnvironmentField.get(null);
	        cienv.putAll(newenv);
	        
	    }
	    catch (NoSuchFieldException e)
	    {
	      try {
	        Class[] classes = Collections.class.getDeclaredClasses();
	        Map<String, String> env = System.getenv();
	        for(Class cl : classes) {
	            if("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
	                Field field = cl.getDeclaredField("m");
	                field.setAccessible(true);
	                Object obj = field.get(env);
	                Map<String, String> map = (Map<String, String>) obj;
	                map.clear();
	                map.putAll(newenv);
	            }
	        }
	      } catch (Exception e2) {
	        e2.printStackTrace();
	      }
	    } catch (Exception e1) {
	        e1.printStackTrace();
	    } 
	}
}
