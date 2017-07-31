/*******************************************************************************
 * Copyright (c) 2017 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi.model;

import org.slf4j.Logger;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.pal.configuration.ConfigurationFactory;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Hypervisor;
import com.att.cdp.zones.spi.map.ObjectMapper;

/**
 * @author <a href= "mailto:ry303t@att.com?subject=com.att.cdp.zones.spi.model.ConnectedHypervisor"> Ryan Young</a>
 * @since Jan 10, 2017
 * @version $Id$
 */

public class ConnectedHypervisor extends Hypervisor {
    protected static final Logger LOG = ConfigurationFactory.getConfiguration().getServerLogger();

    /**
     * The serial number of this class
     */
    private static final long serialVersionUID = 1L;

    /**
     * Create a connected <code>Hypervisor</code> object that is navigable
     * 
     * @param context
     *            The context that we are processing
     */
    public ConnectedHypervisor(Context context) {
        super(context);
    }

    /**
     * @see com.att.cdp.zones.model.Hypervisor#refreshAll()
     */
    @Override
    public void refreshAll() throws ZoneException {
        Context context = getContext();
        Hypervisor copy = context.getComputeService().getHypervisor(getId());
        ObjectMapper.map(copy, this);
    }

    /**
     * @see com.att.cdp.zones.model.Hypervisor#refreshState()
     */
    @Override
    public void refreshState() throws ZoneException {
        Context context = getContext();
        context.getComputeService().refreshHypervisorState(this);
    }

    /**
     * @see com.att.cdp.zones.model.Hypervisor#refreshStatus()
     */
    @Override
    public void refreshStatus() throws ZoneException {
        Context context = getContext();
        context.getComputeService().refreshHypervisorStatus(this);
    }
}
