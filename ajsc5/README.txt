Ajsc runtime requirements

// Testing jenkins job with a comment 
 initial version
===================================

System properties (set by JAVA_OPTS or command line using the '-D' argument)

    AJSC_HOME - this should have the same value as the environment variable. Both
                  this system property and the environment variable must be set.
                  DEFAULT: there is no default for AJSC_HOME

    ajscPersistence - set to "file" or "riak". "file" indicates that the ajsc
                  metadata should be stored on the file system in the $AJSC_HOME/data
                  directory. "riak" tells ajsc to use Riak NoSQL database for
                  metadata storage.
                  DEFAULT: there is no default for ajscPersistence

    clientPersistence - set to "riak" or "cassandra" to indicate which data storage
                  system should be used by routes to store application data.
                  DEFAULT: there is no default for clientPersistence
		  ** This is an optional parameter. When it is present, ClientDataService 
		  will be initialized.

    logback.configurationFile - should point to the XML file that holds the logback
                  configuration. The default version of this file can be found with
                  the ajsc source as "ajsc/conf/logback.xml."
                  DEFAULT: there is no default for logback.configurationFile

    csilog - set to "enable". "enable" indicates that the ajsc enabled the csi logging.
                 
                  DEFAULT: there is no default for csilog


    authN -       set to either "authentication-scheme-1" or "authentication-scheme-2."
                  "authentication-scheme-1" tells ajsc to use the AT&T Global Logon
                  system to authenticate ajsc users. "authentication-scheme-2" tells 			ajsc
                  to use the ajsc user database for user authentication.
                  DEFAULT: there is no default for authN
                  
   EXTERNAL_HAZELCAST - Set to "true" if you want to run Ajsc as Hazelcast client mode and access to External Hazelcast cluster.By default
                        it set to "false" and Ajsc start as Hazelcast cluster (server).
                                       
   ==========================================================DME2===============================================================
     
     AJSC_ENV - set to SOACLOUD for DME2 registration, If it is not set to SOACLOUD ajsc starts
                  without any issue but Ajsc Services would not registered to GRM.
                
     AFT_LATITUDE - AFT Latitude used to connect to AFT Discovery  
     
     AFT_LONGITUDE- AFT Longitude used to connect to AFT Discovery 
     
     AFT_ENVIRONMENT -AFT Environment used to connect to AFT Discovery, for example, AFTUAT for nonprod and AFTPRD for production.
     
     SOACLOUD_NAMESPACE - Ajsc GRM namespace use to register Ajsc service to GRM. For example, com.att.ajsc
    
     SOACLOUD_ENV_CONTEXT -SOA Cloud environment within a given SOA Cloud Platform. For example, DEV, PROD, UAT, TEST, and LAB
    
     SOACLOUD_ROUTE_OFFER - Route offer is just a logical name to group endpoints, it can carry any alphanumeric value. For example, WEST, and EAST
     
  ========================================================End of DME2==========================================================
  
  
  ==========================================================Hawtio===============================================================
     
     -Dhawtio.authenticationEnabled=false - this will disable hawtio security login,  pass this system property value 
      via the CATALINA_OPTS environment variable.
     
  ========================================================End of Hawtio==========================================================
  
  ==========================================================OpenEJB================================================================================================
     
     Download the dependency jar (javaee-api-6.0-5.jar) from Maven central repository: http://mvnrepository.com/artifact/org.apache.openejb/javaee-api/6.0-5
     and copy it to ${TOMCAT_HOME}/lib if it's not already there, before booting up Ajsc. Without this dependency jar, Ajsc won't start
     
  ========================================================End of OpenEJB===========================================================================================
 
 ========================================================Cassandra Configurable Parameters===============================================
 

The following optional conﬁg properties can be set through system variables. The values provided below are the defaults from Cassandra installation. It could change depending upon the installation 

cassandra.ip           -  "127.0.0.1" or the IP of the node hosting the cassandra 
cassandra.port         -  9042	 - default cassandra port , could be different based on the installation.	
simultaneousRequests   -  128
local.coreConnections  -  4
local.maxConnections   -  4
remote.coreConnections -  1
remote.maxConnections  -  1 


==================================================End Cassandra Configurable Parameters===============================================
 

========================================================Logback configuration for Centralized logger=================================

For logging into the Centralized Logger(syslog server in another node) the following configuration changes has to be made in the logback.xml

*** Edit the syslogHost and port (514 is default), replace it with the IP and Port of the remote centralized logger node.
    <appender name="SYSLOG" class="ch.qos.logback.classic.net.SyslogAppender">
        <syslogHost>10.1.42.36</syslogHost>
        <port>514</port>
        <facility>USER</facility>
        <suffixPattern>[%thread] %logger %msg</suffixPattern>
    </appender>

*** The appender reference(s) in the root will determine where the logging details will be sent. 

For example , in the following configuration, logging data will be sent to  remote logger and STDOUT.
 <root level="DEBUG">
    <appender-ref ref="SYSLOG" />
    <appender-ref ref="STDOUT" />
  </root>


In the second example, the logging data will be sent to remote logger, STDOUT as well as the ajsc log files(info and error).
 <root level="DEBUG">
    <appender-ref ref="ERROR" />
    <appender-ref ref="INFO" />
    <appender-ref ref="SYSLOG" />
    <appender-ref ref="STDOUT" />
  </root>

In the third example, the logging data will only be sent to STDOUT and ajsc log files(info and error).
 <root level="DEBUG">
    <appender-ref ref="ERROR" />
    <appender-ref ref="INFO" />
    <appender-ref ref="STDOUT" />
  </root>


========================================================End Logback configuration for Centralized logger=============================


Environment variables:

    AJSC_HOME - set this to the directory where ajsc configuration, data (file
                  system persistence), log files, et. al. will be kept.
                  (see "Ajsc home directory" for details)

Ajsc home directory

    An essential Ajsc home directory should include:
        data - a directory that's initially empty but may hold ajsc metadata
        conf/logback.xml - the logging configuration file
        conf/logback_JMS.xml - the CSI logging configuration file
        conf/cadi.properties - the AT&T Global Logon and AT&T Authorization Framewokr (AAF) config
        templates - a directory that is a copy of the directory from our source code (contains
                  files used by the UI)
                  
                  
                  
                  
                  
                  
