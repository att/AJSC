/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Image;
import com.att.cdp.zones.spi.map.ObjectMapper;

/**
 * @since Oct 8, 2013
 * @version $Id$
 */
public class ConnectedImage extends Image {

    /**
     * The serial number of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected <code>Image</code> object that is navigable
     * 
     * @param context
     *            The context that we are processing
     */
    public ConnectedImage(Context context) {
        super(context);
    }
    
    /**
     * @see com.att.cdp.zones.model.Image#refreshAll()
     */
    @Override
    public void refreshAll() throws ZoneException {
        Context context = getContext();
        Image copy = context.getImageService().getImage(getId());
        ObjectMapper.map(copy, this);
    }
}
