/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;
import java.util.ArrayList;
import java.util.Map;

import org.junit.*;

import static org.junit.Assert.*;
public class DocTest extends BaseTestCase {
	
	Doc doc;
	FilePersistenceService fps;
	
	@Before
	public void setUp() {
		super.setUp();
		doc=new Doc();
		fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		Doc.setAjscMetaDataService(fps);
	}
	
	@Test
	public void equalsTest(){
		doc.setNamespace("GMS");
		doc.setDocName("doc1");
		doc.setDocVersion("1.2.3");
		fps.putMapEntry("ajsc.Doc", doc.generateId(), (String)AjscMetaDataUtil.asJson(doc));
		
		Doc doc2=new Doc();
		doc2.setNamespace("GMS");
		doc2.setDocName("doc1");
		doc2.setDocVersion("1.2.3");
		
		Doc doc3=new Doc();
		doc3.setNamespace("GNB");
		doc3.setDocName("doc2");
		doc3.setDocVersion("1.2.3");
		
		boolean a= doc.equals(doc2);
		assertTrue(a);
		
		boolean b=doc.equals(doc3);
		assertFalse(b);
	}
	
	@Test
	public void validateExtendedTest(){
		doc.setNamespace("VNB");
		doc.setDocName("yun");
		doc.setDocVersion("1.2.3");
		fps.putMapEntry("ajsc.Doc", doc.generateId(), (String)AjscMetaDataUtil.asJson(doc));
		
		Doc doc2=new Doc();
		doc2.setNamespace("VNB");
		doc2.setDocName("yun");
		doc2.setDocVersion("1.2.3");
		boolean a=doc.validateExtended();
		assertFalse(a);
	}
	
	@Test
	public void hashCodeTest(){
		doc.setNamespace("hashcode");
		assertEquals(1639219440, doc.hashCode());
	}
	
	@Ignore("Nope City. Not gonna work")
	@Test
	public void clearContentCacheTest(){
		//doc.clearContentCache(doc)
	}
	
	@Test
	public void getMessageMapTest(){		
		Map<String,String>a=(Map<String, String>) doc.getMessageMap("method", "msgnum");
		assertTrue(a.get("METHOD").equals("method"));
	}
	
	@Test
	public void getAllByNamespaceLikeAndDocVersionLike(){
		doc.setNamespace("BVN");
		doc.setDocName("ert");
		doc.setDocVersion("1.2.3");
		fps.putMapEntry("ajsc.Doc", doc.generateId(), (String)AjscMetaDataUtil.asJson(doc));
		
		doc.setDocName("SUP");
		doc.save();
		String s=doc.getAllByNamespaceLikeAndDocVersionLike("BVN", "1.2.3");
		assertTrue(s.contains("SUP") && s.contains("BVN"));
	}
	
	@Test
	public void findAllByNamespaceTest(){
		doc.setNamespace("CVB");
		doc.setDocName("dks");
		doc.setDocVersion("1.1.3");
		fps.putMapEntry("ajsc.Doc", doc.generateId(), (String)AjscMetaDataUtil.asJson(doc));
		
		doc.setDocName("not");
		doc.setDocVersion("3");
		doc.save();
		ArrayList<Doc>arr=(ArrayList<Doc>)doc.findAllByNamespace("CVB");
		assertEquals(2,arr.size());
		
	}
	
	@Test
	public void findAllByNamespaceAndDocNameAndDocVersionTest(){
		doc.setNamespace("ECLP");
		doc.setDocName("bjur");
		doc.setDocVersion("1.1.3");
		fps.putMapEntry("ajsc.Doc", doc.generateId(), (String)AjscMetaDataUtil.asJson(doc));
		
		ArrayList<Doc>arr=(ArrayList<Doc>)doc.findAllByNamespaceAndDocNameAndDocVersion("ECLP","bjur","1.1.3");
		assertEquals(1,arr.size());
	}
	
	@Test
	public void findAllByNamespaceAndDocVersion(){
		doc.setNamespace("VBA");
		doc.setDocName("vnli");
		doc.setDocVersion("1.1.3");
		fps.putMapEntry("ajsc.Doc", doc.generateId(), (String)AjscMetaDataUtil.asJson(doc));
		
		doc.setNamespace("VBA");
		doc.setDocName("dd");
		doc.setDocVersion("1.1.3");
		doc.save();
		ArrayList<Doc>arr=(ArrayList<Doc>)doc.findAllByNamespaceAndDocVersion("VBA","1.1.3");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLikeTest(){
		doc.setNamespace("HYNU");
		doc.setDocName("vnli");
		doc.setDocVersion("1.1.3");
		fps.putMapEntry("ajsc.Doc", doc.generateId(), (String)AjscMetaDataUtil.asJson(doc));
		
		doc.setNamespace("HYNU");
		doc.setDocName("dd");
		doc.setDocVersion("1.1.3");
		doc.save();
		ArrayList<Doc>arr=(ArrayList<Doc>)doc.findAllByNamespaceLike("HYNU");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLikeAndDocNameLike(){
		doc.setNamespace("GGFF");
		doc.setDocName("nvbd");
		doc.setDocVersion("1.1.3");
		fps.putMapEntry("ajsc.Doc", doc.generateId(), (String)AjscMetaDataUtil.asJson(doc));
		
		doc.setNamespace("GGFF");
		doc.setDocName("nvbd");
		doc.setDocVersion("0.1.3");
		doc.save();
		ArrayList<Doc>arr=(ArrayList<Doc>)doc.findAllByNamespaceLikeAndDocNameLike("GGFF","nvbd");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLikeAndDocNameLikeAndDocVersionLikeTest(){
		doc.setNamespace("NNVB");
		doc.setDocName("vcbz");
		doc.setDocVersion("1.1.3");
		fps.putMapEntry("ajsc.Doc", doc.generateId(), (String)AjscMetaDataUtil.asJson(doc));
		
		ArrayList<Doc>arr=(ArrayList<Doc>)doc.findAllByNamespaceLikeAndDocNameLikeAndDocVersionLike("NNVB","vcbz","1.1.3");
		assertEquals(1,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLikeAndDocVersionLikeTest(){
		doc.setNamespace("KDNV");
		doc.setDocName("nvbd");
		doc.setDocVersion("3.1.3");
		fps.putMapEntry("ajsc.Doc", doc.generateId(), (String)AjscMetaDataUtil.asJson(doc));
		
		doc.setNamespace("KDNV");
		doc.setDocName("bbv");
		doc.setDocVersion("3.1.3");
		doc.save();
		ArrayList<Doc>arr=(ArrayList<Doc>)doc.findAllByNamespaceLikeAndDocVersionLike("KDNV","3.1.3");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void deleteTest(){
		doc.setNamespace("NIII");
		doc.setDocName("vqpp");
		doc.setDocVersion("9.1.3");
		doc.save();
		String s=fps.getMapEntry("ajsc.Doc", doc.generateId());
		assertNotNull(s);
		doc.delete();
		String ss=fps.getMapEntry("ajsc.Doc", doc.generateId());
		assertNull(ss);
	}
}
