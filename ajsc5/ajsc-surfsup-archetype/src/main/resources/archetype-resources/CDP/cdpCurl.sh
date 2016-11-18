#Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
#!/bin/sh -x

fail() {
    rc=$1
    shift;
    echo -e "ERROR: $@"
    exit $rc
}

# Mandatory Variables
TENANT_ID=${TENANT_ID:="a2a0236d-f750-4c38-8b38-325a2ebde8c5"}
ZONE_ID=${ZONE_ID:="1c8d644a-20fa-456c-896e-92d9a1b9717a"}
BLUEPRINT_LOCATION=${BLUEPRINT_LOCATION:="."}
BLUEPRINT_FILE=${BLUEPRINT_FILE:="__module_ajsc_namespace_name__Blueprint.xml"}
CDP_USERID=${CDP_USERID:="m83931"}
CDP_PASSWORD=${CDP_PASSWORD:="cdptest"}
CDP_ENV=${CDP_ENV:="cdp-demo.test.att.com"}
IS_PUBLISH_BLUEPRINT=${IS_PUBLISH_BLUEPRINT:="true"}
IS_PROVISION_BLUEPRINT=${IS_PROVISION_BLUEPRINT:="true"}
STACK_ENVIRONMENT=${STACK_ENVIRONMENT:="LAB"}
STACK_NAME=${STACK_NAME:="stack`date +'%m%d%H%M%S'`"}
# Optional Variables
BLUEPRINT_NAME=""
BLUEPRINT_VERSION=""

# Check if Blueprint file is available and accesible

[ -f ${BLUEPRINT_LOCATION}/${BLUEPRINT_FILE} ] || fail 1 "Blueprint file ${BLUEPRINT_FILE} unavailable at location ${BLUEPRINT_LOCATION}" 

# Retrieve the Name of Blueprint from blueprint file
if [ "${BLUEPRINT_NAME}x" = "x" ]; then
BLUEPRINT_NAME=`xmllint --shell ${BLUEPRINT_LOCATION}/${BLUEPRINT_FILE} <<EOF
setns x=http://cdp.att.com
cat /x:blueprint/x:name/text()
EOF`
BLUEPRINT_NAME=`echo ${BLUEPRINT_NAME} | awk 'BEGIN { FS = "-------" } { for (i = 2; i <= NF; i++) print $i}' | sed -e 's@ / >@@g;s/^ *//g;s/ *\$//g'`
fi

# Retrieve the Version of Blueprint from blueprint file
if [ "${BLUEPRINT_VERSION}x" = "x" ]; then
BLUEPRINT_VERSION=`xmllint --shell ${BLUEPRINT_LOCATION}/${BLUEPRINT_FILE} <<EOF
setns x=http://cdp.att.com
cat /x:blueprint/x:version/text()
EOF`
BLUEPRINT_VERSION=`echo ${BLUEPRINT_VERSION} | awk 'BEGIN { FS = "-------" } { for (i = 2; i <= NF; i++) print $i}' | sed -e 's@ / >@@g;s/^ *//g;s/ *\$//g'`
fi

# Validate CDP Environment and credentials
[ `curl -s -o /dev/null -u ${CDP_USERID}:${CDP_PASSWORD} -w "%{http_code}" http://${CDP_ENV}/api/v1/version` -ne 200 ] && fail 2 "CDP Credentials invalid for the cdp environment ${CDP_ENV}."


# Validate Tenant
[ `curl -s -o /dev/null -u ${CDP_USERID}:${CDP_PASSWORD} -w "%{http_code}" http://${CDP_ENV}/api/v1/tenants/${TENANT_ID}` -ne 200 ] &&  fail 3 "Tenant with Tenant ID ${TENANT_ID} not available in cdp env ${CDP_ENV}."


# Validate Zone
[ `curl -s -o /dev/null -u ${CDP_USERID}:${CDP_PASSWORD} -w "%{http_code}" http://${CDP_ENV}/api/v1/tenants/${TENANT_ID}/zones/${ZONE_ID}/credentials` -ne 200 ] &&  fail 4 "Zone with Zone ID ${ZONE_ID} not created for Tenant ${TENANT_ID}."


# ***PUBLISHING OF THE BLUEPRINT****

if [ "${IS_PUBLISH_BLUEPRINT}" = "true" ]; then
	
# Validate that the Blueprint is not already present
[ `curl -s -o /dev/null -u ${CDP_USERID}:${CDP_PASSWORD} -w "%{http_code}" http://${CDP_ENV}/api/v1/blueprints/${BLUEPRINT_NAME}/${BLUEPRINT_VERSION}?view=NORMAL` -eq 200 ] && fail 5 "Blueprint with name \"${BLUEPRINT_NAME}\" and version \"${BLUEPRINT_VERSION}\" already present."
	
