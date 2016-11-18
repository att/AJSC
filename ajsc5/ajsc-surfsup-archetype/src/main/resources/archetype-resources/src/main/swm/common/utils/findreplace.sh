#!/bin/sh
# Copyright 2011 AT&T Intellectual Properties
##############################################################################
# findreplace.sh <template> <destination>
#
#     This script searches a provided file for templatized variable names
#     in the format __varname__ and, if found in the current environment
#     replaces those.  Once complete, it will move the final copy of the file
#     to <destination>.
#
##############################################################################
TEMPLATE=${1:?"Template file path required"}
DESTINATION=${2:?"Destination file path required"}

if [ ! -f "${TEMPLATE}" ]; then
    echo "ERROR: Specified template file does not exist: ${TEMPLATE}"
    exit 100
fi

DIRECTORY=`dirname ${DESTINATION}`
if [ ! -d "${DIRECTORY}" ]; then
    echo "ERROR: Destination directory does not exist: ${DIRECTORY}"
    exit 200
fi

SED_SCR=/tmp/sed.$$
echo "{" > ${SED_SCR}

# create a sed script for replacing variables from current environment
for  i in `env | awk -F= '{ print $1}'`; do
    if [ "$i" = "IFS" ] ; then
       continue;
    fi

    VALUE=`eval echo '$'${i}` || {
        echo 'WARNING: Unable to format '${i}' for sed replacement'
        continue;
    }
    
    for x in '@' '^' '&' '?' '#' '~' '%' '|' '+' '/'; do
        echo ${VALUE} | grep "$x" 2>/dev/null 1>/dev/null
        if [ $? != 0 ]; then
            CCHAR="$x"
            break
        fi
    done
    
    if [ -z "${CCHAR}" ]; then
        echo "WARNING: Unable to find a suitable sed replacement character for ${VALUE}, will ignore setting ${KEY} in templates"
        continue;
    fi
    
    echo "      s${CCHAR}__${i}__${CCHAR}${VALUE}${CCHAR}g" >> ${SED_SCR}
done

sed -e 's/\\\@/\\\\@/g' ${SED_SCR} > ${SED_SCR}.1 || exit 300

if [ -f ${DESTINATION} ]; then
	TIMESTAMP=`date +%Y%m%d%H%M%S`
	o_dir=`dirname ${DESTINATION}`
	o_file=`basename ${DESTINATION}`
    mv ${DESTINATION} ${o_dir}/bu.${o_file}.${TIMESTAMP}
fi

mv -f ${SED_SCR}.1 ${SED_SCR} || exit 400

echo "}" >> ${SED_SCR} || exit 500

sed -f ${SED_SCR} ${TEMPLATE} > ${DESTINATION} || exit 600

rm -f $SED_SCR

exit 0