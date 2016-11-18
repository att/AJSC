/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.openejb.config.Deployment;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;

@SuppressWarnings("static-access")
public class UserDefinedBeansDefTest extends BaseTestCase {
	@SuppressWarnings("unchecked")
	@Before
	public void setUp(){
		super.setUp();
		
	}
	@Test
	public void findByIdTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		FilePersistenceService fps=new FilePersistenceService();
		
		dp.setNamespace("dsm");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("1.2.0");
		fps.putMapEntry("ajsc.UserDefinedBeansDef", dp.generateId(), AjscMetaDataUtil.asJson(dp).toString());
		RouteMgmtService.setPersistenceService(fps);
		dp.setId(dp.generateId());
		UserDefinedBeansDef dp2=dp.findById(dp.generateId());
		assertEquals("dsm:test:1.2.0",dp2.generateId());	
	}
	@Test
	public void saveTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		FilePersistenceService fps=new FilePersistenceService();
		
		dp.setNamespace("dsm");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("1.2.0");
		fps.putMapEntry("ajsc.UserDefinedBeansDef", dp.generateId(), AjscMetaDataUtil.asJson(dp).toString());
		//RouteMgmtService.setPersistenceService(fps);
		dp.setAjscMetaDataService(fps);
		UserDefinedBeansDef dp2=dp.save();
		assertEquals("dsm:test:1.2.0",dp2.generateId());
	}
	
	public void equalsTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		UserDefinedBeansDef dp2=new UserDefinedBeansDef();
		UserDefinedBeansDef dp3=new UserDefinedBeansDef();
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		
		dp.setNamespace("dsm");
		dp.setBeansDefVersion("1.2.3.");
		dp.setBeansDefName("test");
		
		dp2.setNamespace("dsm");
		dp2.setBeansDefVersion("1.2.3.");
		dp.setBeansDefName("test");
		
		dp3.setNamespace("RFV");
		dp3.setBeansDefVersion("3.2.3.");
		dp.setBeansDefName("test");
		
		boolean a=dp.equals(dp2);
		assertTrue(a);
		
		boolean b=dp.equals(dp3);
		assertFalse(b);
		
		
	}
	@Test
	public void validateExtendedTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("bsm");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("2.2.0");
		dp.setAjscMetaDataService(fps);
		dp.save();
		boolean a=dp.validateExtended();	
		assertTrue(a);
		dp.setBeansDefName("test.groovy");
		dp.save();
		
		boolean b=dp.validateExtended();
		assertFalse(b);
	}
	
	@Test
	public void hashTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		dp.setNamespace("test");
		int a=dp.hashCode();
		
		assertEquals(574739567, a);
	}
	
	@Ignore("Throws an error but it doesn't like its own constructor in the UserDefinedBeansDefContext.")
	@Test
	public void addToContextsTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("kdn");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("2.2.0");
		RouteMgmtService.setPersistenceService(fps);
		dp.setAjscMetaDataService(fps);
		UserDefinedBeansDefContext d=new UserDefinedBeansDefContext();
		d.setUserDefinedBeansDefId("test");
		d.setContextId("lol");
		
		d.setAjscMetaDataService(fps);
		d.save();
		dp.addToContexts(d);
		
		dp.save();
		
	}
	
	@Test
	public void clearContext(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		FilePersistenceService fps=new FilePersistenceService();
		
		RouteMgmtService.setPersistenceService(fps);
		dp.setAjscMetaDataService(fps);
		RouteMgmtService.setPersistenceService(fps);
		dp.clearContexts();

	}
	@Ignore("Two lines and context addcontext gave me errors. Please see addcontext test")
	@Test
	public void getContextsTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("kdn");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("2.2.0");
		
		UserDefinedBeansDefContext d=new UserDefinedBeansDefContext();
		d.setUserDefinedBeansDefId("test");
		d.setContextId("lol");
		
		d.setAjscMetaDataService(fps);
		d.save();
		dp.addToContexts(d);
		
		
		dp.save();
		RouteMgmtService.setPersistenceService(fps);
		dp.setAjscMetaDataService(fps);
		RouteMgmtService.setPersistenceService(fps);
		
		dp.getContexts();
	}
	
	@Ignore("")
	@Test
	public void removeFromContextTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("kdn");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("2.2.0");
		dp.save();
		RouteMgmtService.setPersistenceService(fps);
		dp.setAjscMetaDataService(fps);
		RouteMgmtService.setPersistenceService(fps);
		UserDefinedBeansDefContext d=new UserDefinedBeansDefContext();
		d.setUserDefinedBeansDefId("test");
		d.setContextId("lol");
		
		d.setAjscMetaDataService(fps);
		d.save();
		dp.removeFromContexts(d);
	}
	
	@Test
	public void getMessageMapTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		dp.setNamespace("FSM");
		Map<Object,Object>map=(Map<Object, Object>) dp.getMessageMap("method", "msgnum");
		
		assertEquals("ajsc",map.get("MODULE"));
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void getAllByNamespaceLikeAndBeansDefVersionLikeTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("ldn");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("2.2.0");
		fps.putMapEntry("ajsc.UserDefinedBeansDef", "ldn:test:2.2.0",(String)AjscMetaDataUtil.asJson(dp));
		dp.setAjscMetaDataService(fps);

		dp.save();
		UserDefinedBeansDef dp2=(UserDefinedBeansDef)AjscMetaDataUtil.fromJson(UserDefinedBeansDef.class,dp.getAllByNamespaceLikeAndBeansDefVersionLike("ldn", "2.2.0"));
		assertEquals("[ldn]", dp2.getNamespace());
	}
	
	@Test
	public void getTransientIdTest()
	{
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("ldn");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("2.2.0");
		String s=dp.getTransientId();
		assertEquals("ldn:test:2.2.0",s);
	}
	@Ignore
	@SuppressWarnings("static-access")
	@Test
	public void findAllbyContextIdTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("mdn");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("2.2.0");
		dp.setAjscMetaDataService(fps);
		
		UserDefinedBeansDefContext d=new UserDefinedBeansDefContext();
		d.setUserDefinedBeansDefId("test");
		d.setContextId("lol");
		d.setAjscMetaDataService(fps);
		d.save();
		dp.addToContexts(d);
		
		dp.save();
		boolean hasMdn=false;
		List<UserDefinedBeansDef>arr=dp.findAllByContextId(d.getId());
		//System.out.println(arr.size());
		for(UserDefinedBeansDef a: arr){
			if(a.getNamespace().equals("mdn"))hasMdn=true;
		}
		assertTrue(hasMdn);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void findAllbyNamespace(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		UserDefinedBeansDef dp2=new UserDefinedBeansDef();

		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("hdn");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("2.2.0");
		
		dp2.setNamespace("hdn");
		dp2.setBeansDefName("test");
		dp2.setBeansDefVersion("2.3.0");
		dp.setAjscMetaDataService(fps);
		dp.save();
		dp2.save();
		ArrayList<UserDefinedBeansDef>arr=(ArrayList<UserDefinedBeansDef>) dp.findAllByNamespace("hdn");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceAndBeansDefNameAndBeansDefVersionTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		UserDefinedBeansDef dp2=new UserDefinedBeansDef();

		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("tew");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("2.2.0");
		
		dp2.setNamespace("tew");
		dp2.setBeansDefName("test");
		dp2.setBeansDefVersion("2.3.0");
		dp.setAjscMetaDataService(fps);
		dp.save();
		dp2.save();
		
		ArrayList<UserDefinedBeansDef>arr=(ArrayList<UserDefinedBeansDef>)dp.findAllByNamespaceAndBeansDefNameAndBeansDefVersion("tew", "test", "2.3.0");
		assertEquals(1,arr.size());
		
	}
	@Test
	public void findAllByNamespaceLikeTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		UserDefinedBeansDef dp2=new UserDefinedBeansDef();

		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("tew");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("2.2.0");
		
		dp2.setNamespace("tew");
		dp2.setBeansDefName("test");
		dp2.setBeansDefVersion("2.3.0");
		dp.setAjscMetaDataService(fps);
		dp.save();
		dp2.save();
		ArrayList<UserDefinedBeansDef>arr=(ArrayList<UserDefinedBeansDef>)dp.findAllByNamespaceLike("tew");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLikeAndBeansDefNameLikeAndBeansDefVersionLikeTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		UserDefinedBeansDef dp2=new UserDefinedBeansDef();

		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("bnh");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("2.2.0");
		
		dp2.setNamespace("bnh");
		dp2.setBeansDefName("test");
		dp2.setBeansDefVersion("2.3.0");
		dp.setAjscMetaDataService(fps);
		dp.save();
		dp2.save();
		
		ArrayList<UserDefinedBeansDef>arr=(ArrayList<UserDefinedBeansDef>)dp.findAllByNamespaceLikeAndBeansDefNameLikeAndBeansDefVersionLike("bnh", "test", "2.3.0");
		assertEquals(1,arr.size());
	}
	
	@Test
	public void findAllByNamespaceAndBeansDefVersionTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		UserDefinedBeansDef dp2=new UserDefinedBeansDef();

		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("cvh");
		dp.setBeansDefName("testers");
		dp.setBeansDefVersion("2.3.0");
		
		dp2.setNamespace("cvh");
		dp2.setBeansDefName("test");
		dp2.setBeansDefVersion("2.3.0");
		dp.setAjscMetaDataService(fps);
		dp.save();
		dp2.save();
		
		ArrayList<UserDefinedBeansDef>arr=(ArrayList<UserDefinedBeansDef>)dp.findAllByNamespaceAndBeansDefVersion("cvh",  "2.3.0");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		UserDefinedBeansDef dp2=new UserDefinedBeansDef();

		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("pop");
		dp.setBeansDefName("testers");
		dp.setBeansDefVersion("2.3.0");
		
		dp2.setNamespace("pop");
		dp2.setBeansDefName("test");
		dp2.setBeansDefVersion("2.3.0");
		dp.setAjscMetaDataService(fps);
		dp.save();
		dp2.save();
		
		ArrayList<UserDefinedBeansDef>arr=(ArrayList<UserDefinedBeansDef>)dp.findAllByNamespace("pop");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLikeAndBeansDefNameLikeTest(){
		
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		UserDefinedBeansDef dp2=new UserDefinedBeansDef();

		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("yut");
		dp.setBeansDefName("test");
		dp.setBeansDefVersion("2.3.0");
		
		dp2.setNamespace("yut");
		dp2.setBeansDefName("test");
		dp2.setBeansDefVersion("2.4.0");
		dp.setAjscMetaDataService(fps);
		dp.save();
		dp2.save();
		
		ArrayList<UserDefinedBeansDef>arr=(ArrayList<UserDefinedBeansDef>)dp.findAllByNamespaceLikeAndBeansDefNameLike("yut","test");
		
		assertEquals(2,arr.size());
	}
	
	@Test
	public void findAllByNamespaceLikeAndBeansDefVersionLikeTest(){

		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		UserDefinedBeansDef dp2=new UserDefinedBeansDef();

		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("fup");
		dp.setBeansDefName("testz");
		dp.setBeansDefVersion("2.3.0");
		
		dp2.setNamespace("fup");
		dp2.setBeansDefName("testr");
		dp2.setBeansDefVersion("2.3.0");
		dp.setAjscMetaDataService(fps);
		dp.save();
		dp2.save();
		
		ArrayList<UserDefinedBeansDef>arr=(ArrayList<UserDefinedBeansDef>)dp.findAllByNamespaceLikeAndBeansDefVersionLike("fup","2.3.0");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void deleteTest(){
		UserDefinedBeansDef dp=new UserDefinedBeansDef();
		FilePersistenceService fps=new FilePersistenceService();
		dp.setNamespace("ops");
		dp.setBeansDefName("testz");
		dp.setBeansDefVersion("2.3.0");
		dp.setAjscMetaDataService(fps);
		dp.save();
		dp.delete();
		ArrayList<UserDefinedBeansDef>arr=(ArrayList<UserDefinedBeansDef>)dp.findAllByNamespace("ops");
		assertEquals(0, arr.size());
		
	}
	
}
