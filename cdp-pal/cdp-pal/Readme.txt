INTRODUCTION
------------

	DME2 is the second version of the Direct Messaging Engine available under
the AT&T Frameworks and Tools (AFT) umbrella. DME2 will efficiently transport 
JMS (Java Message Service) data using HTTP protocols and without intermediate 
brokers. Additionally, DME2 can be used instead of vendor HTTP stacks or in 
proxy mode. A key benefit of DME2 is that it allows the dynamic routing of
 messages based upon data, business partner or geographic affinity. It also has
capabilities supporting dynamic registration, load balancing and failover for
service endpoints. DME2 can be implemented and utilized by clients, servers,
or both, and it can process messages asynchronously over HTTP and 
websockets.

	The DME2 product consists of three core components:
dme2-api: The core implementation of DME2 which encapsulates Servlet 
Continuations and delivers a non-blocking, non-queued connection transparency
for client communication and also enables service providers to host and manage
requests in a non-blocking manner.

dme2-jms-provider: A JMS implementation/extension to dme2-api that provides a
 standard interface for creating, sending, and receiving non-persistent messages
using generic JMS interface classes.

dme2-jaxws-client: A JAX-WS Client Handler that will allow you to call 
DME2-enabled Webservices from a JAX-WS implementation.
These are delivered as a unitary DME2 jar file that can be utilized with a small
number of container dependencies to quickly enable an application utilizing 
DME2.


REQUIREMENTS
------------

* Java Development Kit (JDK) 1.6 and above
* Java Message Service (JMS) 1.1 (or higher when backwards compatible with 1.1)
* Servlet API 2.5 (or higher when backwards compatible with 2.5)

INSTALLATION:
* Build dme2-base project using maven command “mvn clean install”.
* After building dme2-base, Build dme2 project using maven command
 “mvn clean install”. This project generates dme2.jar.


CONFIGURATION
-------------

This project generates dme2.jar artifact. Any appliation which wants to use DME
should add the below dependency. This dependency is generated from dme2 
project.

<dependency> 
	<groupId>com.att.aft</groupId> 
	<artifactId>dme2</artifactId> 
	<version>3.1.200</version> 
</dependency>


MAINTAINERS
-------------

AT&T DME Development Team
