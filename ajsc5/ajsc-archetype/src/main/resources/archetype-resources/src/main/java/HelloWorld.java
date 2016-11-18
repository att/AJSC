package ${package};

import org.apache.camel.Exchange;

public class HelloWorld {
	public HelloWorld () {
	}
	
	public final void speak(Exchange e) {
		e.setOut(e.getIn());
		e.getOut().setBody("Hello World!");
	}
}