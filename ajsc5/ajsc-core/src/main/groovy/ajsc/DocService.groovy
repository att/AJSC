/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc


//Logging
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ajsc.util.MessageMgr

/**
 * The DocService is responsible for managing Ajsc document files on the filesystem.  It is a message listener
 * on the ajsc.DocService.topic topic and performs insert, update, and delete operations based on messages it receives.
 *
 * Creating or Updating a Document
 * DocService will take any ajsc.Doc object placed on the ajsc.DocService.topic and write it to the filesystem if it doesn't already exist,
 * or update it if it does.  So, to have DocService store a document file on the filesystem, simply create a ajsc.Doc object and put it
 * on the ajsc.DocService.topic.
 *
 * Deleting a Document
 * To delete a document file from the filesystem, put a String of the following format on the ajsc.DocService.topic:
 *
 * delete <namespace>/<docVersion>/<docName>
 */
class DocService {
	
	def deferoService
	def static NAMESPACE = "ajsc"
	def static SERVICENAME = "DocService"
	def static TOPIC_NAME = NAMESPACE+"."+SERVICENAME+".topic"
	def static ADMIN_TOPIC = NAMESPACE+".admin.topic"
	def static DEFAULT_DOC_FS_ROOT = System.getenv('AJSC_HOME') ?: System.getProperty('AJSC_HOME')
	def static AJSC_DOCS = "/docs/"
	final static Logger logger = LoggerFactory.getLogger(DocService.class)

	def static props
	def started = false
	def initialized = false
	def registeredListener=false
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"DocService",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}
	
	static transactional = true
	static expose = ['jmx']

	
	def start = {
		def LMETHOD = "start"
		started=true

		MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,1))
	}

	def stop = {
		def LMETHOD = "stop"
		if(started) {
			started=false

			MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,1))
		}
	}

	def init() {
		start()
		def LMETHOD = "init()"

		MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,1))
		
		try {
			MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,2))
			
			def myProps = null
			
			MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,3),["${SERVICENAME}"] as Object[])
			
			def propsList = ServiceProperties.findAllByNamespaceAndServiceNameAndServiceVersion(NAMESPACE, SERVICENAME, "0")
			if (propsList) {
				if (isValidProps(propsList[0])) {
					myProps = propsList[0]

					MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,4),["${myProps}"] as Object[])
				}
			}
			if(!myProps){
				// attempt to auto configure
				myProps = new ServiceProperties(serviceName:SERVICENAME, serviceVersion:'0')
				
				["fileSystemRoot":DEFAULT_DOC_FS_ROOT].each { key,value ->
							def prop = new ServiceProperty(name:key, value:value)
							myProps.addToProps(prop)
				}
				myProps.saveWithNotify()
				
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,5),["${myProps}"] as Object[])
			}
				myProps.props.each { 
					MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,6),["${it.name}","${it.value}"] as Object[])
				}
				
			props = myProps.toProperties()
			
			MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,7),["${props}"] as Object[])
		}
		catch (all) {
		   MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,8),all)
		}
		
		MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,9))
		
		// Create Directory for Docs
		def dir = "${props.getProperty('fileSystemRoot')}${AJSC_DOCS}"
		if (createDir(dir)) {			
			// Get Docs and write to filesystem
			def docList = Doc.list()
			if (!docList) {
				MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,10))
			} else {
				docList.each{
					insertOrUpdateDoc(it)
				}
				MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,11))
			}
			initialized=true
			println "DocService initialized successfully"
			MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,12))
		} else {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,13),["${dir}"] as Object[])
		}
		
	}
	
	def isValidProps(serviceProperties) {
		def propNames = serviceProperties?.props*.name
		return ["fileSystemRoot"] == propNames
	}

	def shutdown = {
		stop()
	}

	def listDocs() {
		def docDir = "${props.getProperty('fileSystemRoot')}${AJSC_DOCS}"
		return listDocs(docDir)
	}
	
	def listDocs(docDir) {
		def dir = new File(docDir)
		def fList = new ArrayList()
		try{
		dir.eachFileRecurse { f->
			if (f.isFile()) {
				fList.add(f.canonicalPath)
			}
		}
		}catch(eatme){}
		return fList
	}
	
	/**
	 * Creates or updates a document file.  Files are created or updated at ${fileSystemRoot}/ajsc/docs/<namespace>/<docVersion>/<docName>.
	 * @param doc
	 * @return
	 */
	def insertOrUpdateDoc(doc) {
		def LMETHOD = "insertOrUpdateDoc(doc)"
		
		if (!started) {
			init()
		}

		def baseDir = "${props.getProperty('fileSystemRoot')}${AJSC_DOCS}"
		def docDir = buildDirName(doc)
		def fileName = doc.docName
		def fileNameAndPath = "${baseDir}${docDir}${fileName}"
		
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${baseDir}","${docDir}","${fileName}","${fileNameAndPath}"] as Object[])
			
		// Delete if exists
		deleteDoc("${docDir}${fileName}")
		
		// Create the directory
		createDir("${baseDir}${docDir}")
		
		// Add new	
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${fileNameAndPath}"] as Object[])
			
		def bwtr = new BufferedWriter(new FileWriter(fileNameAndPath))
		bwtr << doc.docContent
		bwtr.flush()
		bwtr.close()
	} 
	
	/**
	 * Delete a document file from the filesystem.  docName should be of the form: <namespace>/<docVersion>/<docName>.
	 * @param docName
	 * @return
	 */
	def deleteDoc(docName) {
		def LMETHOD = "deleteDoc(docName)"

		def baseDir = "${props.getProperty('fileSystemRoot')}${AJSC_DOCS}"

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${baseDir}","${docName}"] as Object[])
			
		boolean fileSuccessfullyDeleted = new File("${baseDir}${docName}").delete()  
	}
	
	def buildDirName(doc) {
		return "${doc.namespace}/${doc.docVersion}/"
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
			}
			
			// terry's hack 101
			try{new File(dir).mkdir}catch(eatit){}
			try{new File(dir).mkdirs}catch(eatit){}
					
		} catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,2),all,["${dir}"] as Object[])
		}
		if (!returnVal) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,3),["${dir}"] as Object[])
		}
		return returnVal
	}
}

