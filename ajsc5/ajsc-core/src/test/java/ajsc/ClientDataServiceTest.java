/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import static org.junit.Assert.*;

import java.util.*;

import ajsc.ClientDataService;
import ajsc.providers.FilePersistenceClient;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.SpringCamelContext;
import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.ConsumerTemplate;

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for
 * usage instructions
 */

public class ClientDataServiceTest extends BaseTestCase {
	private ClientDataService clientDataService;
	private static ComputeService computeService;
	static ApplicationContext appCtx;
	static SpringCamelContext camelCtx;

	@BeforeClass
	public static void setUpClass() throws Exception {

	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {

		super.setUp();
		appCtx = new ClassPathXmlApplicationContext(
				"applicationContext_test.xml");
		computeService = appCtx.getBean(ComputeService.class);
		String ctxKey = "testContext:v1";
		computeService.addContext(ctxKey);
		camelCtx = (SpringCamelContext) ((HashMap<String, CamelContext>) computeService
				.getCtxMap()).get(ctxKey);
		clientDataService = new ClientDataService();
		clientDataService.init(new FilePersistenceClient());
	}

	@Test
	public void pingTest() throws Exception {
		clientDataService.ping();
	}

	@Test
	public void putMapEntryTest() throws Exception {
		ProducerTemplate producer = camelCtx.createProducerTemplate();
		ConsumerTemplate consumer = camelCtx.createConsumerTemplate();

		consumer.start();
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("MAP_KEY", "putMapEntryTest_key");
		headers.put("MAP_NAME", "putMapEntryTest_value");
		headers.put("SCHEMA", "FPTest");
		producer.sendBodyAndHeaders("seda:test", "Test", headers);
		Exchange e = consumer.receive("seda:test");
		clientDataService.putMapEntry(e);

		assertEquals("putMapEntryTest_key",
				e.getOut().getHeader("MAP_KEY", "Not found"));
		consumer.stop();
		//cleanup here instead of teardown
		clientDataService.removeMapEntry(e);

	}

	@Test
	public void getMapEntryTest() throws Exception {
		ProducerTemplate producer = camelCtx.createProducerTemplate();
		ConsumerTemplate consumer = camelCtx.createConsumerTemplate();

		consumer.start();
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("MAP_KEY", "getMapEntryTest_key");
		headers.put("MAP_NAME", "getMapEntryTest_mapname");
		headers.put("SCHEMA", "FPTest");
		producer.sendBodyAndHeaders("seda:test", "Test", headers);
		Exchange e = consumer.receive("seda:test");
		clientDataService.putMapEntry(e);
		clientDataService.removeMapEntry(e);
		producer.sendBodyAndHeaders("seda:test", "Test", headers);
		Exchange ex = consumer.receive("seda:test");


		clientDataService.getMapEntry(ex);
		assertEquals("getMapEntryTest_key",
				ex.getOut().getHeader("MAP_KEY", "Not found"));
		consumer.stop();
		//cleanup here instead of teardown
		clientDataService.removeMapEntry(e);

	}

	@Test
	public void setSchemaTest() throws Exception {
		ProducerTemplate producer = camelCtx.createProducerTemplate();
		ConsumerTemplate consumer = camelCtx.createConsumerTemplate();

		consumer.start();
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("SCHEMA", "FPTest");
		producer.sendBodyAndHeaders("seda:test", "Test", headers);
		Exchange e = consumer.receive("seda:test");
		clientDataService.setSchema(e);
		assertEquals("FPTest", e.getOut().getHeader("SCHEMA", "Not found"));
		consumer.stop();
	}

	@Test
	public void putMapEntryFromHeaderTest() throws Exception {
		ProducerTemplate producer = camelCtx.createProducerTemplate();
		ConsumerTemplate consumer = camelCtx.createConsumerTemplate();

		consumer.start();
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("MAP_KEY", "putMapEntryToHeaderTest_key");
		headers.put("MAP_NAME", "putMapEntryToHeaderTest_mapname");
		headers.put("MAP_VALUE", "putMapEntryToHeaderTest_value");
		headers.put("SCHEMA", "FPTest");
		producer.sendBodyAndHeaders("seda:test", "Test", headers);
		Exchange e = consumer.receive("seda:test");
		clientDataService.putMapEntryFromHeader(e);
		assertEquals("putMapEntryToHeaderTest_value",
				e.getOut().getHeader("MAP_VALUE", "Not found"));
		consumer.stop();
	}

	@Test
	public void getMapEntryToHeaderTest() throws Exception {
		ProducerTemplate producer = camelCtx.createProducerTemplate();
		ConsumerTemplate consumer = camelCtx.createConsumerTemplate();

		consumer.start();
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("MAP_KEY", "getMapEntryToHeaderTest_key");
		headers.put("MAP_NAME", "getMapEntryToHeaderTest_mapname");
		headers.put("MAP_VALUE", "getMapEntryToHeaderTest_value");
		headers.put("SCHEMA", "FPTest");
		producer.sendBodyAndHeaders("seda:test", "Test", headers);
		Exchange e = consumer.receive("seda:test");
		clientDataService.putMapEntryFromHeader(e);
		headers.remove("MAP_VALUE");
		producer.sendBodyAndHeaders("seda:test", "Test", headers);
		Exchange ex = consumer.receive("seda:test");
		clientDataService.getMapEntryToHeader(ex);
		assertEquals("getMapEntryToHeaderTest_value",
				e.getOut().getHeader("MAP_VALUE", "Not found"));
		consumer.stop();
	}

//	@Test
//	public void removeMapEntryTest() throws Exception {
//		ProducerTemplate producer = camelCtx.createProducerTemplate();
//		ConsumerTemplate consumer = camelCtx.createConsumerTemplate();
//
//		consumer.start();
//		Map<String, Object> headers = new HashMap<String, Object>();
//		headers.put("MAP_KEY", "removeMapEntry_key");
//		headers.put("MAP_NAME", "removeMapEntry_mapname");
//		headers.put("MAP_VALUE", "removeMapEntry_value");
//		headers.put("SCHEMA", "FPTest");
//		producer.sendBodyAndHeaders("seda:test", "Test", headers);
//		Exchange e = consumer.receive("seda:test");
//		clientDataService.putMapEntryFromHeader(e);
//		headers.remove("MAP_VALUE");
//		producer.sendBodyAndHeaders("seda:test", "Test", headers);
//		Exchange ex = consumer.receive("seda:test");
//		clientDataService.removeMapEntry(ex);
//		assertEquals("removeMapEntry_key",
//				ex.getOut().getHeader("MAP_KEY", "Not found"));
//		producer.sendBodyAndHeaders("seda:test", "Test", headers);
//		Exchange ex1 = consumer.receive("seda:test");
//		clientDataService.removeMapEntry(ex1);
//		assertEquals("removeMapEntry_key",
//				ex1.getOut().getHeader("MAP_KEY", "Not found"));
//		consumer.stop();
//	}

	@Test
	public void getMapKeysTest() throws Exception {
		ProducerTemplate producer = camelCtx.createProducerTemplate();
		ConsumerTemplate consumer = camelCtx.createConsumerTemplate();
		consumer.start();
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("MAP_KEY", "getMapKeys_key1");
		headers.put("MAP_NAME", "getMapKeysTest_value");
		headers.put("SCHEMA", "FPTest");
		producer.sendBodyAndHeaders("seda:test", "Test", headers);
		Exchange e = consumer.receive("seda:test");
		clientDataService.putMapEntry(e);
//		headers.remove("MAP_KEY");
//		headers.put("MAP_KEY", "getMapKeys_key2");
//		producer.sendBodyAndHeaders("seda:test", "Test", headers);
//		e = consumer.receive("seda:test");
//		clientDataService.putMapEntry(e);
//		headers.remove("MAP_KEY");
		producer.sendBodyAndHeaders("seda:test", "Test", headers);
		e = consumer.receive("seda:test");
		clientDataService.getMapKeys(e);
		System.out.println("sdfsfsfsfd*****"+e.getOut()
				.getHeader("MAP_KEYS"));
		assertEquals("[getMapKeys_key1]", e.getOut()
				.getHeader("MAP_KEYS", "Not found"));
		consumer.stop();
	}

	@Test
	public void putMapEntryExtendedTest() throws Exception {
		ProducerTemplate producer = camelCtx.createProducerTemplate();
		ConsumerTemplate consumer = camelCtx.createConsumerTemplate();

		consumer.start();
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("MAP_KEY", "putMapEntryExtendedTest_key");
		headers.put("MAP_NAME", "putMapEntryExtendedTest_mapname");
		headers.put("MAP_VALUE", "putMapEntryExtendedTest_value");
		headers.put("OBJECT_USER_METADATA",
				"[ \"filterType1\" : \"filterValue1\", \"filterType2\" : \"filterValue2\"]  ");
		headers.put("OBJECT_INDEXDATA",
				"[ \"500001\" : '50001indexFilterValue1',  \"filterType1\":\"50001\"]");
		headers.put(
				"OBJECT_LWLINKS",
				"[[\"bucket\":\"destBucket1\", \"key\":\"destKey1\", \"tag\":\"linkRelationDescriptor1\"], [\"bucket\":\"destbucket1\", \"key\":\"destKey2\","
						+ " \"tag\":\"linkRelationDescriptor2\"]]");
		headers.put("OBJECT_CONTENTTYPE", "text/plain");

		producer.sendBodyAndHeaders("seda:test", "Test", headers);
		Exchange e = consumer.receive("seda:test");
		clientDataService.putMapEntryExtended(e);
		assertEquals("Not Implemented", e.getOut().getBody());
		consumer.stop();
	}

	public void putMapEntryExtendedRegularTest() throws Exception {
		clientDataService.putMapEntryExtended("", null, null, null, null, null,
				null);
	}

	public void getMapReduceResultTest() throws Exception {
		ProducerTemplate producer = camelCtx.createProducerTemplate();
		ConsumerTemplate consumer = camelCtx.createConsumerTemplate();
		consumer.start();
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("MAP_KEY", "getMapReduceResult_key1");
		headers.put("MAP_NAME", "getMapReduceResult_value");
		headers.put("SCHEMA", "FPTest");
		producer.sendBodyAndHeaders("seda:test", "Test", headers);
		Exchange e = consumer.receive("seda:test");
		clientDataService.getMapReduceResult(e);
		
		assertEquals("Not Implemented", e.getOut().getBody());
		consumer.stop();
	}
	
	@Test
	public void oneLiners(){
		clientDataService.start();
		assertTrue((boolean)clientDataService.getStarted());
		
		clientDataService.stop();
		assertFalse((boolean)clientDataService.getStarted());
		
		clientDataService.putMapEntryExtended(null, null, null, null, null, null, null);

	}
	@After
	public void tearDown() {
		super.tearDown();
		// Tear down logic here
	}

}
