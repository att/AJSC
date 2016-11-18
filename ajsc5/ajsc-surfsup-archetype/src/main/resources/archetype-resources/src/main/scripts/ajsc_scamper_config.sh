#!/bin/bash
echo Scamper execution for `date` > output.txt
#variable definitions
release=4.1.0
appName=ajscTestApp
scamperServer=http://zltv3689.vci.att.com:8585
userCredentials=jh0003:123123a
role=com.att.ajsc.TestApp.Administrator
env=nonprod
LONGITUDE=33.6
LATITUDE=23.4
AFT_ENVIRONMENT=AFTUAT
SCLD_ENV=DEV

#oauth.properties File
 
echo Add component OAuth: >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth >> output.txt
echo Add a release: >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/releases/${release} >> output.txt
echo Result: >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/releases/${release} >> output.txt
echo Add an environment >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/environments/${env} >> output.txt
echo Result: >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/environments/${env} >> output.txt
echo Add permissions for the applicaton : >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/roles/${role}/action/Write >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/roles/${role}/action/Write >> output.txt
echo Add permissions for the component : >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/roles/${role}?action=Write >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/roles/${role}?action=Write  >> output.txt
echo Add permissions for the release  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/releases/${release}/roles/${role}?action=Write >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/releases/${release}/roles/${role}?action=Write  >> output.txt
echo Add permissions for environment nonprod  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/environments/${env}/roles/${role}?action=Write >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/environments/${env}/roles/${role}?action=Write  >> output.txt
echo Add oauth.properties file configuration setting ajsc.oauth.cassandraUserName=cassandra  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraUserName?value=cassandra >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraUserName?value=cassandra  >> output.txt
echo Add oauth.properties file configuration setting ajsc.oauth.restServices=/rest/** >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.restServices?value=/rest/** >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.restServices?value=/rest/** >> output.txt
echo Add oauth.properties file configuration setting ajsc.oauth.services=/services/** >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.services?value=/services/** >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.services?value=/services/** >> output.txt
echo Add oauth.properties file configuration setting ajsc.oauth.cassandraPassword=cassandra  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraPassword?value=cassandra >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraPassword?value=cassandra  >> output.txt
echo Add oauth.properties file configuration setting ajsc.oauth.hosts=localhost  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraHosts?value=localhost >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraHosts?value=localhost >> output.txt
echo Add oauth.properties file configuration setting ajsc.oauth.cassandraPort=9042  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraPort?value=9042 >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraPort?value=9042  >> output.txt
echo Add oauth.properties file configuration setting ajsc.oauth.cassandraMinPoolSize=10  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraMinPoolSize?value=10 >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraMinPoolSize?value=10
echo Add oauth.properties file configuration setting ajsc.oauth.cassandraMaxPoolSize=10  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraMaxPoolSize?value=10 >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraMaxPoolSize?value=10 >> output.txt
echo Add oauth.properties file configuration setting ajsc.oauth.cassandraTokenWriteConsistency=ONE  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraTokenWriteConsistency?value=ONE >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraTokenWriteConsistency?value=ONE >> output.txt
echo Add oauth.properties file configuration setting ajsc.oauth.cassandraDefaultReadWriteConsistency=ONE  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraDefaultReadWriteConsistency?value=ONE >> output.txt
echo Result:  >> output.txt 
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configsettings/ajsc.oauth.cassandraDefaultReadWriteConsistency?value=ONE
echo Add oauth.properties file to Scamper  >> output.txt
echo command: curl --user ${userCredentials}  --request POST  -F upload_file=@oauth.properties ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configfiles/oauth.properties >> output.txt
echo Result: >> output.txt
curl --user ${userCredentials}  --request POST  -F upload_file=@oauth.properties ${scamperServer}/scamper-server-app/applications/${appName}/components/OAuth/releases/${release}/environments/${env}/configfiles/oauth.properties >>output.txt
#echo Get effective configuration data from Scamper and create oauth.properties file from configuration settings >> output.txt
#echo command: curl --user jh0003:123123a  --request GET http://zltv3689.vci.att.com:8585/scamper-server-app/applications/ajscTestApp/components/OAuth/releases/4.1.0/environments/nonprod/effconfigdata?archiveFilename=oauth.properties -o config/mydata.tar.gz >> output.txt
#echo Result: >> output.txt
#curl --user jh0003:123123a  --request GET http://zltv3689.vci.att.com:8585/scamper-server-app/applications/ajscTestApp/components/OAuth/releases/4.1.0/environments/nonprod/effconfigdata?archiveFilename=oauth.properties -o config/mydata.tar.gz >>output.txt
#tar -zxvf config/mydata.tar.gz -C config

#cadi.properties File

echo Add component cadiAAF: >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF >> output.txt
echo Add permissions for the cadiAAF component  : >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/roles/${role}?action=Write >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/roles/${role}?action=Write  >> output.txt
echo Add cadi.properties file configuration setting ajsc.cadiAAF.cspDomain=PROD >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.cspDomain?value=PROD >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.cspDomain?value=PROD
echo Add cadi.properties file configuration setting ajsc.cadiAAF.localhostAllow=false >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.localhostAllow?value=false >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.localhostAllow?value=false >> output.txt
echo Add cadi.properties file configuration setting ajsc.cadiAAF.localhostDeny=false >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.localhostDeny?value=false >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.localhostDeny?value=false >> output.txt
echo Add cadi.properties file configuration setting ajsc.cadiAAF.cspDevlLocalhost=true >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.cspDevlLocalhost?value=true >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.cspDevlLocalhost?value=true >> output.txt
echo Add cadi.properties file configuration setting ajsc.cadiAAF.aaf_url=http://DME2RESOLVE/service=com.att.authz.AuthorizationService/version=1.0.0/envContext=UAT/routeOffer=BAU_SE >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.aaf_url?value=http://DME2RESOLVE/service=com.att.authz.AuthorizationService/version=1.0.0/envContext=UAT/routeOffer=BAU_SE  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.aaf_url?value=http://DME2RESOLVE/service=com.att.authz.AuthorizationService/version=1.0.0/envContext=UAT/routeOffer=BAU_SE  >> output.txt
echo Add cadi.properties file configuration setting hostname=mywebserver.att.com  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.hostname?value=mywebserver.att.com  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.hostname?value=mywebserver.att.com  >> output.txt
echo Add cadi.properties file configuration setting ajsc.cadiAAF.aafDmeTimeout=5000  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.aafDmeTimeout?value=5000  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.aafDmeTimeout?value=5000  >> output.txt
echo Add cadi.properties file configuration setting ajsc.cadiAAF.aafUserExpires=15000  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.aafUserExpires?value=15000  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.aafUserExpires?value=15000  >> output.txt
echo Add cadi.properties file configuration setting ajsc.cadiAAF.aafHighCount=100  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.aafHighCount?value=100  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.aafHighCount?value=100  >> output.txt
echo Add cadi.properties file configuration setting ajsc.cadiAAF.AFT_LATITUDE=${LATITUDE}  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.AFT_LATITUDE?value=${LATITUDE}  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.AFT_LATITUDE?value=${LATITUDE}  >> output.txt
echo Add cadi.properties file configuration setting ajsc.cadiAAF.AFT_LONGITUDE=${LONGITUDE}  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.AFT_LONGITUDE?value=${LONGITUDE}  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.AFT_LONGITUDE?value=${LONGITUDE}  >> output.txt
echo Add cadi.properties file configuration setting ajsc.cadiAAF.AFT_ENVIRONMENT=${AFT_ENVIRONMENT}  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.AFT_AFT_ENVIRONMENT?value=${LONGITUDE}  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.AFT_ENVIRONMENT?value=${AFT_ENVIRONMENT}  >> output.txt
echo Add cadi.properties file configuration setting ajsc.cadiAAF.SCLD_PLATFORM=${SCLD_ENV}  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.SCLD_PLATFORM?value=${SCLD_ENV}  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configsettings/ajsc.cadiAAF.SCLD_PLATFORM?value=${SCLD_ENV}  >> output.txt
echo Add oauth.properties file to Scamper  >> output.txt
echo command: curl --user ${userCredentials}  --request POST  -F upload_file=@cadi.properties ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configfiles/cadi.properties >> output.txt
echo Result: >> output.txt
curl --user ${userCredentials}  --request POST  -F upload_file=@cadi.properties ${scamperServer}/scamper-server-app/applications/${appName}/components/cadiAAF/releases/${release}/environments/${env}/configfiles/cadi.properties >>output.txt

#hazelcast-client.properties File

echo Add component hazelcast: >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast >> output.txt
echo Add permissions for the hazelcast component  : >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/roles/${role}?action=Write >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/roles/${role}?action=Write  >> output.txt
echo Add hazelcast-client.properties file configuration setting ajsc.hazelcast.clientGroupName=ajsc >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientGroupName?value=ajsc >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientGroupName?value=ajsc >> output.txt
echo Add hazelcast-client.properties file configuration setting ajsc.hazelcast.clientGroupPass=ajscpass >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientGroupPass?value=ajscpass >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientGroupPass?value=ajscpass >> output.txt
echo Add hazelcast-client.properties file configuration setting ajsc.hazelcast.clientConnectionTimeout=30000 >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientConnectionTimeout?value=30000 >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientConnectionTimeout?value=30000 >> output.txt
echo Add hazelcast-client.properties file configuration setting ajsc.hazelcast.clientConnectionAttemptsLimit=3 >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientConnectionAttemptsLimit?value=3 >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientConnectionAttemptsLimit?value=3 >> output.txt
echo Add hazelcast-client.properties file configuration setting ajsc.hazelcast.clientConnectionAttemptsLimit=3 >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientConnectionAttemptsLimit?value=3 >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientConnectionAttemptsLimit?value=3 >> output.txt
echo Add hazelcast-client.properties file configuration setting ajsc.hazelcast.ajsc.hazelcast.clientReconnectionTimeout=5000 >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientReconnectionTimeout?value=5000 >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientReconnectionTimeout?value=5000 >> output.txt
echo Add hazelcast-client.properties file configuration setting ajsc.hazelcast.clientReconnectionAttemptsLimit=5 >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientReconnectionAttemptsLimit?value=5 >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientReconnectionAttemptsLimit?value=5 >> output.txt
echo Add hazelcast-client.properties file configuration setting ajsc.hazelcast.clientShuffleAddresses=false >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientShuffleAddresses?value=false >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientShuffleAddresses?value=false >> output.txt
echo Add hazelcast-client.properties file configuration setting ajsc.hazelcast.clientUpdateAutomatic=true >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientUpdateAutomatic?value=true >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientUpdateAutomatic?value=true >> output.txt
echo Add hazelcast-client.properties file configuration setting ajsc.hazelcast.clientAddresses=localhost,+127.0.0.1 >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientAddresses?value=localhost,+127.0.0.1 >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configsettings/ajsc.hazelcast.clientAddresses?value=localhost,+127.0.0.1 >> output.txt
echo command: curl --user ${userCredentials}  --request POST  -F upload_file=@hazelcast-client.properties ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configfiles/hazelcast-client.properties >> output.txt
echo Result: >> output.txt
curl --user ${userCredentials}  --request POST  -F upload_file=@hazelcast-client.properties ${scamperServer}/scamper-server-app/applications/${appName}/components/hazelcast/releases/${release}/environments/${env}/configfiles/hazelcast-client.properties >>output.txt

#logback_jms.xml File

echo Add component logbackJMS: >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS >> output.txt
echo Add permissions for the logbackJMS component  : >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/roles/${role}?action=Write >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/roles/${role}?action=Write  >> output.txt
echo Add logback_jms.xml file configuration setting ajsc.logbackJMS.auditQ.providerURL=tcp://q27csi1c3.vci.att.com:27812 >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.auditQ.providerURL?value=tcp://q27csi1c3.vci.att.com:27812 >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.auditQ.providerURL?value=tcp://q27csi1c3.vci.att.com:27812 >> output.txt
echo Add logback_jms.xml file configuration setting ajsc.logbackJMS.auditQ.userName=sg >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.auditQ.userName?value=sg >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.auditQ.userName?value=sg >> output.txt
echo Add logback_jms.xml file configuration setting ajsc.logbackJMS.auditQ.password=its4test >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.auditQ.password?value=its4test >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.auditQ.password?value=its4test >> output.txt
echo Add logback_jms.xml file configuration setting ajsc.logbackJMS.perfTrkQ.ProviderURL=tcp://q27csi1c3.vci.att.com:27812 >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.perfTrkQ.ProviderURL?value=tcp://q27csi1c3.vci.att.com:27812 >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.perfTrkQ.ProviderURL?value=tcp://q27csi1c3.vci.att.com:27812 >> output.txt
echo Add logback_jms.xml file configuration setting ajsc.logbackJMS.perfTrkQ.userName=sg >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.perfTrkQ.userName?value=sg >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.perfTrkQ.userName?value=sg >> output.txt
echo Add logback_jms.xml file configuration setting ajsc.logbackJMS.perfTrkQ.password=its4test >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.perfTrkQ.password?value=its4test >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.perfTrkQ.password?value=its4test >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.perfTrkQ.userName?value=sg >> output.txt
echo Add logback_jms.xml file configuration setting ajsc.logbackJMS.timerLogQ.ProviderURL=tcp://q27csi1c3.vci.att.com:27812 >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.timerLogQ.ProviderURL?value=tcp://q27csi1c3.vci.att.com:27812 >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.timerLogQ.ProviderURL?value=tcp://q27csi1c3.vci.att.com:27812 >> output.txt
echo Add logback_jms.xml file configuration setting ajsc.logbackJMS.timerLogQ.userName=sg >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.timerLogQ.userName?value=sg >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.timerLogQ.userName?value=sg >> output.txt
echo Add logback_jms.xml file configuration setting ajsc.logbackJMS.timerLogQ.password=its4test >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.timerLogQ.password?value=its4test >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configsettings/ajsc.logbackJMS.timerLogQ.password?value=its4test >> output.txt
echo command: curl --user ${userCredentials}  --request POST  -F upload_file=@logback_jms.xml ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configfiles/logback_jms.xml >> output.txt
echo Result: >> output.txt
curl --user ${userCredentials}  --request POST  -F upload_file=@logback_jms.xml ${scamperServer}/scamper-server-app/applications/${appName}/components/logbackJMS/releases/${release}/environments/${env}/configfiles/logback_jms.xml >> output.txt

#template.lrm.xml File

echo Add component templateLRM: >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/templateLRM >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/templateLRM >> output.txt
echo Add permissions for the templateLRM component  : >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/templateLRM/roles/${role}?action=Write >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/templateLRM/roles/${role}?action=Write  >> output.txt
echo Add template.lrm.xml file configuration setting ajsc.templateLRM.authN=authentication-scheme-2 >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/templateLRM/releases/${release}/environments/${env}/configsettings/ajsc.templateLRM.authN?value=authentication-scheme-2 >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/templateLRM/releases/${release}/environments/${env}/configsettings/ajsc.templateLRM.authN?value=authentication-scheme-2 >> output.txt
echo Add template.lrm.xml file configuration setting ajsc.templateLRM.ajscPersistence=file >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/templateLRM/releases/${release}/environments/${env}/configsettings/ajsc.templateLRM.ajscPersistence?value=file >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/templateLRM/releases/${release}/environments/${env}/configsettings/ajsc.templateLRM.ajscPersistence?value=file >> output.txt
echo Add template.lrm.xml file configuration setting ajsc.templateLRM.enableSSL=true >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/templateLRM/releases/${release}/environments/${env}/configsettings/ajsc.templateLRM.enableSSL?value=enable >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/templateLRM/releases/${release}/environments/${env}/configsettings/ajsc.templateLRM.enableSSL?value=true >> output.txt
echo command: curl --user ${userCredentials}  --request POST  -F upload_file=@template.lrm.xml ${scamperServer}/scamper-server-app/applications/${appName}/components/templateLRM/releases/${release}/environments/${env}/configfiles/template.lrm.xml >> output.txt
echo Result: >> output.txt
curl --user ${userCredentials}  --request POST  -F upload_file=@template.lrm.xml ${scamperServer}/scamper-server-app/applications/${appName}/components/templateLRM/releases/${release}/environments/${env}/configfiles/template.lrm.xml >> output.txt


# Define Global Config Settings
# for AJSC Application

echo Add permissions for all components  : >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/*/roles/${role}?action=Write >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/*/roles/${role}?action=Write  >> output.txt
echo Add permissions for all releases  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/releases/*/roles/${role}?action=Write >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/releases/*/roles/${role}?action=Write  >> output.txt
echo Add permissions for all environments  >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/environments/*/roles/${role}?action=Write >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/environments/*/roles/${role}?action=Write  >> output.txt

echo Add global configuration setting ajscFile.header.warning >> output.txt
echo command: curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/*/releases/*/environments/*/configsettings/ajscFile.header.warning?value=This+file+was+generated+via+Scamper.++Any+direct+modifications+to+this+file+will+be+lost+after+a+new+AJSC+SWM+deployment. >> output.txt
echo Result:  >> output.txt
curl --user ${userCredentials} --request PUT ${scamperServer}/scamper-server-app/applications/${appName}/components/*/releases/*/environments/*/configsettings/ajscFile.header.warning?value=This+file+was+generated+via+Scamper.++Any+direct+modifications+to+this+file+will+be+lost+after+a+new+AJSC+SWM+deployment. >> output.txt