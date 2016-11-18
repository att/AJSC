/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import ajsc.AjscMetaDataUtil;
import ajsc.Doc;
import ajsc.FilePersistenceService;
import ajsc.RouteMgmtService;
import ajsc.ServiceProperties;
import ajsc.ServiceProperty;

import java.util.ArrayList;
import java.util.Map;

import org.junit.*;

import static org.junit.Assert.*;

public class ServicePropertiesTest extends BaseTestCase {
	ServiceProperties sp;
	FilePersistenceService fps;
	
	@Before
	public void setUp() {
		super.setUp();
		sp =new ServiceProperties();
		fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		ServiceProperties.setAjscMetaDataService(fps);
	}
	
	@Test
	public void equalsTest(){
		sp.setNamespace("name");
		sp.setServiceName("ser");
		sp.setServiceVersion("0");
		sp.save();
		
		ServiceProperties sp2=new ServiceProperties();
		sp2.setNamespace("name");
		sp2.setServiceName("ser");
		sp2.setServiceVersion("0");
		sp2.save();
		
		ServiceProperties sp3=new ServiceProperties();
		sp3.setNamespace("name1");
		sp3.setServiceName("ser3");
		sp3.setServiceVersion("0.3");
		sp3.save();
		
		boolean a=sp.equals(sp2);
		assertTrue(a);
		
		boolean b=sp.equals(sp3);
		assertFalse(b);
		
	}
	
	@Test
	public void hashCodeTest(){
		sp.setNamespace("hashcode");
		assertEquals(1639219440, sp.hashCode());
	}
	
	@Test
	public void getMessageMapTest(){		
		Map<String,String>a=(Map<String, String>) sp.getMessageMap("method", "msgnum");
		assertTrue(a.get("METHOD").equals("method"));
	}
	
	@Test
	public void getAllByNamespaceLikeAndServiceVersionLike(){
		sp.setNamespace("BVND");
		sp.setServiceName("VIR");
		sp.setServiceVersion("1.2.4");
		fps.putMapEntry("ajsc.ServiceProperties", sp.generateId(), (String)AjscMetaDataUtil.asJson(sp));
		
		sp.setServiceName("namesNot");
		sp.save();
		String s=sp.getAllByNamespaceLikeAndServiceVersionLike("BVND", "1.2.4");
		assertTrue(s.contains("namesNot") && s.contains("VIR"));
	}
	
	@Test
	public void findAllByNamespace(){
		sp.setNamespace("QWSA");
		sp.setServiceName("VIR");
		sp.setServiceVersion("1.2.4");
		fps.putMapEntry("ajsc.ServiceProperties", sp.generateId(), (String)AjscMetaDataUtil.asJson(sp));
		sp.setServiceName("BIR");
		sp.save();
		ArrayList<ServiceProperties> arr=(ArrayList<ServiceProperties>)sp.findAllByNamespace("QWSA");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLike(){
		sp.setNamespace("GTYH");
		sp.setServiceName("sss");
		sp.setServiceVersion("1.2.4");
		fps.putMapEntry("ajsc.ServiceProperties", sp.generateId(), (String)AjscMetaDataUtil.asJson(sp));
		sp.setServiceName("ddd");
		sp.save();
		ArrayList<ServiceProperties> arr=(ArrayList<ServiceProperties>)sp.findAllByNamespace("GTYH");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLikeAndServiceNameLike(){
		sp.setNamespace("NVBH");
		sp.setServiceName("idk");
		sp.setServiceVersion("1.0.3");
		fps.putMapEntry("ajsc.ServiceProperties", sp.generateId(), (String)AjscMetaDataUtil.asJson(sp));
		
		sp.setServiceVersion("2.3.4");
		sp.save();
		
		ArrayList<ServiceProperties> arr=(ArrayList<ServiceProperties>)sp.findAllByNamespaceLikeAndServiceNameLike("NVBH","idk");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLikeAndServiceNameLikeAndServiceVersionLikeTest(){
		sp.setNamespace("POLA");
		sp.setServiceName("idk");
		sp.setServiceVersion("1.0.3");
		fps.putMapEntry("ajsc.ServiceProperties", sp.generateId(), (String)AjscMetaDataUtil.asJson(sp));
		
		ArrayList<ServiceProperties> arr=(ArrayList<ServiceProperties>)sp.findAllByNamespaceLikeAndServiceNameLikeAndServiceVersionLike("POLA","idk","1.0.3");
		assertEquals(1,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLikeAndServiceVersionLikeTest(){
		sp.setNamespace("TAPL");
		sp.setServiceName("vz");
		sp.setServiceVersion("1.0.3");
		fps.putMapEntry("ajsc.ServiceProperties", sp.generateId(), (String)AjscMetaDataUtil.asJson(sp));
		sp.setServiceName("dd");
		sp.save();
		ArrayList<ServiceProperties> arr=(ArrayList<ServiceProperties>)sp.findAllByNamespaceLikeAndServiceVersionLike("TAPL","1.0.3");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void createServicePropertyTest(){
		sp.setNamespace("BVOO");
		sp.setServiceName("vz");
		sp.setServiceVersion("1.0.3");
		
		fps.putMapEntry("ajsc.ServiceProperties", sp.generateId(), (String)AjscMetaDataUtil.asJson(sp));
		
		sp.createServiceProperty("BVOO", "vz", "1.0.3", "fdfdf", "d");
		//System.out.println(sp.getProps().get(0).getName());
	}
	
	@Test
	public void deleteTest(){
		sp.setNamespace("BVOO");
		sp.setServiceName("vz");
		sp.setServiceVersion("1.0.3");
		
		fps.putMapEntry("ajsc.ServiceProperties", sp.generateId(), (String)AjscMetaDataUtil.asJson(sp));
		sp.delete();
		String s=fps.getMapEntry("ajsc.ServiceProperties", sp.generateId());
		assertNull(s);
	}
}
