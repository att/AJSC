/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.codehaus.plexus.util.FileUtils;
import org.slf4j.Logger;

public abstract class BaseTestCase {
	public static final String AJSC_TEST_HOME = System.getProperty("java.io.tmpdir")+File.separator + "ajscTestHome";
	public static final String TEST_RSC_DIR = "src/test/resources/";

	public void setUp() {
		System.out
				.println(" <<<<<<<<AJSC Test Home >>>>>>>> " + AJSC_TEST_HOME);
		System.setProperty("AJSC_HOME", AJSC_TEST_HOME);
		System.setProperty("AJSC_CONF_HOME", AJSC_TEST_HOME);

		try {
			createDir(new File(AJSC_TEST_HOME));
			createDir(new File(AJSC_TEST_HOME + File.separator + "etc"));
			createDir(new File(AJSC_TEST_HOME + File.separator + "etc"
					+ File.separator + "appprops"));
			createDir(new File(AJSC_TEST_HOME + File.separator + "etc"
					+ File.separator + "sysprops"));
			createDir(new File(AJSC_TEST_HOME + File.separator + "extApps"));
			createDir(new File(AJSC_TEST_HOME + File.separator + "logs"));
			createDir(new File(AJSC_TEST_HOME + File.separator + "export"));
			createDir(new File(AJSC_TEST_HOME + File.separator + "runtime"));
			createDir(new File(AJSC_TEST_HOME + File.separator + "services"));
			createDir(new File(AJSC_TEST_HOME + File.separator + "extJars"));
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
 
	public void tearDown()   {
		System.out.println("Deleting AJSC Home ----->"+AJSC_TEST_HOME);
		try {
			File f = new File(AJSC_TEST_HOME);
			// f.delete();
			deleteDir(f);
		} catch (Exception ex) {
			// TODO: handle exception
			System.out.println("Exception caught in BaseTestCase:tearDown ->");
			ex.printStackTrace();
		}

	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
			// The directory is now empty so delete it
			 dir.deleteOnExit();
		}
		return true;
	}
//	@AfterClass
//	public void afterExecution(){
//		deleteDirectory(new File(AJSC_TEST_HOME));
//	}

	public static  boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	public static void createDir(File path) {

		if (!path.exists()) {

			if (path.mkdir()) {

				System.out.println(path.getName() + " directory is created!");

			} else {

				System.out.println("Failed to create " + path.getName()
						+ " directory!");

			}
		}
	}

	public static void copyFiles(File source, File destn) {

		try {
			if (source.exists() && !destn.exists()) {

				Path p = Paths.get(source.getAbsolutePath());

				Path p1 = Paths.get(destn.getAbsolutePath());

				java.nio.file.Files.copy(p, p1);

			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static void copyDir(File source, File dest) {

		try {
			FileUtils.copyDirectory(source, dest);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public String getFilePath(String directoryName, String fileName) {
		return AJSC_TEST_HOME + File.separator + directoryName + File.separator + fileName;
	}
	
	//TODO Replace sysout with a logger
	public void print_to_console(Logger logger,String message){
		System.out.println(message);
	}
}