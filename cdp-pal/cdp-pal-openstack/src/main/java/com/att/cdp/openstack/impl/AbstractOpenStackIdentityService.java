/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.att.cdp.exceptions.AuthenticationException;
import com.att.cdp.exceptions.ContextConnectionException;
import com.att.cdp.exceptions.ResourceNotFoundException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.CommonIdentityService;
import com.att.cdp.openstack.OpenStackContext;
import com.att.cdp.openstack.connectors.NovaConnector;
import com.att.cdp.openstack.i18n.OSMsg;
import com.att.cdp.openstack.model.OpenStackKeyPair;
import com.att.cdp.openstack.model.OpenStackTenant;
import com.att.cdp.pal.util.Time;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.ContextFactory;
import com.att.cdp.zones.model.KeyPair;
import com.att.cdp.zones.model.Tenant;
import com.att.cdp.zones.spi.AbstractIdentity;
import com.att.cdp.zones.spi.RequestState;
import com.att.eelf.i18n.EELFResourceManager;
import com.woorea.openstack.base.client.HttpMethod;
import com.woorea.openstack.base.client.OpenStackConnectException;
import com.woorea.openstack.base.client.OpenStackRequest;
import com.woorea.openstack.base.client.OpenStackResponseException;
import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.base.client.OpenStackTokenProvider;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.api.TokensResource;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Authentication;
import com.woorea.openstack.keystone.model.Role;
import com.woorea.openstack.keystone.model.Roles;
import com.woorea.openstack.keystone.model.Tenants;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;
import com.woorea.openstack.nova.model.KeyPairs;

/**
 * @author <a href="mailto:dh868g@att.com?subject=com.att.cdp.openstack.impl.AbstractOpenStackIdentityService">Dewayne
 *         Hafenstein</a>
 * @since Sep 26, 2014
 * @version $Id$
 */
public abstract class AbstractOpenStackIdentityService extends AbstractIdentity implements CommonIdentityService {

    /**
     * The OpenStack model object that represents authenticated access to the service
     */
    private Access access;

    /**
     * The Keystone service object that allows us to make calls on the OpenStack keystone service.
     */
    private Keystone keystone;

    /**
     * The URL to the keystone service. We obtain this from the configuration held by the context object
     */
    private String keystoneUrl;

    /**
     * our mapping of the tenant to the rime api abstraction
     */
    private OpenStackTenant tenant;

    /**
     * The name of the tenant that we are connecting to. We obtain this from the context object as well.
     */
    private String tenantName;

    /**
     * A "token provider" that manages the authentication token that we obtain when logging in
     */
    private OpenStackSimpleTokenProvider tokenProvider;

    /**
     * The local time when the token expires. Compare to Time.getCurrentUTCDate().getTime()
     */
    private long expiresLocal = 0;

    /**
     * @param context
     *            The context that we are servicing
     */
    public AbstractOpenStackIdentityService(Context context) {
        super(context);
    }

    /**
     * @see com.att.cdp.zones.IdentityService#authenticate(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public void authenticate(String principal, String credential) throws IllegalArgumentException, ZoneException {

        OpenStackContext context = (OpenStackContext) getContext();
        checkPrincipal(principal);
        checkCredential(credential);
        if (!context.isLoggedIn()) {

            try {
                keystoneUrl = context.getProperties().getProperty(ContextFactory.PROPERTY_IDENTITY_URL);
                tenantName = context.getProperties().getProperty(ContextFactory.PROPERTY_TENANT);

                /*
                 * DH868g: Allow the specification of a client connector to override the default mechanism of the
                 * service loader. This is needed to support use within an OSGi container.
                 */
                keystone = new Keystone(keystoneUrl, context.getClientConnector());

                /*
                 * dh868g: configure the client object to set any defined proxy and trusted host list
                 */
                String proxyHost = context.getProperties().getProperty(ContextFactory.PROPERTY_PROXY_HOST);
                String proxyPort = context.getProperties().getProperty(ContextFactory.PROPERTY_PROXY_PORT);
                String trustedHosts = context.getProperties().getProperty(ContextFactory.PROPERTY_TRUSTED_HOSTS, "");
                if (proxyHost != null && proxyHost.length() > 0) {
                    getKeystone().getProperties().setProperty(com.woorea.openstack.common.client.Constants.PROXY_HOST,
                        proxyHost);
                    getKeystone().getProperties().setProperty(com.woorea.openstack.common.client.Constants.PROXY_PORT,
                        proxyPort);
                }
                if (trustedHosts != null) {
                    getKeystone().getProperties().setProperty(
                        com.woorea.openstack.common.client.Constants.TRUST_HOST_LIST, trustedHosts);
                }

