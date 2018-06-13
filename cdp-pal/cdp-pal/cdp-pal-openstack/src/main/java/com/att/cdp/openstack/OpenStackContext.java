/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.att.cdp.pal.configuration.Configuration;
import com.att.cdp.pal.configuration.ConfigurationFactory;
import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.exceptions.NotSupportedException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.openstack.connectors.CinderConnector;
import com.att.cdp.openstack.connectors.Connector;
import com.att.cdp.openstack.connectors.GlanceConnector;
import com.att.cdp.openstack.connectors.HeatConnector;
import com.att.cdp.openstack.connectors.KeystoneConnector;
import com.att.cdp.openstack.connectors.NovaConnector;
import com.att.cdp.openstack.connectors.QuantumConnector;
import com.att.cdp.openstack.i18n.OSMsg;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.ContextFactory;
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
import com.att.cdp.zones.spi.AbstractContext;
import com.att.eelf.i18n.EELFResourceManager;
import com.woorea.openstack.base.client.OpenStackClientConnector;
import com.woorea.openstack.common.client.Constants;
import com.woorea.openstack.keystone.model.Access.Service;

/**
 * This is the OpenStack context implementation.
 * <p>
 * A context is a configured "connection" to a specific provider zone (infrastructure instance). It is configured from
 * properties that are provided to it by the client, as well as default properties supplied by the resource file loaded
 * with the provider implementation (if any). The client-supplied configuration properties override the default
 * properties.
 * </p>
 * 
 * @since Sep 24, 2013
 * @version $Id$
 */
public class OpenStackContext extends AbstractContext {

    private static final String COMPUTE_SERVICE_CLASSNAME = "com.att.cdp.openstack.%s.OpenStackComputeService";

    private static final String IDENTITY_SERVICE_CLASSNAME = "com.att.cdp.openstack.%s.OpenStackIdentityService";

    private static final String IMAGE_SERVICE_CLASSNAME = "com.att.cdp.openstack.%s.OpenStackImageService";

    private static final String NETWORK_SERVICE_CLASSNAME = "com.att.cdp.openstack.%s.OpenStackNetworkService";

    /**
     * An array list of required configuration properties
     */
    private static final List<String> REQUIRED_KEYS;

    private static final String SNAPSHOT_SERVICE_CLASSNAME = "com.att.cdp.openstack.%s.OpenStackSnapshotService";

    private static final String STACK_SERVICE_CLASSNAME = "com.att.cdp.openstack.%s.OpenStackStackService";

    private static final String VOLUME_SERVICE_CLASSNAME = "com.att.cdp.openstack.%s.OpenStackVolumeService";

    /**
     * This property, if defined, causes the service catalog NOT to fail the login attempt if no region is specified and
     * there are multiple regions in the service catalog. The result of this is that the login will likely not be
     * usable, but this can be useful for diagnostic tools.
     */
    public static final String ALLOW_UNKNOWN_REGION = "com.att.cdp.openstack.allow_unknown_region";

    /**
     * Initialize the required property keys. This list contains only the keys for the identity service URL and the
     * tenant name. It is possible to obtain the remainder of the URLs when the context is logged in.
     */
    static {
        REQUIRED_KEYS = new ArrayList<String>();

        REQUIRED_KEYS.add(ContextFactory.PROPERTY_IDENTITY_URL);
        REQUIRED_KEYS.add(ContextFactory.PROPERTY_TENANT);
    }

    /**
     * This method strips all extraneous encoding from a URL to represent the connection only. All extra path
     * information, encoded values and query parameters are removed.
     * 
     * @param url
     *            The URL to be processed
     * @return The url without any tenant and version, so we can access the service without regard to any specific
     *         tenant
     */
    protected static String connectionOnly(String url) {
        if (url == null) {
            return url;
        }

        String regex = "(http(s)?://[a-z0-9_.]+(:[0-9]+)?)(/.*)?";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }

        return url.trim();
    }

    /**
     * This collection is used to determine if a floating IP is allocated or not to protect an IP from being stolen from
     * another VM.
     */
    private Set<String> allocatedFloatingIPs = new HashSet<String>();

    /**
     * The service catalog
     */
    private ServiceCatalog catalog;

    /**
     * The version number for the compute service
     */
    private String computeVersion;

    /**
     * The configured connector to access the Glance provider for this context
     */
    private GlanceConnector glanceConnector;

    /**
     * The connector to access the heat service if installed
     */
    private HeatConnector heatConnector;

    /**
     * The version number for the identity service
     */
    private String identityVersion;

    /**
     * The version number for the image service
     */
    private String imageVersion;

    /**
     * The configured connector to access the Keystone provider for this context
     */
    private KeystoneConnector keystoneConnector;

    /**
     * The configured connector to access the Nova provider for this context
     */
    private NovaConnector novaConnector;

    /**
     * The configured connector to access the Cinder provider for this context
     */
    private CinderConnector cinderConnector;

    /**
     * The metadata for this provider
     */
    private OpenStackMetadata providerMetadata;

    /**
     * The configured connector to access the Quantum client
     */
    private QuantumConnector quantumConnector;

    /**
     * The id of the tenant this context is connected to.
     */
    private String tenantId;

    /**
     * The version number for the volume service
     */
    private String volumeVersion;

    /**
     * If the configuration properties include the declaration of the connector class to be used, then the connector is
     * pre-created and cached here and used for all clients. Otherwise, the OpenStack library uses the Java
     * ServiceLoader to locate the connector to be used. The issue is that the ServiceLoader does not work correctly
     * under OSGi.
     */
    private OpenStackClientConnector clientConnector;

    /**
     * @param provider
     *            The provider we are part of
     * @param defaults
     *            The default properties for the provider
     * @param config
     *            The client-supplied properties used to configure the context
     */
    @SuppressWarnings({
        "unchecked", "nls"
    })
    public OpenStackContext(Provider provider, Properties defaults, Properties config) {
        super(provider, defaults, config);

        Logger appLogger = getLogger();
        appLogger.debug(String.format("Creating OpenStack provider context for provider named [%s]", provider));

        /*
         * The proxyHost and proxyPort properties are the same for the abstraction and Openstack api, but the trusted
         * hosts list property name is different. See if the abstraction-defined property exists, and if it does,
         * translate that to the appropriate property for Openstack api.
         */
        Properties properties = getModifiableProperties();
        String value = properties.getProperty(ContextFactory.PROPERTY_TRUSTED_HOSTS);
        if (value != null && value.length() > 0) {
            properties.setProperty(Constants.TRUST_HOST_LIST, value);
        }

        /*
         * Allow the specification of a client connector to override the default mechanism of the service
         * loader. This is needed to support use within an OSGi container. Check to see if the client connector class
         * property is specified. If it is, then we will load and instantiate that class and use it as the client
         * connector. If not specified, then we will allow the default connector to be determined using the Java service
         * loader.
         */
        String connectorClassname = properties.getProperty(ContextFactory.PROPERTY_CLIENT_CONNECTOR_CLASS);
        if (connectorClassname != null && connectorClassname.trim().length() > 0) {
            try {
                Class<? extends OpenStackClientConnector> cls =
                    (Class<? extends OpenStackClientConnector>) Class.forName(connectorClassname);
                clientConnector = cls.newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                // If it failed, then we just fall-back to service loader
                appLogger.warn(String.format("Connector class %s cannot be loaded or instantiated, "
                    + "fall-back to service loader.  Reason = %s: %s", connectorClassname,
                    e.getClass().getSimpleName(), e.getMessage()));
            }
        }

        providerMetadata = new OpenStackMetadata(this);
        validateRequiredConfiguration(REQUIRED_KEYS);
        checkProxySettings(properties);
        catalog = new ServiceCatalog(this);
    }

    /**
     * This method first checks to see if the IP address has been allocated or not. If it has, then it immediately
     * returns false. If it has not been allocated, then the IP address is added to the collection of allocated
     * addresses and the method returns true.
     * 
     * @param ip
     *            The ip address to be tested/allocated
     * @return True if the IP address was NOT already in use and was allocated, false indicates that the IP address is
     *         already being used and should not be used.
     */
    public boolean allocateFloatingIP(String ip) {
        synchronized (allocatedFloatingIPs) {
            if (allocatedFloatingIPs.contains(ip)) {
                return false;
            }
            allocatedFloatingIPs.add(ip);
        }
        return true;
    }

    /**
     * @see com.att.cdp.zones.spi.AbstractContext#close()
     */
    @Override
    public void close() throws IOException {
        logout();
        super.close();
    }

    /**
     * Deallocates a floating IP address from the list of allocated IP addresses, if it was allocated.
     * 
     * @param ip
     *            The IP address to be deallocated.
     */
    public void deallocateFloatingIP(String ip) {
        synchronized (allocatedFloatingIPs) {
            allocatedFloatingIPs.remove(ip);
        }
    }

    /**
     * @see com.att.cdp.zones.Context#getComputeService()
     */
    @Override
    public synchronized ComputeService getComputeService() {
        if (super.getComputeService() == null) {
            String version = providerMetadata.getComputeVersion();
            com.att.cdp.zones.Service service = loadServiceImplementation(COMPUTE_SERVICE_CLASSNAME, version);
            setComputeService((ComputeService) service);
        }
        return super.getComputeService();
    }

    /**
     * Allow the specification of a client connector to override the default mechanism of the service loader.
     * This is needed to support use within an OSGi container.
     * 
     * @return The client connector to be used if one was specified, or null if we want to use the default service
     *         loader mechanism to locate and load the client connector via the class path.
     */
    public OpenStackClientConnector getClientConnector() {
        return clientConnector;
    }

    /**
     * @return The configured glance connector
     */
    public GlanceConnector getGlanceConnector() {
        if (glanceConnector == null) {
            glanceConnector = new GlanceConnector(this);
        }
        return glanceConnector;
    }

    /**
     * @return The cached heat connector, or null if orchestration is not installed
     */
    public HeatConnector getHeatConnector() {
        if (providerMetadata.isStackSupported()) {
            if (heatConnector == null) {
                heatConnector = new HeatConnector(this);
            }
        }

        return heatConnector;
    }

    /**
     * @return The cached keystone connector
     */
    public KeystoneConnector getKeystoneConnector() {
        if (keystoneConnector == null) {
            keystoneConnector = new KeystoneConnector(this);
        }

        return keystoneConnector;
    }

    /**
     * @see com.att.cdp.zones.Context#getIdentityService()
     */
    @Override
    public synchronized IdentityService getIdentityService() {
        if (super.getIdentityService() == null) {
            String version = providerMetadata.getIdentityVersion();
            com.att.cdp.zones.Service service = loadServiceImplementation(IDENTITY_SERVICE_CLASSNAME, version);
            setIdentityService((IdentityService) service);
        }
        return super.getIdentityService();
    }

    /**
     * @see com.att.cdp.zones.Context#getImageService()
     */
    @Override
    public synchronized ImageService getImageService() {
        if (super.getImageService() == null) {
            try {
                String version = providerMetadata.getImageVersion();
                com.att.cdp.zones.Service service = loadServiceImplementation(IMAGE_SERVICE_CLASSNAME, version);
                setImageService((ImageService) service);
            } catch (NotLoggedInException e) {
                e.printStackTrace();
            }
        }
        return super.getImageService();
    }

    /**
     * @see com.att.cdp.zones.Context#getNetworkService()
     */
    @Override
    public synchronized NetworkService getNetworkService() {
        if (super.getNetworkService() == null) {
            String version = providerMetadata.getIdentityVersion();
            com.att.cdp.zones.Service service = loadServiceImplementation(NETWORK_SERVICE_CLASSNAME, version);
            setNetworkService((NetworkService) service);
        }
        return super.getNetworkService();
    }

    /**
     * This method is used to obtain the reference to the configured Nova connector, or to create one if it does not
     * already exist.
     * 
     * @return The nova connector
     */
    public synchronized NovaConnector getNovaConnector() {
        if (novaConnector == null) {
            novaConnector = new NovaConnector(this);
        }
        return novaConnector;
    }

    /**
     * This method is used to obtain the reference to the configured Cinder connector, or to create one if it does not
     * already exist.
     * 
     * @return The cinder connector
     */
    public synchronized CinderConnector getCinderConnector() {
        if (cinderConnector == null) {
            cinderConnector = new CinderConnector(this);
        }
        return cinderConnector;
    }

    /**
     * @see com.att.cdp.zones.Context#getObjectStoreService()
     */
    @Override
    public synchronized ObjectService getObjectStoreService() {
        // if (super.getObjectService() == null) {
        // String version = providerMetadata.getObjectVersion();
        // com.att.cdp.zones.Service service = loadServiceImplementation(OBJECT_SERVICE_CLASSNAME, version);
        // setObjectService((ObjectService) service);
        // }
        // return super.getObjectService();
        return null;
    }

    /**
     * @see com.att.cdp.zones.Context#getProviderMetadata()
     */
    @Override
    public ProviderMetadata getProviderMetadata() {
        return providerMetadata;
    }

    /**
     * @return The configured Quantum connector
     */
    public QuantumConnector getQuantumConnector() {
        if (quantumConnector == null) {
            quantumConnector = new QuantumConnector(this);
        }
        return quantumConnector;
    }

    /**
     * @return The service catalog
     */
    public ServiceCatalog getServiceCatalog() {
        return catalog;
    }

    /**
     * @see com.att.cdp.zones.Context#getSnapshotService()
     */
    @Override
    public synchronized SnapshotService getSnapshotService() {
        if (super.getSnapshotService() == null) {
            String version = providerMetadata.getSnapshotVersion();
            com.att.cdp.zones.Service service = loadServiceImplementation(SNAPSHOT_SERVICE_CLASSNAME, version);
            setSnapshotService((SnapshotService) service);
        }
        return super.getSnapshotService();
    }

    /**
     * @see com.att.cdp.zones.Context#getStackService()
     */
    @Override
    public StackService getStackService() {
        if (super.getStackService() == null) {
            String version = providerMetadata.getStackVersion();
            com.att.cdp.zones.Service service = loadServiceImplementation(STACK_SERVICE_CLASSNAME, version);
            setStackService((StackService) service);
        }
        return super.getStackService();
    }

    /**
     * @return The cached tenant object for this context
     * @throws ZoneException
     *             If the user has not logged in
     * @see com.att.cdp.zones.spi.AbstractContext#getTenant()
     */
    @Override
    public synchronized Tenant getTenant() throws ZoneException {
        IdentityService service = getIdentityService();
        return service.getTenant();
    }

    /**
     * Returns the tenant id that this context is connected to.
     * 
     * @return The tenant id
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @see com.att.cdp.zones.Context#getVolumeService()
     */
    @Override
    public synchronized VolumeService getVolumeService() {
        if (super.getVolumeService() == null) {
            String version = providerMetadata.getVolumeVersion();
            com.att.cdp.zones.Service service = loadServiceImplementation(VOLUME_SERVICE_CLASSNAME, version);
            setVolumeService((VolumeService) service);
        }
        return super.getVolumeService();
    }

    /**
     * This is a convenience method that allows the user to perform the login directly from the context object after
     * obtaining the context, rather than looking up the identity service and calling it.
     * 
     * @throws IllegalStateException
     *             If the identity service is not available or cannot be created
     * @throws IllegalArgumentException
     *             If the principal and/or credential are null or empty.
     * @throws ZoneException
     *             If the login cannot be performed because the principal and/or credentials are invalid.
     * @see com.att.cdp.zones.Context#login(java.lang.String, java.lang.String)
     */
    @Override
    public void login(String principal, String credential) throws ZoneException {
        if (principal != null && credential != null) {
            super.login(principal, credential);
        }
    }

    /**
     * @see com.att.cdp.zones.Context#logout()
     */
    @Override
    public void logout() {
        super.logout();
        if (getIdentityService() != null) {
            ((CommonIdentityService) getIdentityService()).destroyToken();
        }
    }

    /**
     * This method is used to parse the service catalog and register all published services with the context so that the
     * user does not have to supply the URL's for the services.
     * <p>
     * They can of course, if they want to, in which case this method will NOT override the user defined URL with the
     * value from the service catalog. This capability is provided to allow the client to override the service catalog
     * to correct for errors in the catalog, provide alternate connection points, etc. It is intended only to be used if
     * the service catalog cannot be for some reason.
     * </p>
     * <p>
     * When services are registered in the OpenStack service catalog (maintained by the Identity service), they
     * represent the connection endpoints for the various services provided by that openstack installation. The actual
     * URL registered in the catalog may be different from one installation to another, and may or may not contain
     * version information. When a service is actually invoked, the URL that is used must be the host and port, version,
     * and usually tenant id, as well as additional encoded path information. The URL obtained from the catalog (or
     * provided by the user for that matter), may not be entirely correct as it may not include the version information.
     * </p>
     * <p>
     * OpenStack also supports the concept of <em>regions</em>, which are reflected in the service catalog. The services
     * are examined to determine the total number of regions and their identifications before a specific set of services
     * is registered. If there are more than one region represented in the service catalog, then the user must supply
     * which region to connect to, unless they have supplied the URL to be used.
     * </p>
     * <p>
     * Since dynamically determining the version information involves a call to the service, it is avoided until the
     * service is actually used the first time. This lazy initialization allows us to correct the URL in the registry to
     * include the version information. This also means that when the URLs are registered in this method, we strip them
     * down to just the basic connectivity information, which we will then add back when the services are actually
     * called based on what is actually installed and configured. This approach guarantees portability among O/S
     * installations where in some cases the version is included in the catalog and in others it is not.
     * </p>
     * 
     * @param services
     *            The service catalog.
     */
    public void registerServices(List<Service> services) throws ZoneException {
        catalog.registerServices(services);
    }

    @Override
    public void relogin() throws IllegalStateException, IllegalArgumentException, ZoneException {
        super.login(getPrincipal(), getCredentials());
    }

    /**
     * Sets the tenant to be used
     * 
     * @param tenant
     *            The tenant object
     */
    public void setTenant(Tenant tenant) {
        super.setTenant(tenant);
        this.tenantId = tenant.getId();
    }

    /**
     * Sets the tenant id for use in this context. The tenant id is the identifier for the tenant that this context is
     * connected to. It is set during login.
     * 
     * @param tenantId
     *            The tenant id
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * Used to update the property object with a new value. This is used so that the {@link #getProperties()} can still
     * return an immutable collection to protect the context configuration but allow the provider to update it as
     * needed.
     * 
     * @param key
     *            The key to be modified/set
     * @param value
     *            The new value of the property
     */
    protected void updateProperty(String key, String value) {
        setProperty(key, value);
    }

    @Override
    public boolean isStale() {
        return this.getIdentityService().isAuthExpired();
    }

    /**
     * This method will check proxy settings for the provider. The code looks for a zone property
     * "provider.disable.httpProxy". If it is set to true, no proxy will be used and the connection will be a direct
     * connection to the url. If it is set to false (default value is also false), proxy will be used. The code will
     * first look for "http.proxyHost" and "http.proxyPort" properties in the zone, as the highest level of precedence.
     * If that is not defined, it will look for default configuration property of "http.proxyHost" and "http.proxyPort"
     * as the second level of precedence. If that is not defined, it looks in the environment table (system environment
     * variables) to see if "HTTP_PROXY" has been defined. If that is also not defined, then the connection will be a
     * direct connection to the url.
     */
    private void checkProxySettings(Properties config) {

        if (config == null) {
            return;
        }
        Configuration configuration = ConfigurationFactory.getConfiguration();
        String disableProxy = config.getProperty(ContextFactory.PROPERTY_DISABLE_PROXY, "false");

        if (disableProxy != null && disableProxy.equals("false")) {
            String providerProxyHost = config.getProperty(ContextFactory.PROPERTY_PROXY_HOST);
            String providerProxyPort = config.getProperty(ContextFactory.PROPERTY_PROXY_PORT);
            /*
             * If proxy host or port is not defined in zone, use from default.properties
             */
            if (providerProxyHost == null || providerProxyPort == null) {
                String proxyHost = configuration.getProperty(Constants.PROXY_HOST);
                Integer proxyPort = configuration.getIntegerProperty(Constants.PROXY_PORT, 8080);
                if (proxyHost == null) {
                    proxyHost = System.getenv("HTTP_PROXY");
                    if (proxyHost != null) {
                        Pattern pattern =
                            Pattern.compile("http[s]?://([^:/]+):([0-9]+){0,1}.*", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(proxyHost);
                        if (matcher.matches()) {
                            proxyHost = matcher.group(1);
                            if (matcher.group(2) != null) {
                                try {
                                    proxyPort = Integer.parseInt(matcher.group(2));
                                } catch (NumberFormatException e) {
                                    proxyPort = Integer.valueOf(8080);
                                }
                            }
                        } else {
                            proxyHost = null;
                            getLogger().error(String.format("Illegal proxy specification %s", proxyHost));
                        }
                    }
                }

                /*
                 * If proxyHost and port is not null set that in context properties
                 */
                if (proxyHost != null && proxyPort != null) {
                    updateProperty(ContextFactory.PROPERTY_PROXY_HOST, proxyHost);
                    updateProperty(ContextFactory.PROPERTY_PROXY_PORT, proxyPort.toString());
                }

            }

        }

    }

    /**
     * @param connector
     *            The connector to be updated with the new token
     */
    public synchronized void refreshIfStale(Connector connector) {
    	String logId = "PAL-5558";
    	Logger appLogger = getLogger();
    	appLogger.info(new Date().toString()+ " " + logId+":OpenStackContext.refreshIfStale - in");
        if (isStale()) {
        	appLogger.info(new Date().toString()+ " " +logId+":OpenStackContext.refreshIfStale - Connection is stale");
            try {
                logout();
                relogin();
            } catch (IllegalStateException | IllegalArgumentException | ZoneException e) {
                e.printStackTrace();
            }

        }
        appLogger.info(new Date().toString()+ " " +logId+":OpenStackContext.refreshIfStale - old token -"+connector.getAccess().getToken().getId());
        connector.updateToken();
        appLogger.info(new Date().toString()+ " " +logId+":OpenStackContext.refreshIfStale - new token -"+connector.getAccess().getToken().getId());
        
    }

    /**
     * @return The region we are connecting to, or an empty string if not specified.
     */
    @Override
    public String getRegion() {
        return catalog.getRegion();
    }
    
    @Override
    public void reloadKeyPair(String name, String publicKey, String privateKey, String fingerprint)
        throws ZoneException {
        throw new NotSupportedException(EELFResourceManager.format(OSMsg.PAL_OS_RESOURCE_UNAVAILABLE,
            "Reload key pair", getProvider().getName()));

    }
}
