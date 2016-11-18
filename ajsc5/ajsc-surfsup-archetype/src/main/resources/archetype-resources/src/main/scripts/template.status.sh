#!/bin/sh
##############################################################################
# - SCLD GRM SERVICE
# - Copyright 2009 AT&T Intellectual Properties
# OVERVIEW
#    A simple start wrapper for lrmcli.  LRM is the manager of the service
#    and will handle start, stop, monitoring, a boot startup of the service.
#    This script is simply a convience for development work or adhoc management
#    of the container instance.
##############################################################################

exec __INSTALL_ROOT__/opt/app/aft/scldlrm/bin/lrmcli -status -name ${groupId}.${artifactId} -version __MAJOR_VERSION__.__MINOR_VERSION__.__PATCH_VERSION__ -routeoffer __SCLD_ROUTE_OFFER__
