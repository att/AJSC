/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.spring.SpringCamelContext;
import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ajsc.ComputeRoute;
import ajsc.ComputeService;

public class ComputeServiceTest extends BaseTestCase {

	private static ComputeService computeService;
	static ApplicationContext appCtx;
	static SpringCamelContext camelCtx;

	@BeforeClass
	public static void setUpClass() throws Exception {

	}

	@Before
	public void setUp() {
		super.setUp();
		appCtx = new ClassPathXmlApplicationContext("applicationContext_test.xml");
		computeService = appCtx.getBean(ComputeService.class);
		File openEJBPropsFile = new File(System.getProperty("AJSC_HOME")+File.separator+"etc"+File.separator+"OpenEjb.properties");
		File f=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"OpenEjb.properties");

		copyFiles(f, openEJBPropsFile);
	}

	@After
public void tearDown() { }

	// private long getCamelContextMapSize(){
	// Map<String, CamelContext> ctx_map =
	// (HashMap<String,CamelContext>)computeService.getCtxMap();
	// System.out.println("+++ getCamelContextMapSize()::ctxMap -> "+ctx_map);
	// return ctx_map.size();
	// }

	private void setSystemPropertiesForGRMRegistration() {
		System.setProperty("AFT_LATITUDE", "23.4");
		System.setProperty("AFT_LONGITUDE", "33.6");
	}
	//@Ignore
	@Test
	public void testAddContext() {
		System.out.println("Running testAddContext");
		String ctxKey = "testContext:v1";
		computeService.addContext(ctxKey);
		camelCtx = (SpringCamelContext) ((HashMap<String, CamelContext>) computeService.getCtxMap()).get(ctxKey);

		// assertEquals("Context 'testContext:v1' was not added", 1,
		// getCamelContextMapSize());
		assertEquals("Context 'testContext:v1' was not added", "testContext-v1", camelCtx.getName());
	}
	//@Ignore
	@Test
	public void testContextStop() {
		System.out.println("running testContextStop");
		// stop the context
		String ctxKey = "testContext:v1";
		computeService.stop();
		camelCtx = (SpringCamelContext) ((HashMap<String, CamelContext>) computeService.getCtxMap()).get(ctxKey);
		assertNull("Context 'testContext:v1' was not stopped ", camelCtx);
	}
	//@Ignore
	@Test
	public void testContextRestart() {
		System.out.println("running testContextRestart");
		// restart the context
		computeService.restart("testContext:v1");
		camelCtx = (SpringCamelContext) ((HashMap<String, CamelContext>) computeService.getCtxMap())
						.get("testContext:v1");
		assertEquals("Context 'testContext:v1' was not found", "testContext-v1", camelCtx.getName());
	}
	//@Ignore
	@Test
	public void testContextDelete() {
		System.out.println("running testContextDelete");
		// delete the context
		String ctxKey = "testContext:v1";
		// computeService.deleteContext("default:0");
		computeService.addContext(ctxKey);
		computeService.deleteContext(ctxKey);
		// camelCtx =
		// (SpringCamelContext)((HashMap<String,CamelContext>)computeService.getCtxMap()).get("default:0");
		camelCtx = (SpringCamelContext) ((HashMap<String, CamelContext>) computeService.getCtxMap()).get(ctxKey);
		assertNull("Context 'testContext-v1' was not deleted ", camelCtx);
	}
	//@Ignore
	@Test
	public void testRouteAdded() {
		System.out.println("running RouteAdded test");

		String testRouteStr = "<route id=\"helloWorld\" xmlns=\"http://camel.apache.org/schema/spring\" trace=\"true\">"
						+ "<from uri=\"direct:route1\" />"
						+ "<setBody><constant>Hello World</constant></setBody>"
						+ "</route>";

		ComputeRoute cr = new ComputeRoute();
		cr.setId("testNamespace:helloWorld:v1");
		cr.setNamespace("testNamespace");
		cr.setRouteName("helloWorld");
		cr.setRouteVersion("v1");
		cr.setRouteDefinition(testRouteStr);
		cr.save();

		/*
		 * String testRouteStr1=
		 * "<route id=\"helloWorld1\" xmlns=\"http://camel.apache.org/schema/spring\" trace=\"true\">"
		 * + "<from uri=\"direct:route1\" />"+
		 * "<setBody><constant>Hello World</constant></setBody>"+ "</route>";
		 * ComputeRoute cr1 = new ComputeRoute();
		 * cr1.setId("testNamespace:helloWorld1:v2");
		 * cr1.setNamespace("testNamespace"); cr1.setRouteName("helloWorld1");
		 * cr1.setRouteVersion("v2"); cr1.setRouteDefinition(testRouteStr1);
		 * cr1.save();
		 */

		computeService.addContext("default:0");
		computeService.addRoute(cr);

		camelCtx = (SpringCamelContext) ((HashMap<String, CamelContext>) computeService.getCtxMap()).get("default:0");
		// assert whether route was indeed saved in the default camel context
		// assertNotNull("Default Camel Context returned no Route with such id",
		// camelCtx.getRoute("testNamespace:helloWorld:v1"));

		// validate whether this route is unique across the camel context
		assertNull("Route being added is not unique and is present already in some other context",
						computeService.validateFromEndpointUnique(cr));
		// assertNotNull("Route being added is not unique and is present already in some other context",
		// computeService.validateFromEndpointUnique(cr1));

		// assert whether you get a non-null RouteDefinitionList
		assertNotNull("RouteDefinition list is null", computeService.getRouteDefinitions());
		assertNotNull("RouteDefinition list for the context is null", computeService.getRouteDefinitions("default:0"));

		// register the endpoint with GRM
		setSystemPropertiesForGRMRegistration();
		computeService.registersServicesToGRM();

		// test endpoint map
		assertNotNull("Endpoint map is null", computeService.getEndpointMap());
		assertNotNull("Endpoint map for the given camel context is null", computeService.getEndpointMap("default:0"));

		// test Endpoints collection
		assertNotNull("Endpoint list", computeService.getEndpoints());
		assertNotNull("Endpoint collection for the given camel context is null",
						computeService.getEndpoints("default:0"));

		// assert context is not empty
		assertFalse("Context is empty", (Boolean) computeService.isEmptyContext("default:0"));

		// delete the route
		computeService.delRoute("testNamespace:helloWorld:v1");
		camelCtx = (SpringCamelContext) ((HashMap<String, CamelContext>) computeService.getCtxMap()).get("default:0");
		// assert whether route was indeed deleted from the default camel
		// context
		assertNull("Route with such id was not deleted from the context",
						camelCtx.getRoute("testNamespace:helloWorld:v1"));
		// assertTrue("Context is not empty",
		// (Boolean)computeService.isEmptyContext("default:0"));

	}
	//@Ignore
	@Test
	public void testLoadBeans() {
		System.out.println("running loadBeans Test");

		String ctxKey = "default:0";
		computeService.addContext(ctxKey);

		// assert that there were no beans loaded for the context
		computeService.loadBeans(ctxKey);
		camelCtx = (SpringCamelContext) ((HashMap<String, CamelContext>) computeService.getCtxMap()).get(ctxKey);
		assertTrue("Beans were added to the context", camelCtx.getApplicationContext().getParent().equals(appCtx));
	}
	@Ignore
	@Test
	public void testStart(){
		System.out.println("running start test");
		computeService.stop();
		computeService.start();
		assertTrue((boolean)computeService.getStarted());
		
	}
	@Ignore
	@Test
	public void testCreateEjbContext(){
		System.out.println("running CreateEjbContext test");
		String ctxKey = "default:0";
		computeService.createEjbContext(ctxKey);
	}
	@Ignore("Already called else where")
	@Test
	public void testGetDefaultEjbJndiProps(){
		System.out.println("running getDefaultEjbJndiProps test");
		computeService.getDefaultEjbJndiProps();
		
	}
	
	
	@Test
	public void testInit(){
		System.out.println("running init test");
		computeService.init();
		
	}
	
	@Test
	public void testInitProp(){
		System.out.println("running initProp test");
		//ServiceProperties.createServiceProperty("ajsc", "ComputeService", "1.0.2", "tests", "8");
		ServiceProperties sp=new ServiceProperties();
		sp.setNamespace("ajsc");
		sp.setServiceName("ComputeService");
		sp.setServiceVersion("0");
		Properties prop=new Properties();
		
		List<ServiceProperty> props = new ArrayList<ServiceProperty>();
		ServiceProperty serp1=new ServiceProperty();
		serp1.setName("fileSystemRoot");
		serp1.setValue("0");
		
		ServiceProperty serp2=new ServiceProperty();
		serp2.setName("retryCount");
		serp2.setValue("3");
		
		ServiceProperty serp3=new ServiceProperty();
		serp3.setName("retryInterval");
		serp3.setValue("3");
		
		
		props.add(serp1);
		props.add(serp2);
		props.add(serp3);
		
		
		sp.setProps(props);
		//System.out.println("size:"+sp.getProps().size());
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		sp.setAjscMetaDataService(fps);
		sp.save();
		computeService.initProp();
	}
	
	
}
