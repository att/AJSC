#!/bin/bash
echo Scamper execution for `date` > output.txt
#variable definitions
appName=demoAJSC0115
componentName=JaxrsEchoService
release=4.7.0-RC2.2
scamperServer=http://zldv3675.vci.att.com:8585
#scamperServer=http://zltv3689.vci.att.com:8585
userCredentials=jh0003:123123a
role=demoAjsc0115Admin
env=dev
LONGITUDE=33.6
LATITUDE=23.4
AFT_ENVIRONMENT=AFTUAT
SCLD_ENV=DEV
SCRIPT_DIR=${INSTALL_ROOT}\${distFilesRoot}/bin
CONF_DIR=${INSTALL_ROOT}\${distFilesRoot}/conf
ETC_DIR=${INSTALL_ROOT}\${distFilesRoot}/etc

#testScamper.properties
echo Get effective configuration data from Scamper and create testScamper.properties file from configuration settings >> output.txt
echo command: curl --user ${userCredentials}  --request GET ${scamperServer}/scamper-server-app/applications/${appName}/components/${componentName}/releases/${release}/environments/${env}/effconfigdata?archiveFilename=testScamper.properties -o ${SCRIPT_DIR}/config2/testScamper.properties.tar.gz >> output.txt
echo Result: >> output.txt
curl --user ${userCredentials}  --request GET ${scamperServer}/scamper-server-app/applications/${appName}/components/${componentName}/releases/${release}/environments/${env}/effconfigdata -o ${SCRIPT_DIR}/config2/testScamper.properties.tar.gz >> output.txt
tar -zxvf ${SCRIPT_DIR}/config2/testScamper.properties.tar.gz -C ${SCRIPT_DIR}/config2

if [ ! $? -eq 0   ]
then
echo An error was found during Scamper file generation of testScamper.properties.
echo `cat ${SCRIPT_DIR}/config2/testScamper.properties.tar.gz`
exit 1
fi

if [ -f ${SCRIPT_DIR}/config2/testScamper.properties ]
then
echo "Script directory is " `ls -lg ${SCRIPT_DIR}/config2/testScamper.properties`
cp ${SCRIPT_DIR}/config2/testScamper.properties ${ETC_DIR}
echo "Scamper generated testScamper.properties file copied to etc directory."
else
echo "Scamper did not generate testScamper.properties file.  Please refer to Scamper output files for
a detailed explanation"
fi

#persistence.xml
echo Get effective configuration data from Scamper and create persistence.xml file from configuration settings >> output.txt
echo command: curl --user ${userCredentials}  --request GET ${scamperServer}/scamper-server-app/applications/${appName}/components/${componentName}/releases/${release}/environments/${env}/effconfigdata?archiveFilename=persistence.xml -o ${SCRIPT_DIR}/config2/persistence.xml.tar.gz >> output.txt
echo Result: >> output.txt
curl --user ${userCredentials}  --request GET ${scamperServer}/scamper-server-app/applications/${appName}/components/${componentName}/releases/${release}/environments/${env}/effconfigdata -o ${SCRIPT_DIR}/config2/persistence.xml.tar.gz >> output.txt
tar -zxvf ${SCRIPT_DIR}/config2/persistence.xml.tar.gz -C ${SCRIPT_DIR}/config2

if [ ! $? -eq 0   ]
then
echo An error was found during Scamper file generation of persistence.xml.
echo `cat ${SCRIPT_DIR}/config2/persistence.xml.tar.gz`
exit 1
fi

if [ -f ${SCRIPT_DIR}/config2/persistence.xml ]
then
echo "Script directory is " `ls -lg ${SCRIPT_DIR}/config2/persistence.xml`
cp ${SCRIPT_DIR}/config2/persistence.xml ${ETC_DIR}
echo "Scamper generated persistence.xml file copied to etc directory."
else
echo "Scamper did not generate persistence.xml file.  Please refer to Scamper output files for
a detailed explanation"
fi

