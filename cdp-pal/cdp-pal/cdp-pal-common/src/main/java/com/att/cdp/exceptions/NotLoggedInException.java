/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.exceptions;

/**
 * This exception is thrown if the user is not logged in to the provider and attempts to make a service request
 */
public class NotLoggedInException extends AuthenticationException {

    /**
     * Serial version ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *            The diagnostic message
     */
    public NotLoggedInException(String message) {
        super(message);
    }

    /**
     * @param message
     *            The diagnostic message
     * @param cause
     *            The cause of the exception
     */
    public NotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     *            The cause of the exception
     */
    public NotLoggedInException(Throwable cause) {
        super(cause);
    }
}
