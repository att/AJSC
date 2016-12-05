/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.exceptions;

/**
 * This exception indicates that the provider does not support the requested operation.
 */

public class NotSupportedException extends ZoneException {

    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param operation
     *            the operation being attempted
     */
    public NotSupportedException(String operation) {
        super(operation);
    }

    /**
     * @param operation
     *            the operation being attempted
     * @param message
     *            the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public NotSupportedException(String operation, String message) {
        super(message);
    }

    /**
     * @param message
     *            the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause
     *            the cause (which is saved for later retrieval by the getCause() method). (A null value is permitted,
     *            and indicates that the cause is nonexistent or unknown.) *
     */
    public NotSupportedException(String message, Throwable cause) {
        super(message, cause);
    }
}
