/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import java.util.List;
import java.util.Set;

import com.att.cdp.exceptions.AuthenticationException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.model.KeyPair;
import com.att.cdp.zones.model.Tenant;

/**
 * The identity service allows other user and role oriented operations. Login is implied when the context is created,
 * and logout is implied when the context is closed. This service provides direct access to other identity related
 * operations.
 * 
 * @since Sep 23, 2013
 * @version $Id$
 */

public interface IdentityService extends Service {

    /**
     * This method is used to authenticate the user to the provider. The actual connection configuration, such as
     * tenant, region, or other values is obtained from the context configuration and will vary from provider to
     * provider.
     * 
     * @param principal
     *            The principal to authenticate
     * @param credential
     *            The credentials for the principal
     * @throws AuthenticationException
     *             If the principal cannot be authenticated
     * @throws IllegalArgumentException
     *             If any arguments are omitted or invalid.
     * @throws ZoneException
     *             If more than one region is supported and the user has not specified the region to be used, or if the
     *             user has specified a region that cannot be matched to one of the published regions.
     */
    void authenticate(String principal, String credential) throws AuthenticationException, IllegalArgumentException,
        ZoneException;

    /**
     * Creates and returns the associated key pair.
     * <p>
     * The object provided is just a model object and is not actually connected to the context. When the service
     * completes, the actual object that is connected to the context is returned.
     * </p>
     * 
     * @param keyPair
     *            The model object to be created
     * @return The actual key pair created
     * @throws ZoneException
     *             If the key pair cannot be created, likely because no login has been performed.
     */
    KeyPair createKeyPair(KeyPair keyPair) throws ZoneException;

    /**
     * Deletes the key pair from the tenant
     * 
     * @param keyPair
     *            The pair to be deleted
     * @throws ZoneException
     *             If the key-pair cannot be deleted, likely because no login has been performed
     */
    void deleteKeyPair(KeyPair keyPair) throws ZoneException;

    /**
     * Looks up and returns the named key pair definition if it exists.
     * 
     * @param name
     *            The name of the requested key pair definition
     * @return The key pair definition if it exists, null otherwise
     * @throws ZoneException
     *             If no valid login has been performed.
     */
    KeyPair getKeyPair(String name) throws ZoneException;

    /**
     * Lists all key-pairs associated with the current tenant that has been logged in. This only applies to the current
     * tenant for the context and only if a successful login has been performed.
     * 
     * @return The list of key pairs for the tenant, if any
     * @throws ZoneException
     *             If no valid login has been performed.
     */
    List<KeyPair> getKeyPairs() throws ZoneException;

    /**
     * Returns a list of role names that the current user is assigned.
     * 
     * @return The list of roles, or an empty list.
     * @throws ZoneException
     *             if the resource cannot be found or there is some error in accessing the service provider
     */
    List<String> getRoles() throws ZoneException;

    /**
     * Checks if the authorization token has expired.
     * 
     * @return True if the authorization token is still valid for requests. False we need to authenticate again before
     *         our request.
     */
    boolean isAuthExpired();

    /**
     * Returns a list of tenants that the current user has access to.
     *
     * @return The list of tenants or an empty list.
     * @throws ZoneException
     *             if there is some error accessing the service provider
     */
    Set<Tenant> getTenants() throws ZoneException;
}
