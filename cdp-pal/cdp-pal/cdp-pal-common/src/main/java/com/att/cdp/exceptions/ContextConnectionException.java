/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.exceptions;

/**
 * This exception is used to indicate that the provider cannot be accessed over the current context. This is typically
 * caused by a communications failure, failure of the provider itself, or anything that prevents communication with the
 * provider.
 */
public class ContextConnectionException extends ZoneException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     */
    public ContextConnectionException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ContextConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public ContextConnectionException(Throwable cause) {
        super(cause);
    }
}
