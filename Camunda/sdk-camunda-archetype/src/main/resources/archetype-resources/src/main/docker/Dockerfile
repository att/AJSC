#*******************************************************************************
# Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
#  
#*******************************************************************************

FROM ${docker-registry}/com.att.ajsc/anapsix/alpine-java:8_jdk
VOLUME /tmp
ADD ${artifactId}-${version}.jar app.jar
VOLUME /etc
ADD /etc/ /etc/
ADD startService.sh /startService.sh
RUN chmod 700 /startService.sh
ENTRYPOINT ./startService.sh 
