/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.util.HashMap;
import java.util.List;

import com.woorea.openstack.nova.model.Flavor;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.map.ObjectMapper;
import com.att.cdp.zones.spi.model.ConnectedTemplate;

/**
 * This class implements the OpenStack Template abstraction
 * 
 * @since Oct 18, 2013
 * @version $Id$
 */

public class OpenStackTemplate extends ConnectedTemplate {

    /**
     * The serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The context we are servicing
     * @param flavor
     *            The OpenStack "flavor" that we map the template from
     */
    @SuppressWarnings("nls")
    public OpenStackTemplate(Context context, Flavor flavor) {
        super(context);

        HashMap<String, String> dictionary = new HashMap<>();
        dictionary.put("id", "id");
        dictionary.put("name", "name");
        dictionary.put("vcpus", "cpus");
        dictionary.put("ram", "ram");
        dictionary.put("disk", "disk");
        ObjectMapper.map(flavor, this, dictionary);

        setEnabled(Boolean.valueOf(flavor.getDisabled() == null ? true : !flavor.getDisabled().booleanValue()));
        List<com.woorea.openstack.nova.model.Link> links = flavor.getLinks();
        for (com.woorea.openstack.nova.model.Link link : links) {
            getLinks().add(link.getHref());
        }
    }
}
