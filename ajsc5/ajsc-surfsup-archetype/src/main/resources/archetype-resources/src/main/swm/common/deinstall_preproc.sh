#!/bin/sh

. `dirname $0`/deinstall.env

LRMCLI=${INSTALL_ROOT}/opt/app/aft/scldlrm/bin/lrmcli
PATH=$PATH:`dirname $0`/utils; export PATH

${LRMCLI} -shutdown -name ${groupId}.${artifactId} -version ${AFTSWM_ACTION_NEW_VERSION} -routeoffer ${SCLD_ROUTE_OFFER} -ttw 240 || exit 100

${LRMCLI} -delete -name ${groupId}.${artifactId} -version ${AFTSWM_ACTION_NEW_VERSION} -routeoffer ${SCLD_ROUTE_OFFER} || exit 101

rm -rf ${INSTALL_ROOT}/${ROOT_DIR}/logs || {
    echo "WARNING: Unable to purge logs directory during deinstall"
}

exit 0
