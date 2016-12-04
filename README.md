# AJSC
Java Service Container

#OVERVIEW

The AJSC is a Java service container designed to support the rapid development of APIs and deployment of services in the SOA cloud. With a tight integration between any Service can be created and deployed in a short amount of time with full visibility and exposure through the any Cloud. The container provides key Enterprise Integration patterns through the use of Apache Camel Routing that can speed up solution delivery and the re-usability of exposed business logic through Spring Bean Creation, RESTful services, JAX-RS, as well as other business logic implementations.

#BUILD  
AJSC can be cloned and builb using Maven 
In the repository use
mvn - clean install

Project Build will be Successful

#RUN 
To create and run AJSC archtype service follow these steps.
Pre-requisites to run the service
Java JDK 1.8
Maven 3x version
 1) Open cmd/terminal use
  mvn archetype:create                                   
  -DarchetypeGroupId=com.att.ajsc
  -DarchetypeArtifactId=ajsc-archetype-parent           
  -DarchetypeVersion=1.0.0                
  -DgroupId=my.groupid                                
  -DartifactId=my-artifactId
  
 2)use mvn clean package 

 3)use mvn -P runAjsc to start the service
 
#CONFIGURATION 
Recommended 
Environment - Unix/Windows based
Java - 1.8
Maven - 3.2.5 

