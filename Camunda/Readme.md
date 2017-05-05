# AJSC 6 CAMUNDA

## OVERVIEW

AJSC Camunda is the new open source alternative BPM platform based on Camunda powered by AJSC. Camunda is written in Java and a perfect match for Java EE and spring while providing a powerful REST API and script language support. You can use Camunda for system integration workflows as well as for human workflow and case management. 

## REQUIREMENTS

* Java Development Kit (JDK) 1.7 and above
* Maven
* Database instance like MySQL required and refer to camunda script here
* For BPM workflow development, you would need Camunda Modeler


## BUILD
Checkout code using command:

git checkout https://github.com/att/AJSC.git

* Build sdk-camunda-archetype project using maven command “mvn clean install”.
* Build sdk-camunda-core using maven command “mvn clean install”.

## RUN
1. Add the archetype to eclipse with below properties:
<groupId>com.att.ajsc<groupId>
<artifactId>sdk-camunda-archetype<artifactId>
<version>7.6.2-00<version>
As of this writing, the latest version of ajsc-6 camunda archetype(sdk-camunda-archetype) is: 7.6.2-00. It is using Camunda BPM 7.6.0
![alt text](https://github.com/att/AJSC/blob/master/Camunda/images/ajsc6-camunda-add-archetype.jpg "Add Archetype")

2. Create a service project. Please give your Artifact Id/Project Name in small letters only
![alt text](https://github.com/att/AJSC/blob/master/Camunda/images/ajsc6-camunda-maven-project.jpg  "Create Service Project")
Note: Above screen shot details are provided as an example

docker-registry: <<Give your Docker server>>
kube-namespace :<< Give your Kubernetes namespace >> 
service-account: << Give your Kubernetes service-account>>

3. . Configure the resources/application.properties file with the appropriate database information
The included code assumes you are using a MySQL database; change the entries for your Camunda engine database,
if you aren't using MySQL and add the database driver dependency to your project's pom.xml file
 
#### application.properties
Camunda Process Engine DataSource connection Details
spring.datasource.url=jdbc:<dbtype>://<hostname>:port/<dbname>
spring.datasource.username=<DBuser>
spring.datasource.password=<dbpwd>
spring.datasource.driver-class-name=<db driver calass name>

Example configuration for local MySQL would look like this
#### application.properties
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/camundabpm
spring.datasource.username=camunda
spring.datasource.password=password

4.(Optional) suppose if you don't have CADI-AAF access then you can disable CADI-AAF authentication (in application.properties file) as described in below
   For example: if you want to disable CADI-AAF authentication then ajsc.enableCADIAAF=false
application.properties
#Enable/disable CADI-AAF
ajsc.enableCADIAAF=false

5. Build the service by doing a mvn clean package.
![alt text](https://github.com/att/AJSC/blob/master/Camunda/images/clean_package.jpg "Build Project")

6.Start the service
using command prompt --> go to your $Project_Location and run as command  'java -jar target/<<service project.jar>>
If using STS(Spring Tool suite/Eclipse) right click on pom.xml file and run it as Run-As > mvn spring-boot:run 
![alt text](https://github.com/att/AJSC/blob/master/Camunda/images/spring_boot_run.jpg "Start Service")

7.Verify the service is running by accessing the context root.
         a. Check JAX-RS service http://localhost:8080/restservices/<<artifect-id>>/v1/service/hello
              You should receive JSON response: {"message":"Hello world!"}
         b. Camunda Tasklist page
              http://localhost:8080/camunda/
          c. Camunda Rest Services
              http://localhost:8080/engine-rest/engine/default/process-definition/
          d. Using below JAX-RS Rest service, Start Process Instance of log-message workflow
              http://localhost:8080/restservices/<<artifect-id>>/v1/log/log-message/{hello log}
