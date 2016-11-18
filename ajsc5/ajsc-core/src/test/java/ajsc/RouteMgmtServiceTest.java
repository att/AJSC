/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.camel.spring.SpringCamelContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RouteMgmtServiceTest extends BaseTestCase {
	private static RouteMgmtService routeMgmtService;
	static ApplicationContext appCtx;
	static PropertiesService propertiesService;
	 boolean propertyServiceInitialized = false;

	static DocService docService;
	static UserDefinedBeansDefService userDefinedBeansDefService;
	static UserDefinedJarService userDefinedJarService;
	static ComputeService computeService;
	static LoggingConfigurationService loggingConfigurationService;
	static VandelayService vandelayService;
	boolean vandelayServiceInitialized = false;

	//static com.tibco.tibjms.TibjmsQueueConnectionFactory jmsConnectionFactory;
	//static com.ibm.mq.jms.MQQueueConnectionFactory jmsConnectionFactory // This is for WMQ
	//static org.apache.activemq.ActiveMQConnectionFactory jmsConnectionFactory;  This is for ActiveMQ
	static FilePersistenceService persistenceService;
	boolean persistenceServiceInitialized = false;
	
	@Before
	public void setUp() {

		super.setUp();
		System.setProperty("csiEnable", "true");

		copyFiles(new File("src/test/resources/logback_jms.xml"), new File(
				AJSC_TEST_HOME + File.separator + "etc" + File.separator
						+ "logback_jms.xml"));
		copyDir(new File("src/test/resources/appprops"), new File(
				AJSC_TEST_HOME + File.separator + "etc" + File.separator
						+ "appprops"));
		copyDir(new File("src/test/resources/sysprops"), new File(
				AJSC_TEST_HOME + File.separator + "etc" + File.separator
						+ "sysprops"));
		copyFiles(new File("src/test/resources/runtimeEnvironment.zip"),
				new File(AJSC_TEST_HOME + File.separator + "runtime"
						+ File.separator + "runtimeEnvironment.zip"));

		copyFiles(new File("src/test/resources/demo_v1.zip"), new File(
				AJSC_TEST_HOME + File.separator + "services" + File.separator
						+ "demo_v1.zip"));

		copyFiles(new File("src/test/resources/demo_v1_props.zip"), new File(
				AJSC_TEST_HOME + File.separator + "services" + File.separator
						+ "demo_v1_props.zip"));

		appCtx = new ClassPathXmlApplicationContext(
				"applicationContext_test.xml");
		propertiesService  = appCtx.getBean(PropertiesService.class);
		routeMgmtService = appCtx.getBean(RouteMgmtService.class);
	}

	@After
	public void tearDown() {
		//deleteDirectory(new File(AJSC_TEST_HOME));
	}

	@Test
	public void testgetMessageMap() {
		try {
			routeMgmtService.getMessageMap("stagedDeployServices()", 2);
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	@Test
	public void testStagedDeployRuntime() {
		try {
			routeMgmtService.stagedDeployRuntime();
			//verify properties are set properly
			//verify map
			//verify archive file is created
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	@Test
	public void testStagedDeployServices() {
		try {
			routeMgmtService.stagedDeployServices();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	@Test
	public void testDisplayLogo() {
		try {
			routeMgmtService.archiveFiles(new File(AJSC_TEST_HOME
					+ File.separator + "runtime"), "runtimeEnvironment.zip");
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	@Test
	public void testInitializeRouteMgmtService() {

		assertTrue("csiEnable is true and initilize csilogging", (System.getProperty("csiEnable") != null && System
				.getProperty("csiEnable").equals("true")));
		assertTrue(!propertyServiceInitialized);
		routeMgmtService.init();
	}

}
