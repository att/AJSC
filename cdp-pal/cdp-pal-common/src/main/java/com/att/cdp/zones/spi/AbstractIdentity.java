/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.IdentityService;

/**
 * This class is used to provide common operations and state for an identity service implementation.
 * 
 * @since Sep 24, 2013
 * @version $Id$
 */

public abstract class AbstractIdentity extends AbstractService implements IdentityService {

    /**
     * Creates the abstract identity object for the specified context object
     * 
     * @param context
     *            The context object we are servicing.
     */
    public AbstractIdentity(Context context) {
        super(context);
        getLogger().debug("AbstractIdentity(): context={}", context);
    }

    /**
     * This method checks to make sure that the credential value represents a legal value.
     * 
     * @param credential
     *            The credential value to be checked
     * @throws IllegalArgumentException
     *             If the credential is null or an empty string (or all whitespace).
     */
    protected void checkCredential(String credential) {
        if (credential == null || credential.trim().length() == 0) {
            throw new IllegalArgumentException("Credential may not be null or an empty string!");
        }
    }

    /**
     * Convenience method to check the logged in state of the caller. If the context is logged in, the method returns
     * silently. If not logged in, a RimeException is thrown.
     * 
     * @throws ZoneException
     *             If the user is not logged in
     */
    protected void checkLoggedIn() throws ZoneException {
        Context context = getContext();
        getLogger().info("checkLoggedIn(): context={} isLoggedIn={}", context, context.isLoggedIn());
        if (context.isLoggedIn()) {
            return;
        }
        throw new NotLoggedInException("Not logged in!");
    }

    /**
     * This method checks to make sure that the principal value represents a legal value.
     * 
     * @param principal
     *            The principal value to be checked
     * @throws IllegalArgumentException
     *             If the principal is null or an empty string (or all whitespace).
     */
    protected void checkPrincipal(String principal) throws IllegalArgumentException {
        if (principal == null || principal.trim().length() == 0) {
            throw new IllegalArgumentException("Principal may not be null or an empty string!");
        }
    }
}
