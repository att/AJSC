/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.att.cdp.exceptions.ContextConnectionException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.pal.configuration.Configuration;
import com.att.cdp.pal.configuration.ConfigurationFactory;
import com.att.cdp.pal.i18n.Msg;
import com.att.cdp.pal.util.UnmodifiableProperties;
import com.att.cdp.zones.ComputeService;
import com.att.cdp.zones.Context;
import com.att.cdp.zones.ContextFactory;
import com.att.cdp.zones.IdentityService;
import com.att.cdp.zones.ImageService;
import com.att.cdp.zones.NetworkService;
import com.att.cdp.zones.ObjectService;
import com.att.cdp.zones.Provider;
import com.att.cdp.zones.Service;
import com.att.cdp.zones.SnapshotService;
import com.att.cdp.zones.StackService;
import com.att.cdp.zones.VolumeService;
import com.att.cdp.zones.model.Tenant;
import com.att.eelf.i18n.EELFResourceManager;

/**
 * This class is intended to assist the implementations by handing all of the common functions, and providing support
 * for the implementation classes.
 * <p>
 * The context object represents a configured connection to a specific cloud zone using a specific provider to access
 * the services of that zone. This means that the provider must be identified correctly for the zone (that ensures we
 * get the right API support), and the configuration must be passed to the context when it is created. To make
 * configuration a little easier, the provider default properties are passed to the context, as well as any provided by
 * the client. These properties are used to create the configured properties object for the context. First, the default
 * properties are copied into the context properties object. Then the client supplied properties (if any), then the
 * system properties.
 * </p>
 * <p>
 * The context may also request validation that the minimum required configuration properties exist. This is supplied in
 * this class using the {@link #validateRequiredConfiguration(List)} method. This method is passed a list of property
 * names that must exist. If the configuration properties are missing any of the keys specified in the list, an
 * exception is thrown.
 * </p>
 * <p>
 * Once configured, the context implementation should only reference the properties object of the context. This is
 * because additional context objects may be created with different configurations.
 * </p>
 * <p>
 * Also, because the context object represents a usable connection to the cloud zone, and it implements the
 * <code>Closeable</code> interface, there is a concept of Open and Closed. When a context is created, it is implicitly
 * in an open state. Once it is closed, it can no longer be used to request services. This is because the resources used
 * by the context are released and no longer available. The actual implementation details from one cloud infrastructure
 * to another may vary, but this general contract is enforced by this abstraction because it is the most portable across
 * the various implementations.
 * </p>
 * <p>
 * This class also handles the registration, removal, and invocation of any registered interceptors. Interceptors are
 * classes that the client can create to handle specific events. When these events occur, the interceptor(s) that are
 * registered at that point in time are invoked.
 * </p>
 * 
 * @author <a href="mailto:dh868g@att.com?subject=com.att.cdp.zones.spi.AbstractContext">Dewayne Hafenstein</a>
 * @since Sep 23, 2013
 * @version $Id$
 */
public abstract class AbstractContext implements Context {

    /**
     * The application logger
     */
    private Logger appLogger;

    /**
     * The cached reference to the compute service. This is created the first time it is requested.
     */
    private ComputeService computeService;

    /**
     * The CDP framework configuration
     */
    private Configuration configuration;

    /**
     * The credentials used to login this context, if it is logged in
     */
    private String credentials;

    /**
     * The cached reference to the identity service. This is created the first time it is requested.
     */
    private IdentityService identityService;

    /**
     * The cached reference to the Image service. This is created the first time it is requested.
     */
    private ImageService imageService;

    /**
     * The locale of the caller for generation of NLS-aware messages
     */
    private Locale locale;

    /**
     * True when the context has been authenticated, false otherwise
     */
    private boolean loggedIn;

    /**
     * The cached reference to the Network service. This is created the first time it is requested.
     */
    private NetworkService networkService;

    /**
     * The cached reference to the object store service. This is created the first time it is requested.
     */
    private ObjectService objectService;

    /**
     * This flag checks to see if the context is in a usable (open) state. It is initially assumed to be usable when it
     * is created. No explicit "open" needs to be performed. However, once closed, no further operations are allowed.
     */
    private AtomicBoolean openFlag = new AtomicBoolean(true);

    /**
     * The principal that was used to login this context, if it is logged in
     */
    private String principal;

    /**
     * The properties used to configure this context
     */
    private Properties properties;

    /**
     * A reference to the provider that created this context object
     */
    private Provider provider;

    /**
     * The security logger
     */
    private Logger securityLogger;

    /**
     * The cached reference to the Snapshot service. This is created the first time it is requested.
     */
    private SnapshotService snapshotService;

    /**
     * A reference to the loaded stack service, if one exists.
     */
    private StackService stackService;

