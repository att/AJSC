/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import static org.junit.Assert.assertTrue;
import groovy.json.JsonBuilder;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.junit.Ignore;

public class VandelayServiceTest extends BaseTestCase {
	
	final static Logger logger = LoggerFactory.getLogger(VandelayServiceTest.class);
	
	private static VandelayService vandelayService;
	static ApplicationContext appCtx;

	@Before
	public void setUp() {

		super.setUp();
		System.setProperty("csiEnable", "true");

		copyFiles(new File(TEST_RSC_DIR + "runtimeEnvironment.zip"),
						new File(getFilePath("runtime", "runtimeEnvironment.zip")));
		copyFiles(new File(TEST_RSC_DIR + "logback_jms.xml"), new File(getFilePath("etc", "logback_jms.xml")));
		copyDir(new File(TEST_RSC_DIR + "appprops"), new File(getFilePath("etc", "appprops")));
		copyDir(new File(TEST_RSC_DIR + "sysprops"), new File(getFilePath("etc", "sysprops")));
		copyFiles(new File(TEST_RSC_DIR + "demo_v1.zip"), new File(getFilePath("services", "demo_v1.zip")));
		copyFiles(new File(TEST_RSC_DIR + "demo_v1_props.zip"), new File(getFilePath("services", "demo_v1_props.zip")));
		copyFiles(new File(TEST_RSC_DIR + "demo4-0.0.1-SNAPSHOT.jar"),
						new File(getFilePath("lib", "demo4-0.0.1-SNAPSHOT.jar")));

		appCtx = new ClassPathXmlApplicationContext("applicationContext_test.xml");
		vandelayService = appCtx.getBean(VandelayService.class);

	}
	/*
	@Ignore
	@AfterClass
	public static void deleteAll() {
		deleteDirectory(new File(AJSC_TEST_HOME));
	}
	*/
	
	@Test
	public void testExportServices() {
		System.out.println("running testExportServices");
		vandelayService.exportZip("export", "v1", "export_v1", "export_v1_prop");
		File exportZipFile = new File(getFilePath("export", "export_v1.zip"));
		File propertiesZipFile = new File(getFilePath("export", "export_v1_prop.zip"));

		assertTrue(exportZipFile.exists());
		assertTrue(propertiesZipFile.exists());

	}
	
	@Test
	public void testProcessImportRuntime() {
		System.out.println("running testProcessImportRuntime");
		File file = new File(getFilePath("runtime", "runtimeEnvironment.zip"));
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			vandelayService.processImportRuntime(fis);
			FilePersistenceService ajscMetaDataService = RouteMgmtService.getPersistenceService();
			String mapEntry = ajscMetaDataService.getMapEntry("ajsc.Context", "default:0");
			String deploymentEntry = ajscMetaDataService.getMapEntry("ajsc.DeploymentPackage", "demo:v1");

			assertTrue(mapEntry.contains("contextName"));
			assertTrue(deploymentEntry.contains("demo:v1"));

		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		}
	}




	@Test
	public void testGetJarContents() throws IOException{
		System.out.println("Running testgetjar");
		File f=new File("src"+File.separator+"test"+File.separator+"resources"+File.separator+"demo4-0.0.1-SNAPSHOT.jar");
		System.out.println(f.getAbsolutePath());
		ZipInputStream zipInputStream=new ZipInputStream(new FileInputStream(f));
		BufferedInputStream is=new BufferedInputStream(zipInputStream);
		zipInputStream.getNextEntry();
		ZipEntry currentEntry = zipInputStream.getNextEntry();
		//System.out.println(currentEntry.getName());
		String output =vandelayService.getContents(currentEntry, zipInputStream);
		assertTrue(output.contains("Manifest-Version: 1.0"));
		System.out.println(output);
		
		
	}
	
	/*@Test
	public void testExportRuntimeEnvironment() {

		vandelayService.exportRuntimeEnvironment();
		File runtimeZipFile = new File(getFilePath("export", "runtimeEnvironment.zip"));
		assertTrue(runtimeZipFile.exists());
		
	}*/
	@Ignore("Got to have testExportRuntimeEnvironment working first")
	@Test
	public void testimportRuntimeEnvironment(){
		System.out.println("Running ExportRunTimeEnvironment");
		vandelayService.exportRuntimeEnvironment();
	}
	@Ignore
	@Test
	public void testImportZip() throws FileNotFoundException{
		vandelayService.exportZip("export", "v1", "export_v1", "export_v1_prop");
		File exportZipFile = new File(getFilePath("export", "export_v1.zip"));
		File propertiesZipFile = new File(getFilePath("export", "export_v1_prop.zip"));
		vandelayService.importZip(new FileInputStream(exportZipFile), null, "j");
		
	}
	
	public String createJson() {
	
		HashMap<String,String> jsonHash = new HashMap<>();
		jsonHash.put("contextClass", "ajsc.Context");
		jsonHash.put("contextName", "demo");
		jsonHash.put("contextVersion", "v1");
		return new JsonBuilder(jsonHash).toString();
	}
	
	public static void main(String[] args) throws IOException {
		ZipInputStream zipInputStream=new ZipInputStream(new FileInputStream(new File("none.zip")));
		BufferedInputStream is=new BufferedInputStream(zipInputStream);
		ZipEntry currentEntry = zipInputStream.getNextEntry();
		
		Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		char[] buffer = new char[2048];
		int n;
		while(( n = reader.read(buffer)) != -1){
			System.out.println(n);
		}
	}

}
