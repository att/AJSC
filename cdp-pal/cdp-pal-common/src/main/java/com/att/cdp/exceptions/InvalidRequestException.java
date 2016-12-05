/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.exceptions;

/**
 * This exception indicates that the operation requested is invalid. This can be from missing arguments, invalid
 * lengths, etc.
 */

public class InvalidRequestException extends ZoneException {

    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *            the message detailing the operation being attempted
     */
    public InvalidRequestException(String message) {
        super(message);
    }

    /**
     * @param message
     *            the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause
     *            the cause (which is saved for later retrieval by the getCause() method). (A null value is permitted,
     *            and indicates that the cause is nonexistent or unknown.) *
     */
    public InvalidRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}
