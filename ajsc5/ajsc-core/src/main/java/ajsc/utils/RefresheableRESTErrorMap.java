/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.utils;


import java.io.File;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.http.HttpServletResponse;

import ajsc.common.CommonNames;
import ajsc.exceptions.RestError;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


public class RefresheableRESTErrorMap 
{
	private static AtomicReference<HashMap<String, RestError>> wrapped = new AtomicReference<HashMap<String, RestError>>();
	
	public static void refresh(File file) throws Exception
	{
		try
		{
			System.out.println("Loading error to http status code map...");
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<HashMap<String, RestError>> typeRef = new TypeReference<HashMap<String, RestError>>() {};
			HashMap<String, RestError> map = mapper.readValue(file, typeRef);
			wrapped.set(map);
			System.out.println("File " + file.getName() + " is loaded into the error map");
		}
		catch ( Exception e )
		{
			System.err.println("File " + file.getName() + " cannot be loaded into the error map " + e.getMessage());
			throw new Exception("Error reading REST error map file " + file.getName(), e);
		}
		finally
		{
			System.out.println("Done with load...");
		}
	}
	
	public static RestError getHttpCodeForCSIError(String error)
	{
		RestError re = null;
		if ( wrapped != null && wrapped.get() != null ) 
			re = wrapped.get().get(error);
		if ( re == null )
		{
			re = new RestError();
			String coarseErrorCode = error.substring(0, 3);
			
			if ( CommonNames.CSI_AUTH_ERROR.equals(coarseErrorCode) )
			{
				re.status = HttpServletResponse.SC_FORBIDDEN;
				re.MessageId = "POL1000";
				re.Message = "The operation is not allowed: %1";
			}
			else if ( CommonNames.CSI_SERVICE_UNAVAIL_ERROR.equals(coarseErrorCode))
			{
				re.status = HttpServletResponse.SC_SERVICE_UNAVAILABLE;
				re.MessageId = "SVC2000";
				re.Message = "A resource required by the service is unavailable: %1";
			}
			else if ( CommonNames.CSI_DATA_ERROR.equals(coarseErrorCode) )
			{
				re.status = HttpServletResponse.SC_BAD_REQUEST;
				re.MessageId = "SVC3000";
				re.Message = "A data error has occurred: %1";
			}
			else if ( CommonNames.CSI_REQUEST_XML_ERROR.equals(coarseErrorCode) )
			{
				re.status = HttpServletResponse.SC_BAD_REQUEST;
				re.MessageId = "SVC4000";
				re.Message = "The request is invalid: %1";
			}
			else if ( CommonNames.CSI_BUS_PROC_ERROR.equals(coarseErrorCode) )
			{
				re.status = HttpServletResponse.SC_BAD_REQUEST;
				re.MessageId = "SVC5000";
				re.Message = "A business processing error has occured: %1";
			}
			else
			{
				re.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
				re.MessageId = "SVC9999";
				re.Message = "An internal error has occurred: %1";
			}
		}
		return re;
	}
}
