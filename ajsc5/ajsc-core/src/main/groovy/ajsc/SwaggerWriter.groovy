/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc

import grails.converters.JSON
import java.util.regex.Matcher
import java.util.regex.Pattern
import groovy.json.*
import org.springframework.web.util.HtmlUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ajsc.util.MessageMgr

class SwaggerWriter {

	def static paramRX= Pattern.compile(/\{([a-zA-Z0-9]+)\}/)

	final static Logger logger = LoggerFactory.getLogger(SwaggerWriter.class)
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"nimbus",
			"COMPONENT":"SwaggerWriter",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}

	/**
	 * This method generate a resource string in JSON format.
	 * @param outcontext
	 * @param groupedRest
	 * @param apiVer
	 * @return resource string in JSON format.
	 */
	def static resourcesIndex( outcontext, groupedRest,apiVer )
	{

		def builder = new groovy.json.JsonBuilder()


		def apiList= []

		for (g in groupedRest)
		{

			apiList.add([path:g.key ,description:""])
		}


		groovy.json.JsonBuilder root = builder {
			apiVersion apiVer
			swaggerVersion "1.0"
			basePath outcontext

			if ((!apiList.empty) && apiList.size()==1)
			{
				apis(apiList.each {



					it.each{ key, value->
						if (key) key: value

					}
				})
			} else if (!apiList.empty && apiList.size()> 1)
			{


				apis(apiList.each {
					[
						it.each{ key, value->
							if (key) key: value

						}
					]})


			}



		}

		return  root.toPrettyString()

	}


	/**
	 * This method generate a resource string for each operation group.
	 * 
	 * @param method
	 * @return 
	 */
	def static methodOrder( method ){
		//println "method" + method
		def opOrder = ["GET","POST","PUT","DELETE"]
		return opOrder.indexOf(method.toString().toUpperCase() )
	}

	/**
	 * This method generate a resource string for each operation group.
	 * @param serverPath
	 * @param path A resources path
	 * @param entries restedgroups[cat]= routes by sub category map 
	 * @param apiVersion
	 * @return JSON string
	 */
	def static resource( serverPath, path, entries, apiVer )
	{
		def apiList= []
		
		entries.each { key, value->
			def operationsList=[]
			//println "value" + value.toString() 
			def entry=value.sort{
				//println "it---->" + it
				methodOrder(
								it["rest"]["method"])
			}; //Sorts sub category by method
			entry.each {
			operationsList.add(JSON.parse(resourceop(it).toString()))

				
			}
			
			
			apiList.add([operations:operationsList,path: "/"+key ,description:""])
		}
		

		def builder = new groovy.json.JsonBuilder()
		groovy.json.JsonBuilder root = builder {
			apiVersion apiVer
			swaggerVersion "1.0"
			basePath serverPath
			resourcePath path
			//entry(operationsList)
			apis(apiList.each{

				[
						
					it.each{ key, value->
   
						
						 if (key) key: value

						}
					]})


		}

		return root.toPrettyString()


	}
		
/**
 * This method generate a resource string for each operation in operation groups.
 * @param serverPath
 * @param path
 * @param entries
 * @param apiVersion
 * @return JSON string
*/
/**
 * @param entry
 * @return
 */
