#!/bin/sh

#*******************************************************************************
# Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
#  
#*******************************************************************************

touch /app.jar

java -Djava.security.egd=file:/dev/./urandom -Xms1024m -Xmx1024m -jar /app.jar

