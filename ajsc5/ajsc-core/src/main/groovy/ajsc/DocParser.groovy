/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc

import java.util.regex.Matcher
import java.util.regex.Pattern

class DocParser {

	def static docRX = Pattern.compile(/[ \t]*API[ \t]*[\n]?(.*?)[\n]?[ \t]*(@.*)/, Pattern.MULTILINE|Pattern.DOTALL)
	def static tagRX = Pattern.compile(/[ \t]*@([a-zA-Z0-9\-_]+)[ \t]+(.+?)(?:[\n]|\Z)/,Pattern.MULTILINE|Pattern.DOTALL )
	def static tagParamRX = Pattern.compile(/([a-zA-Z0-9\-_]+)[ \t]+(?:(\*|\+|\?)[ \t]+){0,1}\{([a-zA-Z0-9\-_]+)\}[ \t]+(?:[=]([a-zA-Z0-9_"]+){0,1}(\[[a-zA-Z0-9_",]+\]){0,1}[ \t]+){0,1}(.*)/,Pattern.DOTALL )
	def static tagHeaderRX= Pattern.compile(/([a-zA-Z0-9\-_]+)=(.*?)[ \t]+(.*)/,Pattern.DOTALL)
	def static tagErrorRX= Pattern.compile(/([0-9]+)[ \t]+(.*)/,Pattern.DOTALL)
	def static tagReturnsRX= Pattern.compile(/([a-zA-Z\-\/]+)[ \t]+\{([a-zA-Z0-9\-]+)\}[ \t]+(.*)/,Pattern.DOTALL)
	
	
	/**
	 * Parse the return tag and returns fields(elements) in map
	 * @param returnTagStr in String format
	 * @return map of return tag elements
	 */
	def static tagReturns( returnTagStr ){
	 def matcher = tagReturnsRX.matcher(returnTagStr)
	
		matcher.find()
		def obj = [:]
		obj['contentType'] = matcher.group(1)
		obj['type'] = matcher.group(2)
		obj['desc'] = matcher.group(3)

	  return obj
	}
	
	
	/**
	 * Parse the param tag and returns field(element) in map
	 * @param paramStr param tag in String format
	 * @return map of return tag elements
	 */
	def static tagParam( paramStr ){
				
				def matcher = tagParamRX.matcher(paramStr)
								
				matcher.find()
				def obj = [:]
				obj['name'] = matcher.group(1)
				obj['type'] = matcher.group(3)
				obj['desc'] = matcher.group(6)
	
				def occ = matcher.group(2)
				switch (occ) {
					case "?":
					obj['required'] = false
					obj['multiple'] = false
					break
					case "*":
					obj['required'] = false
					obj['multiple'] = true
					case "+":
					obj['required'] = true
					obj['multiple'] = true
					break
				
					default:
					obj['required'] = true
					obj['multiple'] = false
					break
	
				}
				
			
				def dft = matcher.group(4)
				if (dft) obj['default'] = dft
	
				def enm = matcher.group(5)
				if( enm) obj['enum'] = enm
				//println " enm" + enm
	
				return obj
	}

		
	/**
	 * Parse the header tag and returns field(element) in map
	 * @param headerStr header tag in String format
	 * @return map of header tag elements
	 */
	
	def static tagHeader( String headerStr ){
		
		 def matcher = tagHeaderRX.matcher(headerStr)
			
         matcher.find()
		     
	     def obj = [:]
		 obj['header'] = matcher.group(1)
		 obj['value'] = matcher.group(2)
		 obj['desc'] = matcher.group(3)
		
		return obj
	}
	
	/**
	 * Parse the error tag and returns error tag field(element) in map
	 * @param errorStr error tag in String format
	 * @return map of header tag elements
	 */
	def static tagError( str ){
	    def matcher = tagErrorRX.matcher(str)
			
        matcher.find()
		     
		def obj = [:]
		obj['code'] = matcher.group(1)
		obj['desc'] = matcher.group(2)
	
		return obj
	}
	
	
	/**
	 * Parse the doc string and returns field(element) in map
	 * 
	 * @param docStr
	 * @return map of the doc elements. 
	 */
	def static doc( docStr ){

		if (!docStr.find(/^[ \t]*API.*/))
		{
			print "No Nimbus Doc"
			return null
		}
		def docObj = [:]
		docObj['params'] = []
		docObj['headers'] = []
		docObj['errors'] = []
		docObj['returns'] = [:]

		Matcher matcher = docRX.matcher(docStr);
		matcher.find()
		//print "before match **************" +matcher.group(1).trim()
		docObj['desc']= matcher.group(1).trim().replaceAll(/(?m)[\t]+|[ ]{2,}/," ")
		//docObj['desc'] = re.sub( '(?m)[\t]+|[ ]{2,}', " ", match.group(1).strip() )
		//docObj['desc']= docObj['desc'].replaceAll('(?m)[\n]',"<br/>")
		//println "docObj['desc']" + docObj['desc']

		def tagbody =  matcher.group(2)

	    matcher = tagRX.matcher(tagbody )
		
		while ( matcher.find()) {		
			def p = matcher.group(1)
			def p2 =matcher.group(2)
			try{
			switch (p) {
				case "summary":
				docObj['summary'] = p2
				break
				case "function":
				docObj['fn'] = p2.split(" ")[0]
				break
				case "returns":
				docObj['returns'] =tagReturns(p2)
				break
				case "param":
				docObj['params'].add(tagParam(p2)) 
				break
				case "header":
				docObj['headers'].add(tagHeader(p2))
				break
				case "error":
				docObj['error'].add(tagError(p2)) 
				break
				default:
				print "ERROR: unknown nimbus doc tag '"+ p +"'"
				break
		
			}
			} catch(all)
			{
				//
			}
			
		  }
		return docObj
    }


	static main(args) {
			String t = new String("""API
    A Proxy / Pass-through call to retrieve MyInfo contact data based on user authentication from the MyInfo api.

    Template Path:  /contacts/0/myinfo?restletMethods=get

    
   
    @summary A Proxy / Pass-through call to retrieve MyInfo contact data based on user authentication.
    @function getMyInfo
    @returns contentType {JSON} Returns a myInfo JSON object with all of the MyInfo data.""");
			doc(t)
			//    A Proxy / Pass-through call to retrieve MyInfo contact data based on user authentication from the MyInfo api.
			//
			//    Template Path:  /contacts/0/myinfo?restletMethods=get
			//
			//
			//
			//    @summary A Proxy / Pass-through call to retrieve MyInfo contact data based on user authentication.
			//    @function getMyInfo
			//    @returns contentType {JSON} Returns a myInfo JSON object with all of the MyInfo data.
			//
			//""")


		}

	}


