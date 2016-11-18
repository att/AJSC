/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package com.att.ajsc.runner;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.xml.XmlConfiguration;

import com.att.ajsc.runner.xml.ServletMappingType;
//import com.sun.java.xml.ns.javaee.*;
import com.att.ajsc.runner.xml.WebAppType;

public class Runner {
	/**
	 * @param args
	 * @throws Exception
	 */
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		try {
			System.out.println("Initializing the AJSC . . . ");
			// cleaning AJSC_HOME. During a Soa Cloud Install to a node with an
			// InstallRoot of "/",
			// AJSC_HOME is prefixed with "//" and causes subsequent program
			// failure. This code
			// 'cleans' AJSC_HOME and replaces "//" with "/" for those
			// particular Installations
			String ajscHome = System.getProperty("AJSC_HOME").replace("//", "/");
			System.setProperty("AJSC_HOME", ajscHome);
			System.out.println("AJSC_HOME has been set to: " + System.getProperty("AJSC_HOME"));
			
			String ajscConfHome = System.getProperty("AJSC_CONF_HOME");
			if(ajscConfHome == null || ajscConfHome.trim().length() <= 0){
				System.setProperty("AJSC_CONF_HOME", ajscHome); 
			}else{
				System.setProperty("AJSC_CONF_HOME",ajscConfHome.replace("//", "/"));
			}
			System.out.println("AJSC_CONF_HOME has been set to: "+ System.getProperty("AJSC_CONF_HOME"));

			//Scan the sysprops directory and set the properties to System
			Properties prop = new Properties();
			String dirName = System.getProperty("AJSC_CONF_HOME") + "/etc/sysprops";
			System.out.println("System properties scanning directory has been set to: "+dirName);
			scanSystemPropsDir(dirName, prop);
			
			for(String key : prop.stringPropertyNames()) {
				System.setProperty(key, (String) prop.get(key));
			}
			
			String ajscSharedConfigLoc=System.getProperty("AJSC_SHARED_CONFIG");
			
			if(ajscSharedConfigLoc == null||ajscSharedConfigLoc.trim().length() <=0){
				ajscSharedConfigLoc = System.getProperty("AJSC_CONF_HOME");
			}
			
			
			String AJSC_JETTY_CONFIG_XML_LOC = System
					.getProperty("AJSC_HOME") + "/etc/ajsc-jetty.xml";

			// Setting up Temporary Directory for jetty to deploy webapps from
			File dir = new File(System.getProperty("AJSC_HOME")
					+ File.separator + "jetty" + File.separator + "webapps");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			
			//Setting up log directory for Java Util (JUL) Framework to work as intended
			File logDir = new File(System.getProperty("AJSC_HOME")
					+ File.separator + "log");
			if (!logDir.exists()) {
				logDir.mkdirs();
			}
			
			Path basedir = FileSystems.getDefault().getPath(
					System.getProperty("AJSC_HOME") + File.separator + "jetty"
							+ File.separator + "webapps");
			// Path p = Files.createTempDirectory("ajsc-temp.dir");
			
			//creates the temp directory to store ajsc.war - incorrect.  this is setting up the temp dir to explode ajsc-war
			Path p = Files.createTempDirectory(basedir, "ajsc-temp.dir");
			
			//setting the ajsc-temp-dir system property to point to ajsc.war
			//still need to set the ajsc-temp-dir, i believe to account for multiple instances.  this also sets the dir
			//underneath jetty/webapps which is what we still want
			System.setProperty("AJSC_TEMP_DIR", p.toString());
			System.out.println("AJSC_TEMP_DIR has been set to: "
					+ System.getProperty("AJSC_TEMP_DIR"));
			
			//adding a shutdownHook to delete the temp dir created by each ajsc instance
			Runtime.getRuntime().addShutdownHook(new Thread() {

			      @Override
			      public void run() {
			    	  File dir = new File(System.getProperty("AJSC_TEMP_DIR"));
			    	  try {
						FileUtils.deleteDirectory(dir);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			      }
			 });
			
			File ajscWarDir = new File(System.getProperty("AJSC_HOME")
					+ File.separator + "lib");
			if (!ajscWarDir.exists()) {
				ajscWarDir.mkdirs();
			}
			// Setting URL Pattern for CamelServlet to register properly with
			// dme2

			setURLPatterns();

			// Retrieving the ajsc Context Path from program args and setting
			// the AJSC_CONTEXT_PATH to be used by ajsc-jetty.xml
			String ajscWarPath = getAjscWarCtxPath(args);
			System.setProperty("AJSC_CONTEXT_PATH", ajscWarPath);
			System.out.println("AJSC_CONTEXT_PATH has been set to: "
					+ System.getProperty("AJSC_CONTEXT_PATH"));

			// I am also setting APP_CONTEXT_PATH here as well as
			// AJSC_CONTEXT_PATH as I believe the program later uses
			// APP_CONTEXT_PATH for some other logic. I would like to convert
			// that to AJSC_CONTEXT_PATH when I get a chance
			System.setProperty("APP_CONTEXT_PATH", ajscWarPath);

			// Retrieving the location of the ajsc war and setting this path to
			// be used by ajsc-jetty.xml
			String runTheMethod = getAjscWarWarPath(args).toString();
			String ajscWarWarPath = System.getProperty("AJSC_HOME") + File.separator + "lib" + File.separator + "ajsc.war";
			
			//Changing above code to simply set the folder path to /ajscWar for the ajsc.war location
			//String ajscWarWarPath = System.getProperty("AJSC_HOME").toString() + "/ajscWar";
			System.setProperty("AJSC_WAR_PATH", ajscWarWarPath);
			System.out.println("AJSC_WAR_PATH has been set to: "
					+ System.getProperty("AJSC_WAR_PATH"));


			// Searching for a port to listen to and register with dme2 as an
			// ephemeral port
			int listenerPort = getListenerPort(args);
			if (listenerPort == 0) {
				ServerSocket s = new ServerSocket(0);
				listenerPort = s.getLocalPort();
				s.close();
			}
			System.setProperty("AJSC_HTTP_PORT", Integer.toString(listenerPort));
			System.out.println("AJSC_HTTP_PORT has been set to: "
					+ System.getProperty("AJSC_HTTP_PORT"));
			System.setProperty("server.port", Integer.toString(listenerPort));

			// Setting up https port for ssl. Port will be passed to
			// ajsc-jetty.xml where actual configuration will take place
			String enableSSL = System.getProperty("enableSSL");
			if (enableSSL != null && enableSSL.equals("true")) {
				int httpsListenerPort = getHttpsListenerPort(args);
				if (httpsListenerPort == 0) {
					ServerSocket s = new ServerSocket(0);
					httpsListenerPort = s.getLocalPort();
					s.close();
				}
				System.setProperty("AJSC_HTTPS_PORT",
						Integer.toString(httpsListenerPort));
				System.out.println("AJSC_HTTPS_PORT has been set to: "
						+ System.getProperty("AJSC_HTTPS_PORT"));
			}

			// Setting up the jetty Server by reading the ajsc-jetty.xml
			// configuration file
			XmlConfiguration ajscJettyConfigXml = new XmlConfiguration(
					getAjscJettyConfigXml(AJSC_JETTY_CONFIG_XML_LOC));
			
			int blockingQueueSize = 10;
            int corePoolSize = 100;
            int maxPoolSize = 100;
            int keepAliveTime  = 3000;
           
            if(System.getProperty("AJSC_JETTY_BLOCKING_QUEUE_SIZE") != null){
                           blockingQueueSize = Integer.parseInt(System.getProperty("AJSC_JETTY_BLOCKING_QUEUE_SIZE"));
            }
           
            if(System.getProperty("AJSC_JETTY_ThreadCount_MIN") != null){
                           corePoolSize = Integer.parseInt(System.getProperty("AJSC_JETTY_ThreadCount_MIN"));
            }
           
            if(System.getProperty("AJSC_JETTY_ThreadCount_MAX") != null){
                           maxPoolSize = Integer.parseInt(System.getProperty("AJSC_JETTY_ThreadCount_MAX"));
            }
           
            if(System.getProperty("AJSC_JETTY_IDLETIME_MAX") != null){
                           keepAliveTime = Integer.parseInt(System.getProperty("AJSC_JETTY_IDLETIME_MAX"));
            }
           
            ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(blockingQueueSize);
            QueuedThreadPool pool = new QueuedThreadPool(maxPoolSize,corePoolSize,keepAliveTime,queue);
            //ExecutorThreadPool pool = new ExecutorThreadPool(corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS, queue);
            Server server = new Server(pool);
			ajscJettyConfigXml.configure(server);
			try{
				server.start();
				server.join();
			}finally {
				if(server.isFailed()) {
					try{
						server.stop();
						System.exit(0);
					}catch (Exception stopJettyForException){
						server.destroy();
					}
				}
			}
			//server.start();
			//server.join();
		} catch (Exception ex) {
			System.out.println("ERROR:" + ex.getMessage());
		}
	}

