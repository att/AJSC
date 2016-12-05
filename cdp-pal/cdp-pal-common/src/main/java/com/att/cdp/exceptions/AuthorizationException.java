/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.exceptions;

/**
 * This class is thrown when the user is not authorized to perform some operation
 */

public class AuthorizationException extends ZoneException {

    /**
     * Serial version ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *            The diagnostic message
     */
    public AuthorizationException(String message) {
        super(message);
    }

    /**
     * @param message
     *            The diagnostic message
     * @param cause
     *            The cause of the exception
     */
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     *            The cause of the exception
     */
    public AuthorizationException(Throwable cause) {
        super(cause);
    }
}
