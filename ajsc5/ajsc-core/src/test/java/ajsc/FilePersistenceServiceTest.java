/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import static org.junit.Assert.*;

import java.util.*;

import ajsc.FilePersistenceService;

import org.junit.*;

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for
 * usage instructions
 */

public class FilePersistenceServiceTest extends BaseTestCase {
	private static FilePersistenceService filePersistenceService;

	@BeforeClass
	public static void setUpClass() throws Exception {
				
	}

	@Before
	public void setUp() {
		super.setUp();
		filePersistenceService = new FilePersistenceService();
		filePersistenceService.init();

	}

	@After
	public void tearDown() {
		// Tear down logic here
		filePersistenceService.removeMapEntry("testMap", "key1");  
		filePersistenceService.removeMapEntry("testMapKeys", "key1");  
		filePersistenceService.removeMapEntry("testRemoveMap", "key1");  
		filePersistenceService.removeMapEntry("testByteMap", "keybyte");
		filePersistenceService.removeMapEntry("testMapEntries", "key1"); 
		filePersistenceService.removeMapEntry("testBAMapEntries", "key1"); 
	}
	

	@Test
	public void testPutGetMapEntry() {
		
		filePersistenceService.putMapEntry("testMap", "key1", "value"); 
		String val = filePersistenceService.getMapEntry("testMap", "key1");
		assertEquals("value", val );
	}
	
	@Test
	public void testGetMapKeys() {
 
		filePersistenceService.putMapEntry("testMapKeys", "key1", "value"); 
		List fetchList= filePersistenceService.getMapKeys("testMapKeys");
 
		assertEquals(1,fetchList.size());
		assertEquals("key1",fetchList.get(0));
	} 

 
	@Test
	public void testPutGetByteArrayMapEntry() {
		
		byte[] byteValue = new byte[] { 1, 2, 3 };
		filePersistenceService.putByteArrayMapEntry("testByteMap", "keybyte", byteValue);
		byte[] fetchBytes = filePersistenceService.getByteArrayMapEntry("testByteMap", "keybyte");
		assertArrayEquals( fetchBytes, byteValue);
	}
	
	@Test
	public void testRemovetMapEntry() {
		
		filePersistenceService.putMapEntry("testRemoveMap", "key1", "value"); 
		filePersistenceService.removeMapEntry("testRemoveMap", "key1");  
		assertNull(filePersistenceService.getMapEntry("testRemoveMap", "key1"));
	}
	
	@Test 
	public void testGetAllEntries() {
		
		filePersistenceService.putMapEntry("testMapEntries", "key1", "value1"); 
		List fetchEntries= filePersistenceService.getAllEntries("testMapEntries");
		assertEquals(1,fetchEntries.size());
		assertEquals("value1",fetchEntries.get(0)); 
	} 
	
	@Test
	public void testGetAllByteArrayEntries() {
		byte[] byteValue1 = new byte[] { 1, 2, 3 };
 
		filePersistenceService.putByteArrayMapEntry("testBAMapEntries", "key1", byteValue1); 
		Map hm= filePersistenceService.getAllByteArrayEntriesMap("testBAMapEntries");
 
		assertEquals(1,hm.values().toArray().length);
		byte[] fetchBytes =  (byte[]) hm.values().toArray()[0];
		assertArrayEquals( byteValue1, fetchBytes );		
	}

}
