/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesServiceTest extends BaseTestCase{
	
	final static Logger logger = LoggerFactory.getLogger(PropertiesServiceTest.class);
	private static PropertiesService propService;
	private static FilePersistenceService filePersistenceService;


	@Before
	public void setUp() {
		super.setUp();
		filePersistenceService = new FilePersistenceService();
		filePersistenceService.init();
		RouteMgmtService.setPersistenceService(filePersistenceService);
		propService = new PropertiesService();
		propService.init();
	}

	@Test
	public void testInsertOrUpdateProperties() {
		ServiceProperties serviceProperties = new ServiceProperties();
		//"${namespace}.${serviceName}.${serviceVersion}"
		serviceProperties.setNamespace("MyNamespace");
		serviceProperties.setServiceName("MyService");
		serviceProperties.setServiceVersion("1.0");
		serviceProperties.setStatus("");

		ServiceProperty serviceProperty1 = new ServiceProperty();
		serviceProperty1.setName("propName1");
		serviceProperty1.setValue("propValue1");

		ServiceProperty serviceProperty2 = new ServiceProperty();
		serviceProperty2.setName("propName2");
		serviceProperty2.setValue("propValue2");
		serviceProperties.addToProps(serviceProperty1);
		serviceProperties.addToProps(serviceProperty2);

		propService.insertOrUpdateProps(serviceProperties);
		assertEquals("propValue1", System.getProperty("MyNamespace.MyService.1.0.propName1"));
	}

	
	
	@Test
	public void testDeleteProperties() {
		ServiceProperties serviceProperties = new ServiceProperties();
		serviceProperties.setNamespace("MyNamespace");
		serviceProperties.setServiceName("MyService");
		serviceProperties.setServiceVersion("1.0");
		serviceProperties.setStatus("");

		ServiceProperty serviceProperty1 = new ServiceProperty();
		serviceProperty1.setName("propName1");
		serviceProperty1.setValue("propValue1");
		serviceProperties.addToProps(serviceProperty1);

		propService.deleteProps(serviceProperties.getNamespace());
		
		assertNull(System.getProperty("MyNamespace.MyService.1.0.propName1"));
	}

}
