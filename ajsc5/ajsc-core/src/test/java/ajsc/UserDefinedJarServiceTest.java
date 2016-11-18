/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class UserDefinedJarServiceTest extends BaseTestCase {

	final static Logger logger = LoggerFactory.getLogger(UserDefinedJarServiceTest.class);

	private static UserDefinedJarService userDefJarService;
	static ApplicationContext appCtx;
	static File dir;

	@AfterClass
	public static void tearDownClass() throws Exception {
		deleteDirectory(new File(BaseTestCase.AJSC_TEST_HOME));
	}

	@Before
	public void setUp() {
		super.setUp();

		try {
			Resource resource = new ClassPathResource("extJars");
			File sourceDir = resource.getFile();
			File targetDir = new File(BaseTestCase.AJSC_TEST_HOME + File.separator + "extJars");
			copyDir(sourceDir, targetDir);

		} catch (IOException e) {
			logger.info("Error reading file at ");
		}

		appCtx = new ClassPathXmlApplicationContext("applicationContext_test.xml");
		userDefJarService = appCtx.getBean(UserDefinedJarService.class);
	}


	@Test
	public void testInsertUserDefinedJars() throws Exception {

		final String jarFileName = "testJar.jar";

		byte[] jarContent = FileUtils.readFileToByteArray(new File(getFilePath("extJars",
				"HelloWorld-0.0.1-SNAPSHOT.jar")));
		logger.info("*** Jar Content = " + jarContent);

		UserDefinedJar userDefJar = new UserDefinedJar();
		userDefJar.setNamespace("test");
		userDefJar.setJarName(jarFileName);
		userDefJar.setJarVersion("1.0.0");
		userDefJar.setJarContent(jarContent);

		userDefJarService.insertOrUpdateUserDefinedJar(userDefJar);

		File file = new File(getFilePath("lib" + File.separator + "test" + File.separator + "1.0.0", jarFileName));
		assertTrue(file.exists());
		assertTrue(file.getName().contains(jarFileName));
	}

	@Test
	public void testListUserDefinedJars() throws Exception {

		byte[] jarContent = FileUtils.readFileToByteArray(new File(getFilePath("extJars",
				"HelloWorld-0.0.1-SNAPSHOT.jar")));
		logger.info("*** Jar Content = " + jarContent);

		UserDefinedJar userDefJar = new UserDefinedJar();
		userDefJar.setNamespace("test");
		userDefJar.setJarName("testJar.jar");
		userDefJar.setJarVersion("1.0.0");
		userDefJar.setJarContent(jarContent);

		userDefJarService.insertOrUpdateUserDefinedJar(userDefJar);

		List listOfJars = (List) userDefJarService.listUserDefinedJars();
		assertNotNull(listOfJars);
	}

}
