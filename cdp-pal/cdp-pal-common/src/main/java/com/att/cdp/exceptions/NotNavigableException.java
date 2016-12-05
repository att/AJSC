/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.exceptions;

/**
 * This exception is thrown if the user is trying to navigate the object model using an object that is disconnected from
 * the context.
 * <p>
 * When the API service implementations create an object in the rime object model and return it to the caller, that
 * object is "connected" to the context that was in effect when it was created. This allows the service implementation
 * to allow the user to navigate the object model directly, without actually having to lookup and invoke services. This
 * is an alternative way to access the data that may fit easier into different applications.
 * </p>
 * <p>
 * However, the API object model allows the user to create the objects as well. These objects are "disconnected" from
 * any context, and are typically used as "templates" to create actual objects, list filters, etc. They are not
 * navigable as is, because they are disconnected. However, when a template object is used to create an object in the
 * model, it becomes "connected" when that service is successful.
 * </p>
 */
public class NotNavigableException extends ZoneException {

    /**
     * Serial version ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *            The diagnostic message
     */
    public NotNavigableException(String message) {
        super(message);
    }

    /**
     * @param message
     *            The diagnostic message
     * @param cause
     *            The cause of the exception
     */
    public NotNavigableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     *            The cause of the exception
     */
    public NotNavigableException(Throwable cause) {
        super(cause);
    }
}
