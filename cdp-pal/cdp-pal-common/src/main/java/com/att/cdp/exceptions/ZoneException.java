/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.exceptions;


/**
 * This is a base class that defines all checked exceptions that are thrown by the RIME api or any implementation of
 * that API.
 * <p>
 * This is a non-specific exception that represents the base of the exception hierarchy for all specific exceptions
 * thrown from the rime api. This exception can be constructed with either a provider name or a context, whereas the
 * specific exceptions are always created with a context. That is because during bootstrapping of the provider, we may
 * not have a context yet. After that, the more specific exceptions will always have a context.
 * </p>
 */
public class ZoneException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified detail message. The cause is not initialized, and may subsequently
     * be initialized by a call to initCause.
     * 
     * @param message
     *            the detail message. The detail message is saved for later retrieval by the getMessage() method.
     */
    public ZoneException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     * <p>
     * Note that the detail message associated with cause is not automatically incorporated in this exception's detail
     * message.
     * </p>
     * 
     * @param message
     *            the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause
     *            the cause (which is saved for later retrieval by the getCause() method). (A null value is permitted,
     *            and indicates that the cause is nonexistent or unknown.)
     */
    public ZoneException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause and a detail message of (cause==null ? null :
     * cause.toString()) (which typically contains the class and detail message of cause). This constructor is useful
     * for exceptions that are little more than wrappers for other throwables (for example,
     * java.security.PrivilegedActionException).
     * 
     * @param cause
     *            the cause (which is saved for later retrieval by the getCause() method). (A null value is permitted,
     *            and indicates that the cause is nonexistent or unknown.)
     */
    public ZoneException(Throwable cause) {
        super(cause);
    }

}
