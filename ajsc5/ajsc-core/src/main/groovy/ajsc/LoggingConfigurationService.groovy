/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc


import ch.qos.logback.classic.*
import ch.qos.logback.classic.encoder.*
import ch.qos.logback.classic.spi.*
import ch.qos.logback.classic.turbo.ReconfigureOnChangeFilter
import ch.qos.logback.classic.net.*
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.core.*
import ch.qos.logback.core.encoder.*
import ch.qos.logback.core.util.*
import ch.qos.logback.core.rolling.*
import ch.qos.logback.core.sift.*
import ch.qos.logback.core.status.InfoStatus
import ch.qos.logback.core.status.Status
import ch.qos.logback.core.status.StatusListener

import ch.qos.logback.core.joran.spi.JoranException
import ch.qos.logback.core.util.StatusPrinter


import org.slf4j.LoggerFactory
import ajsc.util.MessageMgr

class LoggingConfigurationService implements  LoggerContextListener //, StatusListener
{
	
	static expose = ['jmx']
	

	final static log = LoggerFactory.getLogger(LoggingConfigurationService.class)
	
	def static NAMESPACE = "ajsc"
	def static SERVICENAME = "LoggingWonderlandService"
	def static TOPIC_NAME = "ajsc.LoggingWonderlandService.topic"
	def static ADMIN_TOPIC = "ajsc.admin.topic"
	
	
	// default configuration variables
	// LoggerContext Defaults
	static def defaultLoggerContext = (LoggerContext) LoggerFactory.getILoggerFactory()
	
	//  Encoder & Layout Defaults
	static def defaultEncoderType = PatternLayoutEncoder.class
	static def defaultLayoutPattern = "%d [%thread] %-5level %logger{128} - %msg%n"
	static def defaultLayoutAutoStart = true
	static def defaultEncoderImFlush = true		// quadruples throughput when set to false
	static def defaultEncoderAutoStart = true
	
	// Generic Filter defaults
	static def defaultFilterType = ThresholdFilter.class
	static def defaultFilterAutoStart = true
	
	// ThresholdFilter & LevelFilter Defaults
	static def defaultThresholdFilterLevel = "DEBUG"	// threshold filters accept all + above
	static def defaultLevelFilterLevel = "INFO"  		// level filters are level exclusive
	
	// Policy Defaults
	static def defaultRollingPolicyType = FixedWindowRollingPolicy.class
	static def defaultRollingPolicyAutoStart = true
	static def defaultRollingPolicyMinIndex = 1
	static def defaultRollingPolicyMaxIndex = 9
	static def defaultRollingPolicyCleanHistoryAtStart = false	// not Relavent to FixedWindowRollingPolicy type
	static def defaultTriggerPolicyType = SizeBasedTriggeringPolicy.class
	static def defaultTriggerPolicyAutoStart = true
	static def defaultSizeBasedTriggerPolicySize = "5MB"
	
	// RollingFileAppender Defaults
	static def defaultRollingFileAppenderAutoStart = true
	
	// SyslogAppender Defaults
	static def defaultSyslogAppenderHost 		= 'localhost'
	static def defaultSyslogAppenderPort 		= '512'
	static def defaultSyslogAppenderAutoStart 	= true
	static def defaultSyslogFacility			= "USER"
	static def defaultSyslogSuffixPattern 		= "[%thread] %logger %msg"
	static def defaultSyslogExcludeStackTrace	= false
	static def defaultSyslogStackTracePattern	= '\t'
	
	// Log directory Defaults ( hopefully changed during init()->configureLoggingHome() calls)
	static def ajscHome  = "${System.getProperty('user.dir')}"
	static def loggingHome = "${System.getProperty('user.dir')}/log"
	
	// Auto logging defaults
	static def autoNsLoggingDisabled = false
	static def autoNsLoggingAdditive = false	// whether autoNsLoggers allow multiple sim. appenders
	static def autoNsAppendersDisabled = false
	static def autoNsAppendersUseSubDirs = false
	static def autoNsLoggingAppenderType = RollingFileAppender.class
	
	// we store the names of "autoNamespaceLogging" loggers and appenders so that we can 
	// enumerate them as a non-config-file-persistent subset of all loggers/appenders in the 
	// logger context
	static def namespaceLoggerNames = [:]
	static def namespaceAppenderNames = []
	
	def getMessageMap(method, msgnum) {
		return [
			"MODULE":"ajsc",
			"COMPONENT":"LoggingWonderlandService",
			"METHOD":method,
			"MSGNUM":msgnum
			]
	}
	
	
	def started = false
	
    def init() {
		
		configureLoggingHome()
		configureNsAutoLogging()
		if(!started) {
			
			start()
		}

		println "LoggingWonderland service initialized successfully."
    }
	
