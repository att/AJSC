/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

/**
 * This is the base class for all Heat model objects.
 * 
 * @since May 26, 2015
 * @version $Id$
 */

public abstract class ModelObject implements Cloneable {

    /**
     * Clones the model object base class.
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
