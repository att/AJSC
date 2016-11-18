/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc;

import org.apache.camel.Exchange;

public class HelloWorld {
	public HelloWorld () {
	}
	
	public final void speak(Exchange e) {
		e.setOut(e.getIn());
		e.getOut().setBody("Hello World!");
	}
}