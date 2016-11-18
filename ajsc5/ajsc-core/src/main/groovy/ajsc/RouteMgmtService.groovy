/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc

import java.io.File;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import ajsc.util.MessageMgr
import ajsc.util.ClassLoaderUtil

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.ContextLoader

import com.att.ajsc.filemonitor.AJSCPropertiesMap;
import com.att.ajsc.csi.restmethodmap.RestMethodMapInterceptor;

class RouteMgmtService {
	final static Logger logger = LoggerFactory.getLogger(RouteMgmtService.class)

	private static final String EXTERNAL_JARS_FILE = "externalJars.properties";
	private static final String FILE_SEPARATOR = "\\|";
	private static final String EXT_LIB_KEY = "AJSC_EXTERNAL_LIB_FOLDERS"
	private static final String EXT_PROPERTIES_KEY ="AJSC_EXTERNAL_PROPERTIES_FOLDERS"

	def ajscHome = System.getProperty("AJSC_HOME") ?: System.getenv("AJSC_HOME")
	def ajscVersion = System.getProperty("AJSC_VERSION")

	static PropertiesService propertiesService
	def propertyServiceInitialized = false

	static DocService docService
	static UserDefinedBeansDefService userDefinedBeansDefService
	static UserDefinedJarService userDefinedJarService
	static ComputeService computeService
	static LoggingConfigurationService loggingConfigurationService
	static VandelayService vandelayService
	def vandelayServiceInitialized = false
	//jmsConnectionFactory is no longer needed in the RouteMgmtService. Therefore commenting it out.
	//static com.tibco.tibjms.TibjmsQueueConnectionFactory jmsConnectionFactory; //This is for TibcoMQ
	//static com.ibm.mq.jms.MQQueueConnectionFactory jmsConnectionFactory // This is for WMQ
	//static org.apache.activemq.ActiveMQConnectionFactory jmsConnectionFactory;  This is for ActiveMQ
	static FilePersistenceService persistenceService


	def persistenceServiceInitialized = false
	boolean bOSGIEnable=false;
	String osgiEnable=System.getProperty('isOSGIEnable');


	ClassLoaderUtil clu = new ClassLoaderUtil();


	String csilog = System.getProperty('csilog') ?: System.getenv("csilog")


