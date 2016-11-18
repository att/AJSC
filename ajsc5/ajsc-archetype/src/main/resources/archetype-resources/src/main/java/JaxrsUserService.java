package ${package};

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Map;
import java.util.HashMap;

@Path("/user")
public class JaxrsUserService {
	
	private static final Map<String,String> userIdToNameMap;
	static {
		userIdToNameMap = new HashMap<String,String>();
		userIdToNameMap.put("dw113c","Doug Wait");
		userIdToNameMap.put("so401q","Stuart O'Day");
	}
	
    @GET
    @Path("/{userId}")
    @Produces("text/plain")
    public String lookupUser(@PathParam("userId") String userId) {
    	String name = userIdToNameMap.get(userId);
        return name != null ? name : "unknown id";
    }
    
}