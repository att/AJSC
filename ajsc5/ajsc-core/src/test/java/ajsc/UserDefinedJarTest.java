/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import static org.junit.Assert.*;

public class UserDefinedJarTest extends BaseTestCase {
	UserDefinedJar ud;
	FilePersistenceService fps;
	
	@Before
	public void setUp() {
		super.setUp();
		ud=new UserDefinedJar();
		fps=new FilePersistenceService();
		RouteMgmtService.setPersistenceService(fps);
		UserDefinedJar.setAjscMetaDataService(fps);
	}
	
	@Test
	public void findByIdTEst(){
	ud.setNamespace("ex1");
	ud.setJarName("jar");
	ud.setJarVersion("9");
	//ud.setJarContent(((String)AjscMetaDataUtil.asJson(ud)).getBytes());
	fps.putMapEntry("ajsc.UserDefinedJar", ud.generateId(), (String)AjscMetaDataUtil.asJson(ud));
	//ud.save();
	UserDefinedJar ud2=ud.findById(ud.generateId());
	
	assertEquals(ud.generateId(),ud2.generateId());
	}
	
	@Test
	public void equalsTest(){
		//if(ud.getAjscMetaDataService()==null)System.out.println("help");
		ud.setNamespace("ex1");
		ud.setJarName("jar");
		ud.setJarVersion("9");
		//fps.putMapEntry("ajsc.UserDefinedJar", ud.generateId(), (String)AjscMetaDataUtil.asJson(ud));

		UserDefinedJar ud2=new UserDefinedJar();
		ud2.setNamespace("ex1");
		ud2.setJarName("jar");
		ud2.setJarVersion("9");
		
		UserDefinedJar ud3=new UserDefinedJar();
		ud3.setNamespace("ex2");
		ud3.setJarName("jdr");
		ud3.setJarVersion("9.4.5");
		
		boolean a= ud.equals(ud2);
		assertTrue(a);
		
		boolean b=ud.equals(ud3);
		
	}
	
	@Test
	public void hashCodeTest(){
		ud.setNamespace("name");
		ud.setJarVersion("2.4.5");
		int a=ud.hashCode();
		assertEquals(372096575,a);
	}
	
	@Ignore
	@Test
	public void addToContexts(){
		UserDefinedJarContext ujc=new UserDefinedJarContext();
		ujc.setAjscMetaDataService(fps);
		ujc.setUserDefinedJarId("ajscc");
		ujc.setContextId("dd");
		ujc.save();
		ud.setNamespace("ex1");
		ud.setJarName("jar");
		ud.setJarVersion("9");
		ud.setJarContent(((String)AjscMetaDataUtil.asJson(ud)).getBytes());
		fps.putMapEntry("ajsc.UserDefinedJar", ud.generateId(), (String)AjscMetaDataUtil.asJson(ud));
		
		//ud.save();
		ud.addToContexts(ujc);
		UserDefinedJarContext arr=ujc.findByUserDefinedJarIdAndContextId(ud.generateId(), "Naw:Naw");
		//assertEquals("Naw:Naw",arr.generateId());
		if(arr==null)System.out.println("tell me");
	}
	@Ignore
	@Test
	public void getClearContentsTest(){
		ud.setNamespace("ex1");
		ud.setJarName("jar");
		ud.setJarVersion("9");
		ud.setJarContent(((String)AjscMetaDataUtil.asJson(ud)).getBytes());
		ud.save();
		
		//UserDefinedJar udj=ud.getContexts();
		//assertNull(udj);
	}
	
	@Test
	public void getMessageMapTest(){
		Map<String,String>a=(Map<String, String>) ud.getMessageMap("method", "msgnum");
		assertTrue(a.get("METHOD").equals("method"));
	}
	@Test
	public void getAllByNamespaceLikeAndJarVersionLike(){
		ud.setNamespace("e21");
		ud.setJarName("nvb");
		ud.setJarVersion("9");
		fps.putMapEntry("ajsc.UserDefinedJar", ud.generateId(), (String)AjscMetaDataUtil.asJson(ud));
		ud.setJarName("far");
		ud.setJarContent(((String)AjscMetaDataUtil.asJson(ud)).getBytes());
		ud.save();
		
		String s=ud.getAllByNamespaceLikeAndJarVersionLike("e21", "9");
		assertTrue(s.contains("nvb") && s.contains("far"));
		String trans=ud.getTransientId();
		assertEquals("e21:far:9",trans);
	}
	
