/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.exception;

import com.att.cdp.exceptions.ZoneException;

/**
 * 
 * @since May 26, 2015
 * @version $Id$
 */

public class UnmarshallException extends ZoneException {

    /**
     * @param message
     */
    public UnmarshallException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public UnmarshallException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public UnmarshallException(Throwable cause) {
        super(cause);
    }
}
