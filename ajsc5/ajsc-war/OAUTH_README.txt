#Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
Instructions for the setup and test of Oauth2 Integration into AJSC:
Environment Assumptions:
1-Cassandra is installed
Environment Setup Steps:
1-startup cassandra by typing sudo ./bin/cassandra
2-startup cqlsh by typing ./bin/cqlsh
3-Run the ddl to create the keyspace and tables found in the CQL_OAUTH_README.txt file
4-Verify that the tables exist by typing:
a-use oauth;
b-desc tables;
After typing desc tables, you should see three tables listed. They are:
oauth_access_token oauth_client_details oauth_refresh_token
5-Place a copy of the oauth.properties file in the bin/conf tomcat directory.
6-Insert clients using the junit ClientDataServicesTest2.java and run this using run as junit. Be sure to set the values in testClientDetails method- Set the client id. This must be unique.
****Please note that you need to enter data using this JUnit since the client secret needs to be in an encrypted format in order to be recognized.
You can not insert directly into the oauth_client_details table.
This JUnit method is equivalent to having a ui that is used to register the client.
7- Go to web browser and type http://localhost:8080/ajsc/oauth/token?client_id=norma10&client_secret=secret&grant_type=client_credentials&scope=read
This will indicate a token. The response will look like this:
{"access_token":"c83012c6-b670-4549-925d-5138e29de07a","token_type":"bearer","expires_in":86399,"scope":"read"}
or go to the command line and use curl
curl "http://localhost:8080/ajsc/oauth/token?client_id=norma10&client_secret=secret&grant_type=client_credentials&scope=read"
{"access_token":"164fe400-f09a-474b-a9db-fd5255294b85","token_type":"bearer","expires_in":86399,"scope":"read"}
8- copy the token and go to the command line.
You can use the curl command that looks like this to test
curl -H "Authorization:Bearer c83012c6-b670-4549-925d-5138e29de07a" "http://localhost:8080/ajsc/rest/ajsc-examples/v1/helloRestlet/22"
The should give a response such as:
Hello Restlet Method: GET ID:22
If the token has expired, it will send back a message indicating so. 
An example of this is:
Normas-MacBook-Pro:bin nmusciotto$ curl -H "Authorization:Bearer 9a64fb59-be10-47fa-b5ad-8fdabad892dd" "http://localhost:8080/ajscrt/rest/ajsc-examples/v1/helloRestlet/22"
{"error":"invalid_token","error_description":"Access token expired: 9a64fb59-be10-47fa-b5ad-8fdabad892dd"}Normas-MacBook-Pro:bin nmusciotto$ 
The expiration is set when the client entry is made via the junit add method. It is set via the ajscClientDetails.setAccessTokenValiditySeconds(86400); The parameter is in units of seconds.
9- To enable the OAuth infrastructure in AJSC, indicate -Dspring.profiles.active=“oauth" when starting your web server.
To disable the OAuth infrastructure, do not specify this system parameter when starting the server.
10- To enable and disable oauth2 in AJSC for specific resources, the oauth.properties file has the following entries. These entries indicate the resource uri pattern.
restServices=/rest/**
#restServices=oauthDisabled1
services=/services/**
services=oauthDisabled2
To disable oauth2, uncomment the one that indicates oauthDisabled.
For example:
#restServices=/rest/**
restServices=oauthDisabled1
#services=/Services/**
services=oauthDisabled2
Save the file. Bring tomcat down and then up again. Oauth will now be disabled and a bearer token will no longer be required. Try to run the restlet. It should run without an authorization error.
Now if I request the service for instance using curl, it will execute:
curl "http://localhost:8080/ajsc/rest/ajsc-examples/v1/helloRestlet/22"
Hello Restlet Method: GET ID:22

Another example:
curl -H "Authorization:Bearer f3afd7f4-f287-48af-be7d-9410767b4def" "http://localhost:8080/ajsc/rest/ajsc-examples/v1/helloWorldBean"
Hello World, Welcome to Ajsc - Patrick

This example shows invalid token:
curl -H "Authorization:Bearer 6954f3d9-63d1-4830-b83b-3744a3ebc45a" "http://localhost:8080/ajsc/rest/ajsc-examples/v1/helloWorldBean"
{"error":"invalid_token","error_description":"Invalid access token: 6954f3d9-63d1-4830-b83b-3744a3ebc45a"}Normas-MacBook-Pro:bin nmusciotto$ 
