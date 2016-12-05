/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Stack;
import com.att.cdp.zones.spi.map.ObjectMapper;

/**
 * This class represents a stack object that was obtained from the provider, and is therefore "connected" to the context
 * and which can be used to navigate the object model.
 * 
 * @since Jan 16, 2015
 * @version $Id$
 */
public class ConnectedStack extends Stack {

    /**
     * The serial version id for this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Creates the connected stack with the supplied name
     * 
     * @param context
     *            The context that we are connected to
     */
    public ConnectedStack(Context context) {
        super(context);
    }

    /**
     * @see com.att.cdp.zones.model.Stack#refresh()
     */
    @Override
    public void refresh() throws ZoneException {
        Context context = getContext();
        Stack copy = context.getStackService().getStack(getName(), getId());
        ObjectMapper.map(copy, this);
    }
}
