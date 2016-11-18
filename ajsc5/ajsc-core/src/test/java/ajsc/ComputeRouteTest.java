/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import java.util.ArrayList;
import java.util.Map;

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

import static org.junit.Assert.*;

public class ComputeRouteTest extends BaseTestCase{
	ComputeRoute cr;
	@SuppressWarnings("unchecked")
	@Before
	public void setUp(){
		super.setUp();
		cr=new ComputeRoute();
	}
	
	//TODO - Running when the individual test is run but failing on running all tests 
/*	@Test
	public void list(){
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		ComputeRoute.setRiakService(fps);
		cr.setNamespace("IOD");
		cr.setRouteName("BVNM");
		cr.setRouteVersion("88");
		cr.save();
		
		ArrayList<ComputeRoute> arr=(ArrayList<ComputeRoute>)cr.list();
		
		boolean hasIOD=false;
		for(ComputeRoute r:arr){
			if(r.getNamespace().equals("IOD")){
				hasIOD=true;
				break;
			}
		}
		assertTrue(hasIOD);
		
		
	}*/
	
	@Ignore
	@Test
	public void findByIdTest(){
		cr.setNamespace("SBC");
		cr.setRouteName("DM");
		cr.setRouteVersion("5.2.3");
		
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		ComputeRoute.setRiakService(fps);
		
		System.out.println(AjscMetaDataUtil.asJson(cr));
		fps.putMapEntry("ajsc.ComputeRoute", cr.generateId(), (String)AjscMetaDataUtil.asJson(cr));

		ComputeRoute cr2=cr.findById("SBC:DM:5.2.3");
		System.out.println(cr2.getId());
	}
	
	@Test
	public void equalsTest(){
		ComputeRoute cr2=new ComputeRoute();
		ComputeRoute cr3=new ComputeRoute();

		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		ComputeRoute.setRiakService(fps);
		
		cr.setNamespace("LSD");
		cr.setRouteName("JM");
		cr.setRouteVersion("8");
		cr.save();
		
		cr2.setNamespace("LSD");
		cr2.setRouteName("JM");
		cr2.setRouteVersion("8");
		cr2.save();
		
		cr3.setNamespace("NBM");
		cr3.setRouteName("UI");
		cr3.setRouteVersion("3");
		cr3.save();
		
		boolean a=cr.equals(cr2);
		assertTrue(a);
		
		boolean b=cr.equals(cr3);
		assertFalse(b);
		
		
	}
	
	@Test
	public void hashCodeTest(){
		cr.setNamespace("hashcode");
		assertEquals(1639219440, cr.hashCode());
	}
	
	@Test
	public void  validateExtendedTest(){
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		ComputeRoute.setRiakService(fps);
		
		cr.setNamespace("LSD");
		cr.setRouteName("JM");
		cr.setRouteVersion("8");
		cr.save();
		
		ComputeRoute cr2=new ComputeRoute();
		cr2.setNamespace("LSD");
		cr2.setRouteName("JM");
		cr2.setRouteVersion("8");
		boolean a=cr2.validateExtended();
		assertFalse(a);
		
		ComputeRoute cr3=new ComputeRoute();
		cr3.setNamespace("NMD");
		cr3.setRouteName("NV");
		cr3.setRouteVersion("45");
		boolean b=cr3.validateExtended();
		assertTrue(b);
	}
	
	@Test
	public void getMessageMapTest(){
		ComputeRoute con1 =new ComputeRoute();
		Map<String,String>a=(Map<String, String>) con1.getMessageMap("method", "msgnum");
		assertTrue(a.get("METHOD").equals("method"));
	}
	
	@Test
	public void getAllByNamespaceLikeAndRouteVersionLikeTest(){
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		ComputeRoute.setRiakService(fps);
		
		cr.setNamespace("YUI");
		cr.setRouteName("DCV");
		cr.setRouteVersion("21");
		cr.save();
		
		ComputeRoute cr2 =new ComputeRoute();
		cr2.setNamespace("YUI");
		cr2.setRouteName("NHO");
		cr2.setRouteVersion("21");
		fps.putMapEntry("ajsc.ComputeRoute", cr2.generateId(), (String)AjscMetaDataUtil.asJson(cr2));
		String s=cr.getAllByNamespaceLikeAndRouteVersionLike("YUI", "21");
		assertTrue(s.contains("DCV") && s.contains("NHO"));
	}
	
