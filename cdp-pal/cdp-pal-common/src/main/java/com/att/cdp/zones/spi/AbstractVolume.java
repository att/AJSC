/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.VolumeService;

/**
 * @since Oct 7, 2013
 * @version $Id$
 */

public abstract class AbstractVolume extends AbstractService implements VolumeService {

    /**
     * Create the abstract volume service implementation for the specified context
     * 
     * @param context
     *            The context that we are providing the services for
     */
    public AbstractVolume(Context context) {
        super(context);
    }

}
