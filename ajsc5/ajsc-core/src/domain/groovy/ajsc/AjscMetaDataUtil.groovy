package ajsc;

import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

public class AjscMetaDataUtil {
	
	static def dataFields = { cl ->
		cl.metaClass.properties.findAll { f ->
			// Locate all non-static, non-final, non-transient class members
			(f.modifiers&(java.lang.reflect.Modifier.STATIC|java.lang.reflect.Modifier.FINAL|java.lang.reflect.Modifier.TRANSIENT)) == 0 &&
			// weed out Objects because they are generally closures and Class types because those can't
			// be set
			!(f.type.canonicalName == "java.lang.Object" || f.type.canonicalName == "java.lang.Class") // &&
			// If this is a readonly property then the setter will be null (DOESN'T WORK)
			//f.getSetterName(f.name) != null
		}
	}

	static def asJson(Object obj) {
		
		List<MetaProperty> toFields = dataFields(obj.class)
		
		def toValues = [:]
		
		// Make sure the class name is saved
		toValues['class'] = obj.class.name

		// Create a map with just the fields we want
		toFields.each {
			toValues["${it.name}"] = obj[it.name]
		}
		
		// Pass our map to the json builder and it'll create json from it
		def jsonBuilder = new JsonBuilder(toValues)
		return jsonBuilder.toString()
	}
	
	//
	// Attempt to initialize a new object of type 'toClass' using
	// the content of the jsonString
	//
	static def fromJson(Class toClass, String jsonString) {
		
		JsonSlurper slurp = new JsonSlurper()
		
		List<MetaProperty> fromDataFields = dataFields(toClass)
		
		// Convert the string to a json object
		def jsonObj = slurp.parseText(jsonString)
		
		// 'class' cannot be set on any object
		jsonObj.remove("class")
		
		def newObj = toClass.newInstance()
		
		fromDataFields.each {
			def newVal = jsonObj[it.name]
			// we only want to set the value if it's not-null
			if (newVal != null) {
				if (it.type.canonicalName == "java.util.Date") {
					newVal = Date.parse("yyyy-MM-dd'T'HH:mm:ssZ",newVal) //e.g. 2013-10-17T13:49:09+0000
				}
				try {
					newObj."$it.name" = newVal
				} catch (groovy.lang.ReadOnlyPropertyException e) {
					// this is a workaround until I can figure out how to filter out
					// read-only properties.
				}
			}
		}
		
		return newObj
	}
}
