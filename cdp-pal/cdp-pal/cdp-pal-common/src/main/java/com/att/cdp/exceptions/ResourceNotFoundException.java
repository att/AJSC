/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.exceptions;

/**
 * This exception indicates that a resource that was being requested could not be found.
 */
public class ResourceNotFoundException extends ZoneException {

    /**
     * Serial version ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *            The message that explains the exception
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * @param message
     *            The message that explains the exception
     * @param cause
     *            The nested exception that is being wrapped
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     *            The nested exception that is being wrapped
     */
    public ResourceNotFoundException(Throwable cause) {
        super(cause);
    }
}
