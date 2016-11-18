/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc

class RouteParser {
	
	/**
	 * Parse the route 
	 * @param routeDef 
	 * @param routeKey
	 * @param errorByRouteIdMap
	 * @return Map of route elements
	 */
	def static parseSrc(String routeDef, routeKey,errorByRouteIdMap)
	{


		//			'''
		//        <route xmlns="http://camel.apache.org/schema/spring" trace="true">
		//            <from uri="restlet:/messages/1/all?restletMethods=post"/>
		//            <to uri="direct:att.messaging/sendMessage/0.1" />
		//        </route>

		def route= [:]
		route.put("src",routeKey)
		route.put("passthrough", false)

		routeDef=routeDef.replaceAll("<!--", "<ElementTree.Comment><![CDATA[")
		routeDef=routeDef.replaceAll("-->", "]]></ElementTree.Comment>")

		

		def routeDefRoot = new XmlSlurper().parseText(routeDef)
		def isFromElement= false
        def strFrom= ""
		routeDefRoot.children().each {

			if (it.name() == "from") {

				//println  "url"+ it.@uri.text()
				route.put("from",it.@uri.text())
				strFrom=it.@uri.text()
				isFromElement= true
			//	println it.name()

			} else if (isFromElement && it.name() == "ElementTree.Comment")
			{
                def docStr=new String(it.text()) 
				//route.put("doc",docStr )
				
				if (!docStr?.find(/^[ \t]*API.*/))
				{
					// println "strFrom" + strFrom
					if (strFrom.find("restlet"))
					{
						//println "Missing Route Document. Document must be define after from tag"
						errorByRouteIdMap.put(routeKey,"Missing Route Document. Document must be defined after from tag")
					
					}
				}else 
				{
					if (strFrom.find("restlet"))
					{
				    	route.put("doc",docStr )
					}
				}
			
				isFromElement= false
			}else if (isFromElement && it.name() != "ElementTree.Comment"){
				isFromElement= false
				if (strFrom.find("restlet"))
				{
				   errorByRouteIdMap.put(routeKey,"Missing Route Document. Document must be defined after from tag")
				}
				
			}
			// TODO: we are not implementing pass-through feature.It is use for routes static


		}
		//println "route doc type" + route['doc'].getClass()
		return route
	}

	/**
	 * This method parse the restlet endpoint to set the uri and methods in route map 
	 * @param routeDefs
	 * @param errorByRouteIdMap
	 * @return restlet route map list
	 */
	def static resolveRestlet( routeDefs,errorByRouteIdMap )
	{
		def resolved = routeDefs
		
		for ( route in routeDefs  ) {
			try{
				
			def rest= [:]
			def strFrom =route.get("from")

			if (!strFrom || !(strFrom.find("restlet")))
			{
				//print "invalid route: " + route["src"]
				continue
			}

			
			
			//try for 3 /// 
			def urlwithOptions 
			
			if (strFrom.find("restlet:///"))
			{	
				urlwithOptions=strFrom.replace("restlet:///", "")
			} else if (strFrom.find("restlet://")){
				urlwithOptions =strFrom.replace("restlet://", "")
			} else if (strFrom.find("restlet:/")){
				urlwithOptions =strFrom.replace("restlet:/", "")
			}
			def parts=  urlwithOptions.split("\\?")
			//set get as default method for rest
			if (parts.size()==1)
			{
				urlwithOptions=urlwithOptions+"?restletMethod=get"
				parts=  urlwithOptions.split("\\?")
			} 
			

			if (parts.size() != 2)
			{
				throw new Exception("invalid restlet uri: " + route["from"])
			}

			rest["uri"]= parts[0]
			def qs = queryParams( parts[1] )

			rest.put("uri", parts[0])
		
			if (qs["restletMethod"])
			{
				def methods =qs["restletMethod"].split(',')

				rest["method"] = qs["restletMethod"]

			}else if (qs["restletMethods"])
			{
				def methods =qs["restletMethods"].split(',')

				rest["method"] = methods[0]

			}else{

				//route["rest"] =  [:]
				rest["method"] = 'get'
				//print "no restlet method: " + route["from"]
				continue

			}
						
		    rest["path"] = "/".plus( parts[0].split('/')[0..2].join("/")) //@TODO: If namespace added to path

			route["rest"] = rest

			}catch(all)
			{
			  errorByRouteIdMap.put(route["src"],"resolveRestlet:="+all.getMessage())
			
			}

		}
		
		return resolved

	}

	/**
	 * Parse the query string returns error params in map
	 * @param queryStr
	 * @return map 
	 */
	def static queryParams( queryStr )
	{
		def	paramsMap = [:]
		def paramsList = queryStr.split("&")
		for (p in paramsList)
		{
			def a = p.split("=")
			paramsMap[a[0]] = a[1]
		}
		return paramsMap
	}


	/**
	 * Group the rest routes base on cat=operation groups(contacts/all, contacts/myinfo) and operation sub cat=(contacts/all, contacts/all/{contactid}) .
	 * @param routeDefs
	 * @param errorByRouteIdMap
	 * @return
	 */
	def static groupRest(routeDefs,errorByRouteIdMap)
	{
		def sortedRoutes = [:]
		routeDefs = routeDefs.sort {
			
			it.value?.from
			
			}
      
		for (route in routeDefs)
		{
	
			try{
				if (!route["from"])
				{
					//print "invalid route: " + route["src"]
					continue
				}
				def cat = route["rest"]["path"]
				//print "cat" + cat
				if(!sortedRoutes[cat])
				{
					sortedRoutes[cat] = [:]
				}	
				def subcat = route["rest"]["uri"]
					//print "subcat" + subcat
				if (!sortedRoutes[cat][subcat])
				{
						sortedRoutes[cat][subcat]=[]
				}
					sortedRoutes[cat][subcat].add(route)
			
			}catch(all)
			{
			  errorByRouteIdMap.put(route["src"],"groupRest:="+all.getMessage())
			
			}
		}
		return sortedRoutes
	}