    /**
     * A cached reference to the tenant object, if one has been created or requested
     */
    private Tenant tenant;

    /**
     * The name of the tenant to which this context is associated
     */
    private String tenantName;

    /**
     * The cached reference to the Volume service. This is created the first time it is requested.
     */
    private VolumeService volumeService;

    /**
     * The maximum number of times to retry a connection before failing
     */
    private int retryLimit;

    /**
     * The amount of time, in seconds, that the system is to wait between retry attempts
     */
    private int retryDelay;

    /**
     * Construct the abstract context object.
     * <p>
     * The abstract context object manages the merging of the default properties, any client specified properties, and
     * then the system properties into a single property object. This ensures that command-line specified (-D) defined
     * properties override client specified, and that client specified properties override defaults. It also means that
     * meaningful default values can be loaded as part of the provider implementation that will make it more usable for
     * the client.
     * </p>
     * 
     * @param provider
     *            The provider that created us
     * @param defaults
     *            The default properties, if any exist. These are generally loaded as a default configuration with the
     *            provider.
     * @param config
     *            The (optional) configuration properties
     */
    @SuppressWarnings("nls")
    public AbstractContext(Provider provider, Properties defaults, Properties config) {
        configuration = ConfigurationFactory.getConfiguration();
        appLogger = configuration.getApplicationLogger();
        securityLogger = configuration.getSecurityLogger();

        this.provider = provider;
        properties = new Properties();
        if (defaults != null) {
            properties.putAll(defaults);
        }
        if (config != null) {
            properties.putAll(config);
        }
        properties.putAll(System.getProperties());

        /*
         * Extract or default the retry delay and limit properties
         */
        try {
            retryLimit = Integer.parseInt(properties.getProperty(ContextFactory.PROPERTY_RETRY_LIMIT, "1"));
        } catch (NumberFormatException e) {
            retryLimit = 1;
        }
        try {
            retryDelay = Integer.parseInt(properties.getProperty(ContextFactory.PROPERTY_RETRY_DELAY, "0"));
        } catch (NumberFormatException e) {
            retryDelay = 0;
        }

    }

    /**
     * This method does nothing other than to clear the open flag. All resource cleanup must be done in the descendant
     * classes. It is therefore imperative that the context implementations override this method and perform the
     * necessary cleanup of resources, then call this method to clear the open state.
     * 
     * @see java.io.Closeable#close()
     */
    @SuppressWarnings("nls")
    @Override
    public void close() throws IOException {
        if (isLoggedIn()) {
            logout();
        }
        appLogger.debug(EELFResourceManager.format(Msg.ZONE_CONTEXT_CLOSED, provider.getName(), (tenant == null
            ? "UNKNOWN" : tenantName)));
        openFlag.set(false);
    }

    /**
     * @return the value of computeService
     */
    @Override
    public ComputeService getComputeService() {
        return computeService;
    }

    /**
     * @return the value of credentials
     */
    protected String getCredentials() {
        return credentials;
    }

    /**
     * @return the value of identityService
     */
    @Override
    public IdentityService getIdentityService() {
        return identityService;
    }

    /**
     * @return the value of imageService
     */
    @Override
    public ImageService getImageService() {
        return imageService;
    }

    /**
     * @see com.att.cdp.zones.Context#getLocale()
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * @return The current application logger
     */
    public Logger getLogger() {
        return appLogger;
    }

    /**
     * Returns the modifiable properties. This is not exposed to the client, only to internal provider code
     * 
     * @return The actual properties object which can be modified by the caller
     */
    public Properties getModifiableProperties() {
        return properties;
    }

    /**
     * @return the value of networkService
     */
    @Override
    public NetworkService getNetworkService() {
        return networkService;
    }

    /**
     * @return the value of objectService
     */
    @Override
    public ObjectService getObjectStoreService() {
        return objectService;
    }

    /**
     * @see com.att.cdp.zones.Context#getPrincipal()
     */
    @Override
    public String getPrincipal() {
        return principal;
    }

    /**
     * @see com.att.cdp.zones.Context#getProperties()
     */
    @Override
    public Properties getProperties() {
        return new UnmodifiableProperties(properties);
    }

    /**
     * @see com.att.cdp.zones.Context#getProvider()
     */
    @Override
    public Provider getProvider() {
        return provider;
    }

    /**
     * @return The current security logger
     */
    public Logger getSecurityLogger() {
        return securityLogger;
    }

    /**
     * @return the value of snapshotService
     */
    @Override
    public SnapshotService getSnapshotService() {
        return snapshotService;
    }

    /**
     * @return the value of stackService
     */
    @Override
    public StackService getStackService() {
        return stackService;
    }

    /**
     * All providers need to implement this method that creates and caches the Tenant implementation object, as well as
     * returns the cached object when it is requested.
     * 
     * @return The Tenant
     * @throws ZoneException
     *             If the tenant cannot be obtained
     */
    @Override
    public Tenant getTenant() throws ZoneException {
        return tenant;
    }

