package ajsc

import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder
// TODO Remove this workaround in Grails 2.0

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ajsc.util.MessageMgr

import org.springframework.web.context.WebApplicationContext
import org.springframework.web.context.ContextLoader

//import org.apache.shiro.SecurityUtils

/**
 * Doc
 * <li>Facilitates a central location for all Transformations</li>
 * <li>Provides a web interface</li>
 * <li>Provides a restful interface</li>
 * <p>
 * @author Terry Walters <mailto:terry.walters@bellsouth.com>
 */
class Doc implements Serializable {
	private static final long serialVersionUID = 1L;
	def static NAMESPACE = "ajsc"
	def static SERVICENAME = "Doc"
	def static MAPNAME = "${NAMESPACE}.Doc"
	
	final static Logger audit = LoggerFactory.getLogger("${Doc.class.name}.AUDIT")
	
	final static Logger logger = LoggerFactory.getLogger(Doc.class)
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"Doc",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}

	String id
	String namespace="ajsc"
	String docName
	String docVersion
	String docContent
	String contentType="text/xml"
	String encoding="UTF-8"
	
	def asJson = {
		def builder = new groovy.json.JsonBuilder()
		def json = builder {
			'class' Doc.class.name
			id id
			namespace namespace
			docName docName
			docVersion docVersion
			docContent docContent
			contentType contentType
			encoding encoding
			}
		builder.toString()
	}

	static transients = ['status', 'deferoService', 'ajscMetaDataService']
	String status
	def static transient deferoService
	def static transient ajscMetaDataService
	
	static constraints = {
		docContent(blank:false,maxSize:104857600)
		namespace(blank:false)
		docName(blank:false)
		docVersion(blank:false)
		namespace(unique:['docName','docVersion'])
	}

	static mapping = {
		id generator:'assigned'
	}
	static mapWith = "none"
	
	def static initAjscMetaDataService = {
		ajscMetaDataService = RouteMgmtService.persistenceService
		}

	
	boolean validateExtended() {
		def baseResult = true//this.validate()
		def duplicate = Doc.findAllByNamespaceAndDocNameAndDocVersion(this.namespace, this.docName, this.docVersion)
		if((duplicate != null) && (duplicate.size > 0)) {
			//this.errors.rejectValue("docName", "ajsc.Doc.docName.not.unique")
			println "ajsc.Doc.docName.not.unique"
			baseResult = false
		}
		return baseResult
	}
	
	String generateId() {
		return "${this.namespace}:${this.docName}:${this.docVersion}"
	}

    boolean equals(other) {
        if (!(other instanceof Doc)) {
            return false
        }
        other.namespace == namespace && other.docName == docName && other.docVersion == docVersion
    }

    int hashCode() {
        def builder = new HashCodeBuilder()
        builder.append namespace
        builder.append docName
        builder.append docVersion
        builder.toHashCode()
    }

	def saveWithNotify = {
		def LMETHOD = "saveWithNotify"
		
		def saveResult = this.save()
		if (!saveResult) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),["${this.id}","${namespace}","${docName}","${docVersion}"] as Object[])
		} else {
//		    deferoService.publishMessage(DocService.TOPIC_NAME,this)
//			clearContentCache(this)
			RouteMgmtService.docService.insertOrUpdateDoc(this);
		}
		return saveResult
	}
	
	def deleteWithNotify = {
		def LMETHOD = "deleteWithNotify"

		def deleteResult = true
		try {
			this.delete()
//			deferoService.publishMessage(DocService.TOPIC_NAME,"delete ${this.generateId()}")
		} catch (all) {
			deleteResult = false
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}","${namespace}","${docName}","${docVersion}"] as Object[])
		}
		return deleteResult
	}
	
	def clearContentCache(doc)
	{
	   def docName=doc.docName
		//Clear Camel Content Case when doc is xslt or velocity
		if ((docName.toLowerCase().indexOf(ClearContentCacheService.XSLT_FILE_EXT)!= -1) || docName.toLowerCase().indexOf(ClearContentCacheService.VELOCITY_FILE_EXT)!= -1)
		{
//			 deferoService.publishMessage(ClearContentCacheService.TOPIC_NAME,doc)
		}
	}

