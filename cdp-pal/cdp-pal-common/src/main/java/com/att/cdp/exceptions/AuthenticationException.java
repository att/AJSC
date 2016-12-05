/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.exceptions;

/**
 * This exception is thrown if the login fails because the principal or credential are invalid or not supplied.
 */
public class AuthenticationException extends ZoneException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *            The diagnostic message
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * @param message
     *            The diagnostic message
     * @param cause
     *            The cause of the exception
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     *            The cause of the exception
     */
    public AuthenticationException(Throwable cause) {
        super(cause);
    }
}
