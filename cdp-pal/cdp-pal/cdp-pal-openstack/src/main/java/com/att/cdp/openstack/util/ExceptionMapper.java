/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.util;

import java.util.Map;

import org.apache.http.HttpStatus;

import com.att.cdp.exceptions.AuthorizationException;
import com.att.cdp.exceptions.ContextConnectionException;
import com.att.cdp.exceptions.InvalidRequestException;
import com.att.cdp.exceptions.ResourceNotFoundException;
import com.att.cdp.exceptions.ResourceUnavailableException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.i18n.OSMsg;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.RequestState;
import com.att.eelf.i18n.EELFResourceManager;
import com.woorea.openstack.base.client.OpenStackBaseException;
import com.woorea.openstack.base.client.OpenStackConnectException;
import com.woorea.openstack.base.client.OpenStackResponseException;

/**
 * Maps exceptions from OpenStack to an appropriate ZoneException as defined by the abstraction
 * 
 * @since Oct 21, 2013
 * @version $Id$
 */
public final class ExceptionMapper {

    /**
     * This method is used to map an OpenStack connect exception to the appropriate Zone exception specialization
     * 
     * @param ex
     *            The exception to be mapped
     * @throws ZoneException
     *             The resulting zone exception specialization
     */
    @SuppressWarnings("nls")
    private static void mapConnectException(OpenStackConnectException ex) throws ZoneException {
        RequestState.getState();
        String methodName = (String) RequestState.get(RequestState.METHOD);
        String className = (String) RequestState.get(RequestState.CLASS);
        String lineNumber = (String) RequestState.get(RequestState.LINE_NUMBER);
        String threadName = (String) RequestState.get(RequestState.THREAD);
        String provider = (String) RequestState.get(RequestState.PROVIDER);
        if (provider == null) {
            provider = "UNKNOWN";
        }
        String service = (String) RequestState.get(RequestState.SERVICE);
        if (service == null) {
            service = "UNKNOWN";
        }
        String serviceUrl = (String) RequestState.get(RequestState.SERVICE_URL);
        if (serviceUrl == null) {
            serviceUrl = "UNKNOWN";
        }

        throw new ContextConnectionException(EELFResourceManager.format(OSMsg.PAL_OS_CONNECTION_FAILED, provider, service,
            serviceUrl, className, methodName, lineNumber, threadName), ex);
    }

    /**
     * This method is used to map an OpenStackBaseException to an appropriate exception message and exception
     * 
     * @param ex
     *            The openStackBaseException to be mapped
     * @throws ZoneException
     *             The Exception appropriate to the OpenStack exception
     */
    public static void mapException(OpenStackBaseException ex) throws ZoneException {
        if (ex instanceof OpenStackResponseException) {
            mapResponseException((OpenStackResponseException) ex);
        } else if (ex instanceof OpenStackConnectException) {
            mapConnectException((OpenStackConnectException) ex);
        }
    }

    /**
     * This method is used to map an OpenStack response exception to the appropriate Zone exception specialization
     * 
     * @param ex
     *            The exception to be mapped
     * @throws ZoneException
     *             The resulting zone exception specialization
     */
    @SuppressWarnings("nls")
    private static void mapResponseException(OpenStackResponseException ex) throws ZoneException {
        String exceptionType = ex.getClass().getSimpleName();
        int code = ex.getStatus();
        org.glassfish.grizzly.http.util.HttpStatus status =
            org.glassfish.grizzly.http.util.HttpStatus.getHttpStatus(code);
        String meaning = status.getReasonPhrase();
        String errorEntity = ex.getResponse().getErrorEntity(String.class);
        Map<String, Object> state = RequestState.getState();
        String methodName = (String) state.get(RequestState.METHOD);

        StringBuffer buffer = new StringBuffer();
        for (String key : state.keySet()) {
            if (state.get(key) != null) {
                buffer.append(key + "=" + state.get(key).toString() + ", ");
            }
        }
        if (buffer.length() > 1) {
            buffer.delete(buffer.length() - 2, buffer.length());
        }

        String message =
            EELFResourceManager.format(OSMsg.PAL_OS_PROVIDER_EXCEPTION, exceptionType, Integer.toString(code), meaning,
                methodName, ex.getMessage() + " " + errorEntity, buffer.toString());

        switch (code) {
            case HttpStatus.SC_UNAUTHORIZED: // 401 - Unauthorized
            case HttpStatus.SC_FORBIDDEN: // 403 - Forbidden
            case HttpStatus.SC_METHOD_NOT_ALLOWED: // 405 - Not allowed
            case HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED: // 407 - Proxy auth required
                throw new AuthorizationException(message);

            case HttpStatus.SC_NOT_FOUND: // 404 - Not found
                throw new ResourceNotFoundException(message);

            case HttpStatus.SC_BAD_REQUEST: // 400 - Bad Request
            case HttpStatus.SC_NOT_ACCEPTABLE: // 406 - Not acceptable
            case HttpStatus.SC_CONFLICT: // 409 - Conflict
            case HttpStatus.SC_GONE: // 410 - Gone
            case HttpStatus.SC_LENGTH_REQUIRED: // 411 - Length Required
            case HttpStatus.SC_PRECONDITION_FAILED: // 412 - Precondition failed
            case HttpStatus.SC_REQUEST_TOO_LONG: // 413 - Request entity too large
            case HttpStatus.SC_REQUEST_URI_TOO_LONG: // 414 - Request URI too long
            case HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE: // 415 - Unsupported Media type
            case HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE: // 416 - Requested range not satisfied
            case HttpStatus.SC_EXPECTATION_FAILED: // 417 - Expectation failed
                throw new InvalidRequestException(message);

            case HttpStatus.SC_REQUEST_TIMEOUT: // 408 - Timeout
                throw new ResourceUnavailableException(message);
        }

        throw new ZoneException(message);
    }

    /**
     * Private constructor prevents instantiation
     */
    private ExceptionMapper() {

    }
}