# Linent Validation
	BP_LEN_RES=$( curl -s -u ${CDP_USERID}:${CDP_PASSWORD} -w "\n%{http_code}" -X POST --data-binary @${BLUEPRINT_LOCATION}/${BLUEPRINT_FILE} http://${CDP_ENV}/api/v1/blueprints?action=lenient -H "Content-Type: application/xml" )
	[ `echo "${BP_LEN_RES}" | tail -1` -ne 200 ] && fail 6 "Lenient Validation of Blueprint \"${BLUEPRINT_NAME}\" with version \"${BLUEPRINT_VERSION}\" failed with error:\n${BP_LEN_RES}"
	
	
# Creating a draft for the blueprint
	BP_DRAFT_RES=$( curl -s -u ${CDP_USERID}:${CDP_PASSWORD} -w "\n%{http_code}" -X POST --data-binary @${BLUEPRINT_LOCATION}/${BLUEPRINT_FILE} http://${CDP_ENV}/api/v1/blueprints/${TENANT_ID}/blueprint/ -H "Content-Type: application/xml" )
	[ `echo "${BP_DRAFT_RES}" | tail -1` -ne 201 ] && fail 7 "Draft Creation of Blueprint \"${BLUEPRINT_NAME}\" with version \"${BLUEPRINT_VERSION}\" failed with error:\n${BP_DRAFT_RES}"
	
# Strict Validation
	BP_STRICT_RES=$( curl -s -u ${CDP_USERID}:${CDP_PASSWORD} -w "\n%{http_code}" -X POST --data-binary @${BLUEPRINT_LOCATION}/${BLUEPRINT_FILE} http://${CDP_ENV}/api/v1/blueprints?action=strict -H "Content-Type: application/xml" )
	[ `echo "${BP_STRICT_RES}" | tail -1` -ne 200 ] && fail 8 "Strict Validation of Blueprint \"${BLUEPRINT_NAME}\" with version \"${BLUEPRINT_VERSION}\" failed with error:\n${BP_STRICT_RES}"
	
# Final Publishing of the blueprint
	BP_PUBLISH_RES=$( curl -s -u ${CDP_USERID}:${CDP_PASSWORD} -w "\n%{http_code}" -X PUT http://${CDP_ENV}/api/v1/blueprints/catalog/${TENANT_ID}/${BLUEPRINT_NAME}/${BLUEPRINT_VERSION}?action=PUBLISH -H "Content-Type: application/xml" )
	[ `echo "${BP_PUBLISH_RES}" | tail -1` -ne 200 ] && fail 9 "Final Publishing of Blueprint \"${BLUEPRINT_NAME}\" with version \"${BLUEPRINT_VERSION}\" failed with error:\n${BP_PUBLISH_RES}"

fi

# ***PROVISIONING OF THE BLUEPRINT****

if [ "${IS_PROVISION_BLUEPRINT}" = "true" ]; then

# Forming Request Data for Provisioning the blueprint	
VARIABLE_NAME=`xmllint --shell ${BLUEPRINT_LOCATION}/${BLUEPRINT_FILE} <<EOF
setns x=http://cdp.att.com
cat /x:blueprint/x:environment/x:variable/@name
EOF`
VARIABLE_NAME=`echo ${VARIABLE_NAME} | awk 'BEGIN { FS = "-------" } { for (i = 2; i <= NF; i++) print $i"\n"}' | sed -e 's@ / >@@g;s/^ *//g;s/ *\$//g'`
IFS=$'\n'
i=0; for var in ${VARIABLE_NAME}; do ARR_VARIABLE_NAME[i]=`echo ${var} | awk -F '"' '{print $2}'`; i=`expr $i + 1`; done
unset IFS

VARIABLE_VALUE=`xmllint --shell ${BLUEPRINT_LOCATION}/${BLUEPRINT_FILE} <<EOF
setns x=http://cdp.att.com
cat /x:blueprint/x:environment/x:variable/@default
EOF`
VARIABLE_VALUE=`echo ${VARIABLE_VALUE} | awk 'BEGIN { FS = "-------" } { for (i = 2; i <= NF; i++) print $i"\n"}' | sed -e 's@ / >@@g;s/^ *//g;s/ *\$//g'`
IFS=$'\n'
i=0; for var in ${VARIABLE_VALUE}; do ARR_VARIABLE_VALUE[i]=`echo ${var} | awk -F '"' '{print $2}'`; i=`expr $i + 1`; done
unset IFS

[ "${#ARR_VARIABLE_NAME[@]}" -ne "${#ARR_VARIABLE_VALUE[@]}" ] && fail 10 "Some variables dont have a default value.Modify your Blueprint to have default values for all variables."


PROVISION_XML_REQ=`printf "<variableValueList xmlns=\"http://cdp.att.com\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://cdp.att.com cdp.xsd \">"`
for ((j=0;j<${#ARR_VARIABLE_NAME[@]};j++));
do
	 PROVISION_XML_REQ=`printf '%s<VariableValue>' "${PROVISION_XML_REQ}"`
	 PROVISION_XML_REQ=`printf '%s<name>%s</name><value>%s</value>' "${PROVISION_XML_REQ}" "${ARR_VARIABLE_NAME[$j]}" "${ARR_VARIABLE_VALUE[$j]}"`
	 PROVISION_XML_REQ=`printf '%s</VariableValue>' "${PROVISION_XML_REQ}"`
done
PROVISION_XML_REQ=`printf '%s</variableValueList>' "${PROVISION_XML_REQ}"`

BP_PROVISION_RES=$( curl -s -u ${CDP_USERID}:${CDP_PASSWORD} -w "\n%{http_code}" -X POST --data-binary "${PROVISION_XML_REQ}" "http://${CDP_ENV}/api/v1/stacks/${TENANT_ID}/${BLUEPRINT_NAME}/${BLUEPRINT_VERSION}/zones/${ZONE_ID}?stackName=${STACK_NAME}&environment=${STACK_ENVIRONMENT}" -H "Content-Type: application/xml" )
[ `echo "${BP_PROVISION_RES}" | tail -1` -ne 201 ] && fail 11 "Provisioning of Blueprint \"${BLUEPRINT_NAME}\" with version \"${BLUEPRINT_VERSION}\" failed with error:\n${BP_PROVISION_RES}"

echo "Stack \"${STACK_NAME}\" provisioned in CDP environment \"${CDP_ENV}\" for blueprint \"${BLUEPRINT_NAME}\" with version \"${BLUEPRINT_VERSION}\".Check Job for status."
	
fi

