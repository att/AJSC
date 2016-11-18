Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.

The bundleconfig-local directory contains the necessary configuration files to be used for running locally. When running 
locally, the "mvn -P runLocal" or the "mvn -P runAjsc" profiles will be using this bundleconfig-local directory as the AJSC_CONF
directory. When deploying to a CSI env, the bundleconfig-csi directory will be copied to the ultimate installation/bundleconfig 
directory and will be used for your AJSC service once installed. If you are not deploying to a CSI env, please look at the 
antBuild/build.xml file for help in some simple copying of the appropriate folders/files for a NON-CSI env. 

The ajsc-shared-config directory houses the shared configurations that will be used in CSI envs. This includes the logging 
functionality of the logback.xml and some csm related artifacts that may be necessary to use while running locally.
When running locally, the system property, "AJSC_SHARED_CONFIG", will point to this location to utilize the logback.xml.