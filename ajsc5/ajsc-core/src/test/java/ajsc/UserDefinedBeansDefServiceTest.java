/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class UserDefinedBeansDefServiceTest extends BaseTestCase{
	
	final static Logger logger = LoggerFactory.getLogger(UserDefinedBeansDefServiceTest.class);

	private static UserDefinedBeansDefService userDefJarService;
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
			Resource resource = new ClassPathResource("conf");
			File sourceDir = resource.getFile();
			File targetDir = new File(BaseTestCase.AJSC_TEST_HOME + File.separator + "conf");
			copyDir(sourceDir, targetDir);

		} catch (IOException e) {
			logger.info("Error reading file");
		}

		appCtx = new ClassPathXmlApplicationContext("applicationContext_test.xml");
		userDefJarService = appCtx.getBean(UserDefinedBeansDefService.class);
	}


	@Test
	public void testInsertUserDefinedBeans() throws Exception {

		final String beanDefFile = "bean_def.xml";
		
		UserDefinedBeansDef  userDefBeans = new UserDefinedBeansDef();
		userDefBeans.setNamespace("test");
		userDefBeans.setBeansDefVersion("1.0.0");
		userDefBeans.setBeansDefName(beanDefFile);
		userDefBeans.setBeansDefContent("content");
		
		userDefJarService.insertOrUpdateUserDefinedBeansDef(userDefBeans);

		File file = new File(getFilePath("conf" + File.separator + "test" + File.separator + "1.0.0", beanDefFile));
		assertTrue(file.exists());
		assertTrue(file.getName().contains(beanDefFile));
	}



}
