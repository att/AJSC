/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;

import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.IdentityService;
import com.att.cdp.zones.ImageService;
import com.att.cdp.zones.NetworkService;
import com.att.cdp.zones.ObjectService;
import com.att.cdp.zones.ProviderMetadata;
import com.att.cdp.zones.SnapshotService;
import com.att.cdp.zones.StackService;
import com.att.cdp.zones.VolumeService;
import com.att.cdp.zones.model.Tenant;
import com.att.cdp.zones.spi.AbstractContext;
import com.att.cdp.zones.spi.DefaultProviderMetadata;

/**
 * @since Sep 23, 2013
 * @version $Id$
 */

public class DummyProviderContext extends AbstractContext {

    /**
     * An array list of required configuration properties
     */
    private static final ArrayList<String> requiredKeys;

    static {
        requiredKeys = new ArrayList<>();
        /*
         * Add the keys defined above to this list if they are required for configuration of the context
         */
    }

    /**
     * 
     */
    private DefaultProviderMetadata defaultProviderMetadata;

    /**
     * Create the test provider context using the test provider
     * 
     * @param provider
     *            The provider we are servicing
     * @param defaults
     *            The provider default properties, if any exist
     * @param config
     *            a set of properties that can be used to configure the context, or null
     */
    public DummyProviderContext(DummyProvider provider, Properties defaults, Properties config) {
        super(provider, defaults, config);
        defaultProviderMetadata = new DefaultProviderMetadata(this);
        validateRequiredConfiguration(requiredKeys);
    }

    /**
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        super.close();
    }

    /**
     * @see com.att.cdp.zones.Context#getComputeService()
     */
    @Override
    public synchronized ComputeService getComputeService() {
        // TODO Auto-generated method stub
        if (super.getComputeService() == null) {
            setComputeService(new DummyCompute(this));
        }
        return super.getComputeService();
    }

    /**
     * @see com.att.cdp.zones.Context#getIdentityService()
     */
    @Override
    public synchronized IdentityService getIdentityService() {
        if (super.getIdentityService() == null) {
            setIdentityService(new DummyIdentity(this));
        }
        return super.getIdentityService();
    }

    /**
     * @see com.att.cdp.zones.Context#getImageService()
     */
    @Override
    public synchronized ImageService getImageService() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Locale getLocale() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.Context#getNetworkService()
     */
    @Override
    public synchronized NetworkService getNetworkService() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.Context#getObjectStoreService()
     */
    @Override
    public synchronized ObjectService getObjectStoreService() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.Context#getPrincipal()
     */
    @Override
    public String getPrincipal() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.Context#getProviderMetadata()
     */
    @Override
    public ProviderMetadata getProviderMetadata() {
        return defaultProviderMetadata;
    }

    /**
     * @see com.att.cdp.zones.spi.AbstractContext#getTenant()
     */
    @Override
    public Tenant getTenant() throws ZoneException {
        if (super.getTenant() == null) {
            setTenant(new DummyTenant(this));
        }
        return super.getTenant();
    }

    /**
     * @see com.att.cdp.zones.Context#getVolumeService()
     */
    @Override
    public synchronized VolumeService getVolumeService() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.Context#login(java.lang.String, java.lang.String)
     */
    @Override
    public void login(String principal, String credential) {
        // TODO Auto-generated method stub
    }

    /**
     * @see com.att.cdp.zones.Context#logout()
     */
    @Override
    public void logout() {
        // TODO Auto-generated method stub
    }

    @Override
    public void relogin() throws IllegalStateException, IllegalArgumentException, ZoneException {
        // TODO Auto-generated method stub
    }

    @Override
    public void setLocale(Locale locale) {
        // TODO Auto-generated method stub
    }

    /**
     * @see com.att.cdp.zones.Context#getStackService()
     */
    @Override
    public StackService getStackService() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.att.cdp.zones.spi.AbstractContext#getSnapshotService()
     */
    @Override
    public SnapshotService getSnapshotService() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 
     */
    /**
     * @see com.att.cdp.zones.Context#isStale()
     */
    @Override
    public boolean isStale() {
        return false;
    }

    /**
     * @see com.att.cdp.zones.Context#getRegion()
     */
    @Override
    public String getRegion() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public void reloadKeyPair(String name, String publicKey, String privateKey,
    		String fingerprint) throws ZoneException {
    	// TODO Auto-generated method stub
    	
    }
}
