/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.exceptions;

/**
 * This exception indicates that a resource that was being requested was not available. This is likely from resource
 * exhaustion.
 */
public class ResourceUnavailableException extends ZoneException {

    /**
     * Serial version ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *            The message that explains the exception
     */
    public ResourceUnavailableException(String message) {
        super(message);
    }

    /**
     * @param message
     *            The message that explains the exception
     * @param cause
     *            The nested exception that is being wrapped
     */
    public ResourceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     *            The nested exception that is being wrapped
     */
    public ResourceUnavailableException(Throwable cause) {
        super(cause);
    }
}
