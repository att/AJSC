# CONTINUOUS DEPLOYMENT PLATFORM - PROVIDER ABSTRACTION LAYER

## OVERVIEW

CDP-PAL is a thin, opaque API that surrounds the underlying cloud implementation, abstracting the implementation, providing a consistent and standard interface to access cloud infrastructure providers. CDP-PAL helps automate the creation, migration, and management of cloud-deployed applications and their needed hardware/software environment.
PAL will be used in the common cloud architecture to allow for the deployment, migration, and scaling up/down of cloud deployed applications.
This is more than just managing the VMs, or deploying just the software, it is both in an orchestrated and model-based approach.

The PAL API abstracts cloud IaaS service providers to a common model so that any application that interacts with PAL is isolated from the specific provider and becomes provider agnostic.  Applications that use PAL only need to know PAL regardless of the provider actually being used.

The CDP-PAL is composed of the following components:
### cdp-pal
This is the Maven project that controls the build of all the modules in the PAL layer.

### cdp-pal-common
This is the PAL API and consists mostly of interfaces that define the API behaviors that are exposed to the client of the API. Service provider interfaces and classes that get implemented by the provider are located here as well.

### cdp-pal-openstack
This is the OpenStack implementation of the PAL API


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

