/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc
import grails.spring.BeanBuilder

import javax.xml.bind.JAXBContext
import javax.xml.bind.Unmarshaller

import java.util.Properties

import org.apache.camel.*
import org.apache.camel.builder.*
import org.apache.camel.component.ejb.EjbComponent
import org.apache.camel.model.*
import org.apache.camel.model.config.*
import org.apache.camel.model.dataformat.*
import org.apache.camel.model.language.*
import org.apache.camel.model.loadbalancer.*
import org.apache.camel.spi.ExecutorServiceManager
import org.apache.camel.spi.InterceptStrategy
import org.apache.camel.spi.ThreadPoolProfile
import org.apache.camel.spring.SpringCamelContext
import org.apache.camel.util.ServiceHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware

import ajsc.common.CommonNames;
import ajsc.http4.AjscHttpHeaderFilterStrategy
import ajsc.rest.AjscRestletHeaderFilterStrategy
import ajsc.util.AjscTemplateEngine
import ajsc.util.MessageMgr
import ajsc.utils.DME2Helper
import ajsc.utils.SystemErrorHandlerUtil

import com.att.aft.dme2.api.DME2Manager
import com.att.ajsc.beans.PropertiesMapBean
import com.att.ajsc.csi.restmethodmap.RefresheableSimpleRouteMatcher;


/**
 * ComputeService - Wrapper for Apache Camel
 * <p>
 * Apache Camel is a powerful open source integration framework based on known Enterprise
 * Integration Patterns with powerful Bean Integration.
 * Camel lets you create the Enterprise Integration Patterns to implement routing and mediation
 * rules in either a Java based Domain Specific Language (or Fluent API), via Spring based Xml
 * Configuration files or via the Scala DSL. This means you get smart completion of routing rules
 * in your IDE whether in your Java, Scala or XML editor.
 * Apache Camel uses URIs so that it can easily work directly with any kind of Transport or
 * messaging model such as HTTP, ActiveMQ, JMS, JBI, SCA, MINA or CXF Bus API together with
 * working with pluggable Data Format options. Apache Camel is a small library which has minimal
 * dependencies for easy embedding in any Java application. Apache Camel lets you work with the
 * same API regardless which kind of Transport used, so learn the API once and you will be able
 * to interact with all the Components that is provided out-of-the-box.
 * <p>
 * @see <a href="camel.apache.org">Camel</a>
 *
 * @author	Terry Walters
 *
 * @since       1.0
 */
class ComputeService implements ApplicationContextAware {

	def deferoService
	def loggingWonderlandService

	def static NAMESPACE = "ajsc"
	def static SERVICENAME = "ComputeService"
	def static TOPIC_NAME = NAMESPACE+"."+SERVICENAME+".topic"
	def static ADMIN_TOPIC = NAMESPACE+".admin.topic"
	def static USER_JAR_DIR = 'lib'
	def static USER_BEAN_DEF_LOCATION = "conf"
	def static DEFAULT_CONTEXT_KEY = "default:0"
	def static OpenEJB_PROPS = "OpenEjb.properties"

	final static Logger audit = LoggerFactory.getLogger("${ComputeService.class.name}.AUDIT")
	final static Logger logger = LoggerFactory.getLogger(ComputeService.class)
	static Properties props
	def grailsApplication
	def adaptorRouterService
	def restletComponentFactory
	def restletComponent
	def amqpConnectionFactory
	def amqpTemplate
	def amqpAdmin
	def started = false
	def initialized = false
	def registeredListener=false
	//Get ApplicationContext from Grails
	def appCtx

	//Map of Camel Contexts, keyed by namespace:version
	def static ctxMap = new HashMap<String, CamelContext>()

	protected static def defaultEjbJndiProps = null
	def mainEjbContext; //Singleton OpenEJB context initialized by org.openejb.client.LocalInitialContextFactory

	// Camel template - a handy class for kicking off exchanges
	ProducerTemplate template

	// ajsc.beans.processors we want by default
	def namespaceLoggerInterceptorProcessor
	def defaultCamelExceptionHandlerRef

	static transactional = true
	static expose = ['jmx']