	//Default constructor
	RouteMgmtService() {

		if (osgiEnable != null)
			bOSGIEnable = Boolean.parseBoolean(osgiEnable.trim());
		println "isosgiEnable= " + osgiEnable


		synchronized(this)  {
			//println "\n******* RouteMgmtService Constructor ****-*** \n"

			if (!bOSGIEnable)
			{
				addExternalJarsToContextLoader(clu);
			}
		}
	}


	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"RouteMgmtService",
			"METHOD":method,
			"MSGNUM":msgnum
		]
	}

	static boolean initCalled = false

	def init() {
		def LMETHOD = "init"
		if (initCalled) return
			initCalled = true


		synchronized(this)  {
			//			addExternalJarsToContextLoader(clu)
			stagedDeployRuntime()
			stagedDeployServices()

		}

		if (!propertyServiceInitialized) {
			try { propertiesService.init() } 			// CRITICAL
			catch(Exception prpsSvcExcept) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,7),["${prpsSvcExcept}"] as Object[])
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,8))
				throw new Error("propertiesService init failed in bootstrap!", prpsSvcExcept)
			}
		}
		try { docService.init()	}	  				// CRITICAL
		catch(Exception docSvcExcept) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,11),["${docSvcExcept}"] as Object[])
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,12))
			throw new Error("docService init failed in bootstrap!", docSvcExcept)
		}
		try { userDefinedJarService.init() }		// CRITICAL
		catch(Exception udjsExcept) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,13),["${udjsExcept}"] as Object[])
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,14))
			throw new Error("userDefinedJarService init failed in bootstrap!",udjsExcept)
		}
		try { userDefinedBeansDefService.init() }	// CRITICAL
		catch(Exception udbdsExcept) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,15),["${udbdsExcept}"] as Object[])
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,16))
			throw new Error("userDefinedBeansDefService init failed in bootstrap!",udbdsExcept)
		}

		// Load  jars from lib folder
		synchronized(this)  {
			if (!bOSGIEnable) clu.addJars("${ajscHome}/lib",Thread.currentThread().getContextClassLoader())
		}

		try {
			RestMethodMapInterceptor.getInstance();
		}
		catch(Exception except) {
			System.out.println(" RestMethodMapInterceptor failed to initialize ${except.getMessage()}")

		}


		try { computeService.init() }	// CRITICAL
		catch(Exception udbdsExcept) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,15),["${udbdsExcept}"] as Object[])
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,16))
			throw new Error("computeService init failed in bootstrap!",udbdsExcept)
		}

		
		//TODO: We need to find a way to add the ajsc version to aosc.  Within ajsc, we are using the ajsc-runner on 
		// the classpath. This doesn't quite work the same way for aosc.  So, we need to find ajsc-core or read from 
		//the aosc-features.xml to find the version and set system property.  For, now, modifying the logo to NOT 
		//print the version as it will always be "null" for the ajsc version within aosc.
		if (!bOSGIEnable){
			displayLogo()
		}else{
			displayLogoAosc()
		}
	}

	def displayLogo = {println """
\t   ___    ____________________
\t  / . \\  /__  __/ ____// ____/
\t / /_\\ \\___/ / (____ )/ /___
\t/_/   \\/____/ /_____/(_____/
\tVersion: """ + ajscVersion + """
\tAT&T's Java Services Container has initialized successfully and services are now available\n"""}

	def displayLogoAosc = {println """
\t   ___    ____________________
\t  / . \\  /__  __/ ____// ____/
\t / /_\\ \\___/ / (____ )/ /___
\t/_/   \\/____/ /_____/(_____/
\tAT&T's Java Services Container has initialized successfully and services are now available\n"""}

	def start() {
		started = true
	}

	def stop() {
		started = false
	}

	@PreDestroy
	def shutdown() {
	}

	def stagedDeployRuntime() {

		def LMETHOD = "stagedDeployRuntime()"
		try{
			def runTimeDir = new File(ajscHome+"/runtime/")
			if (runTimeDir.exists()) {
				def runTimeFile = new File(ajscHome+"/runtime/"+"runtimeEnvironment.zip")
				if (runTimeFile.exists()) {
					try { propertiesService.init() }
					catch(Exception prpsSvcExcept) {
						MessageMgr.logMessage(logger,'error',getMessageMap("init",7),["${prpsSvcExcept}"] as Object[])
						MessageMgr.logMessage(logger,'error',getMessageMap("init",8))
						throw new Error("propertiesService init failed in bootstrap!", prpsSvcExcept)
					}
					propertyServiceInitialized = true
					vandelayService.processImportRuntime(new FileInputStream(runTimeFile))
					archiveFiles(runTimeDir,"runtimeEnvironment.zip")
				}
			}
		}
		catch (all){
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1))
			throw all
		}
		return
	}

	def archiveFiles(File directory, fileName) {

		def LMETHOD = "archiveFiles(File directory)"
		def zipFile
		def archiveDir = new File(directory.getPath()+File.separator +"deployed"+File.separator)

		if(fileName.equals("runtimeEnvironment.zip"))
			zipFile = new File(ajscHome+File.separator+"runtime"+File.separator+fileName);
		else
			zipFile = new File(ajscHome+File.separator +"services"+File.separator+fileName);

		if (!archiveDir.exists()) { archiveDir.mkdirs()}
		try{
			println archiveDir.getPath()+File.separator
			//println "${fileName}.${new Date().format('yyyy-MM-dd HH.mm.ss.SSS')}.deployed"
			boolean a = zipFile.renameTo( new File( archiveDir.getPath()+File.separator+"${fileName}.${new Date()}.deployed" )	)
		}
		catch(all){
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1))
			throw all
		}
	}

	def stagedDeployServices() {

		def LMETHOD = "stagedDeployServices()"
		def servicesDir = new File(ajscHome+"/services/")
		def contextValue
		def fileName
		try{
			//println "**** Staged Deploy Services ********"
			if (servicesDir.exists()) {

				if(servicesDir.isDirectory()){
					servicesDir.eachFileMatch(~/.*?\.zip/){

						if(!it.isDirectory()){

							def servicesFile = new File(ajscHome+"/services/"+it.getName());
							fileName = it.getName();
							def updateOpts = [:]

							updateOpts.makeActive 		= true;

							updateOpts.makeInactive		= false;

							updateOpts.makeExclusive	= true;

							updateOpts.atomicUpdate =  true

							def value = vandelayService.processImportServices(new FileInputStream(servicesFile),null,updateOpts,"stageDeployment")

							contextValue = value['contextAvail'];

						}
						if(contextValue.equals("YES")){

							archiveFiles(servicesDir,fileName)
						}
						else
							MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,2))

					}
				}

			}
		}
		catch(all){

			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1))

			throw all
		}
		return
	}

	def addExternalJarsToContextLoader(ClassLoaderUtil clu ) {

		//println "addExternalJarsToContextLoader"
		// Add AJSC external Jars first
		clu.addJars("${ajscHome}/extJars",Thread.currentThread().getContextClassLoader())

		try {

			String value = System.getProperty(EXT_LIB_KEY);
			if (value!= null) {
				//println "EXT_LIB_KEY${value}"
				String[] valueArray = value.split(FILE_SEPARATOR);
				for (String val : valueArray) {
					println "parsed string val ${val}"
					if (val != null) {
						clu.addJars(val,Thread.currentThread().getContextClassLoader())
					}
				}
			}
			value = System.getProperty(EXT_PROPERTIES_KEY);
			if (value!= null) {
				//println "EXT_PROPERTIES_KEY${value}"
				String[] valueArray = value.split(FILE_SEPARATOR);
				for (String val : valueArray) {
					//println "EXT_PROPERTIES_KEY${val}"
					if (val != null) {
						clu.addProperties(val,Thread.currentThread().getContextClassLoader())
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

		URL[] urls = clu.getURLsFromTheCurrentThreadLoader(Thread.currentThread().getContextClassLoader())

	}
}
