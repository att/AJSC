/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc

import java.awt.event.ItemEvent;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern

import org.apache.camel.Exchange;
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ajsc.providers.DBAdapter
import ajsc.util.MessageMgr


class ClientDataService implements ApplicationContextAware{
	// deferoService dependency - needed to be strong-typed since we have
	// a setter for testing support...
//	DeferoService deferoService

	def static NAMESPACE = "ajsc"
	def static SERVICENAME = "ClientDataService"
	def applicationContext

	final static Logger logger = LoggerFactory.getLogger(ClientDataService.class)

	static transactional = false

	def initialized = false
	def started = false
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"ClientDataService",
			"METHOD":method,
			"MSGNUM":msgnum
		]
	}
	def nodeIP
	def nodePort 
	def dbAdapter
	def className
 	
	
	def init(DBAdapter adapter) {
		def LMETHOD = "init"
		
		if (!initialized) {
			
			try {
					dbAdapter = adapter
					dbAdapter.init()
					initialized = true
					println "Database provider  initialized successfully"
			}
			catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${all.getMessage()}"] as Object[])
				throw all
				//initialized = true
				//  Just continue even if couldn't connect to Cassandra successfully

			}
		}
		
	}
	def init(){
		def LMETHOD = "init"
		
		if (!initialized) {
			
			try {					
					dbAdapter = applicationContext.getBean("clientDataAdapter")
					dbAdapter.init()
					initialized = true
					println "Database provider  initialized successfully"
			} 
			catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${all.getMessage()}"] as Object[])
				throw all
				//initialized = true
				//  Just continue even if couldn't connect to Cassandra successfully

			}
		}
	}

	def init(ip,port,name) {
		def LMETHOD = "init"
		nodeIP = ip
		nodePort = port
		if (!initialized) {
			try {	
				dbAdapter = applicationContext.getBean("clientDataAdapter")
				dbAdapter.init(nodeIP, nodePort)

				initialized = true
				println "Database provider initialized successfully"
			} 
			catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${all.getMessage()}"] as Object[])
				//initialized = true
				//  Just continue even if couldn't connect to Cassandra successfully
				throw all
			}
		}
	}
	
	// these life cycle events are for this service
	def start() {
		started = true
	}

	def stop() {
		started = false
	}

	def shutdown() {
		if (initialized) {
			dbAdapter.shutdown()
			if (started) {
				stop()
			}
			initialized = false
		}
	}

	
	/*
	 * specified by Exchange header MAP_NAME with key specified by Exchange MAP_KEY
	 * <p>
	 * @param http header names MAP_NAME
	 * @param http header names MAP_KEY
	 * @param http header names SCHEMA -optional only applicable to cassandra
	 *
	 * @see org.apache.camel.Exchange
	 */
	public void putMapEntry(Exchange e) {

		def mapKey
		def mapName
		def	schema
		def LMETHOD = "putMapEntry(Exchange e)"
		if (!initialized) {
			MessageMgr.logMessage(logger,'warn',getMessageMap(LMETHOD,1),["${LMETHOD}"] as Object[])
		} else {
			try {
				mapKey = e.getIn().getHeader("MAP_KEY", String.class)
				mapName = e.getIn().getHeader("MAP_NAME", String.class)
				schema = e.getIn().getHeader("SCHEMA", String.class)
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${mapName}","${mapKey}"] as Object[])
				//bucket.store(mapKey, e.getIn().getBody(String.class)).execute()
				dbAdapter.store(mapName, mapKey, e.getIn().getBody(String.class), schema)

				// support inout pattern
				e.getOut().setHeaders(e.getIn().getHeaders())
				e.getOut().setBody( e.getIn().getBody() )
			} catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,3),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])
				throw all
			}
		}
	}
	
	/**
	 * Retrieve the entry from the Data Store map specified by Exchange header MAP_NAME
	 * with key specified by Exchange MAP_KEY and return it in the Exchange body
	 * <p>
	 * @param http header names MAP_NAME
	 * @param http header names MAP_KEY
	 * @param http header names SCHEMA -optional only applicable to cassandra
	 *
	 * @see org.apache.camel.Exchange
	 */
	public void getMapEntry(Exchange e) {
		def mapKey
		def mapName
		def schema
		def LMETHOD="getMapEntry(Exchange e)"
		if (!initialized) {
			MessageMgr.logMessage(logger,'warn',getMessageMap(LMETHOD,1),["${LMETHOD}"] as Object[])
		} else {
			try {
				mapKey = e.getIn().getHeader("MAP_KEY", String.class)
				mapName = e.getIn().getHeader("MAP_NAME", String.class)
				schema = e.getIn().getHeader("SCHEMA", String.class)
		//		println "schema ${schema}"
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${mapName}", "${mapKey}"] as Object[])
				// support inout pattern
				def headers = e.getIn().getHeaders()

 				//def res = bucket.fetch(mapKey).execute()?.getValueAsString()
				def res = dbAdapter.fetch(mapName, mapKey, schema)
				if (res != null) {
					headers.put("CACHE_ENTRY_FOUND", "true")
					e.getOut().setBody(res)
					MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,3),["${mapName}", "${mapKey}"] as Object[])
				} else {
					headers.put("CACHE_ENTRY_FOUND", "false")
					// support inout pattern
					e.getOut().setBody(e.getIn().getBody())
					MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,4),["${mapName}", "${mapKey}"] as Object[])
				}
				e.getOut().setHeaders(headers)
			} catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,5),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])
				throw all
			}
		}
	}
 
	/**
	 * Set the global schema specified by Exchange header SCHEMA
	 * This is only applicable to cassandra. Other adapters implement an empty method
 	 * <p>

	 * @param http header names SCHEMA 
	 *
	 * @see org.apache.camel.Exchange
	 */
	public void setSchema(Exchange e) {

		 
		def LMETHOD="setSchema(Exchange e)"
		if (!initialized) {
			MessageMgr.logMessage(logger,'warn',getMessageMap(LMETHOD,1),["${LMETHOD}"] as Object[])
		} else {
			try {
				String schema = e.getIn().getHeader("SCHEMA", String.class)
		//		println "schema ${schema}"
				// support inout pattern
				def headers = e.getIn().getHeaders()

				def res = dbAdapter.setSchema(schema)

				e.getOut().setBody(e.getIn().getBody())
		
				e.getOut().setHeaders(headers)
			} catch(all) {
				throw all
			}
		}
	}
	
	public void getSchema(Exchange e) {
		
				def schema
				def LMETHOD="getSchema(Exchange e)"
				if (!initialized) {
					MessageMgr.logMessage(logger,'warn',getMessageMap(LMETHOD,1),["${LMETHOD}"] as Object[])
				} else {
					try {
						schema = e.getIn().getHeader("SCHEMA", String.class)
				//		println "schema ${schema}"
						// support inout pattern
						def headers = e.getIn().getHeaders()
		
						def res = dbAdapter.getSchema()
		
						e.getOut().setBody(res)
				
						e.getOut().setHeaders(headers)
					} catch(all) {
						throw all
					}
				}
			}
	
 
	
  	/**
	 * Retrieve the entry from the Data Store map specified by Exchange header MAP_NAME
	 * with key specified by Exchange MAP_KEY and return it in the Exchange header
	 * <p>
	 * @param http header names MAP_NAME
	 * @param http header names MAP_KEY
	 * @param http header names SCHEMA -optional only applicable to cassandra
	 *
	 * @see org.apache.camel.Exchange
	 */
   public void getMapEntryToHeader(Exchange e) {
	   def mapKey
	   def mapName
	   def schema
	   def LMETHOD = "getMapEntryToHeader(Exchange e)"
	   try{
		   mapKey = e.getIn().getHeader("MAP_KEY", String.class)
		   mapName = e.getIn().getHeader("MAP_NAME", String.class)
		   schema = e.getIn().getHeader("SCHEMA", String.class)
		   MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${mapName}", "${mapKey}"] as Object[])
		   
		   // support inout pattern
		   def headers = e.getIn().getHeaders()

		   def res = dbAdapter.fetch(mapName, mapKey, schema) 
		   if( res != null){
			   headers.put("CACHE_ENTRY_FOUND", "true")
			   headers.put("MAP_VALUE", res)
			   MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${mapName}", "${mapKey}"] as Object[])
		   }else{
			   headers.put("CACHE_ENTRY_FOUND", "false")
			   MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,3),["${mapName}", "${mapKey}"] as Object[])
		   }
		   e.getOut().setBody( e.getIn().getBody() )
		   e.getOut().setHeaders(headers)

	   }catch(all){
			  MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,4),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])
		   throw all
	   }
   }

  /**
	 * Store the header MAP_VALUE from the given Exchange in the Data Store map
	 * specified by Exchange header MAP_NAME with key specified by Exchange MAP_KEY
	 * <p>
	 * @param http header names MAP_NAME
	 * @param http header names MAP_KEY
	 * @param http header names MAP_VALUE
	 * @param http header names SCHEMA -optional only applicable to cassandra
	 *
	 * @see org.apache.camel.Exchange
	 */
   public void putMapEntryFromHeader(Exchange e) {
	   def mapKey
	   def mapName
	   def mapValue
	   def schema
	   def LMETHOD = "putMapEntryFromHeader(Exchange e)"
	   try{
		   mapKey = e.getIn().getHeader("MAP_KEY", String.class)
		   mapName = e.getIn().getHeader("MAP_NAME", String.class)
		   mapValue = e.getIn().getHeader("MAP_VALUE", String.class)
		   schema = e.getIn().getHeader("SCHEMA", String.class)
		  
		   MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${mapName}", "${mapKey}"] as Object[])
		   
		   //bucket.store(mapKey,mapValue).execute()
		   dbAdapter.store(mapName, mapKey, mapValue, schema)
			// support inout pattern
		   e.getOut().setHeaders(e.getIn().getHeaders())
		   e.getOut().setBody( e.getIn().getBody() )
	   }catch(all){
			  MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,2),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])
		   throw all
	   }
   }
	

	/**
	 * Remove the entry from the Data Store map specified by Exchange header MAP_NAME
	 * with key specified by Exchange MAP_KEY
	 * <p>
	 * @param http header names MAP_KEY
	 * @param http header names MAP_NAME
	 * @param http header names SCHEMA -optional only applicable to cassandra
	 *
	 * @see org.apache.camel.Exchange
	 */
	public void removeMapEntry(Exchange e) {
		def mapKey
		def mapName
		def schema
		def LMETHOD = "removeMapEntry(Exchange e)"
		if (!initialized) {
			MessageMgr.logMessage(logger,'warn',getMessageMap(LMETHOD,1),["${LMETHOD}"] as Object[])
		} else {
			try {
				mapKey = e.getIn().getHeader("MAP_KEY", String.class)
				mapName = e.getIn().getHeader("MAP_NAME", String.class)
				schema = e.getIn().getHeader("SCHEMA", String.class)
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${mapName}", "${mapKey}"] as Object[])
				//bucket.delete(mapKey).execute()
				dbAdapter.deleteKey(mapName, mapKey, schema)
				// support inout pattern
				e.getOut().setHeaders(e.getIn().getHeaders())
				e.getOut().setBody(e.getIn().getBody())
			} catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,3),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])
				throw all
			}
		}
	}

	/**
	 * Return the keys from the Data Store map specified by Exchange header MAP_NAME
	 * in the Exchange header MAP_KEYS
	 * <p>
	 * @param http header names MAP_NAME
	 * @param http header names SCHEMA -optional only applicable to cassandra
	 *
	 * @see org.apache.camel.Exchange
	 */
	public void getMapKeys(Exchange e) {
		def mapName
		def schema
		def LMETHOD = "getMapKeys(Exchange e)"
		if (!initialized) {
			MessageMgr.logMessage(logger,'warn',getMessageMap(LMETHOD,1),["${LMETHOD}"] as Object[])
		} else {
			try {
				// support inout pattern
				Map<String,Object> headers = e.getIn().getHeaders()
				mapName = e.getIn().getHeader("MAP_NAME", String.class)
				schema = e.getIn().getHeader("SCHEMA", String.class)

				String res = ""
				//bucket.keys().each { res += "${it}," }
				res = dbAdapter.fetchKeys(mapName, schema)	
				if(res.size()>1) {
					headers.put("MAP_KEYS", (String)res)
				}

				// support inout pattern
				e.getOut().setHeaders(headers)
				e.getOut().setBody(e.getIn().getBody())
			} catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,2),["${mapName}","${all.getMessage()}"] as Object[])
				throw all
			}
		}
	}
	
	/**
	 * specified by Exchange header MAP_NAME with key specified by Exchange header MAP_KEY and
	 * optional extended Object data items via optional headers MAP_VALUE (if sourced from header
	 * instead of body), OBJECT_CONTENTTPE, OBJECT_USER_METADATA, OBJECT_INDEXDATA, &OBJECT_LWLINKS
	 * <p>
	 * @param http header names MAP_NAME
	 * @param http header names MAP_KEY
	 * @param http header names MAP_VALUE (optional)
	 * @param http header names OBJECT_CONTENTTYPE (optional)
	 * @param http header names OBJECT_USER_METADATA (optional)
	 * @param http header names OBJECT_INDEXDATA (optional)
	 * @param http header names OBJECT_LWLINKS (optional)
	 *
	 * @see org.apache.camel.Exchange
	 */
	//TODO
	public void putMapEntryExtended(Exchange e) {
		def mapKey
		def mapName
		def mapValue
		def mapUsermetaItems = [:]
		def mapUserindexItems = [:]
		def userlwlinkItems = []
		def contentType
		def LMETHOD = "putMapEntryExtended(Exchange e)"
		boolean methodImplemented = false;
		if (!initialized) {
			MessageMgr.logMessage(logger,'warn',getMessageMap(LMETHOD,1),["${LMETHOD}"] as Object[])
		} else {
			//println "trying putMapEntryExtended(Exchange)"
 
		if (!methodImplemented) {
			e.getOut().setBody( "Not Implemented" )
		}
		//TODO write logic for "methodImplemented. Until then code below is commented out
		 
		 /*else {
		
			try {
				mapKey = e.getIn().getHeader("MAP_KEY", String.class)
				mapName = e.getIn().getHeader("MAP_NAME", String.class)
				mapValue = e.getIn().getHeader("MAP_VALUE") ?: e.getIn().getBody()
				def usermetaItems = e.getIn().getHeader("OBJECT_USER_METADATA")
				def userindexItems = e.getIn().getHeader("OBJECT_INDEXDATA")
				def lwlinkItems = e.getIn().getHeader("OBJECT_LWLINKS")
				contentType = e.getIn().getHeader("OBJECT_CONTENTTYPE", String.class) ?: "text/plain"
				if(usermetaItems) {
					//print "usermetItems is instance of ${usermetaItems.getClass().toString()}"
					if(usermetaItems instanceof Map) {
						mapUsermetaItems = usermetaItems
					} else {
						try {
							def jsonUserMeta = JSON.parse(usermetaItems)
							if(jsonUserMeta instanceof Map) {
								mapUsermetaItems = jsonUserMeta
							} else {
								throw new Exception("ClientDataService.putTaggedMapEntry(Exchange) requires any supplied Exchange OBJECT_USER_METADATA header value to be either Map object or JSONObject!")
							}
						} catch(all) {
							MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,2),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])
							throw all
						}
					}
				}
				if(userindexItems) {
					//println "userindexItems is instance of ${userindexItems.getClass().toString()}"
					if(userindexItems instanceof Map) {
						mapUserindexItems = userindexItems
					} else {
						try {
							def jsonUserIndex = JSON.parse(userindexItems)
							if(jsonUserIndex instanceof Map) {
								mapUserindexItems = jsonUserIndex
							} else {
								throw new Exception("ClientDataService.putTaggedMapEntry(Exchange) requires any supplied Exchange OBJECT_USER_INDEXDATA header value to be either Map object or JSONObject!")
							}
						} catch(all) {
							MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,3),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])
							throw all
						}
					}
				}
				if(lwlinkItems) {
					//println "lwlinkItems is instance of ${lwlinkItems.getClass().toString()}"
					if(lwlinkItems instanceof java.lang.String) {
						try {
							lwlinkItems = JSON.parse(lwlinkItems);
						} catch (all) {
							MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,4),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])
							throw new Exception("ClientDataService.putTaggedMapEntry(Exchange) requires any supplied "+
								"Exchange OBJECT_LWLINKS header value to be either A.) single lwlink as Groovy Map object "+
								"or JSONObject with keys [ 'bucket', 'key', 'tag' ] for its values, or B.) a list of links "+
								"as a Groovy List containing Groovy Maps with the [ 'bucket, 'key', 'tag' ] keyset in each "+
								"Groovy Map, or C.) a JSONArray of JSONObjects with the ['bucket', 'key','tag' ] keyset in "+
								"each JSONOBject", all)

						}
					}
					if(lwlinkItems instanceof List) {
						//println "lwlinkItems is instance of List"
						
						//  in this case we assume someone sent a list of maps to represent a collection
						//  of unique bucket, key, tag combinded data elements
						 
						def validlinkItems = true
						lwlinkItems.each { item ->
							if((!item instanceof Map)||(! item.every { key, value ->
								"bucket".equals(key) || "key".equals(key) || "tag".equals(key) } )) {
								validlinkItems = false
							}
						}
						if(!validlinkItems)
							throw new Exception("ClientDataService.putTaggedMapEntry(Exchange) requires any supplied "+
								"Exchange OBJECT_LWLINKS header value to be either A.) single lwlink as Groovy Map object "+
								"or JSONObject with keys [ 'bucket', 'key', 'tag' ] for its values, or B.) a list of links "+
								"as a Groovy List containing Groovy Maps with the [ 'bucket, 'key', 'tag' ] keyset in each "+
								"Groovy Map, or C.) a JSONArray of JSONObjects with the ['bucket', 'key','tag' ] keyset in "+
								"each JSONOBject")
							
						userlwlinkItems=lwlinkItems
						
					} else if (lwlinkItems instanceof Map ) {
					   //println "lwlinkItems is instanceof Map"
					   //
						// in this case we assume there was only one link to be added
						//
						if(!lwlinkItems.every { key, value ->
						"bucket".equals(key) || "key".equals(key) || "tag".equals(key) } ) {
					
							throw new Exception("ClientDataService.putTaggedMapEntry(Exchange) requires any supplied "+
								"Exchange OBJECT_LWLINKS header value to be either A.) single lwlink as Groovy Map object "+
								"or JSONObject with keys [ 'bucket', 'key', 'tag' ] for its values, or B.) a list of links "+
							"as a Groovy List containing Groovy Maps with the [ 'bucket, 'key', 'tag' ] keyset in each "+
							"Groovy Map, or C.) a JSONArray of JSONObjects with the ['bucket', 'key','tag' ] keyset in "+
							"each JSONOBject")
						}
						userlwlinkItems=[ lwlinkItems ]
					
					}
				}
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,5),["${mapUsermetaItems}"," ${mapUserindexItems}","${userlwlinkItems}"] as Object[])
				
				putMapEntryExtended(mapName,mapKey,mapValue,contentType,mapUsermetaItems,mapUserindexItems,userlwlinkItems)

				// support inout pattern
				e.getOut().setHeaders(e.getIn().getHeaders())
				e.getOut().setBody( e.getIn().getBody() )
			} catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,6),["${mapName}","${mapKey}","${all.getMessage()}"] as Object[])
				throw all
			}

		}*/ 

		
		 
		}
	}
	
	/**
	 * specified by Exchange header MAP_NAME with key specified by Exchange header MAP_KEY and
	 * optional extended Object data items via optional headers MAP_VALUE (if sourced from header
	 * instead of body), OBJECT_CONTENTTPE, OBJECT_USER_METADATA, OBJECT_INDEXDATA, &OBJECT_LWLINKS
	 * <p>
	 * @param mapName
	 * @param mapKey
	 * @param mapValue
	 * @param contentType
	 * @param mapUsermetaItems
	 * @param mapUserindexItems
	 * @param userlwlinkItems
	 *
	 * @see org.apache.camel.Exchange
	 */
	//TODO
		public void putMapEntryExtended(String mapName, String mapKey, Object mapValue, String contentType, Map mapUsermetaItems, Map mapUserindexItems, List userlwlinkItems) {

		def LMETHOD = "putMapEntryExtended(String mapName, String mapKey, Object mapValue, String contentType, Map mapUsermetaItems, Map mapUserindexItems, List userlwlinkItems)"
		// Not implemented only applicable for riak
			//	 dbAdapter.putMapEntryExtended(  mapName,   mapKey,   mapValue,   contentType,   mapUsermetaItems,   mapUserindexItems,   userlwlinkItems)
 
	}
	
	/**
	 * execute map reduce per supplied job definition as found in Exchange header "MAPREDUCE_JOB"
	 * and store results in either Exchange body or Exchange header "MAPREDUCE_RESULTS_HEADEROUTPUT"
	 * depending on if Exchange header "MAPREDUCE_RESULTS_HEADEROUTPUT" is "true" when recieved
	 * <p>
	 * @param http header names MAPREDUCE_JOB
	 * @param http header names MAPREDUCE_RESULTS_HEADEROUTPUT
	 * @see org.apache.camel.Exchange
	 */
	//TODO
	public void getMapReduceResult(Exchange e) {
		def mapReduceJobSpec = null
		def mapReduceResult = null
		def LMETHOD = "getMapReduceResult(Exchange e)"
		// Not implemented only applicable for riak
		boolean methodImplemented = false;
		
		if (!methodImplemented) {
			e.getOut().setBody("Not Implemented");
		} else {
		
		try {
			mapReduceJobSpec = e.getIn().getHeader("MAPREDUCE_JOB", String.class) ?: e.getIn().getBody(String.class)
			mapReduceResult = getRawJSONMapReduceResult(mapReduceJobSpec)
			
			if(mapReduceResult != null) {
				if("true".equals(e.getIn().getHeader("MAPREDUCE_RESULTS_HEADEROUTPUT", String.class)?.toLower())) {
					e.getOut().setHeader("MAPREDUCE_HEADEROUTPUT", mapReduceResult)
				} else {
					e.getOut().setBody(mapReduceResult)
				}
			}
			
		} catch(all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),["${mapReduceJobSpec}","${all.getMessage()}"] as Object[])
			throw all
		}
		}
		
	}

	/**
	 * Return objects from Data Store per supplied map reduce spec
	 * <p>
	 * @param mapReduceJobSpec
	 *
	 * @return results in raw fmt
	 */
	//TODO
	public String getRawJSONMapReduceResult(String mapReduceJobSpec) {
	
		return dbAdapter.getMapReduceResult(mapReduceJobSpec).getResultRaw()
	}
	
	/**
	 * Return objects from Data Store per supplied map reduce spec
	 * <p>
	 * @param mapReduceJobSpec
	 *
	 * @return results as Map
	 */
	//TODO
	public Map getMapReduceResultAsMap(String mapReduceJobSpec) {
		
		return dbAdapter.getMapReduceResult(mapResultJobSpec).getResult(Map.class)
	}
	
	/**
	 * Return objects from Data Store per supplied map reduce spec
	 * <p>
	 * @param mapReduceJobSpec
	 *
	 * @return results as List
	 */
	//TODO
	public List getMapReduceResultAsList(String mapReduceJobSpec) {
		
		return dbAdapter.getMapReduceResult(mapResultJobSpec).getResult(List.class)
	}
	
	/**
	 * This method is called by the Health Check 
	 */
	public ping() {
		try {
			dbAdapter.ping();
		} catch(all) {
			throw all
		}
	}
	

	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) 	throws BeansException {
		this.applicationContext = applicationContext
	}

}
