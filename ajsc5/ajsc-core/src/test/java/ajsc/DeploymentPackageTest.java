/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class DeploymentPackageTest extends BaseTestCase{

	@SuppressWarnings("unchecked")
	@Before
	public void setUp(){
		super.setUp();
		
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void generateIdTest(){
		final String deployFile="Deploy_def.xml";
		DeploymentPackage dp=new DeploymentPackage();// "ajsc","1.1.0","Closing Time","ajsc",new Date(),new Date()
		dp.setNamespace("test");
		dp.setNamespaceVersion("1.0.0");
		String a =dp.generateId();
		dp.setId(a);
		
		assertEquals("test:1.0.0", dp.getId());
		
		String b=dp.generateId("GSM","5.0.2");
		assertEquals("GSM:5.0.2", b);
	}
	
	@Test
	public void createDeploymentTest(){
		DeploymentPackage dp=new DeploymentPackage();
		
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		DeploymentPackage.createDeploymentpackage("ajsc", "1.0.1", "Testing");
		dp.createDeploymentpackage("nope", "21.3.44", null);
		
		
		ArrayList<DeploymentPackage> arr=(ArrayList<DeploymentPackage>) dp.list();
		boolean hasNope=false;
		boolean hasAJSC=false;
		for(DeploymentPackage l: arr){
			if(l.generateId().equals("nope:21.3.44")){
				hasNope=true;
			}else if(l.generateId().equals("ajsc:1.0.1")){
				hasAJSC=true;
			}
		}
		//System.out.println(arr.contains("nope:21.3.44"));
		assertTrue(hasNope);
		assertTrue(hasAJSC);
	}
	
	@Test
	public void findByIdTest(){
		DeploymentPackage dp=new DeploymentPackage();
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		dp.createDeploymentpackage("GSM", "0.0.1", null);
		dp.setId(dp.generateId());
		DeploymentPackage dp2=dp.findById("GSM:0.0.1");
		assertEquals("GSM:0.0.1",dp2.generateId());		
	}
	
	@Test
	public void findByNamespaceAndVersionTest(){
		DeploymentPackage dp=new DeploymentPackage();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespaceVersion("1.2.3");
		dp.setNamespace("ajsc");
		//dp.setMAPNAME("GSM.DeploymentPackage");
		fps.putMapEntry("ajsc.DeploymentPackage", dp.getNamespace()+":"+dp.getNamespaceVersion(), AjscMetaDataUtil.asJson(dp).toString() );
		RouteMgmtService.setPersistenceService(fps);
		DeploymentPackage dp2=dp.findById("ajsc:1.2.3");
		
		assertEquals("ajsc:1.2.3", dp2.generateId());
	}
	
	@Test
	public void getInstanceTest(){
		DeploymentPackage dp=new DeploymentPackage();
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		dp.createDeploymentpackage("GSM", "1.2.1", "Fun Times");
		DeploymentPackage dp2=DeploymentPackage.getInstance("GSM", "1.2.1", "Fun Times");
		DeploymentPackage dp3=DeploymentPackage.getInstance("TSM", "1.2.1", "Fun Times");
		assertEquals("GSM:1.2.1", dp2.generateId());
		assertEquals("TSM:1.2.1", dp3.generateId());
	}
	
	@Test
	public void saveTest(){
		DeploymentPackage dp=new DeploymentPackage();
		FilePersistenceService fps=new FilePersistenceService();
		//RouteMgmtService.setPersistenceService(fps);
		dp.setNamespace("FSM");
		dp.setNamespaceVersion("1.1.1");
		dp.setDescription("Falls Through");
		dp.setAjscMetaDataService(fps);
		dp.save();
		DeploymentPackage dp2=DeploymentPackage.getInstance("FSM", "1.1.1", "Falls Through");
		assertEquals(null, dp2.getUserId());
		assertEquals("FSM:1.1.1", dp2.generateId());
	}
	
	@Test
	public void getMessageMapTest(){
		DeploymentPackage dp=new DeploymentPackage();
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		dp.setNamespace("FSM");
		Map<Object,Object>map=(Map<Object, Object>) dp.getMessageMap("method", "msgnum");
		
		assertEquals("ajsc",map.get("MODULE"));
	}
	
	@Test
	public void findAllByNamespaceLikeAndVersionLikeTest(){
		DeploymentPackage dp=new DeploymentPackage();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("ajsc");
		dp.setNamespaceVersion("1.5.5");
		fps.putMapEntry("ajsc.DeploymentPackage", dp.getNamespace()+":"+dp.getNamespaceVersion(), AjscMetaDataUtil.asJson(dp).toString() );
		RouteMgmtService.setPersistenceService(fps);
		DeploymentPackage.createDeploymentpackage("FSM", "1.0.1", null);
		DeploymentPackage.createDeploymentpackage("FSM", "1.0.2", null);
		DeploymentPackage.createDeploymentpackage("FSM", "1.0.3", null);
		DeploymentPackage.createDeploymentpackage("GSM", "4.0.1", null);
		ArrayList<DeploymentPackage>arr=(ArrayList<DeploymentPackage>) dp.findAllByNamespaceLikeAndVersionLike("FSM", "1.0.1");
		assertEquals("FSM:1.0.1",arr.get(0).generateId());
	}
	
	@Test
	public void getAllByNamespaceLikeAndVersionLikeTest(){
		DeploymentPackage dp=new DeploymentPackage();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("ajsc");
		dp.setNamespaceVersion("1.5.5");
		fps.putMapEntry("ajsc.DeploymentPackage", dp.getNamespace()+":"+dp.getNamespaceVersion(), AjscMetaDataUtil.asJson(dp).toString() );
		RouteMgmtService.setPersistenceService(fps);
		DeploymentPackage.createDeploymentpackage("FSM", "1.0.1", null);
		DeploymentPackage.createDeploymentpackage("FSM", "1.0.2", null);
		DeploymentPackage.createDeploymentpackage("FSM", "1.0.3", null);
		DeploymentPackage.createDeploymentpackage("GSM", "4.0.1", null);
		String a=dp.getAllByNamespaceLikeAndVersionLike("FSM", "1.0.1");
		assertTrue(a.contains("\"namespace\":\"FSM\",\"namespaceVersion\":\"1.0.1\",\"description\":\"Deployment Package for Namespace=FSM and namespace version=1.0.1\""));
	}
	
	@Test
	public void findAllByNamespaceLike(){
		DeploymentPackage dp=new DeploymentPackage();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("ajsc");
		dp.setNamespaceVersion("1.5.5");
		fps.putMapEntry("ajsc.DeploymentPackage", dp.getNamespace()+":"+dp.getNamespaceVersion(), AjscMetaDataUtil.asJson(dp).toString() );
		RouteMgmtService.setPersistenceService(fps);
		DeploymentPackage.createDeploymentpackage("ISM", "1.0.1", null);
		DeploymentPackage.createDeploymentpackage("ISM", "1.0.2", null);
		DeploymentPackage.createDeploymentpackage("ISM", "1.0.3", null);
		DeploymentPackage.createDeploymentpackage("ZSM", "4.0.1", null);
		ArrayList<DeploymentPackage>arr=(ArrayList<DeploymentPackage>)dp.findAllByNamespaceLike("ISM");
		assertEquals(3, arr.size());
	}
	
	@Test
	public void getNamespaceVersionsTest(){
		DeploymentPackage dp=new DeploymentPackage();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("ajsc");
		dp.setNamespaceVersion("1.5.5");
		fps.putMapEntry("ajsc.DeploymentPackage", dp.getNamespace()+":"+dp.getNamespaceVersion(), AjscMetaDataUtil.asJson(dp).toString() );
		RouteMgmtService.setPersistenceService(fps);
		DeploymentPackage.createDeploymentpackage("ASM", "1.0.1", null);
		DeploymentPackage.createDeploymentpackage("ASM", "1.0.2", null);
		DeploymentPackage.createDeploymentpackage("ASM", "1.0.3", null);
		ArrayList<String>arr=(ArrayList<String>)dp.getNamespaceVersions("ASM");
		assertTrue(arr.contains("1.0.1"));
		assertTrue(arr.contains("1.0.2"));
		assertTrue(arr.contains("1.0.3"));

	}
	@Ignore("The method does not work. Needs to be refactored")
	@Test
	public void getNamespacesTest(){
		DeploymentPackage dp=new DeploymentPackage();
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		DeploymentPackage.createDeploymentpackage("ASM", "1.0.1", null);
		DeploymentPackage.createDeploymentpackage("ASM", "1.0.2", null);
		DeploymentPackage.createDeploymentpackage("ASM", "1.0.3", null);
		ArrayList<String>arr=(ArrayList<String>)dp.getNamespaces("ASM");
		System.out.println(arr.size());
	}
	
	@Test
	public void listTest(){
		DeploymentPackage dp=new DeploymentPackage();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("ajsc");
		dp.setNamespaceVersion("1.5.5");
		fps.putMapEntry("ajsc.DeploymentPackage", dp.getNamespace()+":"+dp.getNamespaceVersion(), AjscMetaDataUtil.asJson(dp).toString() );
		RouteMgmtService.setPersistenceService(fps);
		ArrayList<DeploymentPackage>arr=(ArrayList<DeploymentPackage>)DeploymentPackage.list();
	}
	
	@Test
	public void deleteTest(){
		DeploymentPackage dp=new DeploymentPackage();
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		dp.setNamespace("ajsc");
		dp.setNamespaceVersion("56.2.1");
		dp.setAjscMetaDataService(fps);
		dp.save();
		assertEquals("ajsc:56.2.1",dp.generateId());		
		dp.delete();
		//assertEquals(null,dp.generateId());
		ArrayList<DeploymentPackage>arr=(ArrayList<DeploymentPackage>)DeploymentPackage.list();
		boolean hasSelf=false;
		for(DeploymentPackage d: arr)
			if(d.generateId().equals("ajsc:56.2.1"))
				hasSelf=true;
		assertFalse(hasSelf);
	}
		
	@Test
	public void equalsTest(){
		DeploymentPackage dp=new DeploymentPackage();
		DeploymentPackage dp2=new DeploymentPackage();
		DeploymentPackage dp3=new DeploymentPackage();
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		
		dp.setNamespace("dsm");
		dp.setNamespaceVersion("1.2.3.");
		
		dp2.setNamespace("dsm");
		dp2.setNamespaceVersion("1.2.3.");
		
		dp3.setNamespace("RFV");
		dp3.setNamespaceVersion("3.2.3.");
		
		boolean a=dp.equals(dp2);
		assertTrue(a);
		
		boolean b=dp.equals(dp3);
		assertFalse(b);
		
		
	}
	@Test
	public void hashTest(){
		DeploymentPackage dp=new DeploymentPackage();
		int a=dp.hashCode();
		//System.out.println(a);
		assertEquals(23273, a);
	}
}
