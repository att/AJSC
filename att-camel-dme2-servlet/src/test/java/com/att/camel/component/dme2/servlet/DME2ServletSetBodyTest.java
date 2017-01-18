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

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletUnitClient;

import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;

public class DME2ServletSetBodyTest extends ServletCamelRouterTestSupport {

	@Test
	public void testSetBody() throws Exception {

		//System.out.println("Endpoint Map" + context.getEndpointMap());
		WebRequest req = new GetMethodWebRequest(CONTEXT_URL + "/");
		ServletUnitClient client = newClient();
		WebResponse response = null;
		response = client.getResponse(req);
		assertEquals("The response message is", "Hello World",
				response.getText());
	}

	protected RouteBuilder createRouteBuilder() throws Exception {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("att-dme2-servlet:///").setBody().constant(
						"Hello World");
			}
		};
	}

}
