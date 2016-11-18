#Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.

The ajsc-shared-config folder is included in the service project to provide the functionality of the AJSC_SHARED_CONFIG 
location that will exist in CSI envs. This includes the logback.xml for logging configurations, and some csm related 
artifacts necessary for proper functionality of the csm framework within the CSI env. Within the 2 profiles that can 
be utilized to run the AJSC locally, "runLocal" and "runAjsc", the system propery, "AJSC_SHARED_CONFIG", has been set
to point to this directory. The files in this folder will NOT be copied/moved anywhere within the AJSC SWM package. These 
files will already be in existence within the CSI env.