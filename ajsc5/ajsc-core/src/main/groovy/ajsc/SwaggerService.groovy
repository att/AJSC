/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc
import java.util.List;
import java.util.Properties;

import org.apache.camel.Exchange;


import org.slf4j.Logger
import org.slf4j.LoggerFactory

import ajsc.util.MessageMgr

/**
 * @author apatel
 *
 */
/**
 * @author apatel
 *
 */
class SwaggerService {

	def computeService
	def docService
	def deferoService
	static VandelayService vandelayService

	def static NAMESPACE = "ajsc"
	def static SERVICENAME = "SwaggerService"

	
	def started = false
	def initialized = false
	def registeredListener=false
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"SwaggerService",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}
	
	Properties props
	def static DEFAULT_REST_DOC_FS_ROOT = System.getenv('AJSC_HOME') ?: System.getProperty('AJSC_HOME')
	def static AJSC_REST_DOCS = "/rest/"
	def static AJSC_REST_DOCS_OUT_PATH="${DEFAULT_REST_DOC_FS_ROOT}${AJSC_REST_DOCS}"
	def static RESULT_PROCESS_RESTED_ROUTES_LIST= "RESTED_ROUTES_LIST"
    def static RESULT_PROCESS_REPORT="PROCESS_REPORT"
	def static RESOURCE_BASE_PATH="/swaggerResourcesLookup"
	def static API_BASE_PATH="/rest"
	
	final static Logger logger = LoggerFactory.getLogger(SwaggerService.class)
	
	
	/**
	 * Initialized SwaggerService
	 * <p>
	 * This method create default properties if not exist otherwise it reads the properties and set to static variable props. 
	 * This method calls once from Bootstrap class.
	 * @param
	 *
	 * @return
	 *
	 * @see
	 *
	 */
	def init() {
		start()
		def LMETHOD = "init"
		// service properties

		try{
			def myProps = null
			def propsList = ServiceProperties.findAllByNamespaceAndServiceNameAndServiceVersion(NAMESPACE,  SERVICENAME, "0")
			
			/*if (propsList) {
				myProps = propsList[0]
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${myProps}"] as Object[])
			}
			if(!myProps){
				// attempt to auto configure
				myProps = new ServiceProperties(namespace: NAMESPACE,serviceName: SERVICENAME, serviceVersion:'0')
				myProps.saveWithNotify()

				[
							"apiExcludes":""

						].each{	key,value ->
							def prop = new ServiceProperty(name:key, value:value, parent:myProps)
							myProps.addToProps(prop)
							myProps.saveWithNotify()
						}
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${myProps}"] as Object[])

				myProps.props.each {
					MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,3),["${it.name}", "${it.value}"] as Object[])
				}

				

			}
			props = myProps.toProperties()*/
			initialized=true
			
			println "SwaggerService initialized successfully"
			
		}catch(all){
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,4),all)
		}

	}
	
	/**
	 * Generate Swagger resources in JSON format by reading restlet routes. 
	 * You can exclude the Api by setting appropriate properties in AJSC system properties.
	 * 
	 * @param apiNameList List of the Api Name
	 * @return resultMap returns result with RESULT_PROCESS_RESTED_ROUTES_LIST and RESULT_PROCESS_REPORT keys
	 */
	def generateDoc(List apiNameList ){

		def apiNamespace
    	def apiNamespaceVersion
		def api_pk
		def apiList=[]
		int totalRoutes=0
		int totalRestletRoutes=0
		def restedRouteList=[]
		def resultMap=[:]
		def apiName
	
		def apiExcludes = System.getProperty("ajsc.SwaggerService.0.apiExcludes")

		// exclude the namespace and version from property
		def apiExcludesList

		if (apiExcludes)
		{
			apiExcludesList=apiExcludes.split(",")
		}
      

		apiNameList.each{
			
			def apiElementList=[:]
			int numberofTotalRoute
			int numberofRestletRoute=0
			def routes=[]
			def errorByRouteIdMap=[:]
			
            def apiNamespaceVersionColumnList=it?.split("\\|")
			apiNamespace=apiNamespaceVersionColumnList[0]
			apiNamespaceVersion=apiNamespaceVersionColumnList[1]
			apiName = "${apiNamespace}_${apiNamespaceVersion}"
			
			if (!(apiExcludesList && apiExcludesList.contains(it))) {
				//deleteDir(AJSC_REST_DOCS_OUT_PATH +apiName)
				List<ComputeRoute> routeListByNamespaceAndVersion= ComputeRoute.findAllByNamespaceAndRouteVersion(apiNamespace,apiNamespaceVersion)
				if (routeListByNamespaceAndVersion)
				{
					numberofTotalRoute=routeListByNamespaceAndVersion.size()

					routeListByNamespaceAndVersion.each{
	
						def computeRoute= it
						try{
							//println "routeId=" + computeRoute.id
							//apiVersion=computeRoute.version
							def route=RouteParser.parseSrc(computeRoute.routeDefinition,computeRoute.id,errorByRouteIdMap)
							if (route)
							{
							  routes.add(route)
							}
						}
						catch (all)
						{   
							errorByRouteIdMap.put(computeRoute.id,"parseSrc:="+ "error while parsing a route=>" + all.getMessage())
						}
	
					}

					//Categorize the routes by component
					def routeDef =RouteParser.categorizeRoutes(routes,errorByRouteIdMap)
	
					def rested =RouteParser.resolveRestlet(routeDef["restlet"],errorByRouteIdMap)
	
					if (rested)
					{
						numberofRestletRoute=rested.size()
		
						// group routes by cat and subCat
						def restedgroups = RouteParser.groupRest(rested,errorByRouteIdMap)
		
						// def service=restedgroups.keySet()?.toArray()[0]?.split("/")[1]
						def outcontext= "${RESOURCE_BASE_PATH}/rest/${apiName}" // "../rest/"+service
		
						def content = SwaggerWriter.resourcesIndex(outcontext, restedgroups,apiNamespaceVersion)
						docService.createDir(AJSC_REST_DOCS_OUT_PATH +apiName)
						def file = new File(AJSC_REST_DOCS_OUT_PATH+apiName+"/resources.json")
						file.write(content.toString())
	
						for (restedgroup in restedgroups)
						{
							content=SwaggerWriter.resource(API_BASE_PATH, restedgroup.key, restedgroups[restedgroup.key],apiNamespaceVersion)
							if (content)
							{
								docService.createDir(AJSC_REST_DOCS_OUT_PATH+apiName+restedgroup.key.split('/')[0..2].join("/"))
								file = new File(AJSC_REST_DOCS_OUT_PATH+apiName+restedgroup.key+".json")
								file.write(content.toString())
							}	
		
						}
						
						rested['src'].each{
							restedRouteList.add(it)
							
						}
					}
				}
			
				apiElementList["Api Name"] =apiName
				apiElementList["Rest Route Count"] =numberofRestletRoute
				apiElementList["Total Route Count"] = numberofTotalRoute
				apiElementList["errorMessages"] = errorByRouteIdMap
				
			    totalRoutes=totalRoutes+numberofTotalRoute
				totalRestletRoutes=totalRestletRoutes+numberofRestletRoute
				apiList.add(apiElementList)
			}
		 }
	 
		//Build a response in json format
		//		{
		//			 "Restlet Route Count": "5",
		//			 "Total Route": "18",
		//			 services: [
		//			   {
		//				 "service id" : "att.messages.contacts_0.1",
		//				 "Restlet Route Count": "3",
		//				 "Total Route": "10",
		//				 "errorMessages": [
		//				   {
		//					 "routeId": "att.messages.contacts.rest_aab.getMyInfo.0.1",
		//					 "errorMessage": "Invalid route"
		//				   },
		//				   {
		//					 "routeId": "myrouteid1",
		//					 "errorMessage": "test1"
		//				   }
		//				 ]
		//			   },
		//			   {
		//				 "service id": "att.messaging",
		//				 "Restlet Route Count": "2",
		//				 "Total Route": "8",
		//				 "errorMessages": [
		//				   {
		//					 "routeId": "myrouteid",
		//					 "errorMessage": "test"
		//				   },
		//				   {
		//					 "routeId": "myrouteid1",
		//					 "errorMessage": "test1"
		//				   }
		//				 ]
		//			   }
		//			 ]
		//		   }"

		
		def builder = new groovy.json.JsonBuilder()
	
		def root = builder {
			 "Restlet Route Count" totalRestletRoutes
			 "Total Route Count" totalRoutes
			
			  apis( apiList.each {apiIt-> 
				 [ 
	 		      {
				  
					 apiIt.each {key, value->
				      if (key == "errorMessages") 
	                  {
						  errorMessages JSON.parse(new groovy.json.JsonBuilder(value).toString())
	
					  }
					  else key:value
					 }
	 		     }
				]
			  })
		}

		resultMap[RESULT_PROCESS_RESTED_ROUTES_LIST]= restedRouteList
		resultMap[RESULT_PROCESS_REPORT]=((groovy.json.JsonBuilder) root)
		return resultMap
	}
	
	def start = {
		def LMETHOD = "start"
		started=true
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1))
	}
	def stop = {
		def LMETHOD = "stop"
		if(started) {
			println "Stopping SamlService"
			started=false
			MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,1))
		}
	}
	
	/**
	 * Uses File.mkdirs to delete the given directory if it does exist.
	 * @param dir
	 * @return
	 */
	def deleteDir(dir) {

		def returnVal = true
		def LMETHOD = "deleteDir(dir)"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${dir}"] as Object[])
		try {
			def dirFile = new File(dir)
			if (!dirFile.exists()) {
				returnVal = false
			}
			
			returnVal=dirFile.deleteDir()
					
		} catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,2),all,["${dir}"] as Object[])
		}
		if (!returnVal) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,3),["${dir}"] as Object[])
		}
		return returnVal
	}

	def shutdown() {
		stop()
	}
}
