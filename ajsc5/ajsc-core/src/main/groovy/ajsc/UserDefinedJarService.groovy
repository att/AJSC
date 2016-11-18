/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc

import ajsc.util.MessageMgr

import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * The UserDefinedJarService is responsible for managing User Defined Jar files on the filesystem.  It is a message listener
 * on the ajsc.UserDefinedJarService.topic topic and performs insert, update, and delete operations based on messages it receives.
 *
 * Creating or Updating a User Defined Jar
 * UserDefinedJarService will take any ajsc.UserDefinedJar object placed on the ajsc.UserDefinedJarService.topic and write 
 * it to the filesystem if it doesn't already exist, or update it if it does.  So, to have UserDefinedJarService store a jar 
 * file on the filesystem, simply create a ajsc.UserDefinedJar object and put it on the ajsc.UserDefinedJarService.topic.
 *
 * Deleting a User Defined Jar
 * To delete a jar file from the filesystem, put a String of the following format on the ajsc.UserDefinedJarService.topic:
 *
 * delete <namespace>/<jarVersion>/<jarName>
 */
class UserDefinedJarService {
	
	def deferoService
	def static NAMESPACE = "ajsc"
	def static SERVICENAME = "UserDefinedJarService"
	def static TOPIC_NAME = NAMESPACE+"."+SERVICENAME+".topic"
	def static ADMIN_TOPIC = NAMESPACE+".admin.topic"
	def static DEFAULT_JAR_FS_ROOT = "${System.getenv('AJSC_HOME') ?: System.getProperty('AJSC_HOME')}"
	def static AJSC_LIB = File.separator+"lib"+File.separator


	def started = false
	def initialized = false
	def registeredListener=false
	
	static transactional = true
	static expose = ['jmx']
	
	final static Logger logger = LoggerFactory.getLogger(UserDefinedJarService.class)
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"UserDefinedJarService",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}


	
	def start = {
		def LMETHOD = "start"
		started=true
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1))
	}

	def stop = {
		def LMETHOD = "stop"
		if(started) {
			//println "Stopping UserDefinedJarService"
			started=false
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1))
		}
	}

	def init() {
		start()
		def LMETHOD = "init"

		//println "Initializing UserDefinedJarService"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1))
		
		if (!DEFAULT_JAR_FS_ROOT) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,2))
		} else {
			
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,3))
				
			// Create Directory for UserDefinedJars
			def dir = "${DEFAULT_JAR_FS_ROOT}${AJSC_LIB}"
			if (createDir(dir)) {			
				// Get UserDefinedJars and write to filesystem
				def userDefinedJarList = UserDefinedJar.list()
				if (!userDefinedJarList) {
					MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,4))
					
				} else {
					userDefinedJarList.each{
						insertOrUpdateUserDefinedJar(it)
					}
					MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,5))
					
				}
				initialized=true
				println "UserDefinedJarService initialized successfully"
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,6))
				
			} else {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,7),["${dir}"] as Object[])
				//println "UserDefinedJarService failed to initialize, could not create user defined jar directory: ${dir}"
			}

		}
	}
	
	def shutdown = {
		stop()
	}

	def listUserDefinedJars() {
		def userDefinedJarDir = "${DEFAULT_JAR_FS_ROOT}${AJSC_LIB}"
		return listUserDefinedJars(userDefinedJarDir)
	}
	
	def listUserDefinedJars(userDefinedJarDir) {
		def dir = new File(userDefinedJarDir)
		def fList = new ArrayList()
		try{
		dir.eachFile { f->
			if (f.isFile()) {
				//println "listUserDefinedJars ${f.canonicalPath}"
				fList.add(f.canonicalPath)
			}
		}
		}catch(eatme){}
		return fList
	}
	
	/**
	 * Creates or updates a jar file.  Files are created or updated at ${AJSC_HOME}/lib.
	 * @param userDefinedJar
	 * @return
	 */
	def insertOrUpdateUserDefinedJar(userDefinedJar) {
		
		def LMETHOD = "insertOrUpdateUserDefinedJar(userDefinedJar)"
		def baseDir = "${DEFAULT_JAR_FS_ROOT}${AJSC_LIB}"
		def userDefinedJarDir = buildDirName(userDefinedJar)
		def fileName = userDefinedJar.jarName
		def fileNameAndPath = "${baseDir}${userDefinedJarDir}${fileName}"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),
			["${baseDir}","${userDefinedJarDir}","${fileName}","${fileNameAndPath}"] as Object[])
		
		// Delete if exists
		deleteUserDefinedJar("${userDefinedJar.generateId()}")
		
		// Create the directory
		createDir("${baseDir}${userDefinedJarDir}")
		
		// Add new
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${fileNameAndPath}"] as Object[])
	
		def jarFile = new File(fileNameAndPath)
		jarFile.withOutputStream {
			it.write(userDefinedJar.jarContent)
		} 	
	} 
	
	/**
	 * Delete a user define jar file from the filesystem.  jarName should be of the form: <namespace>:<jarVersion>:<jarName>.
	 * @param jarName
	 * @return
	 */
	def deleteUserDefinedJar(jarId) {
		def LMETHOD = "deleteUserDefinedJar(jarId)"
		def baseDir = "${DEFAULT_JAR_FS_ROOT}${AJSC_LIB}"
		def elements = UserDefinedJar.parseUserDefinedJarId(jarId)
		def jarName = "${elements['jarName']}"
		def namespace = elements['namespace']
		def jarVersion = elements['jarVersion']
		def fileNameAndPath = "${baseDir}${namespace}"+File.separator+"${jarVersion}"+File.separator+"${jarName}" 
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${fileNameAndPath}"] as Object[])
		boolean fileSuccessfullyDeleted = new File("${fileNameAndPath}").delete()  
	}
	
	def buildDirName(userDefinedJar) {
		return "${userDefinedJar.namespace}"+File.separator+"${userDefinedJar.jarVersion}"+File.separator
	}
	
	/**
	 * Uses File.mkdirs to create the given directory if it does not already exist.
	 * @param dir
	 * @return
	 */
	def createDir(dir) {

		def returnVal = true
		def LMETHOD = "createDir(dir)"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),
			["${dir}"] as Object[])
			try {
			def dirFile = new File(dir)
			if (!dirFile.exists()) {
				returnVal = dirFile.mkdirs()
				// terry's hack 101
				try{new File(dir).mkdir}catch(eatit){}
				try{new File(dir).mkdirs}catch(eatit){}
			} else {
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),
				["${dir}"] as Object[])
						}
		} catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,3),all,["${dir}"] as Object[])
		}
		if (!returnVal) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,4),["${dir}"] as Object[])
		}
		return returnVal
	}
}

