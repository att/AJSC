/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import java.util.List;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.model.Firewall;
import com.att.cdp.zones.model.FirewallRule;

/**
 * This interface represents the implementation of the firewall service of the cloud service provider. firewall services
 * are used to manage firewalls and firewall rules
 * 
 * @since Sep 10, 2014
 * @version $Id$
 */

public interface FirewallService extends Service {

    /**
     * @param id
     *            The id of the firewall desired
     * @return The firewall
     * @throws ZoneException
     *             If the firewall cannot be obtained
     */
    Firewall getFirewall(String id) throws ZoneException;

    /**
     * This method is used to list all of the firewalls that are available within the context.
     * 
     * @return The list of firewalls or an empty list if there are none available.
     * @throws ZoneException
     *             If the service fails.
     */
    List<Firewall> listFirewalls() throws ZoneException;

    /**
     * This method is used to list all of the firewalls where the name of the name matches the supplied pattern.
     * 
     * @param pattern
     *            The pattern (regular expression) that is compared to the firewall name to select it for the returned
     *            list. If the pattern is null, then the method acts identically to {@link #listFirewalls()}.
     * @return The list of firewalls that match the specified name pattern
     * @throws ZoneException
     *             If anything fails
     */
    List<Firewall> listFirewalls(String pattern) throws ZoneException;
}
