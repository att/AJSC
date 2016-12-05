/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.exceptions;

/**
 * This exception indicates that the API or an object being operated upon is not in a valid state for the desired
 * operation.
 */
public class StateException extends ZoneException {

    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *            the detail message (which is saved for later retrieval by the getMessage() method).
     */
    public StateException(String message) {
        super(message);
    }

    /**
     * @param message
     *            the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause
     *            the cause (which is saved for later retrieval by the getCause() method). (A null value is permitted,
     *            and indicates that the cause is nonexistent or unknown.)
     */
    public StateException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     *            the cause (which is saved for later retrieval by the getCause() method). (A null value is permitted,
     *            and indicates that the cause is nonexistent or unknown.)
     */
    public StateException(Throwable cause) {
        super(cause);
    }
}