	public static int getIntegerArgument(String name, String[] args) {
		int retInt = -1;
		for (String arg : args) {
			try {
				String[] elems = arg.split("=");
				if (name.equals(elems[0])) {
					retInt = Integer.parseInt(elems[1]);
				}
			} catch (Throwable t) {
				System.out.println("WARN bad argument for " + name
						+ " default value will be used instead!");
			}
		}
		return retInt;
	}

	public static String getStringArgument(String name, String[] args) {

		String retString = null;
		for (String arg : args) {
			try {
				String[] elems = arg.split("=");
				if (name.equals(elems[0])) {
					retString = elems[1];
				}
			} catch (Throwable t) {
				System.out.println("WARN bad argument for " + name
						+ " default value with be used instead!");
			}
		}
		return retString;
	}

	public static int getListenerPort(String[] args) {
		int retPort = (getIntegerArgument("port", args) >= 0) ? getIntegerArgument(
				"port", args) : 0; // else default ephemeral
		return retPort;
	}

	public static int getHttpsListenerPort(String[] args) {
		int retPort = getIntegerArgument("sslport", args);
		return (retPort >= 0) ? retPort : 0; // else default ephemeral
	}

	public static String getAjscWarCtxPath(String[] args) {
		return (getStringArgument("context", args) != null) ? getStringArgument(
				"context", args) : "/ajsc";
	}

