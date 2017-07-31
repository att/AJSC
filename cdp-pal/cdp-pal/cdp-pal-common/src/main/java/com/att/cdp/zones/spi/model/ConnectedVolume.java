/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Tenant;
import com.att.cdp.zones.model.Volume;
import com.att.cdp.zones.spi.map.ObjectMapper;

/**
 * @since Oct 8, 2013
 * @version $Id$
 */
public class ConnectedVolume extends Volume {

    /**
     * The serial number of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected <code>Volume</code> object that is navigable
     * 
     * @param context
     *            The context that we are processing
     */
    public ConnectedVolume(Context context) {
        super(context);
    }

    /**
     * @see com.att.cdp.zones.model.Volume#refresh()
     */
    @Override
    public void refresh() throws ZoneException {
        Context context = getContext();
        Volume copy = context.getVolumeService().getVolume(getId());
        ObjectMapper.map(copy, this);
    }
}