    /**
     * @see com.att.cdp.zones.Context#getTenantName()
     */
    @Override
    public String getTenantName() {
        return tenantName;
    }

    /**
     * @return the value of volumeService
     */
    @Override
    public VolumeService getVolumeService() {
        return volumeService;
    }

    /**
     * @return True if a valid login has been performed and no logout has occurred, false otherwise
     * @see com.att.cdp.zones.Context#isLoggedIn()
     */
    @Override
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * @return True if the context is currently open and usable, false otherwise
     * @see com.att.cdp.zones.Context#isOpen()
     */
    @Override
    public boolean isOpen() {
        return openFlag.get();
    }

    /**
     * This method is used to dynamically load service implementations based on the version(s) of the provider we are
     * connecting to.
     * <p>
     * This method allows different contexts to use different versions if they are connecting to different providers.
     * The method obtains the version by component of the provider from the provider metadata and uses that to
     * substitute into the implementation class name provided. The caller must supply the name pattern to be loaded,
     * which can contain %s substitution parameters. The version of the component returned from the metadata is
     * substituted into the provided pattern name at each occurrence of the %s parameter and the class is loaded.
     * </p>
     * 
     * @param classname
     *            The name of the class to load. Use a %s replacement parameter where the version is inserted into the
     *            package or class name as appropriate. If no %s parameter is found, the name is used as-is. If more
     *            than one instance of %s is encountered, each occurrence is replaced with the version string from the
     *            provider metadata.
     * @param version
     *            The version implementation to be loaded and used.
     * @return The object that represents the service, or null if there was an error loading the service.
     */
    @SuppressWarnings("nls")
    protected Service loadServiceImplementation(String classname, String version) {
        String replacement = version != null ? version.trim() : "v1";
        StringBuffer name = new StringBuffer(classname.trim());
        Pattern pattern = Pattern.compile("%s");
        Matcher matcher = pattern.matcher(name);
        int position = 0;
        while (matcher.find(position)) {
            int start = matcher.start();
            int end = matcher.end();
            name.replace(start, end, replacement);
            position = start + replacement.length();
        }

        try {
            Class<?> clazz = Class.forName(name.toString());
            Constructor<?> ctor = clazz.getConstructor(new Class[] {
                Context.class
            });
            return (Service) ctor.newInstance(new Object[] {
                this
            });
        } catch (Exception t) {
            appLogger.error(EELFResourceManager.format(Msg.LOAD_ZONE_SERVICE_FAILED, name.toString(),
                provider.getName(), replacement), t);
        }

        return null;
    }

