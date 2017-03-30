/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *  
 *******************************************************************************/
#set($symbol_pound='#')#set($symbol_dollar='$')#set($symbol_escape='\')package ${package}.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.att.ajsc.common.AjscProvider;
import com.att.ajsc.common.exception.ServerErrorException;
import com.att.ajsc.common.exception.ServiceException;

@AjscProvider
public class AjscExceptionMapper implements ExceptionMapper<Exception> {

	@Override
	public Response toResponse(final Exception exception) {
		ServiceException serviceException = null;

		if (exception instanceof ServiceException) {
			serviceException = (ServiceException) exception;
		} else {
			serviceException = new ServerErrorException(exception.getMessage());
		}

		return serviceException.toResponse();
	}
}