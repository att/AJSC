/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import java.util.ArrayList;
import java.util.List;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.Rule;
import com.att.cdp.zones.spi.model.ConnectedACL;
import com.woorea.openstack.nova.model.SecurityGroup;

/**
 * @since Oct 23, 2013
 * @version $Id$
 */
public class OpenStackACL extends ConnectedACL {

    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The context we are servicing
     * @param group
     *            The OpenStack security group we are mapping
     */
    public OpenStackACL(Context context, SecurityGroup group) {
        super(context);
        setId(group.getId().toString());
        setName(group.getName());
        setDescription(group.getDescription());

        List<Rule> rules = new ArrayList<>();
        for (SecurityGroup.Rule rule : group.getRules()) {
            rules.add(new OpenStackRule(context, rule));
        }
        setRules(rules);
    }
}
