package ${package};

import java.sql.Timestamp;
import java.util.HashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;
import org.json.JSONObject;

import acsi.grid.gridcore.core.GridBag;
import acsi.grid.gridcore.core.GridManager;
import acsi.grid.gridcore.core.GridProvider;

import com.att.ajsc.beans.PropertiesMapBean;
import ${package}.filemonitor.ServicePropertiesMap;
import ${package}.grid.User;

@Path("/jaxrs-services")
public class JaxrsEchoService {
	
	private static final String GRID_PROVIDER_IMPL = "acsi.grid.gridcore.core.OdysseusGridProviderImpl";
	private GridProvider gridProvider=null;
	
	private void initGrid(){
		try{
			if(gridProvider == null){
				gridProvider = GridManager.getInstance().getGridProvider(
						GRID_PROVIDER_IMPL, "CassandraAdapterForCluster1");
			}
		}catch(Exception e){
			System.out.println("Grid Manager failed to initialize Grid Provider"+e);
		}
	}
	
    @GET
    @Path("/user/{input}")
    @Produces("text/plain")
    public String addUser(@PathParam("input") String input) {
   	 java.util.Date date= new java.util.Date();
     Timestamp ts = new Timestamp(date.getTime());
     String time = ts.toString();
     
     //Initialize the Grid provider instance if not already done so.
     initGrid();
     
     //Create a persistence User object
     User usrInfo = new User();
     usrInfo.setUserName(input);
     usrInfo.setHitTime(time);
     
     
     String result = persistData(usrInfo);
     
     User persistedUser = null;
     
     if(result.equalsIgnoreCase("success"))
    	 persistedUser = retrieveData(usrInfo.getUserName());
     
    // return "Hello, " + input + ". The current time is " + time;
     if(persistedUser!=null)
//    	 return "Hello, "+ persistedUser.getUserName()+". You visited this page on "+persistedUser.getHitTime()+" and you have been persisted successfully.";
    	 return persistedUser.getUserName()+" has been added to Cassandra database and persisted successfully using GRID at "+persistedUser.getHitTime() + ".";
    	 else
    	 return "Attempt to add " + usrInfo.getUserName() + " was UNSUCCESSFUL. Please, check your GRID settings and connectivity to Cassandra database.";
    }
    
    @GET
    @Path("/admin/{input}")
    @Produces("text/plain")
    public String addAdmin(@PathParam("input") String input) {
   	 java.util.Date date= new java.util.Date();
     Timestamp ts = new Timestamp(date.getTime());
     String time = ts.toString();
     
     //Initialize the Grid provider instance if not already done so.
     initGrid();
     
     //Create a persistence User object
     User usrInfo = new User();
     usrInfo.setUserName(input);
     usrInfo.setHitTime(time);
     
     
     String result = persistData(usrInfo);
     
     User persistedUser = null;
     
     if(result.equalsIgnoreCase("success"))
    	 persistedUser = retrieveData(usrInfo.getUserName());
     
    // return "Hello, " + input + ". The current time is " + time;
     if(persistedUser!=null)
//    	 return "Hello, "+ persistedUser.getUserName()+". You visited this page on "+persistedUser.getHitTime()+" and you have been persisted successfully.";
    	 return persistedUser.getUserName()+" has been added to Cassandra database and persisted successfully using GRID at "+persistedUser.getHitTime() + ".";
    	 else
    	 return "Attempt to add " + usrInfo.getUserName() + " was UNSUCCESSFUL. Please, check your GRID settings and connectivity to Cassandra database.";
    }
    
    
    private String persistData(User user){
    	
    	try{
    		GridBag<HashMap<String,String>, User> grid = gridProvider.createGridBag(User.class.getName());
        	grid.put(user);
    	}catch(Exception e){
    		System.out.println("*** Exception occured while persisting the object: "+e);
    		return "error";
    	}
    	return "success";
    	
    }
    
    private User retrieveData(String userName){
    	try{
    		GridBag<HashMap<String,String>, User> grid = gridProvider.createGridBag(User.class.getName());
    		
    		HashMap<String,String> key = new HashMap<String,String>();
    		key.put("userName",userName);
    		
    		User user = grid.get(key, User.class);
    		
    		return user;
    	}catch(Exception e){
    		System.out.println("*** Exception occured while retrieving the object: "+e);
    		return null;
    	}
    }
    
    @GET
    @Path("/property/{fileName}/{input:.*}")
    @Produces("text/plain")
    public String getProperty(@PathParam("fileName") String fileName, @PathParam("input") String input) {
    	String val=null;
    	try {
    		val = ServicePropertiesMap.getProperty(fileName, input);
    		if(val == null || val.isEmpty() || val.length() < 1){
    			val = PropertiesMapBean.getProperty(fileName, input);
    		}
    	}
    	catch(Exception ex) {
    		System.out.println("*** Error retrieving property "+input+": "+ex);
    	} 	 
    	if (val ==null) {
   		 	return "Property is not available";
    	}
    	return "Property value is, " + val +".";
    }  
    
    @GET
    @Path("/returnJSON/{input}")
    @Produces({ "application/xml", "application/json" })
    public Response returnJSON(@PathParam("input") String input) {
    	JSONObject obj = new JSONObject();
    	try {
    		obj.put("App Title", "SurfsUp");
    		obj.put("Name", input);
    	} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return Response.ok().entity(obj).build();
    }
}