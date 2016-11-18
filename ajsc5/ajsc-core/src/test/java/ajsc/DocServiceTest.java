/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for
 * usage instructions
 */

public class DocServiceTest extends BaseTestCase {
	private static PropertiesService propService;
	private static DocService docService;
	private static FilePersistenceService filePersistenceService;

	@BeforeClass
	public static void setUpClass() throws Exception {
		
	}

	// Read more:
	// http://javarevisited.blogspot.com/2012/06/junit4-annotations-test-examples-and.html#ixzz36ntt48gI
	@Before
	public void setUp() {
		super.setUp();
		filePersistenceService = new FilePersistenceService();
		filePersistenceService.init();
		RouteMgmtService.setPersistenceService(filePersistenceService);
		propService = new PropertiesService();
		propService.init();

		docService = new DocService();
		docService.init();
		System.out
				.println("@BeforeClass method will be executed before JUnit test for"
						+ "a Class starts");
	}

	@After
	public void tearDown() {
		
		//docService.deleteDoc("testNamespace/1.0/testDocName.xml");
		//docService.deleteDoc("testNamespace1/1.0/testDocName1.xml ");
		
	}

	@Test
	public void testInsertOrUpdateDoc() {
		Doc aDoc = new Doc();
		aDoc.setNamespace("testNamespace");
		aDoc.setDocName("testDocName.xml");
		aDoc.setDocVersion("1.0");
		aDoc.setDocContent("<test></test>");
		docService.insertOrUpdateDoc(aDoc);

		Doc aDoc1 = new Doc();
		aDoc1.setNamespace("testNamespace1");
		aDoc1.setDocName("testDocName1.xml");
		aDoc1.setDocVersion("1.0");
		aDoc1.setDocContent("<test1></test1>");
		docService.insertOrUpdateDoc(aDoc1);

		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> docsList = (List)docService.listDocs();
		int matchingCount = 0;
		for(int i=0;i<docsList.size();i++){
			if(((String)docsList.get(i)).contains("testDocName1.xml") || ((String)docsList.get(i)).contains("testDocName.xml")){
				matchingCount++;
			}
		}
		assertEquals(2, matchingCount);
	}

}