	static public  Map<String,String> endpointUriMap = new HashMap<Object, String>();


	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"ComputeService",
			"METHOD":method,
			"MSGNUM":msgnum
		]
	}

	def messageMap = ["MODULE":"ajsc","COMPONENT":"ComputeService","METHOD":"loadBeans(ctxKey, ctx)","MSGNUM":"2"]
	def msgCode

	def suspend() {
		def LMETHOD = "suspend"
		ctxMap.each { ctxKey, ctx ->
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${ctxKey}"] as Object[])
			ctx.suspend()
		}
	}
	def resume() {
		def LMETHOD = "resume"
		ctxMap.each { ctxKey, ctx ->
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${ctxKey}"] as Object[])
			ctx.resume()
		}
	}

	def initContextMap = {
		def LMETHOD = "initContextMap"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1))

		// Default Context
		if (!Context.findById(Context.DEFAULT_CONTEXT_KEY))
		{
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2))
			def defaultContext = new Context(contextName:"default", contextVersion:"0", description:"Default Context")
			defaultContext.save()
		}

		ctxMap.clear()
		Context.list().each { ctx ->
			if (!hasContext(ctx.id)) {
				addContext(ctx.id)
			}
		}
	}

	def addContext(ctxKey) {
		def LMETHOD = "addContext(ctxKey)"

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${ctxKey}"] as Object[])
		def ctx = new SpringCamelContext(appCtx)
		ctx.setName(ctxKey.replace(":", "-"))

		// Register Camel Event Notifier
		def ajscEnv = System.getProperty("AJSC_ENV");
		//println "ajscEnv=" +ajscEnv
		if (AjscConstant.AJSC_ENV.SOACLOUD.toString() == ajscEnv)
		{
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2))

		}

		ctxMap.put(ctxKey as String, ctx)

	}



	def hasContext(ctxKey) {
		return ctxMap.keySet().contains(ctxKey as String)
	}

	def deleteContext(ctxKey) {
		def LMETHOD = "deleteContext(ctxKey)"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${ctxKey}"] as Object[])
		def ctx = ctxMap.get(ctxKey as String)
		def defaultContext = ctxMap.	get(Context.DEFAULT_CONTEXT_KEY as String)

		// Remove each route from the context to be deleted and add it to the default context
		ctx.getRoutes().each { route ->
			def routeId = route.getId()
			def routeDefinition = ctx.getRouteDefinition(routeId)
			MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,2),[
				"${routeId}",
				"${ctxKey}",
				"${Context.DEFAULT_CONTEXT_KEY}"] as Object[])
			ctx.stopRoute(routeId)
			ctx.removeRoute(routeId)
			try {
				def status = defaultContext.getRouteStatus(routeId)
				//println "\t\t${msg}"
				MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,3),["${routeId}", "${status}"] as Object[])
			} catch(all) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,4),all,["${routeId}", "${ctxKey}"] as Object[])
				//println all.getMessage()
			}
		}

		stop(ctxKey)
		ctxMap.remove(ctxKey as String)
	}

	def loadBeans(ctxKey) {
		loadBeans(ctxKey, ctxMap.get(ctxKey as String))
	}

	def loadBeans(ctxKey, ctx) {
		def LMETHOD = "loadBeans(ctxKey, ctx)"

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${ctxKey}"] as Object[])
		def keyParts = ctxKey.split(":")
		def ctxNamespace = keyParts[0]
		def ctxVersion = keyParts[1]

		// Use default application context if no beans are defined
		def bbAppCtx = appCtx
		def bb
		
		boolean isOSGIEnable= false;

		if(System.getProperty("isOSGIEnable") != null	&& System.getProperty("isOSGIEnable").equalsIgnoreCase("true"))
		{
			isOSGIEnable= true;
		}
		

		// Load corresponding user defined beans into this context
		def ajscHome = System.getProperty('AJSC_HOME') ?: System.getenv("AJSC_HOME")
		if (!ajscHome) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,2))
		} else {
		
		// User default loader if no jars are defined
		def loader = null;
		if(isOSGIEnable ==false)
		{
			 loader= URLClassLoader.newInstance(appCtx.getClassLoader().getURLs(), appCtx.getClassLoader())

			// Load jars from AJSC_HOME/lib
			UserDefinedJar.findAllByContextId(ctxKey).each { userDefinedJar ->
				try {
					MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,3),[
						"${userDefinedJar.namespace}",
						"${userDefinedJar.jarVersion}",
						"${userDefinedJar.jarName}"] as Object[])
					def jardir = new File("${ajscHome}/${USER_JAR_DIR}/${userDefinedJar.namespace}/${userDefinedJar.jarVersion}")
					//println "jars in dir ("+jardir+"): "+jardir.listFiles()
					def jar   = jardir.listFiles().find { it.name == userDefinedJar.jarName }
					loader.addURL(jar.toURI().toURL())
					//java.net.URL[] urls=  new
					//RootLoader(java.net.URL[] urls, loader);
					//appCtx.getClassLoader().getSystemClassLoader().addURL(jar.toURI().toURL());
					//ClassPathLoader.addURL(jar.toURI().toURL())
				} catch (all) {
					MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,4),all,[
						"${ajscHome}",
						"${USER_JAR_DIR}",
						"${userDefinedJar.namespace}",
						"${userDefinedJar.jarVersion}"] as Object[])

				}
			}
			bb = new BeanBuilder(appCtx, loader)
		}else {
		
		   bb = new BeanBuilder(appCtx, appCtx.getClassLoader());
		}
			/*// User default loader if no jars are defined
			def loader= URLClassLoader.newInstance(appCtx.getClassLoader().getURLs(), appCtx.getClassLoader())

			// Load jars from AJSC_HOME/lib
			UserDefinedJar.findAllByContextId(ctxKey).each { userDefinedJar ->
				try {
					MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,3),[
						"${userDefinedJar.namespace}",
						"${userDefinedJar.jarVersion}",
						"${userDefinedJar.jarName}"] as Object[])
					def jardir = new File("${ajscHome}/${USER_JAR_DIR}/${userDefinedJar.namespace}/${userDefinedJar.jarVersion}")
					//println "jars in dir ("+jardir+"): "+jardir.listFiles()
					def jar   = jardir.listFiles().find { it.name == userDefinedJar.jarName }
					loader.addURL(jar.toURI().toURL())
					//java.net.URL[] urls=  new
					//RootLoader(java.net.URL[] urls, loader);
					//appCtx.getClassLoader().getSystemClassLoader().addURL(jar.toURI().toURL());
					//ClassPathLoader.addURL(jar.toURI().toURL())
				} catch (all) {
					MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,4),all,[
						"${ajscHome}",
						"${USER_JAR_DIR}",
						"${userDefinedJar.namespace}",
						"${userDefinedJar.jarVersion}"] as Object[])

				}
			}
			// Load beans
			bb = new BeanBuilder(appCtx, loader)*/
			def beansLocation = "${ajscHome}/${USER_BEAN_DEF_LOCATION}"
			def beansDefResource = null
			UserDefinedBeansDef.findAllByContextId(ctxKey).each { beansDef ->
				try {
					/*def beansDefFile = new File("${beansLocation}/${beansDef.namespace}/${beansDef.beansDefVersion}/${beansDef.beansDefName}${!beansDef.beansDefName.endsWith('.groovy') ? '.groovy' : ''}")
					 beansDefResource = new FileSystemResource(beansDefFile)
					 MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,5),["${beansDefResource}"] as Object[])
					 bb.loadBeans(beansDefResource) */

					def beansDefFile = "file:${beansLocation}/${beansDef.namespace}/${beansDef.beansDefVersion}/${beansDef.beansDefName}"
					bb.importBeans(beansDefFile)

				} catch (all) {
					MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,6),all,[
						"${beansLocation}",
						"${beansDef.namespace}",
						"${beansDef.beansDefVersion}",
						"${beansDef.beansDefName}"] as Object[])

				}
			}
			bbAppCtx = bb.createApplicationContext()
		}

		ctx.setApplicationContext(bbAppCtx)

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,7),["${ctxKey}"] as Object[])
	}


	def start() {
		def LMETHOD = "start"

		if(started) return
			started=true

		println "ComputeService::start"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1))

		initContextMap()
		ctxMap.each { ctxKey, ctx ->
			//addInterceptStrategies to the default camel context
			if(ctxKey.contains('default')){
				println "Adding intercept strategies to the context"
				addInterceptStrategies(ctx);
			}

			start(ctxKey)

		}
		println "ComputeService::Started"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2))

	}

	def start(ctxKey) {
		def LMETHOD = "start(ctxKey)"

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1), ["${ctxKey}"] as Object[])

		if (!hasContext(ctxKey)) {
			addContext(ctxKey)
		}

		def ctx = ctxMap.get(ctxKey as String)
		loadBeans(ctxKey, ctx)


		//initialize ejb component only if enableEJB property is 'true' for a service
		if(System.getProperty("ENABLE_EJB") != null
		&& System.getProperty("ENABLE_EJB").equalsIgnoreCase("true")) {
			def ejb = ctx.getComponent("ejb", EjbComponent.class)// as per documentation on camel-ejb page
			ejb.setContext(getEjbContext())
		}

		initializeCamelDefaultThreadPoolProfile(ctx)
		ctx.start()

		//Initialize Camel Components
		initializeCamelComponents(ctx);

		// Restlet
		addRestletComponent(ctx, ctxKey)

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2), ["${ctxKey}"] as Object[])
	}

	def getEjbContext() {
		if(!mainEjbContext) {
			this.mainEjbContext = createEjbContext(DEFAULT_CONTEXT_KEY);
		}
		return this.mainEjbContext;
	}


	def createEjbContext(ctxKey) {

		def retContext

		def defaultProps = getDefaultEjbJndiProps()
		def propsToUse  = new java.util.Properties()
		def openEjbProps = null
		propsToUse.putAll(defaultProps)

		//see for the existence of OpenEJB.properties file. If so, load it as well into the InitialContext
		File openEJBPropsFile = new File(System.getProperty("AJSC_HOME")+File.separator+"etc"+File.separator+OpenEJB_PROPS);

		if(openEJBPropsFile.exists()) {
			FileInputStream fis = new FileInputStream(openEJBPropsFile);
			openEjbProps = new Properties();
			openEjbProps?.load(fis);
			propsToUse.putAll(openEjbProps);
		}
		println "Going to initialize EJB container... Please wait\n"
		retContext = new javax.naming.InitialContext(propsToUse);
		println "\nSuccessfully initialized EJB container..."
		return retContext

	}

	def getDefaultEjbJndiProps() {
		if(!defaultEjbJndiProps) {
			defaultEjbJndiProps = new java.util.Properties()
			defaultEjbJndiProps.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.openejb.client.LocalInitialContextFactory")
		}
		return defaultEjbJndiProps
	}


	private addInterceptStrategies(ctx) {

		//read the configuration file for the list of InterceptStrategies if any and add them to the CamelContext

		ArrayList<String> interceptsList = new ArrayList<String>();

		String intercepts = PropertiesMapBean.getProperty("app-intercepts.properties","intercepts");

		if(intercepts!=null && intercepts.trim().length()>0) {
			String[] interceptsArray = intercepts.split(",");

			for(String interceptClass: interceptsArray){
				interceptsList.add(interceptClass.trim());

				//for each intercept class encountered, load the class & instantiate it

				InterceptStrategy instanceOfInterceptStrategyClass = (InterceptStrategy) Class.forName(interceptClass.trim()).newInstance();
				ctx.addInterceptStrategy(instanceOfInterceptStrategyClass);

			}
		}

		println "Intercept strategies added to the context: ${ctx} --> ${interceptsList}"
	}

	private initializeCamelComponents(ctx) {

		def LMETHOD = "initializeCamelComponents(ctx)"


		// MaxTotalConnections is configurable - CAMEL_HTTP_MAX_TOTAL_CONNECTIONS
		def camelHttpMaxTotalConnections= System.getProperty("CAMEL_HTTP_MAX_TOTAL_CONNECTIONS")
		// ConnectionsPerRoute is configurable - CAMEL_HTTP_CONNECTIONS_PER_ROUTE
		def camelHttpConnectionsPerRoute = System.getProperty("CAMEL_HTTP_CONNECTIONS_PER_ROUTE")
		// ConnectionTimeToLive is configurable - CAMEL_HTTP_CONNECTION_TIME_TO_LIVE
		def  camelHttpConnectionTimeToLive= System.getProperty("CAMEL_HTTP_CONNECTION_TIME_TO_LIVE")


		//Add Ajsc specific filters to Component
		AjscHttpHeaderFilterStrategy httpFilterStrategy = new AjscHttpHeaderFilterStrategy();

		//Initialize Http Camel component
		org.apache.camel.component.http4.HttpComponent http4Component= new org.apache.camel.component.http4.HttpComponent();
		http4Component.setHeaderFilterStrategy(httpFilterStrategy)
		if(camelHttpMaxTotalConnections) http4Component.setMaxTotalConnections(Integer.valueOf(camelHttpMaxTotalConnections))
		if(camelHttpConnectionsPerRoute) http4Component.setConnectionsPerRoute(Integer.valueOf(camelHttpConnectionsPerRoute))
		if(camelHttpConnectionTimeToLive) http4Component.setConnectionTimeToLive(Long.valueOf(camelHttpConnectionTimeToLive))
		//http4Component.start()
		ctx.addComponent("http4", http4Component);
		ServiceHelper.startService(http4Component)

		MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,1), [
			"${http4Component.getMaxTotalConnections()}",
			"${http4Component.getConnectionsPerRoute()}",
			"${http4Component.getConnectionTimeToLive()}"] as Object[])


		//Initialize Http Camel component
		org.apache.camel.component.http4.HttpComponent httpComponent=new org.apache.camel.component.http4.HttpComponent();
		httpComponent.setHeaderFilterStrategy(httpFilterStrategy)
		//	 if(camelHttpMaxTotalConnections) httpComponent.setMaxTotalConnections(Integer.valueOf(camelHttpMaxTotalConnections))
		//	 if(camelHttpConnectionsPerRoute) httpComponent.setConnectionsPerRoute(Integer.valueOf(camelHttpConnectionsPerRoute))
		//	 if(camelHttpConnectionTimeToLive) httpComponent.setMaxConnectionTimeToLive(Long.valueOf(camelHttpConnectionTimeToLive))
		//http4Component.start()
		ctx.addComponent("http", httpComponent);
		ServiceHelper.startService(httpComponent)


		//TODO: replace this with some sort of generic "plugable" initialization for components
		//Initialize Hazelcast Camel component
		//		com.att.camel.component.hazelcast.HazelcastComponent hazelcastComponent = new com.att.camel.component.hazelcast.HazelcastComponent();
		//		hazelcastComponent.setHazelcastInstance(deferoService.hazelcast)
		//		ctx.addComponent("att-hazelcast", hazelcastComponent);

		//Initialize camel-spring-amqp Camel component
		//		com.att.amqp.spring.camel.component.SpringAMQPComponent springamqpComponent = new com.att.amqp.spring.camel.component.SpringAMQPComponent(amqpConnectionFactory);
		//		springamqpComponent.setAmqpAdministration(amqpAdmin);
		//		springamqpComponent.setAmqpTemplate(amqpTemplate);
		//		ctx.addComponent("att-spring-amqp", springamqpComponent)
	}


	private initializeCamelDefaultThreadPoolProfile(ctx)

	{
		def LMETHOD = "initializeCamelDefaultThreadPoolProfile(ctx)"
		ExecutorServiceManager executorServiceManager=ctx.getExecutorServiceManager()
		ThreadPoolProfile defaultThreadPoolProfile=executorServiceManager.getDefaultThreadPoolProfile();

		// Initial Pool Size
		def camelPoolSize= System.getProperty("CAMEL_POOL_SIZE")
		if (camelPoolSize) defaultThreadPoolProfile.setPoolSize(Integer.valueOf(camelPoolSize.trim()))

		// Maximum Pool Size
		def camelMaxPoolSize= System.getProperty("CAMEL_MAX_POOL_SIZE")
		if (camelMaxPoolSize) defaultThreadPoolProfile.setMaxPoolSize(Integer.valueOf(camelMaxPoolSize.trim()))

		// Keep Alive Time
		def keepAliveTime=System.getProperty("CAMEL_KEEP_ALIVE_TIME")
		if (keepAliveTime) defaultThreadPoolProfile.setKeepAliveTime(Long.valueOf(keepAliveTime.trim()))

		//Max Queue Size
		def maxQueueSize=System.getProperty("CAMEL_MAX_QUEUE_SIZE")
		if (maxQueueSize) defaultThreadPoolProfile.setMaxQueueSize(Integer.valueOf(maxQueueSize.trim()))

		// Thread Rejection Policy-Abort,CallerRuns,Discard,DiscardOldest
		def rejectedPolicy=System.getProperty("CAMEL_REJECTED_POLICY")
		if (rejectedPolicy) defaultThreadPoolProfile.setRejectedPolicy(ThreadPoolRejectedPolicy.valueOf(rejectedPolicy.trim()));

		MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,1), [
			"${defaultThreadPoolProfile.getMaxPoolSize()}",
			"${defaultThreadPoolProfile.getPoolSize()}",
			"${defaultThreadPoolProfile.getKeepAliveTime()}",
			"${defaultThreadPoolProfile.getMaxQueueSize()}",
			"${defaultThreadPoolProfile.getRejectedPolicy()}"] as Object[])

	}
	private addRestletComponent(ctx, ctxKey) {
		def restletComponentService = restletComponentFactory.getRestletComponent(ctxKey)
		restletComponentService.stop()
		restletComponentService.setCamelContext(ctx)
		restletComponentService.start()
		restletComponentService.setHeaderFilterStrategy(new AjscRestletHeaderFilterStrategy());

		ctx.addComponent("restlet", restletComponentService)
	}

	def stop() {
		def LMETHOD = "stop"

		if(!started) return

			println "ComputeService::stop"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1))
		ctxMap.keySet().each { ctxKey-> stop(ctxKey) }
		initialized = false
		started = false
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2))
	}

	def stop(ctxKey) {
		def LMETHOD = "stop(ctxKey)"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${ctxKey}"] as Object[])

		def ctx = ctxMap.get(ctxKey as String)

		if (!ctx) {
			MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,2),["${ctxKey}", "${ctxMap}"] as Object[])
		} else {

			try{
				def grailsAppCtx = ctx.getApplicationContext()
				grailsAppCtx.close()

				//gracefully shutdown the context. it using a timeout of 300 seconds which then forces a shutdown now
				ctx.stop()

			} catch(all){
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,3),all,["${ctxKey}"] as Object[])
			}

			ctx.getShutdownStrategy().shutdown()
			ctx.stop()


			//shutdown the OpenEJB container & clean up if the default context is restarted
			println "Going to stop EJB container... Please wait"
			if(ctxKey?.toLowerCase().contains("default")){
				this.mainEjbContext?.close();
				this.mainEjbContext = null;
			}
			println "Successfully stopped EJB container..."

			ctxMap.remove(ctxKey as String)
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${ctxKey}"] as Object[])
		}
	}

	/**
	 * <p>getRouteStatus - returns the status for the given route id</p>
	 *
	 * @params ajsc.ComputeRoute
	 * @see also sa.CamelService
	 * @returns
	 */
	/*def routeStatus(rid) {
	 def routeStatus
	 def LMETHOD = "getRouteStatus(rid)"
	 //println "ComputeService.getRouteStatus rid: ${rid}"
	 def cr = ComputeRoute.findById(rid)
	 if (!cr) {
	 MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),["${rid}"] as Object[])
	 } else {
	 def ctxKey = cr.contextId?:Context.DEFAULT_CONTEXT_KEY
	 def ctx = ctxMap.get(ctxKey as String)
	 routeStatus =  ctx.getRouteStatus(rid)
	 }
	 return routeStatus
	 }*/

	def static getDefaultContext() {
		def LMETHOD = "getDefaultContext()"
		def ctx = ctxMap.get(Context.DEFAULT_CONTEXT_KEY as String)
		return ctx;
	}

	/**
	 * <p>listRoutes - list all Route IDs</p>
	 *
	 * @params ajsc.ComputeRoute
	 * @see also sa.CamelService
	 * @returns
	 */
	private void checkAndWaitForRoutesStarted(){
		def LMETHOD = "checkAndWaitForRoutesStarted"
		boolean isAllRoutesStarted=true;
		int retry_count=Integer.valueOf(props.getProperty('retryCount'));
		int retry_interval=Integer.valueOf(props.getProperty('retryInterval'));

		for (int i=1; i<= retry_count; i++){
			ctxMap.each { ctxKey, ctx ->
				ctx.getRoutes().each{route->
					String rid= route.getId()
					def routeStatus =  ctx.getRouteStatus(rid)

					if (!routeStatus.isStarted()) {
						isAllRoutesStarted =false;
						sleep(retry_interval);
						true; // break the ctxMap.each
					}
				}
			}
			if (isAllRoutesStarted){
				break;
			}
		}

		if (!isAllRoutesStarted)
		{
			//After retry if all routes are not started, print the not started routes
			ctxMap.each { ctxKey, ctx ->
				ctx.getRoutes().each{route->
					String rid= route.getId()
					def routeStatus =  ctx.getRouteStatus(rid)

					if (!routeStatus.isStarted()) {

						MessageMgr.logMessage(logger,'warn',getMessageMap(LMETHOD,1),["${route.getId()}"] as Object[])
					}
				}
			}
		}
	}

	def listRoutes ={
		def res = []
		ctxMap.each { ctxKey, ctx ->
			ctx.getRoutes().each{route-> res.add(route.getId())}
		}
		return res
	}

	/**
	 * Validate that the <from > endpoint(s) in the given route are
	 * unique.  Check all routes in all contexts. If a matching endpoint
	 * is found in another route, return its route id.  If no match is found,
	 * return null
	 * @param cr ComputeRoute
	 * @return the route id of the route with matching endpoint or null
	 */
	def validateFromEndpointUnique(cr) {
		def returnVal = null
		def unique = false
		RouteDefinition route = getRouteDefinition(cr)

		// endpoints Map<Object endpoint, String routeId>
		def endpoints = getAllEndpoints()

		List inputs = route.getInputs()
		for (FromDefinition from : inputs) {
			for (Object endpoint : endpoints.keySet()) {
				// Ignore endpoints for the current route, but if an endpoint from another route matches, we have a duplicate
				if (endpoints.get(endpoint) != route.getId() && endpoint == from.getUriOrRef()) {
					returnVal = endpoints.get(endpoint)
					break
				}
			}
		}
		return returnVal
	}

	/**
	 * Determine if the given route has any input (<from: >) endpoints
	 * that already exist in this or any other context
	 * @param route
	 * @return true if all input endpoints are unique, false otherwise
	 */
	def isUniqueEndpoints(route) {
		def returnVal = true

		// endpoints Map<Object endpoint, String routeId>
		def endpoints = getAllEndpoints()

		List inputs = route.getInputs()
		for (FromDefinition from : inputs) {
			for (Object endpoint : endpoints.keySet()) {
				// Ignore endpoints for the current route, but if an endpoint from another route matches, we have a duplicate
				if (endpoints.get(endpoint) != route.getId() && endpoint == from.getUriOrRef()) {
					returnVal = false
					break
				}
			}
		}
		return returnVal
	}

	/**
	 * Return a Map of all endpoints and their corresponding routeId from routes defined in all contexts
	 * @return Map<Object endoint, String routeId>
	 */
	def getAllEndpoints = {
		def endpoints = new HashMap<Object, String>()
		ctxMap.values().each { v ->
			v.getRouteDefinitions().each { rd ->
				rd.getInputs().each { from ->
					endpoints.put(from.getUriOrRef(), rd.getId())
				}
			}
		}
		return endpoints
	}


	def getAllEndpointUrlWithoutQueryParam= {
		def endpoints = new HashMap<Object, String>()
		ctxMap.values().each { v ->
			v.getRouteDefinitions().each { rd ->
				rd.getInputs().each { from ->
					String endPointURI = from.getUriOrRef().replace("{", "");
					endPointURI = endPointURI.replace("}", "");
					endpoints.put(getEndpointUrlWithoutQueryParam(endPointURI), rd.getId());
				}
			}
		}
		return endpoints
	}

	def getRouteDefinition(cr) {
		def LMETHOD = ""
		RouteDefinition route
		JAXBContext context = JAXBContext.newInstance("org.apache.camel:org.apache.camel.model:org.apache.camel.model.config:org.apache.camel.model.dataformat:org.apache.camel.model.language:org.apache.camel.model.loadbalancer")
		Unmarshaller unmarshaller = context.createUnmarshaller()
		Object value = unmarshaller.unmarshal(new StringReader(propertyReplace(cr.routeDefinition)))
		if (value instanceof RouteDefinition) {
			route = (RouteDefinition)value
			route.routeId(cr.generateId())
		} else{
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),[
				"${cr?.generateId()}",
				"${cr.routeDefinition}"] as Object[])
		}
		return route
	}

	/**
	 * <p>addRoute - creates a Route dynamically from the provided XML</p>
	 *
	 * @params sa.ServiceMethodMap
	 * @see also sa.CamelService
	 * @returns
	 */

	def addRoute(cr) {
		def LMETHOD = "addRoute(cr)"

		try {
			//println "ComputeService.addRoute adding ${cr.generateId()}"
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${cr?.generateId()}"] as Object[])
			if (cr) {
				RouteDefinition route = getRouteDefinition(cr)
				def routeName = cr.id
				// Get the current camel context
				def currCtx = findRouteContext(routeName)
				// Get the updated camel context
				def ctxKey = cr.contextId?:Context.DEFAULT_CONTEXT_KEY
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),[
					"${cr?.generateId()}",
					"${ctxKey}"] as Object[])
				if (!ctxMap.keySet().contains(ctxKey as String)) {
					MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,10),["${ctxKey}"] as Object[])
				} else {
					def ctx = ctxMap.get(ctxKey as String)

					// Verify the <from: > endpoint does not already exist
					if (!isUniqueEndpoints(route)) {
						//println "Route ${routeName} contains <from: > endpoint that already exists, route will not be added/started in Camel"
						MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,4),["${routeName}"] as Object[])
					} else {
						// stop and remove route if it already exists
						if (currCtx) {
							MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,5),[
								"${routeName}",
								"${currCtx.getName()}"] as Object[])
							currCtx.stopRoute(routeName)
							currCtx.shutdownRoute(routeName)
							currCtx.removeRoute(routeName)
						}
						// add route to context
						if(cr.deployStatus=="Active") {
							// but first we decorate the route with autonamespacelogging interceptor
							// if that is enabled globally
							/// TODO:  this decoration should probably be farmed out to a seperate method
							///  so that multiple interceptors can be used this way without having to make
							///  this method an encyclopedia of logic
							//TODO: Add loggingWonderlandService back into the build
							//							if(!loggingWonderlandService.autoNsLoggingDisabled) {
							//								loggingWonderlandService.configureNamespaceLoggingSupport(
							//									cr.namespace, cr.routeVersion)
							//								route.adviceWith(ctx, new AdviceWithRouteBuilder() {
							//									@Override
							//									public void configure() throws Exception {
							//
							//										onException(Throwable.class)
							//											.processRef("defaultCamelExceptionHandlerRef")
							//
							//										interceptSendToEndpoint('log:ajscAutoNamespaceLogger?*')
							//											.skipSendToOriginalEndpoint()
							//											.to("namespaceLoggerInterceptorProcessor")
							//									}
							//								})
							//							}
							ctx.addRouteDefinitions(Collections.singletonList(route));
						}
						try {
							def status = ctx.getRouteStatus(routeName)
							//println "\t\t${msg}"
							addedRoute(routeName,ctx)
							MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,6),[
								"${routeName}",
								"${ctxKey}",
								"${status}"] as Object[])
						} catch(all) {
							MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,7),all,["${routeName}"] as Object[])
							//println all.getMessage()
						}
					}
				}
			} else {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,8))
				//println "addRoute failed, route is null"
			}
		} catch(all) {
			//println "addRoute for ${cr?.generateId()} failed"
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,9),all,["${cr?.generateId()}"] as Object[])
		}
	}

	def addedRoute(routeName,ctx){
		println "Adding route: " + routeName +  " to context: " + ctx
	}

	/**
	 * <p>delRoutes - deletes a Route dynamically from its context</p>
	 *
	 * @params ajsc.ComputeRoute
	 * @see also sa.CamelService
	 * @returns
	 */

	def delRoute(routeName) {
		def LMETHOD = "delRoute(routeName)"

		try {
			//println "ComputeService.delRoute routeName: ${routeName}"
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${routeName}"] as Object[])
			def ctx = findRouteContext(routeName)
			if (!ctx) {
				MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,4),["${routeName}"] as Object[])
			} else {
				RouteDefinition route = ctx.getRouteDefinition(routeName)
				if (route) {
					ctx.removeRouteDefinition(route)
				}
			}
		} catch(all){
			//println "Failed to delete route ${routeName}"
			//println all.getMessage()
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,3),all,["${routeName}"] as Object[])
		}
	}

	/**
	 * Determines if there are any routes defined for this context
	 * @param ctxKey
	 * @return true if no routes exist for this context, false otherwise
	 */
	def isEmptyContext(ctxKey) {
		def returnVal = false
		def ctx = ctxMap.get(ctxKey as String)
		if (ctx.getRouteDefinitions().isEmpty() && !ComputeRoute.findAllByContextId(ctxKey)) {
			returnVal = true
		}
		return returnVal
	}

	/**
	 * <p>init - Initialize this service:</p>
	 * <ul>
	 * <li>starts routes</li>
	 * <li>subscribes to Admin messages</li>
	 * <li>subscribes to Service messages</li>
	 * </ul>
	 * @params ajsc.ComputeRoute
	 * @see also sa.CamelService
	 * @returns
	 */
	def init() {
		start()
		def LMETHOD = "init"

		//println "Initializing ComputeService"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1))

		try {
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2))
			def routeList = ComputeRoute.list()
			if (!routeList) {
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,3))
			} else {
				routeList.each { addRoute(it) }
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,4))
			}

			endpointUriMap=getAllEndpointUrlWithoutQueryParam();

			if (System.getProperty("SOA_CLOUD_ENV") != null
			&& System.getProperty("SOA_CLOUD_ENV").equalsIgnoreCase("true")) {
				println "System Property found: SOA_CLOUD_ENV=true. Registering Services with GRM . . ."
				DME2Helper.init();
			}

			initProp();
			checkAndWaitForRoutesStarted();

			if (System.getProperty("SOA_CLOUD_ENV") != null
			&& System.getProperty("SOA_CLOUD_ENV").equalsIgnoreCase("true")) {
				registersServicesToGRM()
			}

			initialized=true
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,5))
		}catch(all){

			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,6),all)
			SystemErrorHandlerUtil.callSystemExit(all);

		}
	}

	def initProp()
	{

		def LMETHOD = "initProp()"

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1))

		try {

			def myProps = null

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

				["retryCount":"5","retryInterval":"500"].each { key,value ->
					def prop = new ServiceProperty(name:key, value:value)
					myProps.addToProps(prop)
				}
				myProps.saveWithNotify()

				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,5),["${myProps}"] as Object[])
			}
			myProps.props.each {
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,6),["${it.name}", "${it.value}"] as Object[])
			}

			props = myProps.toProperties()

			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,7),["${props}"] as Object[])
		}
		catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,8),all)
		}

		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,9))

	}


	def registersServicesToGRM()
	{
		try{

			DME2Helper dme2Helper = new DME2Helper();
			DME2Manager dme2Manager = DME2Manager.getDefaultInstance();
			//ctxMap.values().each { v ->
			ctxMap.each { ctxKey, v ->
				v.getRouteDefinitions().each { rd ->
					def routeStatus =  v.getRouteStatus(rd.getId())

					if(routeStatus){
						if (routeStatus.isStarted()) {
							rd.getInputs().each { from ->
								dme2Helper.registerServiceToGRM(from.getUriOrRef(),dme2Manager, false)
							}
						}
					}
				}
			}

			// Register dme2url defined in rest method map
			String servletUriPattern = System.getProperty("APP_SERVLET_URL_PATTERN");
			String restletUriPattern = System.getProperty("APP_RESTLET_URL_PATTERN");

			def routes = RefresheableSimpleRouteMatcher.getRouteMatcher().getRoutes();

			routes.each { route->
				//verify if the service is a SOAP service. If so, then register it in GRM as per CSI norm
				if(route.getType() != null && route.getType().equalsIgnoreCase("soap")) {

					dme2Helper.registerSOAPServiceToGRM(route.getDme2Url(), route.getServiceName(), dme2Manager);

				} else if (route.getDme2Url()!= null) {


					String serviceUri=route.getDme2Url().replace("{","");
					serviceUri=serviceUri.replace("}","");
					if (route.getDme2Url().startsWith(servletUriPattern)){
						serviceUri=serviceUri.replaceFirst(servletUriPattern+"/", "/")
					}else if  (route.getDme2Url().startsWith(restletUriPattern)){
						serviceUri=serviceUri.replaceFirst( restletUriPattern+"/", "/")
					}
					endpointUriMap.put(serviceUri, route.getDme2Url());
					dme2Helper.registerServiceToGRM(route.getDme2Url(),dme2Manager, true)
				}
			}



		}catch(Exception grmException) {
			SystemErrorHandlerUtil.callSystemExit(grmException);

		}

	}
	/**
	 * Initialize the context for the given context key
	 * @param ctxKey
	 * @return
	 */
	def init(ctxKey) {
		def LMETHOD = "init(ctxKey)"

		//println "Initializing Context: ${ctxKey}"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${ctxKey}"] as Object[])

		start(ctxKey)


		try {
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,3),["${ctxKey}"] as Object[])
			def routeList = ComputeRoute.findAllByContextId(ctxKey)
			if (!routeList) {
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,4),["${ctxKey}"] as Object[])
			} else {
				routeList.each { addRoute(it) }
				MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,5),["${ctxKey}"] as Object[])
			}
			//println "ComputeService Context ${ctxKey} initialized sucessfully"
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,6),["${ctxKey}"] as Object[])
		}catch(all){
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,1),all,["${ctxKey}"] as Object[])
		}
	}

	/**
	 * Restart the given context, which includes 1) stopping and removing the context,
	 * 2) re-creating the context, 3) adding user defined beans to the context,
	 * 4) adding routes defined for the context, and 5) starting routes
	 * @param ctxKey
	 * @return
	 */
	def restart(ctxKey) {
		def LMETHOD = "restart(ctxKey)"
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1),["${ctxKey}"] as Object[])
		stop(ctxKey)

		init(ctxKey)
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,2),["${ctxKey}"] as Object[])
	}

	def shutdown = { stop() }

	/**
	 * Replace placeholders with property values
	 * @param input route
	 * @return output route with values replacing placeholders
	 */
	def propertyReplace(input) {
		def output
		def LMETHOD = "propertyReplace(input)"

		try {
			MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,1))
			output = AjscTemplateEngine.propertyReplace(input, System.properties)
		} catch (all) {
			MessageMgr.logMessage(logger,'error',getMessageMap(LMETHOD,2),all)
			output = input
		}
		MessageMgr.logMessage(logger,'debug',getMessageMap(LMETHOD,3),["${output}"] as Object[])
		return output
	}

	def findRouteContext(routeName) {
		def LMETHOD = "findRouteContext(routeName)"
		def routeContext
		for (def ctxEntry:ctxMap.entrySet()) {
			def ctx = ctxEntry.value
			if (ctx.getRoute(routeName)) {
				routeContext = ctx
				MessageMgr.logMessage(logger,'info',getMessageMap(LMETHOD,1),[
					"${routeContext.getName()}",
					"${routeName}"] as Object[])
				break
			}
		}

		return routeContext
	}

	def isValidProps(serviceProperties) {
		def propNames = serviceProperties?.props*.name
		return ["fileSystemRoot"]== propNames
	}

	/**
	 * <p>getRouteDefinitions</p>
	 *
	 * @returns
	 */
	def getRouteDefinitions() {
		List<RouteDefinition> defs = new ArrayList<RouteDefinition>()
		ctxMap.each { ctxKey, ctx ->
			defs.addAll(ctx.getRouteDefinitions())
		}
		return defs
	}

	/**
	 * @param ctxKey
	 * @return
	 */
	def getRouteDefinitions(ctxKey) {
		def ctx = ctxMap.get(ctxKey as String)
		return ctx.getRouteDefinitions()
	}

	/**
	 * <p>getEndpoints</p>
	 *
	 * @params routeId
	 * @see
	 * @returns
	 */
	def getEndpoints() {
		Collection<Endpoint> endpoints = new ArrayList<Endpoint>()
		ctxMap.each { ctxKey, ctx ->
			endpoints.addAll(ctx.getEndpoints())
		}
		return endpoints
	}

	/**
	 * @param ctxKey
	 * @return
	 */
	def getEndpoints(ctxKey) {
		def ctx = ctxMap.get(ctxKey as String)
		return ctx.getEndpoints()
	}

	/**
	 * <p>getEndpointMap</p>
	 *
	 * @params routeId
	 * @see
	 * @returns
	 */
	def getEndpointMap() {
		Map<String, Endpoint> endpointMap = new HashMap<String, Endpoint>()
		ctxMap.each { ctxKey, ctx ->
			endpointMap.putAll(ctx.getEndpointMap())
		}
		return endpointMap
	}

	/**
	 * @param ctxKey
	 * @return
	 */
	def getEndpointMap(ctxKey) {
		def ctx = ctxMap.get(ctxKey as String)
		return ctx.getEndpointMap()
	}

	/**
	 * <p>startRoute</p>
	 *
	 * @params routeId
	 * @see
	 * @returns
	 */
	/*def startRoute(routeId) {
	 def cr = ComputeRoute.findById(routeId)
	 if (cr) {
	 def ctxKey = cr.contextId?:Context.DEFAULT_CONTEXT_KEY
	 def ctx = ctxMap.get(ctxKey as String)
	 ctx.startRoute(routeId)
	 }
	 }*/

	/**
	 * <p>stopRoute</p>
	 *
	 * @params routeId
	 * @see
	 * @returns
	 */
	/*def stopRoute(routeId){
	 def cr = ComputeRoute.findById(routeId)
	 if (cr) {
	 def ctxKey = cr.contextId?:Context.DEFAULT_CONTEXT_KEY
	 def ctx = ctxMap.get(ctxKey as String)
	 ctx.stopRoute(routeId)
	 }
	 }*/

	/**
	 * <p>removeRoute</p>
	 *
	 * @params routeId
	 * @see
	 * @returns
	 */
	/*def removeRoute(routeId){
	 def cr = ComputeRoute.findById(routeId)
	 def returnVal
	 if (cr) {
	 def ctxKey = cr.contextId?:Context.DEFAULT_CONTEXT_KEY
	 def ctx = ctxMap.get(ctxKey as String)
	 returnVal = ctx.removeRoute(routeId)
	 }
	 return returnVal
	 }*/

	/**
	 * <p>suspendRoute</p>
	 *
	 * @params routeId
	 * @see
	 * @returns
	 */
	/*def suspendRoute(routeId){
	 def cr = ComputeRoute.findById(routeId)
	 if (cr) {
	 def ctxKey = cr.contextId?:Context.DEFAULT_CONTEXT_KEY
	 def ctx = ctxMap.get(ctxKey as String)
	 ctx.suspendRoute(routeId)
	 }
	 }*/

	/**
	 * <p>resumeRoute</p>
	 *
	 * @params routeId
	 * @see
	 * @returns
	 */
	/*def resumeRoute(routeId){
	 def cr = ComputeRoute.findById(routeId)
	 if (cr) {
	 def ctxKey = cr.contextId?:Context.DEFAULT_CONTEXT_KEY
	 def ctx = ctxMap.get(ctxKey as String)
	 ctx.resumeRoute(routeId)
	 }
	 }*/

	@Override
	public void setApplicationContext(ApplicationContext appCtx)	throws BeansException {
		this.appCtx = appCtx
	}

	/**
	 * Get Endpoint Url without query parameters.
	 *
	 * @param endpointUrl
	 * @return
	 */
	private String getEndpointUrlWithoutQueryParam(String endpointUrl) {
		String endpointUrlWithoutQueryParam = endpointUrl.split("\\?")[0];

		if (endpointUrlWithoutQueryParam.contains("att-dme2-servlet:///")) {
			endpointUrlWithoutQueryParam = endpointUrlWithoutQueryParam
					.replace("att-dme2-servlet:///", "/");
		} else if (endpointUrlWithoutQueryParam.contains("att-dme2-servlet://")) {
			endpointUrlWithoutQueryParam = endpointUrlWithoutQueryParam
					.replace("att-dme2-servlet://", "/");
		} else if (endpointUrlWithoutQueryParam.contains("servlet:/")) {
			endpointUrlWithoutQueryParam = endpointUrlWithoutQueryParam
					.replace("att-dme2-servlet:/", "/");
		}

		logger.debug("endpointUrlWithoutQueryParam"
				+ endpointUrlWithoutQueryParam);
		return endpointUrlWithoutQueryParam;
	}

}






