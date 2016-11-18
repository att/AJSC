/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.providers

import java.awt.event.ItemEvent;
import java.util.regex.Pattern
import java.security.MessageDigest

import org.apache.camel.Exchange;

import static groovy.io.FileType.FILES

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware
 

class FilePersistenceClient implements DBAdapter, ApplicationContextAware {

    static Logger logger = LoggerFactory.getLogger(FilePersistenceClient.class)

	def initialized = false
	def started = false
	def schema = ""	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"nimbus",
			"COMPONENT":"FilePersistenceClient",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}

//  This method is taking input parameters ip, port, name, doesn't really need them. 
//	Its just for compatibility with riak init method.
	def init(ip,port,name){
		init()
	}

	def init() {
		def LMETHOD = "init"
		initialized = true
	}
	def ajscHome = System.getProperty("AJSC_HOME") ?: System.getenv("AJSC_HOME")
	def storagePath = ajscHome+"/data/"
	
	def md5 = { text ->
			 new BigInteger(1,MessageDigest.getInstance("MD5").digest(text.getBytes())).toString(16).padLeft(32,"0")}
	
	def mapFilePath = { map ->
		"${storagePath}/${md5 map}"
	}
	
	def valueFilePath = { map, key ->
		"${storagePath}/${md5 map}/${md5 key}".toString()
	}
	
	// Store value to persistent map
	def store(String mapName, String key, String value) {
		//println ">>>>>>>> *FP* store(${mapName},${key},...)"
		def mapDirString = mapFilePath(mapName)
		def mapDir = new File(mapDirString)
		if (!mapDir.exists()) { mapDir.mkdirs()}
		def mapNameFile = new File("${mapDirString}.mapname")
		mapNameFile.write(mapName)
		def valueFileString = valueFilePath(mapName, key)
		def keyFile = new File("${valueFileString}.key")
		keyFile.write(key)
		new File(valueFileString).write(value)
 
	}
	
	
	def storeByteArray(String mapName, String key, byte[] value) {		
		//println ">>>>>>>> *FP* store(${mapName},${key},...)"
		def mapDirString = mapFilePath(mapName)
		def mapDir = new File(mapDirString)
		if (!mapDir.exists()) { mapDir.mkdirs()}
		def mapNameFile = new File("${mapDirString}.mapname")
		mapNameFile.write(mapName)
		def valueFileString = valueFilePath(mapName, key)
		def keyFile = new File("${valueFileString}.key")
		keyFile.write(key)
		new File(valueFileString).withOutputStream{
					it.write value }
	}
	
	// Fetch value from persistent map
	def fetch(String mapName, String key) {
		//println ">>>>>>>> *FP* fetch(${mapName},${key})"
		def valueFile = new File(valueFilePath(mapName, key))
		if (valueFile.exists()) {return valueFile.getText()}
	}
	
	// Fetch value from persistent map
	def fetchByteArray(String mapName, String key) {
		//println ">>>>>>>> *FP* fetchByteArray(${mapName},${key})"
		def valueFile = new File(valueFilePath(mapName, key))
		if (valueFile.exists()) {		
			return new File(valueFilePath(mapName, key)).withInputStream {
					it.getBytes()}
		}
	}
	
	def fetchAllEntries(String mapName) {
		//println ">>>>>>>> *FP*fetchAllValues(${mapName})"
		def mapDirString = mapFilePath(mapName)
		def mapDir = new File(mapDirString)
		
		if (!mapDir.exists()) { return}
		
		def valueList = []
		mapDir.eachFileMatch(~/.*?\.key/) {
			//println ">>>>>>> in ${mapDirString}/${it.name}"
			valueList << new File("${mapDirString}/"+it.name.substring(0,it.name.indexOf('.key'))).getText()
		}
		return valueList
	
	}
 
	def fetchAllByteArrayEntries(String mapName) {
		//println ">>>>>>>> *FP*fetchAllByteArrayValues(${mapName})"

		def mapDirString = mapFilePath(mapName)
		def mapDir = new File(mapDirString)
		
		if (!mapDir.exists()) { return}
		
		Map valueList = new HashMap()
		mapDir.eachFileMatch(~/.*?\.key/) {
			//println ">>>>>>> in ${mapDirString}/${it.name}"
			valueList.put(new File("${mapDirString}/${it.name}").getText(), 
				new File("${mapDirString}/"+it.name.substring(0,it.name.indexOf('.key'))).withInputStream {
					it.getBytes()}
				)
		}
		return valueList
	}
	
	// Delete value from persistent map
	def deleteKey(String mapName, String key) {
		
		def valueFile = new File(valueFilePath(mapName, key))
		if (valueFile.exists()) {valueFile.delete()}
		def keyFile =  new File(valueFilePath(mapName, key)+'.key')
		if (keyFile.exists()) {keyFile.delete()}
		
	}
	
	// Retrieve all keys in a map
	def fetchKeys(String mapName) {
		def mapDirString = mapFilePath(mapName)
		def mapDir = new File(mapDirString)
		
		if (!mapDir.exists()) { mapDir.mkdirs()}
		
		def keyList = []
		mapDir.eachFileMatch(~/.*?\.key/) {
			println ">>>>>>> in ${mapDirString}/${it.name}"
			keyList << new File("${mapDirString}/${it.name}").getText()
		}
		//println ">>>>>>>> keys(${mapName}) == ${keyList}"
		return keyList
	}	
	
	def getMapReduceResult(String mapReduceJobSpec) {
	// This method is currently not implemented by cassandra
	}

	def putMapEntryExtended(String mapName, String mapKey, Object mapValue, String contentType, Map mapUsermetaItems, Map mapUserindexItems, List userlwlinkItems) {
	// This method is currently not implemented by cassandra
	}
	
	def ping() {
		// Nothing to ping here
		return		
	}
	
	def store(String mapName, String key, String value, String schema){
		store(mapName, key, value)		
	}
	
	def storeByteArray(String mapName, String key, byte[] value, String schema){
		storeByteArray(  mapName,   key,   value)
	}
	def fetch(String mapName, String key, String schema){
		fetch(mapName,key)
	}
	def fetchByteArray(String mapName, String key, String schema){
		fetchByteArray(mapName, key)
	}
	def fetchAllEntries(String mapName, String schema){
		fetchAllEntries(  mapName)
	}
	def fetchAllByteArrayEntries(String mapName, String schema){
		fetchAllByteArrayEntries(mapName)
	}
	def deleteKey(String mapName, String key, String schema){
		deleteKey( mapName, key)
	}
	def fetchKeys(String mapName, String schema){
		fetchKeys( mapName)
	}
	def setSchema(String schema){ this.schema = schema }
	def getSchema(){ return this.schema }
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) 	throws BeansException {
		this.applicationContext = applicationContext
	}
}
