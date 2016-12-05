/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.exceptions;

/**
 * This exception is thrown whenever a data conversion operation is requested that cannot be performed.
 */
public class ConversionException extends ZoneException {

    /**
     * The serial version ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create the exception with an unknown provider
     * 
     * @param message
     *            The diagnostic message
     */
    public ConversionException(String message) {
        super(message);
    }

    /**
     * Create the exception with an unknown provider
     * 
     * @param message
     *            The diagnostic message
     * @param cause
     *            The cause of the exception
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create the exception with an unknown provider
     * 
     * @param cause
     *            The cause of the exception
     */
    public ConversionException(Throwable cause) {
        super(cause);
    }
}
