/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import com.att.cdp.zones.Context;

/**
 * A persistent object is any object that can be defined, saved, and persists across server instance lifetimes.
 * 
 * @since Sep 30, 2013
 * @version $Id$
 */
public class PersistentObject extends ModelObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor
     */
    public PersistentObject() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected PersistentObject(Context context) {
        super(context);
    }
}