    /**
     * This method delegates to the identity service the request to login. This is a convenience method.
     * 
     * @throws ZoneException
     *             If any of the following conditions are true:
     *             <ul>
     *             <li>the user has not successfully logged in to the provider</li>
     *             <li>the context has been closed and this service is requested</li>
     *             <li>the current user does not have the rights to perform this operation</li>
     *             <li>the user and/or credentials are not valid</li>
     *             </ul>
     * @see com.att.cdp.zones.Context#login(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("nls")
    @Override
    public void login(String principal, String credential) throws IllegalStateException, IllegalArgumentException,
        ZoneException {

        this.principal = principal;
        this.credentials = credential;

        String msg =
            String.format("About to login principal [%s] to provider [%s] on tenant [%s] ", principal,
                provider.getName(), tenantName);
        appLogger.debug(msg);
        securityLogger.info(msg);

        IdentityService identity = getIdentityService();
        if (identity == null) {
            msg = EELFResourceManager.format(Msg.NO_PROVIDER_SERVICE, "Identity", provider.getName());
            appLogger.error(msg);
            securityLogger.error(msg);
            throw new IllegalStateException(msg);
        }
        if (principal == null || principal.trim().length() == 0) {
            msg = EELFResourceManager.format(Msg.INVALID_PRINCIPAL, principal, provider.getName());
            appLogger.error(msg);
            securityLogger.error(msg);
            throw new IllegalArgumentException(msg);
        }
        if (credential == null || credential.trim().length() == 0) {
            msg = EELFResourceManager.format(Msg.INVALID_CREDENTIAL, provider.getName());
            appLogger.error(msg);
            securityLogger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        /*
         * This logic was incorrect and not handling the failed login attempts correctly. This has been revised. If we
         * catch a connection exception during authentication, we will attempt recovery in case it is a communications
         * error. If the retries are exhausted, then we will throw an IllegalStateException.
         */
        int attempts = 0;
        while (attempts < getRetryLimit()) {
            try {
                identity.authenticate(principal, credential);
                msg =
                    EELFResourceManager.format(Msg.PRINCIPAL_HAS_BEEN_AUTHENTICATED, principal, provider.getName(),
                        tenantName);
                appLogger.debug(msg);
                securityLogger.info(msg);
                loggedIn = true;
                tenantName = identity.getTenant().getName();

                String providerName = provider.getName();
                appLogger.debug(EELFResourceManager.format(Msg.PROVIDER_LOGIN, principal, providerName));
                securityLogger.debug(EELFResourceManager.format(Msg.PROVIDER_LOGIN, principal, providerName));
                break;
            } catch (ContextConnectionException e) {
                appLogger.error(EELFResourceManager.format(Msg.RETRY_PROVIDER_CONNECTION, identity.getURL(), e
                    .getClass().getSimpleName(), e.getMessage(), Integer.toString(attempts + 1), Integer
                    .toString(getRetryLimit()), Integer.toString(getRetryDelay())));
                try {
                    Thread.sleep(getRetryDelay() * 1000L);
                } catch (InterruptedException ex) {
                    // ignore
                }
                attempts++;
            }
        }
        if (attempts >= getRetryLimit()) {
            msg = EELFResourceManager.format(Msg.NO_PROVIDER_SERVICE, "Identity", provider.getName());
            appLogger.error(msg);
            securityLogger.error(msg);
            throw new IllegalStateException(msg);

        }
    }

    /**
     * Logout the context
     * 
     * @see com.att.cdp.zones.Context#logout()
     */
    @SuppressWarnings("nls")
    @Override
    public void logout() {
        loggedIn = false;
        String providerName = provider.getName();
        String msg =
            String.format("Logging out principal [%s] from provider [%s] on tenant [%s] ", principal, providerName,
                tenantName);
        appLogger.debug(msg);
        securityLogger.info(msg);
    }

    /**
     * @param computeService
     *            the value for computeService
     */
    protected void setComputeService(ComputeService computeService) {
        this.computeService = computeService;
    }

    /**
     * @param identityService
     *            the value for identityService
     */
    protected void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    /**
     * @param imageService
     *            the value for imageService
     */
    protected void setImageService(ImageService imageService) {
        this.imageService = imageService;
    }

    /**
     * @see com.att.cdp.zones.Context#setLocale(java.util.Locale)
     */
    @Override
    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    /**
     * @param loggedIn
     *            the value for loggedIn
     */
    protected void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    /**
     * @param networkService
     *            the value for networkService
     */
    protected void setNetworkService(NetworkService networkService) {
        this.networkService = networkService;
    }

    /**
     * @param objectService
     *            the value for objectService
     */
    protected void setObjectStoreService(ObjectService objectService) {
        this.objectService = objectService;
    }

    /**
     * Allows a sub-class to set properties.
     * 
     * @param key
     *            The property key, or name
     * @param value
     *            The value of the named property
     */
    protected void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * @param snapshotService
     *            the value for snapshotService
     */
    protected void setSnapshotService(SnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

    /**
     * @param stackService
     *            the value for stackService
     */
    protected void setStackService(StackService stackService) {
        this.stackService = stackService;
    }

    /**
     * Sets the tenant to be used
     * 
     * @param tenant
     *            The tenant object
     */
    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
        tenantName = tenant.getName();
    }

    /**
     * @param tenantName
     *            the value for tenantName
     */
    protected void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    /**
     * @param volumeService
     *            the value for volumeService
     */
    protected void setVolumeService(VolumeService volumeService) {
        this.volumeService = volumeService;
    }

    /**
     * This method can be used by any provider implementation to validate that the configuration contains the required
     * minimum set of properties. The value of the properties is not checked, only that the properties exist. If any of
     * the required properties do not exist, then an exception is thrown.
     * 
     * @param keys
     *            The list of keys that must be present to be considered a valid configuration
     * @throws IllegalStateException
     *             If the properties used to configure this provider do not contain all of the specified keys
     */
    @SuppressWarnings("nls")
    protected void validateRequiredConfiguration(List<String> keys) throws IllegalStateException {
        if (keys == null || keys.isEmpty()) {
            return;
        }

        StringBuffer missing = new StringBuffer();

        for (String key : keys) {
            if (!properties.containsKey(key)) {
                missing.append(key);
                missing.append(",");
            }
        }

        if (missing.length() > 0) {
            missing.delete(missing.length() - 2, missing.length());
            String msg = EELFResourceManager.format(Msg.ZONE_BAD_CONFIGURATION, provider.getName(), missing.toString());
            appLogger.error(msg);
            throw new IllegalStateException(msg);
        }
    }

    /**
     * @return the value of retryLimit
     */
    public int getRetryLimit() {
        return retryLimit;
    }

    /**
     * @return the value of retryDelay
     */
    public int getRetryDelay() {
        return retryDelay;
    }

}
