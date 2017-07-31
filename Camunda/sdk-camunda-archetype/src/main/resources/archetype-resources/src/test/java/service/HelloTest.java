/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
package ${package}.service;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import ${package}.service.TestConfiguration;

import ${package}.model.HelloWorld;
import ${package}.service.rs.RestService;
import ${package}.Application;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = { Application.class, TestConfiguration.class})
public class HelloTest {

	@Autowired
	RestService service;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testQuickHello() throws Exception {
		HelloWorld helloWorld = service.getQuickHello("test");
		assertEquals("Hello test!", helloWorld.getMessage());
	}
}
