# CONTINUOUS DEPLOYMENT PLATFORM - PROVIDER ABSTRACTION LAYER

## OVERVIEW

CDP-PAL helps automate the creation, migration, and management of cloud-deployed applications and their needed hardware/software environment.
PAL will be used in the common cloud architecture to allow for the deployment, migration, and scaling up/down of cloud deployed applications.
This is more than just managing the VMs, or deploying just the software, it is both in an orchestrated and model-based approach.

The PAL API abstracts cloud IaaS service providers to a common model so that any application that interacts with PAL is isolated from the specific provider and becomes provider agnostic.  Applications that use PAL only need to know PAL regardless of the provider actually being used.


## REQUIREMENTS

* Java Development Kit (JDK) 1.7 and above


## BUILD
Checkout code using command:

https://github.com/att/AJSC.git

* Build cdp-pal project using maven command “mvn clean install”.
* Build cdp-pal-common using maven command “mvn clean install”.
* Build cdp-pal-openstack using maven command “mvn clean install”.


## RUN

Any application which wants to use cdp-pal-common and cdp-pal-openstack,
should add below dependencies.
```
<dependency> 
	<groupId>com.att.cdp</groupId> 
	<artifactId>cdp-pal-openstack</artifactId> 
	<version>0.0.1</version> 
</dependency>

<dependency> 
	<groupId>com.att.cdp</groupId> 
	<artifactId>cdp-pal-common</artifactId> 
	<version>0.0.1</version> 
</dependency>
```

