#!/bin/sh
##############################################################################
# - SCLD GRM SERVICE
# - Copyright 2009 AT&T Intellectual Properties
##############################################################################
. `dirname $0`/install.env

mkdir -p ${ROOT_DIR}/log || fail 100 "Error on creating the conf directory."
mkdir -p ${ROOT_DIR}/conf || fail 100 "Error on creating the conf directory."
mkdir -p ${ROOT_DIR}/docs || fail 100 "Error on creating the docs directory."
mkdir -p ${ROOT_DIR}/lib  || fail 100 "Error on creating the lib directory."
rm -rf ${ROOT_DIR}/jetty
# A simple override for the SOA Cloud platform value.  Normally this is not
# needed outside of SOA Cloud development sandboxes
# this is used in the template.lrm.xml file during startup of the service
if [ ! -z "${SCLD_PLATFORM}" ]; then
	SCLD_OPTIONAL_PLATFORM_FLAG="-Dplatform=${SCLD_PLATFORM}"; export SCLD_OPTIONAL_PLATFORM_FLAG
fi

##############################################################################
# PROCESS TEMPLATE FILES FROM ENVIRONMENT
# pattern: looks for all files starting with "template.", processes them using the
# current environment, then renames them by removing the "template." in the same
# directory
##############################################################################
utilpath=`dirname $0`/utils 
for tfile in `ls ${ROOT_DIR}/bundleconfig/etc/sysprops/template.* ${ROOT_DIR}/bundleconfig/etc/appprops/template.* ${ROOT_DIR}/bin/template.* ${ROOT_DIR}/etc/template.* 2>/dev/null`; do
    dfile=`echo ${tfile} | sed -e 's@/template\.@/@g'`
    sh ${utilpath}/findreplace.sh ${tfile} ${dfile} || exit 200
done

runningCount=`${LRMCLI} -running | grep -w ${groupId}.${artifactId} | wc -l` || fail 300 "Unable to determine how many instances are running prior to notifying LRM of the upgrade"

##############################################################################
# DEPLOY CONTAINER TO LRM
##############################################################################
${LRMCLI} -addOrUpgrade -file ${ROOT_DIR}/etc/lrm.xml || fail 400 "Unable to upgrade resource in LRM"

if [ "${runningCount}" -eq 0 ]; then
    if [ "${PROC_SKIP_START_NEW_ON_ZERO_INSTANCES}" != "true" ]; then
        ${LRMCLI} -start -name ${groupId}.${artifactId} -version ${AFTSWM_ACTION_NEW_VERSION} -routeoffer ${SCLD_ROUTE_OFFER} | egrep SUCCESS\|SCLD-LRM-1041 
        if [ $? -ne 0 ]; then 
    	    fail 500 "Start of ${groupId}.${artifactId} with routeOffer ${SCLD_ROUTE_OFFER} failed" 
	    fi
        ${LRMCLI} -running | grep -w ${groupId}.${artifactId}
    else
        echo "PROC_USER_MSG: PROC_SKIP_START_NEW_ON_ZERO_INSTANCES is set to false and no running instances were found prior to upgrading so ending install with no running service instances."
    fi 
else
    ${LRMCLI} -running | grep -w ${groupId}.${artifactId}
fi

    
exit 0