def static resourceop( entry ){
	 
           // println "in Resource op"
        	def builder = new groovy.json.JsonBuilder()
	        def fnname = entry['src']
			def op =[:]

			def errorResponsesList=[]
			def headersList=[]
			def parametersList=[]
			def docParamsMap=[:]
			def docHeaders = [:]
			def retObj
			def ret= null
			def desc=""
			def smry=""
			def nick=fnname
			def LMETHOD= "resourceop( entry )"
			try{
				def doc = entry["doc"]?DocParser.doc( entry["doc"] ):null
				
				
				if (doc)
		        {
				
					if (doc["returns"])  retObj = doc["returns"] //TODO:else {}
					if (retObj && (retObj["type"])) ret= retObj["type"]
					if (doc["desc"]) desc= doc["desc"]
				
					if (doc["summary"]) {
					  smry= doc["summary"]
					}else{
					  smry=desc.substring(40)
					}
					
					if (doc["fn"]) nick= doc["fn"]
								
								
					for (p in doc["params"])
					{
						docParamsMap[p["name"]] = p
					}
					
					
					for (h in doc["headers"])
					{
						docHeaders[h["header"]] = h
					}
					
					
					for (header in doc["headers"]){
						def headerMap =[:]
						def headerName= ""
						def headerValue= ""
						def headerDesc=""
						def headerRequired= true
						if (header["header"]) headerName=header["header"]
						if (header["value"]) headerValue=header["value"]
						if (header["desc"]) headerDesc=header["desc"]
						if (header["required"]) headerDesc=header["required"]
						headerMap.put("name", headerName)
						headerMap.put("value", headerValue)
						headerMap.put("description",headerDesc)
						headerMap.put("required",headerRequired)
						headersList.add(headerMap)
					}
					
					def matchers = entry['rest']['uri'].findAll(/\{([a-zA-Z0-9]+)\}/)
					for (m in matchers)
					{
						
						if (docParamsMap.get(m))
						 {
							
							def paramMap =[:]
							def paramName= m
							def paramDesc=docParamsMap[m.key]["desc"]
							def paramDataType= "string"
							def paramRequired= false
							def paramAllowMultiple= false
							def paramType= "path"
							def paramEnumArr=""
							
							paramMap.put("name", paramName)
							paramMap.put("description",paramDesc)
							paramMap.put("dataType",paramDataType)
							paramMap.put("required",paramRequired)
							paramMap.put("allowMultiple",paramAllowMultiple)
							paramMap.put("paramType",paramType)
							docParamsMap.remove(m)
							parametersList.add(paramMap)
							
						}
					}
		
					for (m in docParamsMap){
		
						def p=docParamsMap[m.key]
						def paramMap =[:]
						def paramName= m.key
						def paramDesc=""
						def paramDataType= "string"
						def paramRequired= false
						def paramAllowMultiple= false
						def paramType= "query"
						def paramEnumArr
						
						if (p["desc"]) paramDesc=p["desc"]
						if (p["type"]) paramDataType=p["type"]
						if (p["required"]) paramRequired=p["required"]
						if (p["multiple"]) paramAllowMultiple=p["multiple"]
						if ( (entry["rest"]["method"]).toString().toUpperCase()=="POST")  paramType="post"
						if (p["enum"]) paramEnumArr= p["enum"]
					
		
						  def enumListJSON =(paramEnumArr)?JSON.parse(paramEnumArr):null
						paramMap.put("name", paramName)
						paramMap.put("description",paramDesc)
						paramMap.put("dataType",paramDataType)
						paramMap.put("required",paramRequired)
						paramMap.put("allowMultiple",paramAllowMultiple)
						paramMap.put("paramType",paramType)
						if (enumListJSON)
						{
						   paramMap.put("allowableValues",["valueType":"LIST","values": enumListJSON])
						}
						parametersList.add(paramMap)
		
					}
		
		
				}else {
				   println "response"
					ret = "Response"
					desc = fnname
					smry = fnname
					nick = fnname.split(":")[1]
					desc = "UNDOCUMENTED!  No nimbusDoc was provided in source file.\n\nAny Headers and Parameters are not indicated below."	
				
				}
			   
				def method=(entry["rest"]["method"]).toString().toUpperCase()
				
				groovy.json.JsonBuilder root = builder {
					
					 httpMethod method
					 errorResponses( [] )
					 responseClass  ret
					 
					 parameters(parametersList.each {
						 [
							 it.each{ key, value->
								 if (key) key: value
	 
							 }
						 ]})
					 headers(headersList.each {
						 [
							 it.each{ key, value->
								 if (key) key: value
	 
							 }
						 ]})
					
					nickname  nick
					responseTypeInternal ret
					summary  smry
					notes desc
					
				}
				
				return root
			} catch(all)
			{
				//Print the exception and continue to next operation
				MessageMgr.logMessage(logger, 'error', getMessageMap(LMETHOD,1),all, [ "${all}"] as Object[])
				//all.printStackTrace()
			}

	}


	static main(args) {
		def rest= [:]

		rest["key1"]= "value1"
		rest["key2"]= "value1"

		//println rest["key1"]
	}

}
