/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/
package ajsc.exceptions;

import javax.servlet.http.HttpServletResponse;

import ajsc.utils.RequestErrorInfo;
import ajsc.utils.UtilLib;

import ajsc.utils.ExceptionInfo;

import ajsc.common.CommonErrors;

public class CSIRESTException extends Exception 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int httpCode_ = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
	private ExceptionInfo ex_ = new ExceptionInfo();
	private RequestErrorInfo re_ = new RequestErrorInfo();

	private String xCaetFaultCode_ = CommonErrors.DEF_5NN_FAULT_CODE;
	private String xCaetFaultDesc_ = CommonErrors.DEF_5NN_FAULT_DESC;
	private String xCaetFaultEntity_ = "CSI";
	
	/*
	 * This is the case where the http code, message and all variables are known in advance
	 * 
	 */
	CSIRESTException(int httpCode, String code, String message, String ... variables)
	{
		httpCode_ = httpCode;
		createRequestError(code, message, variables);
	}
	
	/*
	 * Rules for creating the error messages from CSI soap fault
	 * 1. Check if the faultCode is mapped in the error map
	 * 2. If not convert to a standard set of id's and messages.
	 * 3. If there is no message from the error map use the message from the soap fault
	 * 4. If there is a message - check for the %1 argument
	 * 5. If it exists - use the SOAP message as a variable
	 * 6. If it does not use the message from the RestError object
	 */
	CSIRESTException(String code, String message)
	{
		RestError re = RefresheableRESTErrorMap.getHttpCodeForCSIError(code);
		setCaetFaultCode(code);
		setCaetFaultDesc(message);
		httpCode_ = re.status;
		
		if ( UtilLib.isNullOrEmpty(re.Message) )
		{
			createRequestError(re.MessageId, UtilLib.isNullOrEmpty(message)?re.Message:message, new String[]{});
		}
		else
		{
			if ( re.Message.contains("%1") )
				createRequestError(re.MessageId, re.Message, new String[]{message});
			else
				createRequestError(re.MessageId, re.Message, new String[]{});
		}
	}
	
	
	
	public int getHTTPErrorCode()
	{
		return httpCode_;
	}
	
	public void setCaetFaultCode(String xCaetFaultCode)
	{
		if ( !UtilLib.isNullOrEmpty(xCaetFaultCode) && xCaetFaultCode.length() > 3 )
			xCaetFaultCode_ = xCaetFaultCode;
	}
	public String getCaetFaultCode()
	{
		return xCaetFaultCode_;
	}
	
	public void setCaetFaultDesc(String xCaetFaultDesc)
	{
		if ( !UtilLib.isNullOrEmpty(xCaetFaultDesc) )
			xCaetFaultDesc_ = xCaetFaultDesc;
	}
	public String getCaetFaultDesc()
	{
		return xCaetFaultDesc_;
	}
	
	public void setCaetFaultEntity(String xCaetFaultEntity)
	{
		if ( !UtilLib.isNullOrEmpty(xCaetFaultEntity) )
			xCaetFaultEntity_ = xCaetFaultEntity;
	}
	public String getCaetFaultEntity()
	{
		return xCaetFaultEntity_;
	}
	
	private void createRequestError(String code, String message, String vars[])
	{
		ex_.setMessageId(code);
		ex_.setText(message);
		for ( String variable : vars )
		{
			ex_.getVariables().add(variable);
		}
		if ( ex_.getMessageId() != null && ex_.getMessageId().startsWith("POL") )
			re_.setPolicyException(ex_);
		else
			re_.setServiceException(ex_);
	}

	public ExceptionInfo getEx_() {
		return ex_;
	}

	
}

