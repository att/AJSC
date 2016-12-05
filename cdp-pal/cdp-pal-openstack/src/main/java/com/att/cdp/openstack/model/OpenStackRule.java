/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.model;

import com.att.cdp.zones.Context;
import com.att.cdp.zones.spi.model.ConnectedRule;
import com.woorea.openstack.nova.model.SecurityGroup;

/**
 * @since Oct 23, 2013
 * @version $Id$
 */

public class OpenStackRule extends ConnectedRule {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * @param context
     *            The context we are servicing
     * @param rule
     *            The OpenStack SecurityGroup.Rule that we are mapping
     */
    @SuppressWarnings("nls")
    public OpenStackRule(Context context, SecurityGroup.Rule rule) {
        super(context);

        setId(rule.getId().toString());
        setName(rule.getName());
        setFromPort(rule.getFromPort());
        setToPort(rule.getToPort());
        setIpRange(rule.getIpRange().getCidr());
        setIsIncoming(true); // TODO - Update SecurityGroup.Rule to allow for outgoing rules
        
        if (rule.getIpProtocol() == null) {
            setProtocol(PROTOCOL.ANY);
        } else if (rule.getIpProtocol().equalsIgnoreCase("tcp")) {
            setProtocol(PROTOCOL.TCP);
        } else {
            setProtocol(PROTOCOL.UDP);
        }
    }

}
