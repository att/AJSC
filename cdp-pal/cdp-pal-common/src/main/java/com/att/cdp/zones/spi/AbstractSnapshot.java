/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.SnapshotService;

/**
 * @since Mar 24, 2015
 * @version $Id$
 * @Deprecated Use the volume service instead. Snapshots are point-in-time copies of volumes and are better managed as
 *             part of the volume service.
 */
@Deprecated
public abstract class AbstractSnapshot extends AbstractService implements SnapshotService {

    /**
     * Create the abstract snapshot service implementation for the specified context
     * 
     * @param context
     *            The context that we are providing the services for
     */
    public AbstractSnapshot(Context context) {
        super(context);
    }

}