	public static Path getAjscWarWarPath(String[] args) throws IOException,
			URISyntaxException {
		return getWarByPrefixPattern("ajsc-war");
	}

	public static Path getWarByPrefixPattern(String pattern)
			throws IOException, URISyntaxException {

		InputStream warResource = null;

		// TODO: fix this to not have hard coded version dependency

		String runnerJarName = null;

		String warFileName = "ajsc-war-0.0.1-SNAPSHOT.war";

		try {

			runnerJarName = new java.io.File(Runner.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath()).getName();
			//The following code parses the version from the runnerJarName which is in the form, "ajsc-runner-5.0.0-SNAPSHOT.jar"
			int ajscVersionIndexStart = 12;
			int ajscVersionIndexEnd = runnerJarName.length() - 4;
			String ajscVersion = runnerJarName.substring(ajscVersionIndexStart, ajscVersionIndexEnd);
			System.setProperty("AJSC_VERSION", ajscVersion);
			
		} catch (Throwable t) {
			t.printStackTrace();
		}

		if (runnerJarName.contains("ajsc-runner")) {
			warFileName = runnerJarName.replace("ajsc-runner", "ajsc-war")
					.replace(".jar", ".war");
		}

		warResource = ClassLoader.getSystemResourceAsStream(warFileName);
		if (warResource == null) {
		 
		warResource =Runner.class.getClassLoader().getResourceAsStream(warFileName);
		}

		File war = createTempFileFromInputStream(pattern, warResource);
		
		//renameTo may suffer from bugs (especially windows).  should include logic to only renameTo if the file !exist
		war.renameTo(new File(System.getProperty("AJSC_HOME") + File.separator + "lib" + File.separator + "ajsc.war"));

		return war.toPath();

	}

	public static Path createTemporaryDirectoryFor(String pattern)
			throws IOException {
		return Files.createTempDirectory(pattern);
	}

	//looking to change this to write to PERMANENT file instead of tmp for tdice/multiple instances
	public static File createTempFileFromInputStream(String pattern,
			InputStream is) throws IOException {

		File dir = new File(System.getProperty("AJSC_HOME") + File.separator
				+ "jetty" + File.separator + "webapps");
		File f = File.createTempFile(pattern, "", dir);
		writeFileFromInputStream(f, is);

		return f;

	}

	public static void writeFileFromInputStream(File destFile, InputStream is)
			throws IOException {

		OutputStream out = new FileOutputStream(destFile);

		byte buf[] = new byte[2048];
		int len;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		is.close();
	}

