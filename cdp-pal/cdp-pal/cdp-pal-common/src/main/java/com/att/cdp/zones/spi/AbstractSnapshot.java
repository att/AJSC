/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.SnapshotService;
import com.att.cdp.zones.model.Server;
import com.att.cdp.zones.model.Volume;

/**
 * @since Mar 24, 2015
 * @version $Id$
 * @deprecated Use the volume service instead. Snapshots are point-in-time copies of volumes and are better managed as
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