/*	
	static Doc get(String docId) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDoc = null
		def foundJSONDoc = ajscMetaDataService.getMapEntry(MAPNAME, docId)
		if (foundJSONDoc) {
			foundDoc = new Doc(JSON.parse(foundJSONDoc))
			if (!foundDoc.id) { foundDoc.id = foundDoc.generateId() }
		}
		return foundDoc
	}
*/

	static List<Doc> list() {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDocs = new ArrayList()
		def foundJSONDocs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONDocs.each { jsonDoc ->
			if (jsonDoc) {
				def doc = AjscMetaDataUtil.fromJson(Doc, jsonDoc)
				if (!doc.id) { doc.id = doc.generateId() }
				foundDocs.add(doc)
			}
		}
		return foundDocs
	}
	
	static List<Doc> findAllByNamespaceLikeAndDocNameLike(namespaceFilter, nameFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDocs = new ArrayList()
		def foundJSONDocs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONDocs.each { jsonDoc ->
			if (jsonDoc) {
				def doc = AjscMetaDataUtil.fromJson(Doc, jsonDoc)
				if (!doc.id) { doc.id = doc.generateId() }
				
				if ((doc.docName ==~ /${nameFilter}/) &&
					(doc.namespace ==~ /${namespaceFilter}/)) {
					foundDocs.add(doc)
				}
			}
		}
		return foundDocs
	}
	
	static List<Doc> findAllByNamespaceLikeAndDocNameLikeAndDocVersionLike(namespaceFilter, nameFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDocs = new ArrayList()
		def foundJSONDocs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONDocs.each { jsonDoc ->
			if (jsonDoc) {
				def doc = AjscMetaDataUtil.fromJson(Doc, jsonDoc)
				if (!doc.id) { doc.id = doc.generateId() }
				
				if ((doc.docName ==~ /${nameFilter}/) &&
					(doc.namespace ==~ /${namespaceFilter}/) &&
					(doc.docVersion ==~ /${versionFilter}/)) {
					foundDocs.add(doc)
				}
			}
		}
		return foundDocs
	}
	
	static List<Doc> findAllByNamespaceAndDocNameAndDocVersion(namespace, docName, docVersion) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDocs = new ArrayList()
		def foundJSONDocs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONDocs.each { jsonDoc ->
			if (jsonDoc) {
				def doc = AjscMetaDataUtil.fromJson(Doc, jsonDoc)
				if (!doc.id) { doc.id = doc.generateId() }
				
				if ((doc.docName == docName) &&
					(doc.namespace == namespace) &&
					(doc.docVersion == docVersion)) {
					foundDocs.add(doc)
				}
			}
		}
		return foundDocs
	}
	
	static List<Doc> findAllByNamespaceLikeAndDocVersionLike(namespaceFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDocs = new ArrayList()
		def foundJSONDocs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONDocs.each { jsonDoc ->
			if (jsonDoc) {
				def doc = AjscMetaDataUtil.fromJson(Doc, jsonDoc)
				if (!doc.id) { doc.id = doc.generateId() }
				
				if ((doc.namespace ==~ /${namespaceFilter}/) &&
					(doc.docVersion ==~ /${versionFilter}/)) {
					foundDocs.add(doc)
				}
			}
		}
		return foundDocs
	}
	
	static String getAllByNamespaceLikeAndDocVersionLike(namespaceFilter, versionFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundJSONDocs = ajscMetaDataService.getAllEntries(MAPNAME)
		def valueList = []
		
		// For wildcard searches
		def wildcardChar = "*"
		def wildcardPattern = /${wildcardChar}/
		
		namespaceFilter = namespaceFilter.replace(wildcardPattern, ".*")
		versionFilter = versionFilter.replace(wildcardPattern, ".*")
		
		foundJSONDocs.each { jsonDoc ->
			if (jsonDoc) {
				def doc = AjscMetaDataUtil.fromJson(Doc, jsonDoc)
				if (!doc.id) { doc.id = doc.generateId() }
				
				if ((doc.namespace ==~ /${namespaceFilter}/) &&
					(doc.docVersion ==~ /${versionFilter}/)) {
					valueList << jsonDoc
				}
			}
		}
		return valueList
	}
	
	
	static List<Doc> findAllByNamespaceAndDocVersion(namespace, docVersion) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDocs = new ArrayList()
		def foundJSONDocs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONDocs.each { jsonDoc ->
			if (jsonDoc) {
				def doc = AjscMetaDataUtil.fromJson(Doc, jsonDoc)
				if (!doc.id) { doc.id = doc.generateId() }
				
				if ((doc.namespace == namespace) &&
					(doc.docVersion == docVersion)) {
					foundDocs.add(doc)
				}
			}
		}
		return foundDocs
	}
	
	static List<Doc> findAllByNamespaceLike(namespaceFilter) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDocs = new ArrayList()
		def foundJSONDocs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONDocs.each { jsonDoc ->
			if (jsonDoc) {
				def doc = AjscMetaDataUtil.fromJson(Doc, jsonDoc)
				if (!doc.id) { doc.id = doc.generateId() }
				
				if ((doc.namespace ==~ /${namespaceFilter}/)) {
					foundDocs.add(doc)
				}
			}
		}
		return foundDocs
	}
	
	static List<Doc> findAllByNamespace(namespace) {
		// Read with Protocol Buffers client
		if (!ajscMetaDataService) { initAjscMetaDataService() }
		def foundDocs = new ArrayList()
		def foundJSONDocs = ajscMetaDataService.getAllEntries(MAPNAME)
		foundJSONDocs.each { jsonDoc ->
			if (jsonDoc) {
				def doc = AjscMetaDataUtil.fromJson(Doc, jsonDoc)
				if (!doc.id) { doc.id = doc.generateId() }
				
				if (doc.namespace == namespace) {
					foundDocs.add(doc)
				}
			}
		}
		return foundDocs
	}
	
	Doc save() {
		def LMETHOD = "save"
		
		def returnVal = null
		if (!this.id) {
			this.id = generateId()
		}
		try {
			ajscMetaDataService.putMapEntry(MAPNAME, this.id, AjscMetaDataUtil.asJson(this))
			returnVal = this
		} catch(all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}","${namespace}","${docName}","${docVersion}"] as Object[])
			throw all
		}
		return returnVal
	}
	
	void delete() {
		def LMETHOD = "delete"
		try {
			ajscMetaDataService.removeMapEntry(MAPNAME, generateId())
		} catch(all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${this.id}","${namespace}","${docName}","${docVersion}"] as Object[])
			throw all
		}
	}
}