	@Test
	public void findAllByContextIdLike(){
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		ComputeRoute.setRiakService(fps);
		Context.setAjscMetaDataService(fps);
		
		Context con =new Context();
		con.setContextName("XXC");
		con.setContextVersion("1.2.3");
		con.save();
		
		cr.setContextId(con.generateId());
		cr.setNamespace("EWR");
		cr.setRouteName("IZZ");
		cr.setRouteVersion("78");
		cr.save();
		
		ComputeRoute cr2=new ComputeRoute();
		cr2.setNamespace("RRT");
		cr2.setRouteName("BBX");
		cr2.setRouteVersion("42");
		cr2.setContextId(con.generateId());
		fps.putMapEntry("ajsc.ComputeRoute", cr2.generateId(), (String)AjscMetaDataUtil.asJson(cr2));
		
		ArrayList<ComputeRoute>arr=(ArrayList<ComputeRoute>)cr.findAllByContextIdLike(con.generateId());
		assertEquals(2,arr.size());
		
	}
	
	@Test
	public void findAllByNamespace(){
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		ComputeRoute.setRiakService(fps);
		
		cr.setNamespace("FNB");
		cr.setRouteName("QWA");
		cr.setRouteVersion("56");
		cr.save();
		
		cr.setRouteName("DNVV");
		cr.setId(cr.generateId());
		cr.save();
		ArrayList<ComputeRoute> arr=(ArrayList<ComputeRoute>)cr.findAllByNamespace("FNB");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLike(){
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		ComputeRoute.setRiakService(fps);
		
		cr.setNamespace("DISN");
		cr.setRouteName("NDK");
		cr.setRouteVersion("56");
		cr.save();
		
		cr.setRouteName("CVKS");
		cr.setId(cr.generateId());
		fps.putMapEntry("ajsc.ComputeRoute", cr.generateId(), (String)AjscMetaDataUtil.asJson(cr));

		ArrayList<ComputeRoute> arr=(ArrayList<ComputeRoute>)cr.findAllByNamespaceLike("DISN");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLikeAndRouteNameLikeTest(){
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		ComputeRoute.setRiakService(fps);
		cr.setNamespace("IJLK");
		cr.setRouteName("NKLD");
		cr.setRouteVersion("39");
		cr.save();
		
		cr.setRouteVersion("44");
		cr.setId(cr.generateId());
		fps.putMapEntry("ajsc.ComputeRoute", cr.generateId(), (String)AjscMetaDataUtil.asJson(cr));

		ArrayList<ComputeRoute> arr=(ArrayList<ComputeRoute>)cr.findAllByNamespaceLikeAndRouteNameLike("IJLK","NKLD");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLikeAndRouteNameLikeAndRouteVersionLikeTest(){
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		ComputeRoute.setRiakService(fps);
		cr.setNamespace("NYID");
		cr.setRouteName("MURN");
		cr.setRouteVersion("79");
		cr.save();
		
		cr.setNamespace("UOWN");
		cr.setRouteVersion("777");
		cr.setId(cr.generateId());
		fps.putMapEntry("ajsc.ComputeRoute", cr.generateId(), (String)AjscMetaDataUtil.asJson(cr));

		ArrayList<ComputeRoute> arr=(ArrayList<ComputeRoute>)cr.findAllByNamespaceLikeAndRouteNameLikeAndRouteVersionLike("NYID","MURN","79");
		assertEquals(1,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLikeAndRouteVersionLike(){
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		ComputeRoute.setRiakService(fps);
		cr.setNamespace("VJHO");
		cr.setRouteName("BVNM");
		cr.setRouteVersion("88");
		cr.save();
		
		cr.setNamespace("VJHO");
		cr.setRouteVersion("88");
		cr.setRouteName("some");
		cr.setId(cr.generateId());
		fps.putMapEntry("ajsc.ComputeRoute", cr.generateId(), (String)AjscMetaDataUtil.asJson(cr));

		ArrayList<ComputeRoute> arr=(ArrayList<ComputeRoute>)cr.findAllByNamespaceLikeAndRouteVersionLike("VJHO","88");
		assertEquals(2,arr.size());
	}
	

	
	@Test
	public void deleteTest(){
		cr.setNamespace("PJH");
		cr.setRouteName("YVG");
		cr.setRouteVersion("463");
		
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		Context.setAjscMetaDataService(fps);
		
		cr.save();
		String s=fps.getMapEntry("ajsc.ComputeRoute", cr.generateId());
		assertTrue(s.contains("\"id\":\""+cr.generateId()+"\""));
		cr.delete();
		assertNull(fps.getMapEntry("ajsc.Context", "BVM:1.2.3"));
	}
	
}