	/**
	 * This method categorize the routes by component
	 * 
	 * @param routeDefs
	 * @param errorByRouteIdMap
	 * @return map of routes by category(component)
	 */
	def static categorizeRoutes( routeDefs,errorByRouteIdMap ){
		def sortedRoutes = [:]
		
		for (route in routeDefs){
			try{
				if(!route["from"])
				{
				  //print "invalid route: " + route["src"]
				  continue
				}
				def cat = route["from"].split(':')[0]
				if (!sortedRoutes[cat]) sortedRoutes[cat] = []
				 sortedRoutes[cat].add( route )
			}catch(all)
			{
				//all.printStackTrace()
				errorByRouteIdMap.put(route['src'],"categorizeRoutes:="+all.getMessage())
			}
		}
		
		return sortedRoutes
	}
//	static main(args) {
//
//		def routeDef1= """<route xmlns="http://camel.apache.org/schema/spring" trace="{{att.common.Config.0.1.ENABLE_CAMEL_TRACE}}">
//    <from uri="restlet:/contacts/0/all?restletMethods=get"/>
//    <!-- API
//    A Proxy / Pass-through call to retrieve all Contact data based on user authentication.
//
//    Template Path:  /contacts/0/all?restletMethods=get
//
//    Example JSON Object Data Returned (* Data will vary based on user *):
//    <pre><code class="json">
//    {
//       "Body":{
//          "getAllContactsResponse":{
//             "contactItems":{
//                "currentPageIndex":1,
//                "totalRecords":5,
//                "totalPages":1,
//                "previousPage":0,
//                "nextPage":0,
//                "item":[
//                   {
//                      "id":"ab4b9923-2c5a-4636-aa1f-9f018b41105b",
//                      "contactType":"PERSON",
//                      "productId":"WEBSITE",
//                      "creationDate":"2012-05-31T12:55:12.604Z",
//                      "modificationDate":"2012-05-31T12:55:12.636Z",
//                      "formattedName":"John Smith",
//                      "firstName":"John",
//                      "lastName":"Smith",
//                      "phone":{
//                         "number":"(123) 456-7890",
//                         "type":[
//                            "VOICE",
//                            "HOME",
//                            "PREF"
//                         ]
//                      },
//                      "autoUpdate":true
//                   },
//                   {
//                      "id":"12a6a7df-bcec-4a16-af28-705cf7f87f39",
//                      "contactType":"PERSON",
//                      "productId":"WEBSITE",
//                      "creationDate":"2012-05-31T13:02:13.998Z",
//                      "modificationDate":"2012-05-31T13:02:14.020Z",
//                      "formattedName":"Jane",
//                      "firstName":"Joseph",
//                      "lastName":"Smith",
//                      "phone":{
//                         "number":"(000) 111-2222",
//                         "type":[
//                            "VOICE",
//                            "HOME",
//                            "PREF"
//                         ]
//                      },
//                      "autoUpdate":true
//                   }
//                ]
//             }
//          }
//       }
//    }
//    </code></pre>
//
//    @summary A Proxy / Pass-through call to retrieve all Contact data based on user authentication.
//    @function getAllContacts
//    @returns contentType {JSON} Returns a JSON object with full list of Contact data.
//    -->
//
//    <onException>
//        <exception>java.lang.Throwable</exception>
//        <handled><constant>true</constant></handled>
//        <to uri="direct:att.common/handleErrorRestful/0.1"/>
//    </onException>
//
//    <to uri="direct:att.common/processRestletHeaders/0.1"/>
//	<to uri="direct:att.messages.contacts/aab.getAllContactsDirect/0.1"/>
//    <removeHeader headerName="Content-Length"/><!-- This is to guarantee that no override of Content-Length is allowed -->
//</route>"""
//
//
//		String fileContents = new File('/Users/apatel/Desktop/att.messages/0.1/routes/rest_logout_GET.route').text
//		def errorByRouteIdMap = [:]
//		def routes = []
//		routes.add(parseSrc(new String(fileContents),'rest_logout_GET.route', errorByRouteIdMap))
//		
//		
//		
//			
//		//print "********route*****"+ routes.toString()
//		def routeDef =categorizeRoutes(routes,errorByRouteIdMap)
//		
//		//print "********route1*****"+ routeDef.toString()
////		
//		def rested =resolveRestlet(routeDef["restlet"],errorByRouteIdMap)
//		//print "********route2*****"+ rested.toString()
// 
//		 def restedgroups = RouteParser.groupRest(rested,errorByRouteIdMap)
//		// print "********route3*****"+ rested.toString()
//		 
//  		 for (o in restedgroups)
//		 {
//			 
//			 def json=SwaggerWriter.resource("http://host:port/nimbus/rest", "", restedgroups[o.key],"0.1")
//			 println "json" + json
//		 }
//		 
// 
////		 //SwaggerWriter.resource("http://host:port/nimbus/rest", "", restedgroups[0])
//	}

}
