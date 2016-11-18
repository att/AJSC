/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.ajsc.filemonitor.AJSCPropertyService;

//@FixMethodOrder(MethodSorters.)
public class AJSCPropertyServiceTest extends BaseTestCase {
	AJSCPropertyService APService;
	File temp;

	@BeforeClass
	public static void setUpClass() throws Exception {

	}

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		super.setUp();
		temp = new File(System.getProperty("AJSC_CONF_HOME") + File.separator + "etc" + File.separator + "appprops"
				+ File.separator + "temp.properties");
		File n = new File(Paths.get("").toAbsolutePath().toString() + File.separator + "src" + File.separator + "test"
				+ File.separator + "resources" + File.separator + "appprops");

		PrintWriter p;
		try {
			p = new PrintWriter(temp);
			p.println("sfm0=/opt/app/node");
			p.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		APService = new AJSCPropertyService();

		AJSCPropertiesMap map = new AJSCPropertiesMap();
		APService.setFilePropertiesMap(map);
		try {
			APService.init();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void getFilePropertiesMap() {
		String s = APService.getFilePropertiesMap().getProperty("temp.properties", "sfm0");
		assertEquals("/opt/app/node", s);
	}

}
