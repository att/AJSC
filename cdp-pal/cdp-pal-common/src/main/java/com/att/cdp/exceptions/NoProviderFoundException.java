/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.exceptions;

public class NoProviderFoundException extends ZoneException {

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
    public NoProviderFoundException(String message) {
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
    public NoProviderFoundException(String message, Throwable cause) {
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
    public NoProviderFoundException(Throwable cause) {
        super(cause);
    }
}
