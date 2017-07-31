/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.util.HashMap;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedRouter;

/**
 * This class implements an OpenStack specific implementation of the Router abstract object.
 * 
 * @since Oct 8, 2013
 * @version $Id$
 */
public class OpenStackRouter extends ConnectedRouter {
    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The open stack context we are servicing
     * @param router
     *            The open stack server object we are representing
     */
    @SuppressWarnings("nls")
    public OpenStackRouter(Context context, com.woorea.openstack.quantum.model.Router router) {
        super(context);

        /*
         * Use the object mapper to support what we can easily
         */
        HashMap<String, String> dictionary = new HashMap<>();
        dictionary.put("id", "id");
        dictionary.put("name", "name");
        dictionary.put("status", "status");
        ObjectMapper.map(router, this, dictionary);
        String externalNetworkId = null;
        if (router.getExternalGatewayInfo() != null) {
            externalNetworkId = router.getExternalGatewayInfo().getNetworkId();
        }
        this.setExternalNetworkId(externalNetworkId);
    }
}
