#Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
This etc/ directory is being used to house cadi.properties and the keyfile used by cadi during bootstrapping. These are used
when running the "runAjsc" profile locally.  The maven-exec-plugin used within runAjsc defaults all relative directories to 
the project's directory.  You will see the value for cadi.properties within src/main/config/runner-web.xml. When installing 
to a soa cloud node, this property will be read appropriately from the working directory of the project (AJSC_HOME/etc).

Therefore, you will see cadi.properties and keyfile within your ${basedir}/etc AND you will see it within your
src/main/config (this becomes your AJSC_HOME/etc).  This is meant to simplify running locally and installation to a node 
so that you do NOT have to modify runner-web.xml.