	@Test
	public void findAllByNamespace(){
		ud.setNamespace("sss");
		ud.setJarName("nvaab");
		ud.setJarVersion("9.0");
		fps.putMapEntry("ajsc.UserDefinedJar", ud.generateId(), (String)AjscMetaDataUtil.asJson(ud));
		ud.setJarName("fem");
		ud.setJarContent(((String)AjscMetaDataUtil.asJson(ud)).getBytes());
		ud.save();
		ArrayList<UserDefinedJar>arr=(ArrayList<UserDefinedJar>)ud.findAllByNamespace("sss");
		assertEquals(2,arr.size());
		
	}
	
	@Test
	public void  findAllByNamespaceAndJarNameAndJarVersion(){
		ud.setNamespace("fds");
		ud.setJarName("vass");
		ud.setJarVersion("9");
		fps.putMapEntry("ajsc.UserDefinedJar", ud.generateId(), (String)AjscMetaDataUtil.asJson(ud));
		ud.setJarName("fem");
		ud.setJarContent(((String)AjscMetaDataUtil.asJson(ud)).getBytes());
		ud.save();
		ArrayList<UserDefinedJar>arr=(ArrayList<UserDefinedJar>)ud.findAllByNamespaceAndJarNameAndJarVersion("fds", "vass", "9");
		assertEquals(1,arr.size());
	}
	@Test
	public void findAllByNamespaceLikeAndJarNameLike(){
		ud.setNamespace("jui");
		ud.setJarName("bnn");
		ud.setJarVersion("1.4.5");
		fps.putMapEntry("ajsc.UserDefinedJar", ud.generateId(), (String)AjscMetaDataUtil.asJson(ud));
		ud.setJarVersion("1.5.3");
		ud.setJarContent(((String)AjscMetaDataUtil.asJson(ud)).getBytes());
		ud.save();
		ArrayList<UserDefinedJar>arr=(ArrayList<UserDefinedJar>)ud.findAllByNamespaceLikeAndJarNameLike("jui", "bnn");
		assertEquals(2,arr.size());
	}
	
	@Test
	public void listTest(){
		ud.setNamespace("bvbv");
		ud.setJarName("ddd");
		ud.setJarVersion("7.4.5");
		fps.putMapEntry("ajsc.UserDefinedJar", ud.generateId(), (String)AjscMetaDataUtil.asJson(ud));
		Map<String,UserDefinedJar> map=new HashMap<String, UserDefinedJar>();
		ArrayList<UserDefinedJar>arr=(ArrayList<UserDefinedJar>)ud.list(map);
		boolean hassbvbv=false;
		for(UserDefinedJar j:arr){
			if(j.getNamespace().equals("bvbv")){
				hassbvbv=true;
				break;
			}
		}
		assertTrue(hassbvbv);
		
	}
	
	@Test
	public void deleteTest(){
		ud.setNamespace("huyu");
		ud.setJarName("vbv");
		ud.setJarVersion("8.4.5");
		fps.putMapEntry("ajsc.UserDefinedJar", ud.generateId(), (String)AjscMetaDataUtil.asJson(ud));
		assertNotNull(fps.getMapEntry("ajsc.UserDefinedJar", ud.generateId()));
		ud.delete();
		assertNull(fps.getMapEntry("ajsc.UserDefinedJar", ud.generateId()));
	}
	
	@Test
	public void ContextTest(){
		UserDefinedJarContext udj=new UserDefinedJarContext();
		udj.setAjscMetaDataService(fps);
		udj.setUserDefinedJarId("ddd");
		udj.setContextId("BVN");
		udj.save();
		UserDefinedJarContext d=udj.findById("ddd:BVN");
		d.assignId();
		d.generateId("ddd", "BVN");
		String s=d.generateId();
		
		assertEquals("ddd:BVN",s);
		UserDefinedJarContext c=udj.findByUserDefinedJarIdAndContextId("ddd","BVN");
		assertEquals("ddd:BVN",c.generateId());
		
		ArrayList<UserDefinedJarContext> arr=(ArrayList<UserDefinedJarContext>)udj.list();
		boolean hasD=false;
		for(UserDefinedJarContext ucc: arr)
			if(ucc.getUserDefinedJarId().equals("ddd"))
				hasD=true;
		assertTrue(hasD);
		
		udj.delete();
		assertNull(fps.getMapEntry("ajsc.UserDefinedJarContext", "ddd:BVN"));
	}
	
}
