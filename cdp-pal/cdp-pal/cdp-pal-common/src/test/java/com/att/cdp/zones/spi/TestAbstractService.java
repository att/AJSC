/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.IdentityService;
import com.att.cdp.zones.ImageService;
import com.att.cdp.zones.NetworkService;
import com.att.cdp.zones.ObjectService;
import com.att.cdp.zones.Provider;
import com.att.cdp.zones.ProviderMetadata;
import com.att.cdp.zones.SnapshotService;
import com.att.cdp.zones.StackService;
import com.att.cdp.zones.VolumeService;
import com.att.cdp.zones.model.Tenant;

/**
 * @since Apr 1, 2015
 * @version $Id$
 */

public class TestAbstractService {

    private TestService service;
    private Context context;
    private TestProvider provider;

    @Before
    public void initialize() {
        provider = new TestProvider();
        context = provider.openContext(null);
        service = new TestService(context);
    }

    @Test
    public void testTrackRequest() {
        service.trackRequest();

        Map<String, Object> state = RequestState.getState();
        assertNotNull(state);
        assertFalse(state.isEmpty());

        assertEquals("testTrackRequest", state.get(RequestState.METHOD));
        assertEquals(this.getClass().getName(), state.get(RequestState.CLASS));
        assertEquals("Tenant", state.get(RequestState.TENANT));
        assertEquals("Principal", state.get(RequestState.PRINCIPAL));
    }

    @Test(expected = ZoneException.class)
    public void testCheckNullArg() throws ZoneException {
        service.checkArg(null, "Null");
    }

    public class TestService extends AbstractService {

        /**
         * @param context
         */
        public TestService(Context context) {
            super(context);
        }

        /**
         * @see com.att.cdp.zones.Service#getURL()
         */
        @Override
        public String getURL() {
            return "";
        }

    }

    public class TestProvider implements Provider {

        /**
         * @see com.att.cdp.zones.Provider#getName()
         */
        @Override
        public String getName() {
            return "Test";
        }

        /**
         * @see com.att.cdp.zones.Provider#openContext(java.util.Properties)
         */
        @Override
        public Context openContext(Properties properties) {
            return new TestContext(this);
        }
    }

    public class TestContext implements Context {

        private Provider contextProvider;

        public TestContext(Provider provider) {
            this.contextProvider = provider;
        }

        /**
         * @see java.io.Closeable#close()
         */
        @Override
        public void close() throws IOException {
            // TODO Auto-generated method stub

        }

        /**
         * @see com.att.cdp.zones.Context#getComputeService()
         */
        @Override
        public ComputeService getComputeService() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see com.att.cdp.zones.Context#getIdentityService()
         */
        @Override
        public IdentityService getIdentityService() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see com.att.cdp.zones.Context#getImageService()
         */
        @Override
        public ImageService getImageService() throws NotLoggedInException {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see com.att.cdp.zones.Context#getLocale()
         */
        @Override
        public Locale getLocale() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see com.att.cdp.zones.Context#getNetworkService()
         */
        @Override
        public NetworkService getNetworkService() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see com.att.cdp.zones.Context#getObjectStoreService()
         */
        @Override
        public ObjectService getObjectStoreService() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see com.att.cdp.zones.Context#getPrincipal()
         */
        @Override
        public String getPrincipal() {
            return "Principal";
        }

        /**
         * @see com.att.cdp.zones.Context#getProperties()
         */
        @Override
        public Properties getProperties() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see com.att.cdp.zones.Context#getProvider()
         */
        @Override
        public Provider getProvider() {
            return contextProvider;
        }

        /**
         * @see com.att.cdp.zones.Context#getProviderMetadata()
         */
        @Override
        public ProviderMetadata getProviderMetadata() {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see com.att.cdp.zones.Context#getTenant()
         */
        @Override
        public Tenant getTenant() throws ZoneException {
            // TODO Auto-generated method stub
            return null;
        }

        /**
         * @see com.att.cdp.zones.Context#getTenantName()
         */
        @Override
        public String getTenantName() {
            return "Tenant";
        }

        /**
         * @see com.att.cdp.zones.Context#getVolumeService()
         */
        @Override
        public VolumeService getVolumeService() {
            // TODO Auto-generated method stub
            return null;
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
         * @see com.att.cdp.zones.Context#isLoggedIn()
         */
        @Override
        public boolean isLoggedIn() {
            // TODO Auto-generated method stub
            return false;
        }

        /**
         * @see com.att.cdp.zones.Context#isOpen()
         */
        @Override
        public boolean isOpen() {
            // TODO Auto-generated method stub
            return false;
        }

        /**
         * @see com.att.cdp.zones.Context#login(java.lang.String, java.lang.String)
         */
        @Override
        public void login(String principal, String credential) throws IllegalStateException, IllegalArgumentException,
            ZoneException {
            // TODO Auto-generated method stub

        }

        /**
         * @see com.att.cdp.zones.Context#logout()
         */
        @Override
        public void logout() {
            // TODO Auto-generated method stub

        }

        /**
         * @see com.att.cdp.zones.Context#relogin()
         */
        @Override
        public void relogin() throws IllegalStateException, IllegalArgumentException, ZoneException {
            // TODO Auto-generated method stub

        }

        /**
         * @see com.att.cdp.zones.Context#setLocale(java.util.Locale)
         */
        @Override
        public void setLocale(Locale locale) {
            // TODO Auto-generated method stub

        }

        /**
         * @see com.att.cdp.zones.Context#getSnapshotService()
         */
        @Override
        public SnapshotService getSnapshotService() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean isStale() {
            return false;
        }

        @Override
        public String getRegion() {
            return "";
        }

        /**
         * @see com.att.cdp.zones.Context#getRetryLimit()
         */
        @Override
        public int getRetryLimit() {
            return 1;
        }

        /**
         * @see com.att.cdp.zones.Context#getRetryDelay()
         */
        @Override
        public int getRetryDelay() {
            return 0;
        }
        
        @Override
        public void reloadKeyPair(String name, String publicKey,
        		String privateKey, String fingerprint) throws ZoneException {
        	// TODO Auto-generated method stub
        	
        }

    }
}