	private static ArrayList<FileInputStream> getJettyConfigFileList(
			String dirName) throws FileNotFoundException {
		File directory = new File(dirName);

		ArrayList<FileInputStream> fileStreamList = new ArrayList<FileInputStream>();

		class MyFilter implements FileFilter {
			public boolean accept(File file) {
				return (file.isDirectory() || (!file.isHidden() && file
						.getName().endsWith(".xml")));
			}
		}

		File[] fList = directory.listFiles(new MyFilter());

		for (File file : fList) {
			if (file.isFile()) {
				fileStreamList.add(new FileInputStream(file));
			} else if (file.isDirectory()) {
				getJettyConfigFileList(file.getPath());
			}
		}

		return fileStreamList;
	}

	private static FileInputStream getAjscJettyConfigXml(String fileName)
			throws FileNotFoundException {
		File file = new File(fileName);
		FileInputStream ajscFileInputStream = new FileInputStream(file);

		return ajscFileInputStream;
	}

	private static void setURLPatterns() {
		try {
			String ajsc_override_xml = System
					.getProperty("AJSC_HOME") + "/etc/ajsc-override-web.xml";
			
			String camelServletURL = null;
			String restletServletURL = null;

			JAXBContext jc = JAXBContext.newInstance( WebAppType.class );  
			Unmarshaller u = jc.createUnmarshaller();
			u.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
			//u.setSchema( null );
			WebAppType webApp = (WebAppType)u.unmarshal( new FileInputStream(ajsc_override_xml));
			
			List <ServletMappingType> servletMappings = webApp.getServletMapping();
			if  (servletMappings != null || servletMappings.size() != 0 ) {
			
				Iterator it = servletMappings.iterator();
				ServletMappingType servletMapping = null;
			
				while (it.hasNext()) {
					servletMapping = (ServletMappingType) it.next();
				
					if (servletMapping.getServletName().equals("CamelServlet")) {
						camelServletURL = servletMapping.getUrlPattern();
					}
				
					if (servletMapping.getServletName().equals("RestletServlet")) {
						restletServletURL = servletMapping.getUrlPattern();
					}
				}
			}
			
			camelServletURL = removeTrailingSlashAndStar(camelServletURL);
			if (camelServletURL == null || camelServletURL.length() == 0 ) {
				System.setProperty("APP_SERVLET_URL_PATTERN", "");
				System.out.println("APP_SERVLET_URL_PATTERN has been set to: (empty/root), and will be registered as \'/\' with GRM.");
			} else {
				System.setProperty("APP_SERVLET_URL_PATTERN", camelServletURL);
				System.out.println("APP_SERVLET_URL_PATTERN has been set to: "
						+ System.getProperty("APP_SERVLET_URL_PATTERN")
						+ ", and will be registered with GRM.");	
			}

			restletServletURL = removeTrailingSlashAndStar(restletServletURL);
			if (restletServletURL == null || restletServletURL.length() == 0 ) {
				//System.out.println("restletservleturl is null");
				System.setProperty("APP_RESTLET_URL_PATTERN", "");
				System.out.println("APP_RESTLET_URL_PATTERN has been set to: (empty/root), and will be registered as \'/\' with GRM.");

			} else {
				//System.out.println("restletservleturl is "+restletServletURL);
				System.setProperty("APP_RESTLET_URL_PATTERN", restletServletURL );
				System.out.println("APP_RESTLET_URL_PATTERN has been set to: "
						+ System.getProperty("APP_RESTLET_URL_PATTERN")
						+ ", and will be registered with GRM.");
			}
		}
		catch (Exception ex) {
			System.out.println("***** Unmarshall exception *****"+ex.getMessage());
			//something happened with the unmarshalling : either file not present or xml is not well formed
			
			System.setProperty("APP_SERVLET_URL_PATTERN", "/services");
			System.setProperty("APP_RESTLET_URL_PATTERN", "/rest");
		}
		
	}
	private static String removeTrailingSlashAndStar(String str) {
		if (str != null  && str.endsWith("/*")) {
			return str.substring(0,str.length() - 2);
		}
		return str;	
	}
	
	
	private static void scanSystemPropsDir(String dirName, Properties prop) throws IOException {
		File directory = new File(dirName);
		FileInputStream fis = null;
		//read all the files that are ".properties", from the directory (@dirName) 
		//& it's sub-directories iteratively.
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile() && !file.getName().startsWith("template")
					&& file.getPath()
							.endsWith(".properties")) {

				fis = new FileInputStream(file);
				prop.load(fis);
				fis.close();
			} else if (file.isDirectory()) {
				scanSystemPropsDir(file.getPath(), prop);
			}
		}
	}
}