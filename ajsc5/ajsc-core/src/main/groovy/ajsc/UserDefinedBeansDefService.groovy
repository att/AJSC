/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ajsc.util.MessageMgr


/**
 * The UserDefinedBeansDefService is responsible for managing User Defined Beans Definition files on the filesystem.
 * It is a message listener on the ajsc.UserDefinedBeansDefService.topic topic and performs insert, update,
 * and delete operations based on messages it receives.
 *
 * Creating or Updating a User Defined Beans Definition
 * UserDefinedBeansDefService will take any ajsc.UserDefinedBeansDef object placed on the ajsc.UserDefinedBeansDefService.topic
 * and write it to the filesystem if it doesn't already exist, or update it if it does.  So, to have
 * UserDefinedBeansDefService store a Beans Definition file on the filesystem, simply create a
 * ajsc.UserDefinedBeansDef object and put it on the ajsc.UserDefinedBeansDefService.topic.
 *
 * Deleting a User Defined Beans Definition
 * To delete a Beans Definition file from the filesystem, put a String of the following format on the
 * ajsc.UserDefinedBeansDefService.topic:
 *
 * delete <namespace>/<beansDefVersion>/<beansDefName>
 */
class UserDefinedBeansDefService {
	def deferoService
	def static NAMESPACE = "ajsc"
	def static SERVICENAME = "UserDefinedBeansDefService"
	def static TOPIC_NAME = NAMESPACE+"."+SERVICENAME+".topic"
	def static ADMIN_TOPIC = NAMESPACE+".admin.topic"
	def static DEFAULT_BEANS_DEF_FS_ROOT = "${System.getenv('AJSC_HOME') ?: System.getProperty('AJSC_HOME')}"
	def static AJSC_CONF = File.separator+"conf"+File.separator


	def started = false
	def initialized = false
	def registeredListener=false
	
	static transactional = true
	static expose = ['jmx']
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"UserDefinedBeansDefService",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}
	
	final static Logger logger = LoggerFactory.getLogger(UserDefinedBeansDefService.class)

	
	def start = {
		def LMETHOD = "start"
		started=true
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1))
		}

	def stop = {
		
		def LMETHOD = "stop"
		if(started) {
			println "Stopping UserDefinedBeansDefService"
			started=false
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1))
			}
	}

	def init() {
		start()
		def LMETHOD = "init"

		MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,1))

		if (!DEFAULT_BEANS_DEF_FS_ROOT) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,2))
		} else {
			MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,3))

			// Create Directory for UserDefinedBeansDefs
			def dir = "${DEFAULT_BEANS_DEF_FS_ROOT}${AJSC_CONF}"
			if (createDir(dir)) {
				// Get UserDefinedBeansDefs and write to filesystem
				def userDefinedBeansDefList = UserDefinedBeansDef.list()
				if (!userDefinedBeansDefList) {
					MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,4))
				} else {
					userDefinedBeansDefList.each{ insertOrUpdateUserDefinedBeansDef(it) }
					MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,5))

				}
				initialized=true
				println "UserDefinedBeansDefService initialized successfully"
				MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,6))

			} else {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,7),["${dir}"] as Object[])
				//println "UserDefinedBeansDefService failed to initialize, could not create user defined beansDef directory: ${dir}"
			}

		}
	}
	
	def shutdown = {
		stop()
	}

	def listUserDefinedBeansDefs() {
		def userDefinedBeansDefDir = "${DEFAULT_BEANS_DEF_FS_ROOT}${AJSC_CONF}"
		return listUserDefinedBeansDefs(userDefinedBeansDefDir)
	}
	
	def listUserDefinedBeansDefs(userDefinedBeansDefDir) {
		def dir = new File(userDefinedBeansDefDir)
		def fList = new ArrayList()
		try{
		dir.eachFile { f->
			if (f.isFile()) {
				fList.add(f.canonicalPath)
			}
		}
		}catch(eatme){}
		return fList
	}
	
	/**
	 * Creates or updates a beansDef file.  Files are created or updated at ${AJSC_HOME}/lib.
	 * @param userDefinedBeansDef
	 * @return
	 */
	def insertOrUpdateUserDefinedBeansDef(userDefinedBeansDef) {
		def LMETHOD = "insertOrUpdateUserDefinedBeansDef(userDefinedBeansDef)"
		
		def baseDir = "${DEFAULT_BEANS_DEF_FS_ROOT}${AJSC_CONF}"
		def userDefinedBeansDefDir = buildDirName(userDefinedBeansDef)
		//def fileName = "${userDefinedBeansDef.beansDefName}${!userDefinedBeansDef.beansDefName.endsWith('.groovy') ? '.groovy' : ''}"
		def fileName = "${userDefinedBeansDef.beansDefName}"
		def fileNameAndPath = "${baseDir}${userDefinedBeansDefDir}${fileName}"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${baseDir}","${userDefinedBeansDefDir}","${fileName}","${fileNameAndPath}"] as Object[])
		
		// Delete if exists
		deleteUserDefinedBeansDef("${userDefinedBeansDef.generateId()}")
		
		// Create the directory
		createDir("${baseDir}${userDefinedBeansDefDir}")
		
		// Add new
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${fileNameAndPath}"] as Object[])
		def beansDefFile = new File(fileNameAndPath)
		beansDefFile.withWriter() {
			it << userDefinedBeansDef.beansDefContent
		}
	}
	
	/**
	 * Delete a user define beansDef file from the filesystem.  beansDefName should be of the form: <namespace>:<beansDefVersion>:<beansDefName>.
	 * @param beansDefName
	 * @return
	 */
	def deleteUserDefinedBeansDef(beansDefId) {
		//println "UserDefinedBeansDefService.deleteUserDefinedBeansDef beansDefId: ${beansDefId}"
		def LMETHOD = "deleteUserDefinedBeansDef(beansDefId)"
		def baseDir = "${DEFAULT_BEANS_DEF_FS_ROOT}${AJSC_CONF}"
		def elements = UserDefinedBeansDef.parseUserDefinedBeansDefId(beansDefId)
		//println "elements: ${elements}"
		def beansDefName = "${elements['beansDefName']}${!elements['beansDefName'].endsWith('.groovy') ? '.groovy' : ''}"
		def namespace = elements['namespace']
		def beansDefVersion = elements['beansDefVersion']
		def fileNameAndPath = "${baseDir}${namespace}/${beansDefVersion}/${beansDefName}"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${fileNameAndPath}"] as Object[])
		boolean fileSuccessfullyDeleted = new File(fileNameAndPath).delete()
	}
	
	def buildDirName(userDefinedBeansDef) {
		return "${userDefinedBeansDef.namespace}"+File.separator+"${userDefinedBeansDef.beansDefVersion}"+File.separator
	}
	
	/**
	 * Uses File.mkdirs to create the given directory if it does not already exist.
	 * @param dir
	 * @return
	 */
	def createDir(dir) {

		def returnVal = true
		def LMETHOD = "createDir(dir)"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${dir}"] as Object[])
		
			try {
			def dirFile = new File(dir)
			if (!dirFile.exists()) {
				returnVal = dirFile.mkdirs()
				// terry's hack 101
				try{new File(dir).mkdir}catch(eatit){}
				try{new File(dir).mkdirs}catch(eatit){}
			} else {
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${dir}"] as Object[])
			
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

