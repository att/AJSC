/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.att.camel.component.dme2.servlet;

import java.util.Map;
import org.apache.camel.SuspendableService;
import org.apache.camel.component.http.HttpConsumer;
import org.apache.camel.component.servlet.HttpRegistry;
import org.apache.camel.component.servlet.ServletComponent;
import org.apache.camel.component.servlet.ServletEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DME2ServletComponent extends ServletComponent implements
		SuspendableService {

	private String servletName = "CamelServlet";
	private HttpRegistry httpRegistry;

	private static final Logger logger = LoggerFactory
			.getLogger(DME2ServletComponent.class);

	@Override
	protected ServletEndpoint createEndpoint(String uri, String remaining,
			Map<String, Object> parameters) throws Exception {

		ServletEndpoint endpoint = (ServletEndpoint) super.createEndpoint(uri,
				remaining, parameters);

		return endpoint;
	}

	@Override
	public void connect(HttpConsumer consumer) throws Exception {
		super.connect(consumer);
	}

	@Override
	public void disconnect(HttpConsumer consumer) throws Exception {
		super.disconnect(consumer);
	}

	public String getServletName() {
		return servletName;
	}

	public void setServletName(String servletName) {
		this.servletName = servletName;
	}

	public HttpRegistry getHttpRegistry() {
		return httpRegistry;
	}

	public void setHttpRegistry(HttpRegistry httpRegistry) {
		this.httpRegistry = httpRegistry;
	}

	@Override
	public void doStart() throws Exception {
		super.doStart();

	}

	@Override
	public void doStop() throws Exception {

		super.doStop();
	}

}