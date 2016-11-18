/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc


import java.awt.event.ItemEvent;
import java.util.regex.Pattern
import java.security.MessageDigest
import org.apache.camel.Exchange;
import static groovy.io.FileType.FILES
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ajsc.util.MessageMgr

class FilePersistenceService {
	final static Logger logger = LoggerFactory.getLogger(FilePersistenceService.class)

	def initialized = true
	def started = false
	
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"FilePersistenceService",
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

	def start() {
		started = true
	}

	def stop() {
		started = false
	}

	def shutdown() {
		if (initialized) {
			initialized = false
		}
		
	}

	/**
	 * Return the keys from the File System map specified by mapName
	 * <p>
	 * @param mapName
	 *
	 * @return list of keys
	 */
	// TODO
	public List getMapKeys(String mapName) {
		return keys(mapName)

	}

	/**
	 * Store the given Object value in the File System map
	 * specified by mapName with key specified by mapKey
	 * <p>
	 * @param mapName
	 * @param mapKey
	 * @param mapValue
	 */
	public void putMapEntry(String mapName, String mapKey, String mapValue) {
		def LMETHOD = "putMapEntry(String mapName, String mapKey, String mapValue)"
		
		if (!initialized) {
		MessageMgr.logMessage(logger,'warn',getMessageMap(LMETHOD,1),["${LMETHOD}"] as Object[])
		} else {
			try {
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${mapName}","${mapKey}"] as Object[])
				store(mapName, mapKey, mapValue);
			} catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,3),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])
				throw all
			}
		}
	}
	


	/**
	 * Retrieve the entry from the File System  map specified by mapName 
	 * with key specified by mapKey and return it as a JSON String
	 * <p>
	 * @param http header names MAP_NAME
	 * @param http header names MAP_KEY
	 *
	 * @see org.apache.camel.Exchange
	 */
	public String getMapEntry(String mapName, String mapKey) {
		def res = null
		def LMETHOD = "getMapEntry(String mapName, String mapKey)"
		if (!initialized) {
			MessageMgr.logMessage(logger,'warn',getMessageMap(LMETHOD,1),["${LMETHOD}"] as Object[])
			
		} else {
			try {
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${mapName}","${mapKey}"] as Object[])
				res = fetch(mapName, mapKey)
				if (res != null) {
					MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,3),["${mapName}","${mapKey}"] as Object[])
					
				} else {
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,4),["${mapName}","${mapKey}"] as Object[])
				
				}
			} catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,5),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])
				throw all
			}
		}
		return res
	}

	/**
	 * Store the given byte array object value in the File System e map
	 * specified by mapName with key specified by mapKey
	 * <p>
	 * @param mapName
	 * @param mapKey
	 * @param mapValue
	 */
	public void putByteArrayMapEntry(String mapName, String mapKey, byte[] mapValue) {
		
		def LMETHOD = "putByteArrayMapEntry(String mapName, String mapKey, byte[] mapValue)"
		
		if (!initialized) {
			MessageMgr.logMessage(logger,'warn',getMessageMap(LMETHOD,1),["${LMETHOD}",] as Object[])
			
		} else {
			try {
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${mapName}","${mapKey}"] as Object[])
				
				store(mapName, mapKey, mapValue);
			} catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,3),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])
				throw all
			}
			
		}
	}

	/**
	 * Retrieve the entry from the File System  map specified by mapName 
	 * with key specified by mapKey and return it as a byte array
	 * <p>
	 * @param http header names MAP_NAME
	 * @param http header names MAP_KEY
	 *
	 * @see org.apache.camel.Exchange
	 */
	public byte[] getByteArrayMapEntry(String mapName, String mapKey) {
		def res = null
		def LMETHOD = "getByteArrayMapEntry(String mapName, String mapKey)"
		
		if (!initialized) {
			MessageMgr.logMessage(logger,'warn',getMessageMap(LMETHOD,1),["${LMETHOD}",] as Object[])
		} else {
			try {
				res = fetchByteArray(mapName, mapKey)
				if (res != null) {
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${mapName}","${mapKey}"] as Object[])
				} else {
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,3),["${mapName}","${mapKey}"] as Object[])
				}
			} catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,4),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])				
				throw all
			}
		}
		return res
	}

	/**
	 * Remove the entry from the File System map specified by mapName 
	 * with key specified by mapKey
	 * <p>
	 * @param http header names MAP_KEY
	 * @param http header names MAP_NAME
	 *
	 * @see org.apache.camel.Exchange
	 */
	public void removeMapEntry(String mapName, String mapKey) {
		def LMETHOD = "removeMapEntry(String mapName, String mapKey)"
		
		if (!initialized) {
			MessageMgr.logMessage(logger,'warn',getMessageMap(LMETHOD,1),["${LMETHOD}",] as Object[])
		} else {
			try {
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${mapName}","${mapKey}"] as Object[])
				delete(mapName, mapKey)
			} catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,3),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])
				throw all
			}
		}
	}	

	public List getAllEntries(String mapName) {
		return fetchAllValues(mapName)
		}
	
	public Map getAllByteArrayEntriesMap(String mapName) {
		return fetchAllByteArrayValues(mapName)
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
	void store(String mapName, String key, def value) {
		//println ">>>>>>>> *FP* store(${mapName},${key},...)"
		def mapDirString = mapFilePath(mapName)
		def mapDir = new File(mapDirString)
		if (!mapDir.exists()) { mapDir.mkdirs()}
		def mapNameFile = new File("${mapDirString}.mapname")
		mapNameFile.write(mapName)
		def valueFileString = valueFilePath(mapName, key)
		def keyFile = new File("${valueFileString}.key")
		keyFile.write(key)

		if (value instanceof String) {
			new File(valueFileString).write(value)
		} else {
			new File(valueFileString).withOutputStream{
					it.write value }
		}
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
	
	def fetchAllValues(mapName) {
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
 
	def fetchAllByteArrayValues(mapName) {
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
	void delete(String mapName, String key) {
		
		def valueFile = new File(valueFilePath(mapName, key))
		if (valueFile.exists()) {valueFile.delete()}
		def keyFile =  new File(valueFilePath(mapName, key)+'.key')
		if (keyFile.exists()) {keyFile.delete()}
		
	}
	
	// Retrieve all keys in a map
	def keys(String mapName) {
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
}
