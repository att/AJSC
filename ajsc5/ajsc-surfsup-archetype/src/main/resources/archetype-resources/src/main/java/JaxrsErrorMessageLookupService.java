package ${package};

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Produces;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import ajsc.ErrorMessageLookupService;


@Path("/errormessage")
public class JaxrsErrorMessageLookupService {
	@GET
	@Path("/emls")
	@Produces("text/plain")
	public String getMessage(@PathParam("input") String input,
			@HeaderParam("errorCode") String errorCode,
			@HeaderParam("appId") String appId,
			@HeaderParam("operation") String operation,
			@HeaderParam("messageText") String messageText) {
		
		Map<String,String> headers = new HashMap<String,String>();
		headers.put(errorCode, errorCode);
		headers.put(appId, appId);
		headers.put(operation, operation);
		headers.put(messageText, messageText);

		WebApplicationContext applicationContext = ContextLoader
				.getCurrentWebApplicationContext();

		ErrorMessageLookupService e = (ErrorMessageLookupService) applicationContext
				.getBean("errorMessageLookupService");

		String message = e.getExceptionDetails(appId,operation,errorCode,messageText,"true");

		System.out.println("Error code = " + errorCode);
		System.out.println("appId = " + appId);
		System.out.println("operation = " + operation);
		System.out.println("messageText = " + messageText);
		return "The exception message is:\n, " + message;
	}

}