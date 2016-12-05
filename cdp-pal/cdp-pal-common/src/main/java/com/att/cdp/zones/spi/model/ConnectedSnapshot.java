/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Tenant;
import com.att.cdp.zones.model.Snapshot;
import com.att.cdp.zones.spi.map.ObjectMapper;

/**
 * @since Mar 24, 2015
 * @version $Id$
 */
public class ConnectedSnapshot extends Snapshot {

    /**
     * The serial number of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected <code>Snapshot</code> object that is navigable
     * 
     * @param context
     *            The context that we are processing
     */
    public ConnectedSnapshot(Context context) {
        super(context);
    }

    /**
     * @see com.att.cdp.zones.model.Snapshot#refresh()
     */
    @Override
    public void refresh() throws ZoneException {
        Snapshot copy = getContext().getSnapshotService().getSnapshot(getId());
        ObjectMapper.map(copy, this);
    }
}
