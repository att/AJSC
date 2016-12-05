/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.v1;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.AbstractPersistentObject;

/**
 * @since May 2, 2014
 * @version $Id$
 */

public class OpenStackObjectService extends AbstractPersistentObject {

    /**
     * @param context
     *            The context for the provider
     */
    public OpenStackObjectService(Context context) {
        super(context);
    }
}