	/**
	 * Configure the "loggingHome" directory based on a priority order of possibilities
	 *  A. (if writable) LOGGING_HOME as either prop or env variable (property wins),  
	 *  B. (if writable) "log" subdir of AJSC_HOME as either prop or env variable (prop wins)
	 *  C. (if writable) "log" subdir of original "user.dir" (current working directory)
	 *  D. subdirectory of Unix OS "/tmp" dir (always writable by all unix users, but only
	 *     unix uid of this app, or root, can read the items it creates/appends to there)
	 *  
	 *  Also:  prints to console which method was selected and the source configuration for it
	 */
	def configureLoggingHome() {
	
		def loggingHomeErrors = []
		def selectedAjscHomeSource = 'CURRENT_WORKING_DIRECTORY'
		def selectedLoggingHomeSource = 'DERIVED_FROM_CURRENT_WORKING_DIRECTORY'
		
		def LMETHOD = "configureLoggingHome"
		
		if(System.getenv('AJSC_HOME') && (!"".equals(System.getenv('AJSC_HOME')))) {
			println "${SERVICENAME}.configureLoggingHome() saw AJSC_HOME set in ENV"
			ajscHome = System.getenv('AJSC_HOME')
			selectedAjscHomeSource = 'ENV'
		} 
		
		if(System.getProperty('AJSC_HOME') && (!"".equals(System.getProperty('AJSC_HOME')))) {	
			println "${SERVICENAME}.configureLoggingHome() saw AJSC_HOME set in System.Properties"
			ajscHome = System.getProperty('AJSC_HOME')
			selectedAjscHomeSource = 'SYSTEM_PROPERTY'
		}
		
		println "${SERVICENAME}.configureLoggingHome() going with AJSC_HOME value as per ${selectedAjscHomeSource}"
		


		if(System.getenv('LOGGING_HOME') && (!"".equals(System.getenv('LOGGING_HOME')))) {
			println "${SERVICENAME}.configureLoggingHome() saw LOGGING_HOME set in ENV"
			def elHomePath = new File(System.getenv('LOGGING_HOME'))
			if(elHomePath.exists()) {
				if(elHomePath.isDirectory() && elHomePath.canWrite() ) {
					loggingHome = System.getenv('LOGGING_HOME')
					selectedLoggingHomeSource = 'ENV'
				} else {
					loggingHomeErrors.add('user specified LOGGING_HOME from ENV is not a directory or is not writable!')
				}
			} else {
						
				if(elHomePath.parentFile.exists() && 
				   elHomePath.parentFile.isDirectory() && 
				   elHomePath.parentFile.canWrite() ) {
						loggingHome = System.getenv('LOGGING_HOME')
						selectedLoggingHomeSource = 'ENV'
				} else {
					loggingHomeErrors.add('user specified LOGGING_HOME from ENV does not exist and cannot be created')
				}
			}
			elHomePath = null
		}
		if(System.getProperty('LOGGING_HOME') && (!"".equals(System.getProperty('LOGGING_HOME')))) {
			println "${SERVICENAME}.configureLoggingHome() saw LOGGING_HOME set in System.Properties"
			def slHomePath = new File(System.getProperty('LOGGING_HOME'))
			if(slHomePath.exists()) {
				if(slHomePath.isDirectory() && slHomePath.canWrite()) {
					loggingHome = System.getProperty('LOGGING_HOME')
					selectedLoggingHomeSource = 'SYSTEM_PROPERTY'
				} else {
					loggingHomeErrors.add('user specified LOGGING_HOME from System.Properties is not a directory or is not writable!')
				}
			} else {
				if(slHomePath.parentFile.exists() &&
				   slHomePath.parentFile.isDirectory() &&
				   slHomePath.parentFile.canWrite()) {
						loggingHome = System.getProperty('LOGGING_HOME')
						selectedLoggingHomeSource = 'SYSTEM_PROPERTY'
				} else {
					loggingHomeErrors.add('user specified LOGGING_HOME from System.Properties does not exist and cannot be created')
				}
			}
			slHomePath = null
		}
		
		
		if('DERIVED_FROM_CURRENT_WORKING_DIRECTORY'.equals(loggingHome)) {
			if(!'CURRENT_WORKING_DIRECTORY'.equals(selectedAjscHomeSource)) {
				def nHomePath = new File("${ajscHome}")
				def nlHomePath = new File("${ajscHome}/log")
				if(nHomePath.exists() ) {
				  if(!nlHomePath.exists() && !nHomePathCanWrite() ) {
					selectedAjscHomeSource = 'USER_AJSC_HOME_FAILED'
				  	loggingHomeErrors.add('AJSC_HOME is not writable and AJSC_HOME/log does not exist')	
				  } else {
				  	if(nlHomePath.exists() && !nlHomePathCanWrite() ) {
						selectedAjscHomeSource = 'USER_AJSC_HOME_FAILED'
					   	loggingHomeErrors.add('AJSC_HOME/log exists but is not writable')
					 } else {
					 	loggingHome = "${ajscHome}/log"
					 	selectedLoggingHomeSource = 'DERIVED_FROM_USER_SPECIFIED_AJSC_HOME'
					 }
					  
				  }
				} else {
				  selectedAjscHomeSource = 'USER_AJSC_HOME_FAILED'
				  loggingHomeErrors.add('AJSC_HOME base directory does not exist')
				}
				nHomePath = null
				nlHomePath = null
				
			}
			if( 'CURRENT_WORKING_DIRECTORY'.equals(selectedAjscHomeSource) ||
				'USER_AJSC_HOME_FAILED'.equals(selectedAjscHomeSource) ) {
				def lHomePath = new File(loggingHome)
				if((!lHomePath.exists()) || (!lHomePath.canWrite()) || (!lHomePath.isDirectory)) {
					loggingHomeErrors.add('unable to write to "log" subdirectory of current working directory')
					loggingHome = '/tmp'  // last resort "world writeable" directory fallback
					selectedLoggingHomeSource = "/TMP_FALLBACK_AFTER_FAILING_WITH_${selectedLoggingHomeSource}"
					lHomePath = null
				}
			
			}
			
		}
		
		loggingHomeErrors.each { it ->
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,1),["${it}"] as Object[])
		}
		// want this on the console / Catalina.out
		println "loggingHome will be ${loggingHome} as per ${selectedLoggingHomeSource}"
		
		
		
	}
	
	/**
	 * Fetch properties that can be used to configure non-default items for autologging
	 * and store in static vars from a series of source based priorities
	 * 
	 */
	def configureNsAutoLogging() {
		
		def autoNsLoggingDisabledEnv = System.getenv('AUTO_NS_LOGGING_DISABLED')
		def autoNsLoggingDisabledProp = System.getProperty('AUTO_NS_LOGGING_DISABLED')
		
		if(autoNsLoggingDisabledEnv != null) {
			if("true".equals(autoNsLoggingDisabledEnv?.toLowerCase()))
				autoNsLoggingDisabled = true
		}
		if(autoNsLoggingDisabledProp != null) {
			if("true".equals(autoNsLoggingDisabledProp?.toLowerCase()))
				autoNsLoggingDisabled = true
			else if("false".equals(autoNsLoggingDisabledProp?.toLowerCase()))
				autoNsLoggingDisabled = false // override by property if both are set 
		}
		
		def autoNsLoggingAdditiveEnv = System.getenv('AUTO_NS_LOGGING_ADDITIVE')
		def autoNsLoggingAdditiveProp = System.getProperty('AUTO_NS_LOGGING_ADDITIVE')
		
		if(autoNsLoggingAdditiveEnv != null) {
			if("true".equals(autoNsLoggingAdditiveEnv?.toLowerCase()))
				autoNsLoggingAdditive = true
		}
		if(autoNsLoggingAdditiveProp != null) {
			if("true".equals(autoNsLoggingAdditiveProp?.toLowerCase()))
				autoNsLoggingAdditive = true
			else if("false".equals(autoNsLoggingAdditiveProp?.toLowerCase()))
				autoNsLoggingAdditive = false // override by property if both are set
		}
		
		def autoNsAppendersDisabledEnv = System.getenv('AUTO_NS_APPENDERS_DISABLED')
		def autoNsAppendersDisabledProp = System.getProperty('AUTO_NS_APPENDERS_DISABLED')
		
		if(autoNsAppendersDisabledEnv != null) {
			if("true".equals(autoNsAppendersDisabledEnv?.toLowerCase()))
				autoNsAppendersDisabled = true
		}
		if(autoNsLoggingDisabledProp != null) {
			if("true".equals(autoNsAppendersDisabledProp?.toLowerCase()))
				autoNsAppendersDisabled = true
			else if("false".equals(autoNsAppendersDisabledProp?.toLowerCase()))
				autoNsAppendersDisabled = false // override by property if both are set
		}
		
		def autoNsLoggingAppenderTypeEnv = System.getenv('AUTO_NS_LOGGING_APPENDER_TYPE')
		def autoNsLoggingAppenderTypeProp = System.getProperty('AUTO_NS_LOGGING_APPENDER_TYPE')
		
		if(autoNsLoggingAppenderTypeEnv != null) {
			if("syslogappender".equals(autoNsLoggingAppenderTypeEnv?.toLowerCase()))
				autoNsLoggingAppenderType = SyslogAppender.class
		}
		if(autoNsLoggingAppenderTypeProp != null) {
			if("syslogappender".equals(autoNsLoggingAppenderTypeProp?.toLowerCase()))
				autoNsLoggingAppenderType = SyslogAppender.class
			else if("rollingfileappender".equals(autoNsAppendersDisabledProp?.toLowerCase()))
				autoNsLoggingAppenderType = RollingFileAppender.class // override by property if both are set
		}
		
		def autoNsAppendersUseSubDirsEnv = System.getenv('AUTO_NS_APPENDERS_USE_SUBDIRS')
		def autoNsAppendersUseSubDirsProp = System.getProperty('AUTO_NS_APPENDERS_USE_SUBDIRS')
		
		if(autoNsAppendersUseSubDirsEnv != null) {
			if("false".equals(autoNsAppendersUseSubDirsEnv?.toLowerCase()))
				autoNsAppendersUseSubDirs = false
		}
		if(autoNsLoggingDisabledProp != null) {
			if("false".equals(autoNsAppendersUseSubDirsProp?.toLowerCase()))
				autoNsAppendersUseSubDirs = false
			else if("true".equals(autoNsAppendersUseSubDirsProp?.toLowerCase()))
				autoNsAppendersUseSubDirs = true // override by property if both are set
		}
		
	}
	
	/**
	 * starts the service
	 */
	def start() {
		
		if(defaultLoggerContext?.getCopyOfListenerList()?.every { it.equals(this)==false })
			defaultLoggerContext?.addListener(this)
		started = true
	}
	
	/**
	 *  stops the service
	 */
	def stop() {
		
		if(!(defaultLoggerContext?.getCopyOfListenerList()?.every { it.equals(this)==false}))
			defaultLoggerContext?.removeListener(this)
		started = false
	}
	
	/**
	 *  shuts down service
	 */
	def shutdown() {
		
		if(!(defaultLoggerContext?.getCopyOfListenerList()?.every { it.equals(this)==false}))
			defaultLoggerContext?.removeListener(this)

		started = false
	}

	/**
	 *   currently a placeholder method not invoked by anyone but unit tests, maybe useful for
	 *   ui later
	 */
	def invokeConfigurator(params=null, LoggerContext lc=defaultLoggerContext) {
		def LMETHOD = "invokeConfigurator(params=null, LoggerContext lc=defaultLoggerContext)"
		try {
			
			JoranConfigurator configurator = new JoranConfigurator();
			configurator.setContext(lc)
			
			lc.reset()
			
			
			if(!params) {
				// by default are just doing a "reset" and reattaching ourselves to the context
				configurator.doConfigure(System.getProperty('logback.configurationFile'))
				//configurator.doConfigure('resources/logback.xml')
				
				if(defaultLoggerContext?.getCopyOfListenerList()?.every { it.equals(this)==false })
					defaultLoggerContext?.addListener(this)
			} else {
			// TODO:  finish invokeConfigurator to support later file config override from persistence layer
			}
			

			
		} catch (JoranException je) {
			MessageMgr.logMessage(log, 'error', getMessageMap(LMETHOD,1),je,["{je}"] as Object[])
			//je.printStackTrace()
		}
	}
	
	/**
	 *   Standard Hazelcast Pub sub control for this service if wanting to add to Beneficium
	 */
	
	
	/**
	 *  get a "PatternLayout" reference (unlikely to be called directly, more of a sub-builder)
	 *  
	 *  {link @PatternLayout} are a dependency of Encoders, which most Appenders need
	 *  
	 *  @param params	map of configuration options for the PatternLayout ref to be returned
	 */
	def getPatternLayoutInstance(params=null) {

		def retLayout = null
		def LMETHOD = "getPatternLayoutInstance(params=null)"
		
		try {
			if(!params) { params=[:] }
			def context = params?.context ?: defaultLoggerContext
			def pattern = params?.pattern ?: defaultLayoutPattern
			def doStart = params?.layoutAutoStart ?: defaultLayoutAutoStart
			if((!params?.forEncoder)&&(!params?.forAppender)) {
				retLayout = new PatternLayout()
				retLayout.setContext(context)
				retLayout.setPattern(pattern)
				if(doStart) retLayout.start()
				return retLayout
			} else {
				// TODO:  implement support for getting existing PatternLayout ref from encoder/appender
				throw new UnsupportedOperationException("""${SERVICE}.getPatternLayoutInstance(params) \
					does not currently support fetching the PatternLayout object reference from an \
					existing Appender or Encoder."""
					.toString())	
			}
			
		}
		catch (UnsupportedOperationException uoe) {
			throw uoe
		}
		catch (all) {
			MessageMgr.logMessage(log, 'error', getMessageMap(LMETHOD,1),all,["{all}"] as Object[])
			//all.printStackTrace()
		}
	}
	
	/**
	 *  get a "Encoder" reference (unlikely to be called directly, more of a sub-builder)
	 *
	 *  {link @Encoder} are a dependency of Appenders to format log messages
	 *
	 *  @param params	map of configuration options for the Encoder to be returned
	 */
	def getEncoderInstance(params) {
		def retEncoder = null
		def LMETHOD = "getEncoderInstance(params)"
		try {
			if(!params) { params=[:] }
			
			def encType = params?.encoderType ?: defaultEncoderType	
			def context = params?.context ?: defaultLoggerContext
			def imFlush = params?.immediateFlush ?: defaultEncoderImFlush
			def doStart = params?.encoderAutoStart ?: defaultEncoderAutoStart
			
			if(!params?.forAppenderName) {
				// we're return a new encoder with the default settings
				if(encType.equals(PatternLayoutEncoder.class)) {
					retEncoder = new PatternLayoutEncoder()
					retEncoder.setContext(context)
					retEncoder.setImmediateFlush(imFlush)
					retEncoder.setPattern(getPatternLayoutInstance(params).getPattern())
					if(doStart) retEncoder.start()
				}
				
			} else {
				// TODO:  implement support for getting existing Encoder ref from Appender
				throw new UnsupportedOperationException("""LoggingWonderlandService\
					.getEncoderInstance(params) does not currently support fetching the \
					Encoder object reference from an existing Appender.""".toString())	
			}
			
		}
		catch (UnsupportedOperationException uoe) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,1),["${SERVICENAME}"] as Object[])
			throw uoe
		} 
		catch (all) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,2),all,["${SERVICENAME}","${all}"] as Object[])
		}
		
		if(retEncoder)
			log.info("{}.getEncoderInstance(params) returning filter {}", SERVICENAME, "${retEncoder}")
		else
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,3),["${SERVICENAME}"] as Object[])
		return retEncoder
		
	}
	
	/**
	 *  get a "Filter" reference (unlikely to be called directly, more of a sub-builder)
	 *
	 *  {link @Filter} are a dependency of Appenders to select "emit-eligibility" for the
	 *  logging events sent from the loggers they are attached to - dispatches to Filter
	 *  -type specific builders for Filter-type specific configuration
	 *
	 *  @param params	map of configuration options for the Filter to be returned
	 */
	def getFilterInstance(params=null) {
		def LMETHOD = "getFilterInstance(params=null)"
		def retFilter=null
		try {
			if(!params) { params = [:] }
			def doStart
			if(!params?.forAppenderName) {
				def fType   = params?.filterType ?: defaultFilterType
				doStart = params?.autoStart ?: defaultFilterAutoStart	
				/// TODO: add methods for other filter types, then add to this switch block
				switch(fType) {
					case ThresholdFilter.class:
						retFilter = getThresholdFilterInstance(params)
						break;
					//case LevelFilter.class:
					//	retFilter = getLevelFilterInstance(params)
					//	break;
					default:
						throw new UnsupportedOperationException("""\
							${SERVICE}.getFilterInstance does not currently support programmatic
							creation of this LogbackFilter filter type""".toString())
				}
			} else {
				/// TODO: add support for returning pre-existing Filter Object refs found from appender
				throw new UnsupportedOperationException("""\
							${SERVICE}.getFilterInstance does not currently support returning 
							existing filters attached to Appender instances""".toString())
			}
			
			if(doStart && (!retFilter.isStarted())) 
				retFilter?.doStart()
			
			
		}
		catch (UnsupportedOperationException uoe) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,1),["${SERVICENAME}"] as Object[])
			throw uoe
		}
		catch (all) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,2),all,["${SERVICENAME}","${all}"] as Object[])
		}
		if(retFilter)
			log.info("{}.getFilterInstance(params) returning filter {}", SERVICENAME, "${retFilter}")
		else
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,3),["${SERVICENAME}"] as Object[])
		return retFilter
		
	}
	
	
	/**
	 *  get a "ThresholdFilter" reference (unlikely to be called directly, more of a sub-builder)
	 *
	 *  {link @ThresholdFilter} are a dependency of Appenders to select "emit-eligibility" for the
	 *  logging events sent from the loggers they are attached to
	 *
	 *  @param params	map of configuration options for the ThresholdFilter to be returned
	 */
	def getThresholdFilterInstance(params=null) {
		def LMETHOD = "getThresholdFilterInstance(params=null)"
		def retFilter = null
		try {
			if(!params) { params = [:] }
			def context = params?.context ?: defaultLoggerContext
			def level   = params?.thresholdLevel ?: defaultThresholdFilterLevel
			def doStart = params?.autoStart ?: defaultFilterAutoStart
			
			if(!params?.forAppenderName) {
				retFilter = new ThresholdFilter()
				retFilter.setContext(context)
				retFilter.setLevel(level)
				if(doStart) {
					retFilter.start()
				} else {}
			} else {
				/// TODO: add support for returning pre-existing Filter Object refs found from appender
				throw new UnsupportedOperationException("""\
							${SERVICENAME}.getThresholdFilterInstance(params) does not currently \
							support returning existing filters attached to Appender instances"""
							.toString())
			}
		}
		catch (UnsupportedOperationException uoe) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,1),["${SERVICENAME}"] as Object[])
			throw uoe
		}
		catch (all) {
			def something = all
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,2),all,["${SERVICENAME}","${all}"] as Object[])
		}
		if(retFilter)
			log.info("{}.getThresholdFilterInstance(params) returning filter {}", SERVICENAME, "${retFilter}")
		else
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,3),["${SERVICENAME}"] as Object[])
		return retFilter
	}
		
	/**
	 *  get a "TriggeringPolicy" reference (unlikely to be called directly, more of a sub-builder)
	 *
	 *  {link @TriggeringPolicy} are a dependencies of certain classes of Appenders like the
	 *  RollingFileAppender or SMTP appender that determine when to execute certain types of actions
	 *  that do not correspond to every logging event. 
	 *  <p>
	 *  SizeBasedTriggeringPolicies are all that are currently supported for Birch 2
	 *
	 *  @param params	map of configuration options for the TriggeringPolicy to be returned
	 */
	def getTriggeringPolicyInstance(params) {
		def retPolicy=null
		def LMETHOD = "getTriggeringPolicyInstance(params)"
		try {
			if(!params) { params = [:] }
			if(!params?.forAppenderName ){
				def triggerPolicyType = params?.triggerPolicyType ?: SizeBasedTriggeringPolicy.class
				def context = params?.context ?: defaultLoggerContext	
				def doStart = params?.triggerPolicyAutoStart ?: defaultTriggerPolicyAutoStart
				switch(triggerPolicyType) {
					case SizeBasedTriggeringPolicy.class:
						def maxSize = params?.maxSize ?: defaultSizeBasedTriggerPolicySize
						retPolicy = new SizeBasedTriggeringPolicy()
						retPolicy.setMaxFileSize(maxSize)
						retPolicy.setContext(context)
						break
					default:
						throw new UnsupportedOperationException("")	
				}
				if(doStart && (!retPolicy?.isStarted()))
					retPolicy?.start()
				

			} else {
				throw new UnsupportedOperationException("""\
						${SERVICENAME}.getThresholdFilterInstance(params) does not currently \
						support returning existing filters attached to Appender instances"""
						.toString())
			}
				
		}
		catch (UnsupportedOperationException uoe) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,1),["${SERVICENAME}"] as Object[])
			throw uoe
		} 
		catch (all) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,2),all,["${SERVICENAME}","${all}"] as Object[])
		}
		if(retPolicy)
			log.info("{}.getTriggeringPolicyInstance(params) returning filter {}", SERVICENAME, "${retPolicy}")
		else
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,3),["${SERVICENAME}"] as Object[])		
		return retPolicy
	}

	/**
	 *  get a "RollingPolicy" object (unlikely to be called directly, more of a sub-builder)
	 *
	 *  {link @RollingPolicy} are a dependencies unique to RollingFileAppender type of appender
	 *  
	 *  NOTE: RollingPolicy is one of the weird objects that needs to know about its parent
	 *  appender before starting...
	 *
	 *  @param params	map of configuration options for the RollingPolicy to be returned
	 */
	def getRollingPolicyInstance(params=null, RollingFileAppender rfParentAppender) {
		def retPolicy = null
		def LMETHOD = "getRollingPolicyInstance(params=null, RollingFileAppender rfParentAppender)"
		try {
			if(!params) { params=[:] }
			if(! rfParentAppender) {
				throw new UnsupportedOperationException(
					"rfParentAppender is required to build a new RollingPolicy")
			}
			if(!params?.forAppender) {
				def rollingPolicyType = params?.rollingPolicyType ?: defaultRollingPolicyType
				def doStart	= params?.rollingPolicyAutoStart ?: defaultRollingPolicyAutoStart
				
				switch(rollingPolicyType) {
					case FixedWindowRollingPolicy.class:
						retPolicy = getFixedWindowRollingPolicyInstance(params, rfParentAppender)
						break
					case TimeBasedRollingPolicy.class:
						retPolicy = getTimeBasedRollingPolicyInstance(params, rfParentAppender)
						break
					default:
						throw new UnsupportedOperationException("")
				}
				
				retPolicy.setContext(rfParentAppender.getContext())
				retPolicy.setParent(rfParentAppender)
				if(doStart && (!retPolicy?.isStarted()))
					retPolicy.start()
				
			} else {
				throw new UnsupportedOperationException("""\
						${SERVICENAME}.getRollingPolicyInstance(params, rfParentAppender) does not currently \
						support returning existing policies attached to Appender instances"""
						.toString())
			}
	    } 
	    catch (UnsupportedOperationException uoe) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,1),["${SERVICENAME}"] as Object[])	
			throw uoe
		} 
		catch (all) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,2),all,["${SERVICENAME}","${all}"] as Object[])
		}
		
		if(retPolicy)
			log.info("{}.getRollingPolicyInstance(params, rfParentAppender) returning policy {}", SERVICENAME, "${retPolicy}")
		else
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,3),["${SERVICENAME}"] as Object[])
		
		return retPolicy
	}
	
	/**
	 *  get a "FixedWindowRollingPolicy" object (unlikely to be called directly, more a sub-builder)
	 *
	 *  {link @RollingPolicy} are a dependencies unique to RollingFileAppender type of appender
	 *
	 *  NOTE: RollingPolicy is one of the weird objects that needs to know about its parent
	 *  appender before starting...
	 *
	 *  @param params	map of configuration options for the RollingPolicy to be returned
	 */
	def getFixedWindowRollingPolicyInstance(params, RollingFileAppender rfParentAppender) {
		def retPolicy = null
		def LMETHOD = "getFixedWindowRollingPolicyInstance(params, RollingFileAppender rfParentAppender)"
		try {
			if(!params) { params=[:] }
			if(! rfParentAppender) {
				throw new UnsupportedOperationException("")
			}
			if(!params?.forAppender) {
				if(!loggingHome) configureLoggingHome()
				def minIndex = params?.minIndex ?: defaultRollingPolicyMinIndex
				def maxIndex = params?.maxIndex ?: defaultRollingPolicyMaxIndex
				def context  = rfParentAppender?.getContext() ?: defaultLoggerContext
				def doStart  = params?.appenderAutoStart ?: defaultRollingPolicyAutoStart
				def pfile = rfParentAppender?.fileName
				def pname = rfParentAppender?.getName()
				def fallbackPattern = rfParentAppender?.fileName?.replaceAll("\\.log","") ?: "${loggingHome.toString()}/${rfParentAppender?.getName()}"
				def fNamePattern = params?.fnamePattern ?: "${fallbackPattern}.%i.log.zip"
				
				retPolicy = new FixedWindowRollingPolicy()
				retPolicy.setContext(context)
				retPolicy.setFileNamePattern(fNamePattern)
				retPolicy.setMinIndex(minIndex)
				retPolicy.setMaxIndex(maxIndex)
			
				
			} else {
				throw new UnsupportedOperationException("""\
						${SERVICENAME}.getFixedWindowRollingPolicyInstance(params, rfParentAppender) does not currently \
						support returning existing policies attached to Appender instances"""
						.toString())
			}
		}
		catch(UnsupportedOperationException uoe) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,1),["${SERVICENAME}"] as Object[])
			throw uoe
		}
		catch(all) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,2),all,["${SERVICENAME}","${all}"] as Object[])
		}
		
		if(retPolicy)
			log.info("{}.getFixedWindowRollingPolicyInstance(params, rfParentAppender) returning policy {}", SERVICENAME, "${retPolicy}")
		else
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,3),["${SERVICENAME}"] as Object[])		
		return retPolicy
	}
	
	/**
	 *  get a "TimeBasedRollingPolicy" object (unlikely to be called directly, more a sub-builder)
	 *
	 *  {link @RollingPolicy} are a dependencies unique to RollingFileAppender type of appender
	 *
	 *  NOTE: RollingPolicy is one of the weird objects that needs to know about its parent
	 *  appender before starting...
	 *
	 *  @param params	map of configuration options for the RollingPolicy to be returned
	 */
	def getTimeBasedRollingPolicyInstance(params=null, RollingFileAppender rfParentAppender) {
		def retPolicy = null
		def LMETHOD = "getTimeBasedRollingPolicyInstance(params=null, RollingFileAppender rfParentAppender)"
		try {
			if(!params) { params=[:] }
			if(! rfParentAppender) {
				throw new UnsupportedOperationException("")
			}
			if(!params?.forAppender) {
				def maxHistory 		= params?.maxHistory ?: defaultRollingPolicyMaxHistory
				def context  		= rfParentAppender?.getContext() ?: defaultLoggerContext
				def cleanOnStart	= params?.cleanHistoryOnStart ?: defaultRollingPolicyCleanHistoryAtStart
				def fNamePattern 	= params?.fnamePattern ?://Bryan Poole changed getFileName to getName like in the second part of this line
					"${ rfParentAppender?.getName().replace('.log','') ?: ${loggingHome}/${rfParentAppender?.getName()}}.%i.log.zip"
				
				retPolicy = new TimeBasedRollingPolicy()
				retPolicy.setContext(context)
				retPolicy.setFileNamePattern(fNamePattern)
				retPolicy.setMaxHistory(maxHistory)
				
			} else {
				throw new UnsupportedOperationException("""\
						${SERVICENAME}.getTimeBasedRollingPolicyInstance(params, rfParentAppender) does not currently \
						support returning existing policies attached to Appender instances"""
						.toString())
			}
		}
		catch(UnsupportedOperationException uoe) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,1),["${SERVICENAME}"] as Object[])
			throw uoe
		}
		catch(all) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,2),all,["${SERVICENAME}","${all}"] as Object[])
		}
		
		if(retPolicy)
			log.info("{}.getTimeBasedRollingPolicyInstance(params, rfParentAppender) returning policy {}", SERVICENAME, "${retPolicy}")
		else
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,3),["${SERVICENAME}"] as Object[])		
		return retPolicy
	}
	
	/**
	 *  get a "RollingFileAppender" object 
	 *
	 *  {link @RollingFileAppender} - returns existing appender if found, or creates a new one
	 *
	 *  NOTE: RollingPolicy is one of the weird objects that needs to know about its parent
	 *  appender before starting...
	 *
	 *  @param params	map of configuration options for the RollingFileAppender to be returned
	 */
	def getRollingFileAppenderInstance(params) {
		def LMETHOD = "getRollingFileAppenderInstance(params)"
		def retAppender = null
		try {
		
			if(!params) { params = [:] }
			if(!params?.appenderName) {
				throw new UnsupportedOperationException("${SERVICENAME}.getRollingFileAppenderInstance(params): "+
					"params['appenderName'] is required part of appender name")	
			} else {
				def  rfName = params?.appenderName
				def  rfFileName = params?.rfFileName
				def  context = params?.context ?: defaultLoggerContext
				def  doStart = params?.appenderAutoStart ?: defaultRollingFileAppenderAutoStart
	
				def  encoder = params?.encoder
				def  rpolicy = params?.rollingPolicy 
				def  tpolicy = params?.triggerPolicy
				
				def  rfFiltersConf = params?.rfFiltersConf
				def  rfFilters = [] 
				
				
				retAppender = getAppenders([appenderName:rfName])[0]
				
				// if not an existing appender we'll need to build one
				if(!retAppender) {
					
					if(rfFiltersConf) {
						
							rfFilters = getFilters(rfFiltersConf)
					} else {
						if(params?.thresholdFilterLevel) {
							rfFilters.add(getThresholdFilterInstance(params.thresholdFilterLevel))
						} else {
							rfFilters.add(getThresholdFilterInstance())  // will add one WARN level threshold Filter
						}
					}
					
					retAppender = new RollingFileAppender()
					retAppender.setContext(defaultLoggerContext)
					retAppender.setName(rfName)
					rfFilters.each { filter ->
						retAppender.addFilter(filter)
					}
					def useSpecificFilePath = false
					if(rfFileName) {
						def rfFile = new File(rfFileName)
						if(rfFile.exists() && rfFile.canWrite()) {
							useSpecificFilePath = true
						} else if (rfFile?.parentFile?.exists() && rfFile?.parentFile?.canWrite()) {
							useSpecificFilePath = true
						}
						if(!useSpecificFilePath)
							log.warn("""{}.getRollingFileAppenderInstance(params) found {} was \
								not a writable/creatable file path, reverting to use of \
								appender name for filename in the {} directory""", SERVICENAME, 
								rfFileName, loggingHome)
					} 
					if(useSpecificFilePath) {
						retAppender.setFile(rfFileName)
					} else {
						retAppender.setFile("${loggingHome}/${rfName}.log")
					}
					if(!encoder || !(encoder instanceof Encoder)) { 
						encoder = getEncoderInstance(params ?: [:])
					} 
					if(!rpolicy || (!rpolicy instanceof RollingPolicyBase)) {
						rpolicy = getRollingPolicyInstance(params, retAppender)
					}
					if(!tpolicy || (!tpolicy instanceof TriggeringPolicyBase)) {
						tpolicy = getTriggeringPolicyInstance(params ?: [:])
					}
					retAppender.setEncoder(encoder)
					retAppender.setRollingPolicy(rpolicy)
					retAppender.setTriggeringPolicy(tpolicy)
					

					if(doStart && (!retAppender?.isStarted()))
						retAppender.start()
					
				}
				
				
			}
			
		}  catch (UnsupportedOperationException uoe) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,1),["${SERVICENAME}"] as Object[])
			throw uoe
			
 		}  catch (all) {
		 	MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,2),all,["${SERVICENAME}","${all}"] as Object[])
		}
		
		if(retAppender) {
			log.info("{}.getRollingFileAppenderInstance(params) returning appender {}", SERVICENAME, "${retAppender}")
		} else {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,3),["${SERVICENAME}"] as Object[])
		}
		
		return retAppender
		
	}
	
	/**
	 *  get a "SyslogAppender" object
	 *
	 *  {link @SyslogAppender} - returns existing appender if found, or creates a new one
	 *
	 *  NOTE: RollingPolicy is one of the weird objects that needs to know about its parent
	 *  appender before starting...
	 *
	 *  @param params	map of configuration options for the RollingFileAppender to be returned
	 */
	def getSyslogAppenderInstance(params) {
		def retAppender=null
		def LMETHOD = "getSyslogAppenderInstance(params)"
		try {
			
			if(!params) { params = [:] }
			if(!params?.appenderName) {
				throw new UnsupportedOperationException("""${SERVICENAME}.getSyslogAppenderInstance(params) \
					requires map entry in params with appenderName=<USER_SPECIFIED_NAME>""")
			}
			def  slname = params?.appenderName 			// name this appender...
			def  context = params?.context ?: defaultLoggerContext
			def  doStart = params?.appenderAutoStart ?: defaultSyslogAppenderAutoStart
			def  slHost = params?.syslogHost ?: defaultSyslogAppenderHost
			def  slPort = params?.syslogPort ?: defaultSyslogAppenderPort
			def  slFacility = params?.syslogFacility ?: defaultSyslogFacility
			def  slSuffix   = params?.slSuffix ?: defaultSyslogSuffixPattern
			def  slExcludeSt = params?.slExcludeSt ?: defaultSyslogExcludeStackTrace
			def  slPatternSt = params?.slEatternSt ?: defaultSyslogStackTracePattern
			def  slFiltersConf = params?.FiltersConf  
			def  slFilters = [] as Set 
			


			retAppender = getAppenders([appenderName:slname])[0]
			if(!retAppender) {
				if(slFiltersConf) {
					
						slFilters = getFilters(slFiltersConf)
					} else {
						if(params?.thresholdFilterLevel) {
							slFilters.add(getThresholdFilterInstance(params.thresholdFilterLevel))
						} else {
							slFilters.add(getThresholdFilterInstance())  // will add one WARN level threshold Filter
						}
				}
					
				retAppender = new SyslogAppender()
				retAppender.setName(slname)
				slFilters.each() { filter ->
					retAppender.addFilter(filter)
				}
				retAppender.setContext(context)
				retAppender.setSyslogHost(slHost) 
				retAppender.setFacility(slFacility)
				retAppender.setPort(slPort?.toInteger())
				retAppender.setSuffixPattern(slSuffix)
				retAppender.setThrowableExcluded(slExcludeSt)
				retAppender.setStackTracePattern(slPatternSt)
				
				if(doStart && (!retAppender?.isStarted()))
					retAppender?.start()
			}
			
		
		}
		catch (UnsupportedOperationException uoe) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,1),["${SERVICENAME}"] as Object[])
			throw uoe
		} 
		catch (all) {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,2),all,["${SERVICENAME}","${all}"] as Object[])
		}
		if(retAppender) {
			log.info("{}.getSyslogAppenderInstance(params) returning appender {}", SERVICENAME, "${retAppender}")
		} else {
			MessageMgr.logMessage(log,'error',getMessageMap(LMETHOD,3),["${SERVICENAME}"] as Object[])
		}
		
		return retAppender
	}
	
	
	/**
	 *  return a new, existing, or complete set of loggers
	 * 
	 *  @param params  map specifying what we want (returns all existing loggers if null)
	 *  @param lc	   loggerContext to get loggers from (default LoggerContext if null) 
	 *  @return list of loggers
	 */
	def getLoggers(params=null, LoggerContext lc=defaultLoggerContext) {
		
		def loggers = []
		if(!params) { params = [:] }
		
		if(params?.loggerName && (!params?.loggerName?.equals("ALL_LOGGERS"))) {
			def retLogger = lc?.exists(params?.loggerName) ?: lc?.getLogger(params?.loggerName)
			if(params?.setAdditive == false)
				retLogger.setAdditive(false)
			loggers.add(retLogger)
			return loggers
			
		} else {
			lc?.getLoggerList()?.each { logger ->
				if(params?.loggerNames?.contains(logger.getName()) || (!params?.loggerNames))
					loggers.add(logger)
			}
			return loggers
		}


	}

	/**
	 *  return a new, existing, or complete set of Filters (not TurboFilters)
	 * 
	 *  @param params  	map specifying what we want (returns all existing loggers if null)
	 *  @param lc	   	loggerContext to get loggers from (default LoggerContext if null) 
	 *  @return 		list of filters
	 */
	def getFilters(params=null, LoggerContext lc=defaultLoggerContext) {
		def filters=[] as Set
		
		if(!params)
		{
			getAppenders(params, lc).each { appender ->
				appender.getCopyOfAttachedFiltersList().each { filter ->
					filters.add(filter)
				}
			}
		} else {
			
			if(!params?.filters) {//Bryan Poole changed from getThresholdFilter to getThresholdFilterInstance
					filters.add(getThresholdFilterInstance())
			} else {
				if(params?.filters?.tresholdFilterWithLevel) {
					filters.add(getThresholdFilter(
						['thresholdLevel':params.filters.thresholdFilterWithLevel]))
				}
			}
			
		}
		
		
		
		return filters
		
	}
	
	/**
	 *  return existing, or complete set of Appenders
	 * 
	 *  @param params  	map specifying what we want (returns all existing loggers if null)
	 *  @param lc	   	loggerContext to get loggers from (default LoggerContext if null) 
	 *  @return 		list of filters
	 */
	def getAppenders(params=null, LoggerContext lc=defaultLoggerContext) {
		
		def appenders=[] as Set
		getLoggers(params, lc).each { logger ->
			if(params && params?.appenderName) {
				// apply any rule logic for returning a subset of appenders
				logger.iteratorForAppenders().each { appender ->
					if(appender.getName().equals(params?.appenderName))
					appenders.add(appender)
				}
				
			} else {
				logger.iteratorForAppenders().each { appender ->
					appenders.add(appender)
				}
			}
		}
		return appenders as List
	}
	
	/**
	 *  Attaches a specific {@link AppenderBase} to a specific {@link Logger} 
	 *
	 *  @param logger		logger to attach appender to
	 *  @param pAppender	appender to be attached
	 */
	def attachAppenderToLogger(Logger logger=null, AppenderBase pAppender=null) {
		if(logger && pAppender) {
			def alreadyAttached = false
			logger?.iteratorForAppenders()?.each { appender ->
				if(appender?.name.equals(pAppender.name))
					alreadyAttached = true
			}
			if(!alreadyAttached) {
				logger.addAppender(pAppender)
			} else {
				log.info("appender ${pAppender.name} was already connected to logger: ${logger.name}")
			}
		}
	}
	
	/**
	 *  Detaches a specific {@link AppenderBase} from a specific {@link Logger}
	 *
	 *  @param logger		logger to detach appender from
	 *  @param pAppender	appender to be detached
	 */
	def detachAppenderFromLogger(Logger logger=null, AppenderBase pAppender=null) {
		if(logger && pAppender) {
			def alreadyAttached = false
			logger?.iteratorForAppenders()?.each { appender ->
				if(appender?.name.equals(pAppender?.name))
					alreadyAttached = true
			}
			if(alreadyAttached) {
				logger.detachAppender(pAppender)
			} else {
				log.info("appender ${pAppender.name} was not connected to logger: ${logger.name}")
			}
		}
	}
	
	/**
	 *  Attaches an {@link AppenderBase} to an {@link Logger} or multiple loggers
	 *  support "automaticNamespaceLogging" unless otherwise disabled by global system properties.
	 *
	 *  @param params		map referencing which appender to attach to which logger/loggers
	 *  @param lc			LoggerContext ref (defaults to the defaultLoggerContext)
	 */
	def attachAppenderToLoggers(params=null, LoggerContext lc=defaultLoggerContext) {
		
		if(!params) { params = [:]} 
		def loggerName  = params?.loggerName
		def loggerNames = params?.loggerNames
		def appender	= (params?.appender instanceof List) ? params?.appender[0] : params?.appender 
		
		
		if(appender) {
			if(loggerName && (!"ALL_LOGGERS".equals(loggerName))) {
				attachAppenderToLogger(getLoggers(['loggerName':loggerName])[0], appender)
			} else if (loggerNames) {
				getLoggers(params, lc)?.each() { logger ->
					attachAppenderToLogger(logger, appender)
				}
			} else {
				println("attachAppenderToLoggers: ignoring request to attach appender to loggers since"+
					"params map did not explicitly include loggerName=ALL_LOGGERS or a loggerNames list")
			}	 
			return
		}
		
		
		
		
	}
	
	/**
	 *  Detaches an {@link AppenderBase} from a {@link Logger} or multiple loggers
	 *  support "automaticNamespaceLogging" unless otherwise disabled by global system properties.
	 *
	 *  @param params		map referencing which appender to detach from which logger/loggers
	 *  @param lc			LoggerContext ref (defaults to the defaultLoggerContext)
	 */
	def detachAppenderFromLoggers(params=null, LoggerContext lc=defaultLoggerContext) {
		
		if(!params) { params = [:] }
		def loggerName  = params?.loggerName
		def loggerNames = params?.loggerNames
		def appender	= params?.appender
		
		if(appender) {
			if(loggerName && (!"ALL_LOGGERS".equals(loggerName))) {
				detachAppenderFromLogger(getLoggers(['loggerName':loggerName])[0])
			} else  if (loggerNames){
			    def loggers = getLoggers(params, lc)
				loggers?.each() { logger ->
					detachAppenderFromLogger(logger, appender)
				}
			} else {
				log.warn("attachAppenderToLoggers: ignoring request to detach appender from loggers since"+
					"params map did not explicitly include loggerName=ALL_LOGGERS or a loggerNames list")
			}
			return
		}
		
			
		
		
	}
	
	/**
	 *  Given a ajsc artefact namespace and version, pre-configures loggers and appenders to
	 *  support "automaticNamespaceLogging" unless otherwise disabled by global system properties.
	 * 
	 *  @param namespace 	ajsc artifact namespace
	 *  @param ver			ajsc artifact version
	 *  @param lc			LoggerContext ref (defaults to the defaultLoggerContext)
	 */
	def configureNamespaceLoggingSupport(String namespace, String ver, LoggerContext lc=defaultLoggerContext) {
		
		if(!autoNsLoggingDisabled) {
			
			def chkLoggerName = "${namespace}.${ver}".toString()
				
			Logger nvLogger = getLoggers(['loggerName':chkLoggerName, 'setAdditive':autoNsLoggingAdditive])[0]
			nvLogger.level = Level.DEBUG
			
			if(!namespaceLoggerNames[chkLoggerName])
				namespaceLoggerNames[chkLoggerName] = ['namespace':namespace,'ver':ver]
			
			if(!autoNsAppendersDisabled) {
			
				def nvAppender = getAppenders(['appenderName':namespace])[0]
				
				if(!nvAppender) {
				
					switch(autoNsLoggingAppenderType) {
						case RollingFileAppender.class:
							if(autoNsAppendersUseSubDirs)
								nvAppender = getRollingFileAppenderInstance([
									'appenderName':namespace, 
									'rfFileName':"${namespace}/${namespace}".toString()
									])
							else
								nvAppender = getRollingFileAppenderInstance([
									'appenderName':namespace
									])
							
						break
						case SyslogAppender.class:
							nvAppender = getSyslogAppenderInstance(['appenderName':namespace])
							break
							
						default:
							if(autoNsAppendersUseSubDirs)
								nvAppender = getRollingFileAppenderInstance([
									'appenderName':namespace, 
									'rfFileName':"${namespace}/${namespace}".toString()
									])
							else
								nvAppender = getRollingFileAppenderInstance([
									'appenderName':namespace
									])
							
					}
					if(!namespaceAppenderNames.contains(namespace))
						namespaceAppenderNames.add(namespace)
				}
				
				if(!nvLogger.isAttached(nvAppender)) {
					nvLogger.addAppender(nvAppender)
				}
			}

		}
		
	}

	/* Example of StatusListener implementation, if we ever want it,
	 *  too heavy for now and staying hooked through lifecycle events 
	 *  is not low hanging fruit
	@Override
	public void addStatusEvent(Status statusEvent) {
		if(statusEvent.getMessage().contains("...DUMP_LC_STATUS...")) {
			println "dumping status:"
			StatusPrinter.print(defaultLoggerContext)
			StatusPrinter.ps.flush()
		} else { println "statusEvent: ${statusEvent.getMessage()}" }
	}
	*/
	
	
	/**
	 *  @see LoggerStatusListener
	 */
	@Override
	public boolean isResetResistant() {
		// dont muck with this or we won't be able keep other LoggerStatusListener
		// methods hooked to the LoggerContext lifecycle events
		return true; 
		
	}
	/**
	 *  @see LoggerStatusListener
	 */
	@Override
	public void onLevelChange(Logger logger, Level level) {
		// we don't care, but have to implement
	}

	/**
	 *  @see LoggerStatusListener
	 */
	@Override
	public void onReset(LoggerContext lc) {
		// recreate the autonamespaceloggers since they aren't required to be
		// in the config file
		namespaceLoggerNames.each { key, value ->
			configureNamespaceLoggingSupport(value?.namespace, value?.ver, lc)
		}

	}
	
	/**
	 *  @see LoggerStatusListener
	 */
	@Override
	public void onStart(LoggerContext lc) {
		// recreate the autonamespaceloggers since they aren't required to be
		// in the config file
		namespaceLoggerNames.each { key, value ->
			configureNamespaceLoggingSupport(value?.namespace, value?.ver, lc)
		}
		
	
	}
	/**
	 *  @see LoggerStatusListener
	 */
	@Override
	public void onStop(LoggerContext lc) {
		// we don't care (no special tear down required)
	}
	

}
