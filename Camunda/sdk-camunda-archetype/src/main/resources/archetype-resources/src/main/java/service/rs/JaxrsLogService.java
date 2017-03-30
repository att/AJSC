/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
#set($symbol_pound='#')#set($symbol_dollar='$')#set($symbol_escape='\')package ${package}.service.rs;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;


/**
 * Service to invoke example Camunda process.
 * 
 * Try testing by using:
 * http://[hostname]:[serverPort]/jaxrsservices/log/log-message/your-message-here
 * 
 */
@Api(value = "/log")
@Path("/log")
@Produces({MediaType.TEXT_PLAIN})
public interface JaxrsLogService {
	    
    /**
     * REST service that executes example camunda process to log input message.
     * 
     * @param logMessageText
     * @return output from service - comment on what was done
     */
    @GET
    @Path("/log-message/{logMessageText}")
    @Produces(MediaType.TEXT_PLAIN)
    public String logMessage(@PathParam("logMessageText") String logMessageText, @QueryParam("javamail") String javamail, @QueryParam("springmail") String springmail, @QueryParam("commonsmail") String commonsmail);
     
    
    /**
     * REST service that executes example camunda process to log input message.
     * 
     * @return output from service - comment on what was done
     */
    @POST
    @Path("/postLogHist")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.APPLICATION_JSON)
    public String postLogMessage(String  histEventList);

    /**
     * REST service that executes example camunda process to log input message.
     * 
     * @param logMessageText
     * @return output from service - comment on what was done
     */
    @GET
    @Path("/createLog/{startTime}/{endTime}/{serviceName}")
    @Produces(MediaType.TEXT_PLAIN)
    public String createLogMessage(@PathParam("startTime") String startTime,@PathParam("endTime") String endTime,@PathParam("serviceName") String serviceName);
    
    /**
     * REST service that executes example camunda process to log input message.
     * 
     * @param logMessageText
     * @return output from service - comment on what was done
     */
    @GET
    @Path("/createLogHist/{procInstId}/{histEventList}")
    @Produces(MediaType.TEXT_PLAIN)
    public String createLogMessageUsingHistory(@PathParam("procInstId") String procInstId,@PathParam("histEventList") String  histEventList);
    
    /**
     * REST service that executes example camunda process to log input message.
     * 
     * @param logMessageText
     * @return output from service - comment on what was done
     */
    @GET
    @Path("/histLog/{procInstId}")
    @Produces(MediaType.TEXT_PLAIN)
    public String CreateHistLog(@PathParam("procInstId") String procInstId);
    
}