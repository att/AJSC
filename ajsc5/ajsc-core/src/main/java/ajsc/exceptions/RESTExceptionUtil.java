/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.exceptions;

import javax.servlet.http.HttpServletResponse;

import ajsc.common.CommonErrors;
import ajsc.utils.UtilLib;

public class RESTExceptionUtil 
{
	public static CSIRESTException nativeRESTError(int httpCode, String code, String message, String ... variables)
	{
		CSIRESTException csire = new CSIRESTException(httpCode, code, message, variables);
		csire = setCaetParametersFromHttpCode(httpCode, csire);
		return csire;
	}
	public static CSIRESTException nativeRESTErrorFromProvider(int httpCode, 
														String code, 
														String message, 
														String faultEntity,
														String extCode,
														String extDesc,
														String ... variables)
	{
		CSIRESTException csire = new CSIRESTException(httpCode, code, message, variables);
		csire = setCaetParametersFromHttpCode(httpCode, csire);
		csire.setCaetFaultEntity(faultEntity);
		csire.setCaetFaultDesc(csire.getCaetFaultDesc() + "--" + extCode + "--" + extDesc);
		return csire;
	}
	
	public static CSIRESTException csiRESTError(String code, String message)
	{
		CSIRESTException csire = new CSIRESTException(code, message);
		return csire;
	}
	public static CSIRESTException csiRESTErrorFromProvider(String code,
													 String message,
													 String faultEntity,
													 String extCode,
													 String extDesc)
	{
		CSIRESTException csire = new CSIRESTException(code, message);
		csire.setCaetFaultEntity(faultEntity);
		csire.setCaetFaultDesc(csire.getCaetFaultDesc() + "--" + extCode + "--" + extDesc);
		return csire;
	}
	
	private static CSIRESTException setCaetParametersFromHttpCode(int httpCode, CSIRESTException csire)
	{
		if ( httpCode == HttpServletResponse.SC_UNAUTHORIZED )
		{
			csire.setCaetFaultCode(CommonErrors.DEF_401_FAULT_CODE);
			csire.setCaetFaultDesc(CommonErrors.DEF_401_FAULT_DESC);
		}
		else if ( httpCode == HttpServletResponse.SC_FORBIDDEN )
		{
			csire.setCaetFaultCode(CommonErrors.DEF_403_FAULT_CODE);
			csire.setCaetFaultDesc(CommonErrors.DEF_403_FAULT_DESC);
		}
		else if ( httpCode == HttpServletResponse.SC_NOT_IMPLEMENTED )
		{
			csire.setCaetFaultCode(CommonErrors.DEF_501_FAULT_CODE);
			csire.setCaetFaultDesc(CommonErrors.DEF_501_FAULT_DESC);
		}
		else if ( httpCode == HttpServletResponse.SC_SERVICE_UNAVAILABLE )
		{
			csire.setCaetFaultCode(CommonErrors.DEF_503_FAULT_CODE);
			csire.setCaetFaultDesc(CommonErrors.DEF_503_FAULT_DESC);
		}
		else if ( 400 <= httpCode && httpCode <= 499 )
		{
			csire.setCaetFaultCode(CommonErrors.DEF_4NN_FAULT_CODE);
			csire.setCaetFaultDesc(CommonErrors.DEF_4NN_FAULT_DESC);
		}
		
		return csire;
	}
}
