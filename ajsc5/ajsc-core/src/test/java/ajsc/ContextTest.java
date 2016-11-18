/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;
import java.util.ArrayList;
import java.util.Map;

import org.junit.*;

import static org.junit.Assert.*;
public class ContextTest extends BaseTestCase{

	@Before
	public void setUp() {
		super.setUp();
	}
	
	@Test
	public void testEquals(){
		Context con1 =new Context();
		Context con2 =new Context();
		Context con3 =new Context();

		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		Context.setAjscMetaDataService(fps);
		con1.setContextName("name");
		con1.setContextVersion("1.3.6");
		con1.save();
		
		con2.setContextName("name");
		con2.setContextVersion("1.3.6");
		con2.save();
		
		con3.setContextName("name1");
		con3.setContextVersion("3.3.6");
		con3.save();
		
		boolean a=con1.equals(con2);
		assertTrue(a);
		
		boolean b=con1.equals(con3);
		assertFalse(b);
	}
	
	@Test
	public void hashCodeTest(){
		Context con1 =new Context();
		con1.setContextName("name");
		con1.setContextVersion("2.4.5");
		int a=con1.hashCode();
		assertEquals(172448319,a);
	}
	
	@Test
	public void getMessageMapTest(){
		Context con1 =new Context();
		Map<String,String>a=(Map<String, String>) con1.getMessageMap("method", "msgnum");
		assertTrue(a.get("METHOD").equals("method"));
	}
	
	@Test
	public void getAllByContextNameLike(){
		Context con =new Context();
		Context con2 =new Context();

		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		Context.setAjscMetaDataService(fps);
		con.setContextName("yup");
		con.setContextVersion("1.2.3");
		
		fps.putMapEntry("ajsc.Context", "yup:1.2.3", (String)AjscMetaDataUtil.asJson(con));

		
		con2.setContextName("yup");
		con2.setContextVersion("5.2.3");
		con2.save();
		
		String s=con.getAllByContextNameLike("yup");
		assertTrue(s.contains("1.2.3") && s.contains("5.2.3") );
	}
	
	@Test
	public void indentTest(){
		Context con =new Context();
		con.setContextName("GSM");
		con.setContextVersion("4.9.6");
		String s=con.ident();
		assertEquals("GSM:4.9.6",s);
	}
	
	@Test
	public void findAllByContextNameLikeTest(){
		Context con =new Context();
		Context con2 =new Context();

		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		Context.setAjscMetaDataService(fps);
		con.setContextName("LDAP");
		con.setContextVersion("1.2.3");
		con.setDescription("This is pie");
		fps.putMapEntry("ajsc.Context", "LDAP:1.2.3", (String)AjscMetaDataUtil.asJson(con));

		
		con2.setContextName("LDAP");
		con2.setContextVersion("3.2.3");
		con.setDescription("This is not pie");
		con2.save();
		
		ArrayList<Context> s=(ArrayList<Context> )con.findAllByContextNameLike("LDAP");
		assertEquals(2,s.size());
	}
	
	@Test
	public void findAllByContextNameLikeAndContextVersionLikeTest(){
		Context con =new Context();
		Context con2 =new Context();

		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		Context.setAjscMetaDataService(fps);
		con.setContextName("FGH");
		con.setContextVersion("1.2.3");
		con.setDescription("This is pie");
		fps.putMapEntry("ajsc.Context", "FGH:1.2.3", (String)AjscMetaDataUtil.asJson(con));
		
		con2.setContextName("FGH");
		con2.setContextVersion("1.2.3");
		con.setDescription("This is not pie");
		fps.putMapEntry("ajsc.Context", "FGH:1.2.3", (String)AjscMetaDataUtil.asJson(con));

		
		ArrayList<Context> s=(ArrayList<Context> )con.findAllByContextNameLikeAndContextVersionLike("FGH","1.2.3");
		assertEquals(1,s.size());
	}
	
	@Test
	public void deleteTest(){
		Context con =new Context();
		
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		Context.setAjscMetaDataService(fps);
		con.setContextName("BVM");
		con.setContextVersion("1.2.3");
		con.setDescription("This is pie");
		con.save();
		String s=fps.getMapEntry("ajsc.Context", "BVM:1.2.3");
		assertTrue(s.contains("\"id\":\"BVM:1.2.3\""));
		con.delete();
		assertNull(fps.getMapEntry("ajsc.Context", "BVM:1.2.3"));
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void findByContextNameAndContextVersionTest(){
		Context con =new Context();
		FilePersistenceService fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		Context.setAjscMetaDataService(fps);
		con.setContextName("BJM");
		con.setContextVersion("1.2.3");
		con.setDescription("This is pie");
		fps.putMapEntry("ajsc.Context", "BJM:1.2.3", (String)AjscMetaDataUtil.asJson(con));

		Context c=con.findByContextNameAndContextVersion("BJM", "1.2.3");
		assertEquals("BJM",c.getContextName());
	}
}
