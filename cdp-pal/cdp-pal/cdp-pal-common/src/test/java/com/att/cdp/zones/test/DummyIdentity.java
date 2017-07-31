/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.att.cdp.exceptions.AuthenticationException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.model.KeyPair;
import com.att.cdp.zones.model.Tenant;
import com.att.cdp.zones.spi.AbstractContext;
import com.att.cdp.zones.spi.AbstractIdentity;

/**
 * @since Sep 24, 2013
 * @version $Id$
 */

public class DummyIdentity extends AbstractIdentity {

    /**
     * Create the identity service implementation
     * 
     * @param context
     *            The context object we are servicing
     */
    public DummyIdentity(AbstractContext context) {
        super(context);
    }

    /**
     * @see com.att.cdp.zones.spi.AbstractIdentity#authenticate(java.lang.String, java.lang.String)
     */
    @Override
    public void authenticate(String principal, String credential) throws AuthenticationException {
        checkPrincipal(principal);
        checkCredential(credential);
        Context context = getContext();

        if (!principal.equals(credential)) {
            throw new AuthenticationException("Principal " + principal + " cannot be logged on to provider "
                + context.getProvider().getName());
        }
    }

    /**
     * @see com.att.cdp.zones.IdentityService#createKeyPair(com.att.cdp.zones.model.KeyPair)
     */
    @Override
    public KeyPair createKeyPair(KeyPair keyPair) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.IdentityService#deleteKeyPair(com.att.cdp.zones.model.KeyPair)
     */
    @Override
    public void deleteKeyPair(KeyPair keyPair) throws ZoneException {
        // TODO Auto-generated method stub

    }

    /**
     * @see com.att.cdp.zones.IdentityService#getKeyPair(java.lang.String)
     */
    @Override
    public KeyPair getKeyPair(String name) throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.IdentityService#getKeyPairs()
     */
    @Override
    public List<KeyPair> getKeyPairs() throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.IdentityService#getRoles()
     */
    @Override
    public List<String> getRoles() throws ZoneException {
        ArrayList<String> roles = new ArrayList<String>();
        roles.add("DummyRole1");
        roles.add("DummyRole2");
        return roles;
    }

    /**
     * @see com.att.cdp.zones.Service#getTenant()
     */
    @Override
    public Tenant getTenant() throws ZoneException {
        checkLoggedIn();
        Context context = getContext();
        return ((DummyProviderContext) context).getTenant();
    }

    @Override
    public boolean isAuthExpired() {
        return false;
    }

    /**
     * @see com.att.cdp.zones.Service#getURL()
     */
    @Override
    public String getURL() {
        return "";
    }

    @Override
    public Set<Tenant> getTenants() throws ZoneException {
        // TODO Auto-generated method stub
        return null;
    }
}
