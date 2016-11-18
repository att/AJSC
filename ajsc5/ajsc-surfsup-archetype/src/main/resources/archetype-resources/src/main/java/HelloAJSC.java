package ${package};

import org.apache.camel.Exchange;
import org.json.JSONException;
import org.json.JSONObject;

public class HelloAJSC {
	public HelloAJSC () {
	}
	
	public final void returnJson(Exchange e) {
		e.setOut(e.getIn());
		 JSONObject obj = new JSONObject();
		 try {
			obj.put("AppName", "SurfsUp");
			obj.put("Company", "ATT");
			obj.put("Platform", "AJSC");
		 } catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.getOut().setBody(obj);
	}
	
	public final void returnXml(Exchange e) {
		e.setOut(e.getIn());
		 String inputMsg="<Root><company>ATT</company><platform>AJSC</platform><appName>SurfsUp</appName></Root>"; 
		e.getOut().setBody(inputMsg);
	}
}