                // access with unscoped token
                Authentication authentication = new UsernamePassword(principal, credential);
                TokensResource tokens = getKeystone().tokens();
                TokensResource.Authenticate authenticate = tokens.authenticate(authentication);
                authenticate = authenticate.withTenantName(tenantName);
                access = authenticate.execute();

                expiresLocal = getLocalExpiration(access);

                tenant = new OpenStackTenant(context, access.getToken().getTenant());
                context.setTenant(tenant);

                tokenProvider = new OpenStackSimpleTokenProvider(access.getToken().getId());
                getKeystone().setTokenProvider(tokenProvider);

                List<Access.Service> services = access.getServiceCatalog();
                OpenStackContext osContext = context;
                osContext.registerServices(services);

                // Testing that we can access tenants already
                Tenants tenantList = getKeystone().tenants().list().execute();
                for (com.woorea.openstack.keystone.model.Tenant t : tenantList.getList()) {
                    System.out.println(t);
                }

            } catch (OpenStackResponseException e) {
                throw new AuthenticationException(EELFResourceManager.format(
                    OSMsg.PAL_OS_FAILED_PROVIDER_AUTHENTICATION, e, principal, tenantName));
            } catch (OpenStackConnectException e) {
                throw new ContextConnectionException(EELFResourceManager.format(OSMsg.PAL_OS_CONNECTION_FAILED,
                    "Identity", keystoneUrl), e);
            }
        }
    }

    /**
     * @see com.att.cdp.zones.IdentityService#createKeyPair(com.att.cdp.zones.model.KeyPair)
     */
    @SuppressWarnings("nls")
    @Override
    public KeyPair createKeyPair(KeyPair keyPair) throws ZoneException {
        trackRequest();
        RequestState.put(RequestState.KEYPAIR, keyPair.getName());
        Context context = getContext();

        if (context.isLoggedIn()) {
            NovaConnector connector = ((OpenStackContext) context).getNovaConnector();
            com.woorea.openstack.nova.model.KeyPair pair;
            try {
                pair = connector.getClient().keyPairs().create(keyPair.getName()).execute();
            } catch (OpenStackConnectException e) {
                throw new ContextConnectionException(EELFResourceManager.format(OSMsg.PAL_OS_CONNECTION_FAILED,
                    "Compute", connector.getEndpoint()), e);
            } catch (OpenStackResponseException e) {
                throw new ZoneException(EELFResourceManager.format(OSMsg.PAL_OS_REQUEST_FAILURE, "create key-pair "
                    + keyPair.getName()), e);
            }
            return new OpenStackKeyPair(context, pair);
        }
        throw new ZoneException("Unable to create key-pairs when the context has not been logged in and authenticated");
    }

    /**
     * @see com.att.cdp.zones.IdentityService#deleteKeyPair(com.att.cdp.zones.model.KeyPair)
     */
    @SuppressWarnings("nls")
    @Override
    public void deleteKeyPair(KeyPair keyPair) throws ZoneException {
        trackRequest();
        RequestState.put(RequestState.KEYPAIR, keyPair.getName());
        Context context = getContext();

        if (context.isLoggedIn()) {
            NovaConnector connector = ((OpenStackContext) context).getNovaConnector();
            try {
                connector.getClient().keyPairs().delete(keyPair.getName()).execute();
            } catch (OpenStackConnectException e) {
                throw new ContextConnectionException(EELFResourceManager.format(OSMsg.PAL_OS_CONNECTION_FAILED,
                    "Compute", connector.getEndpoint()), e);
            } catch (OpenStackResponseException e) {
                throw new ZoneException(EELFResourceManager.format(OSMsg.PAL_OS_REQUEST_FAILURE, "delete key-pair "
                    + keyPair.getName()), e);
            }
            return;
        }
        throw new ZoneException("Unable to delete key-pairs when the context has not been logged in and authenticated");
    }

    /**
     * @see com.att.cdp.openstack.CommonIdentityService#destroyToken()
     */
    @Override
    public void destroyToken() {
        if (access != null) {
            access = null;
        }
    }

    /**
     * This method is used in the OpenStack implementations to obtain the Access object for security checking
     * 
     * @return The access object
     * @see com.att.cdp.openstack.CommonIdentityService#getAccess()
     */
    @Override
    public Access getAccess() {
        return access;
    }

    /**
     * @see com.att.cdp.zones.IdentityService#getKeyPair(java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public KeyPair getKeyPair(String name) throws ZoneException {
        trackRequest();
        RequestState.put(RequestState.KEYPAIR, name);
        Context context = getContext();

        if (context.isLoggedIn()) {
            NovaConnector connector = ((OpenStackContext) context).getNovaConnector();
            KeyPairs pairs;
            try {
                pairs = connector.getClient().keyPairs().list().execute();
            } catch (OpenStackConnectException e) {
                throw new ContextConnectionException(EELFResourceManager.format(OSMsg.PAL_OS_CONNECTION_FAILED,
                    "Compute", connector.getEndpoint()), e);
            } catch (OpenStackResponseException e) {
                throw new ZoneException(
                    EELFResourceManager.format(OSMsg.PAL_OS_REQUEST_FAILURE, "get key-pair " + name), e);
            }
            if (pairs == null || pairs.getList() == null) {
                return null;
            }
            for (com.woorea.openstack.nova.model.KeyPair pair : pairs.getList()) {
                if (pair.getName().equals(name)) {
                    return new OpenStackKeyPair(context, pair);
                }
            }
            return null;
        }
        throw new ZoneException(
            "Unable to retrieve key-pairs when the context has not been logged in and authenticated");
    }

    /**
     * @see com.att.cdp.zones.IdentityService#getKeyPairs()
     */
    @SuppressWarnings("nls")
    @Override
    public List<KeyPair> getKeyPairs() throws ZoneException {
        trackRequest();
        Context context = getContext();

        if (context.isLoggedIn()) {
            NovaConnector connector = ((OpenStackContext) context).getNovaConnector();
            KeyPairs pairs = null;
            try {
                pairs = connector.getClient().keyPairs().list().execute();
            } catch (OpenStackConnectException e) {
                throw new ContextConnectionException(EELFResourceManager.format(OSMsg.PAL_OS_CONNECTION_FAILED,
                    "Compute", connector.getEndpoint()), e);
            } catch (OpenStackResponseException e) {
                throw new ZoneException(EELFResourceManager.format(OSMsg.PAL_OS_REQUEST_FAILURE, "get key-pair list"),
                    e);
            }
            ArrayList<KeyPair> list = new ArrayList<>();
            for (com.woorea.openstack.nova.model.KeyPair pair : pairs.getList()) {
                OpenStackKeyPair kp = new OpenStackKeyPair(context, pair);
                list.add(kp);
            }
            return list;
        }
        throw new ZoneException(
            "Unable to retrieve key-pairs when the context has not been logged in and authenticated");
    }

    /**
     * @see com.att.cdp.zones.IdentityService#getRoles()
     */
    @SuppressWarnings("nls")
    @Override
    public List<String> getRoles() throws ZoneException {
        trackRequest();
        Context context = getContext();

        ArrayList<String> list = new ArrayList<>();
        if (context.isLoggedIn()) {
            try {
                keystoneUrl = context.getProperties().getProperty(ContextFactory.PROPERTY_IDENTITY_URL);
                // tenantName = context.getProperties().getProperty(ContextFactory.PROPERTY_TENANT);

                Keystone keystone = new Keystone(keystoneUrl);
                OpenStackRequest<Roles> request =
                    new OpenStackRequest<>(keystone, HttpMethod.GET, "/users/" + context.getPrincipal() + "/roles",
                        null, Roles.class);

                Roles roles;
                try {
                    roles = keystone.execute(request);
                } catch (OpenStackConnectException e) {
                    throw new ContextConnectionException(EELFResourceManager.format(OSMsg.PAL_OS_CONNECTION_FAILED,
                        "Identity", keystoneUrl), e);
                }
                for (Role role : roles.getList()) {
                    list.add(role.getName());
                }

            } catch (OpenStackResponseException e) {
                if (e.getStatus() == 404) {
                    throw new ResourceNotFoundException("Attempt to get roles for user " + context.getPrincipal(), e);
                }
                throw new ZoneException("Attempt to get roles for user " + context.getPrincipal(), e);
            }
        }
        return list;
    }

    /**
     * All services must be able to return the tenant object that the user has connected to.
     * 
     * @return The tenant object
     * @throws ZoneException
     *             If the user has not logged in
     * @see com.att.cdp.zones.Service#getTenant()
     */
    @Override
    public Tenant getTenant() throws ZoneException {
        checkLoggedIn();
        Context context = getContext();

        trackRequest();

        Keystone keystone = getKeystone();
        keystoneUrl = context.getProperties().getProperty(ContextFactory.PROPERTY_IDENTITY_URL);

        if (tenant == null) {
            com.woorea.openstack.keystone.model.Tenants tenants;
            try {
                tenants = keystone.tenants().list().execute();
            } catch (OpenStackConnectException e) {
                throw new ContextConnectionException(EELFResourceManager.format(OSMsg.PAL_OS_CONNECTION_FAILED,
                    "Identity", keystoneUrl), e);
            } catch (OpenStackResponseException e) {
                throw new ZoneException(EELFResourceManager.format(OSMsg.PAL_OS_REQUEST_FAILURE, "get tenant "
                    + tenantName), e);
            }
            for (com.woorea.openstack.keystone.model.Tenant t : tenants) {
                if (t.getName().equals(tenantName)) {
                    tenant = new OpenStackTenant((OpenStackContext) context, t);
                    break;
                }
            }
        }
        return tenant;
    }

    /**
     * returns the token provider to be used to access OpenStack services
     * 
     * @return The token provider
     */
    public OpenStackTokenProvider getTokenProvider() {
        return tokenProvider;
    }

    /**
     * This method determines the local time that the current token (held in the Access object) will expire.
     * 
     * @param accessKey
     *            The access to be checked
     * @return The local time the token expires
     */
    private static long getLocalExpiration(Access accessKey) {
        Date now = Time.getCurrentUTCDate();
        if (accessKey != null && accessKey.getToken() != null) {
            Calendar issued = accessKey.getToken().getIssued_at();
            Calendar expires = accessKey.getToken().getExpires();
            if (issued != null && expires != null) {
                long tokenLife = expires.getTimeInMillis() - issued.getTimeInMillis();
                return now.getTime() + tokenLife;
            }
        }
        return now.getTime();
    }

    /**
     * @see com.att.cdp.zones.IdentityService#isAuthExpired()
     */
    @Override
    public boolean isAuthExpired() {
        Date now = Time.getCurrentUTCDate();
        return now.getTime() >= expiresLocal;
    }

    /**
     * @return The keystone client to access the identity service
     */
    public Keystone getKeystone() {
        return keystone;
    }

    /**
     * @see com.att.cdp.zones.Service#getURL()
     */
    @Override
    public String getURL() {
        return keystoneUrl;
    }

    public Set<Tenant> getTenants() throws ZoneException {
        checkLoggedIn();
        Context context = getContext();

        trackRequest();

        Set<Tenant> tenants = new HashSet<Tenant>();

        Keystone keystone = getKeystone();
        keystoneUrl = context.getProperties().getProperty(ContextFactory.PROPERTY_IDENTITY_URL);

        try {
            Tenants tenantList = keystone.tenants().list().execute();
            for (com.woorea.openstack.keystone.model.Tenant t : tenantList.getList()) {
                tenants.add(new OpenStackTenant((OpenStackContext) context, t));
            }
        } catch (OpenStackConnectException e) {
            throw new ContextConnectionException(EELFResourceManager.format(OSMsg.PAL_OS_CONNECTION_FAILED, "Identity",
                keystoneUrl), e);
        } catch (OpenStackResponseException e) {
            throw new ZoneException(
                EELFResourceManager.format(OSMsg.PAL_OS_REQUEST_FAILURE, "get tenant " + tenantName), e);
        }
        return tenants;

    }

}
