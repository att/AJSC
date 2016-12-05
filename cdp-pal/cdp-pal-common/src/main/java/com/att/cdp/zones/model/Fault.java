/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import com.att.cdp.zones.Context;

/**
 * This class represents a fault or error condition. It is generic, and can be used to represent error states and
 * diagnostics for servers, volumes, and other model objects.
 * 
 * @since Oct 25, 2013
 * @version $Id$
 */
public class Fault extends ModelObject {
    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * The error code or status that represents the fault
     */
    private String code;

    /**
     * A diagnostic message that provides the reason or cause of the fault
     */
    private String details;

    /**
     * A diagnostic message that indicates the type of fault
     */
    private String message;

    /**
     * Default constructor
     */
    public Fault() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Fault(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        Fault other = (Fault) obj;

        result = (code.equals(other.code));
        if (message != null && other.message != null) {
            result = message.equals(other.message);
        } else if (message != null || other.message != null) {
            result = false;
        }
        if (details != null && other.details != null) {
            result = details.equals(other.details);
        } else if (details != null || other.details != null) {
            result = false;
        }

        return result;
    }

    /**
     * JavaBean accessor to obtain the value of code
     * 
     * @return the code value
     */
    public String getCode() {
        return code;
    }

    /**
     * JavaBean accessor to obtain the value of details
     * 
     * @return the details value
     */
    public String getDetails() {
        return details;
    }

    /**
     * JavaBean accessor to obtain the value of message
     * 
     * @return the message value
     */
    public String getMessage() {
        return message;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return code.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("FAULT: code(%s), message(%s), detail message(%s)", code,
            (message == null ? "" : message), (details == null ? "" : details));
    }

    /**
     * @param code
     *            the value for code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @param details
     *            the value for details
     */
    public void setDetails(String details) {
        this.details = details;
    }

    /**
     * @param message
     *            the value for message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
