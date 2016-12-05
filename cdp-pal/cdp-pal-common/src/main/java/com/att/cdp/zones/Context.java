/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import java.io.Closeable;
import java.util.Locale;
import java.util.Properties;

import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.model.Tenant;

/**
 * The context is the main interface to accessing a specific RIME service provider.
 * <p>
 * This interface extends the <code>Closeable</code> interface, therefore the compiler will track resource usage and
 * generate a warning (or error) if the resource (i.e., this context) is not closed. This ensures that the code using
 * the implementation will close the context when it is done with it, releasing resources.
 * </p>
 * <p>
 * The client application obtains a <code>Context</code> to a cloud service provider by calling the
 * {@link ContextFactory#getContext(String, Properties)} method. Once obtained, the caller uses the context object to
 * access services or the object model, or both. The caller provides the symbolic name of the service provider desired,
 * and optionally configuration properties used to configure the context. The service provider may be used to create any
 * number of contexts, each with different configurations if needed. The service provider represents a <em>type</em> of
 * cloud provider, like <code>OpenStack</code>, <code>AWS</code>, <code>VMWare</code>, and others. It does <em>not</em>
 * represent any specific version or instance.
 * </p>
 * <p>
 * When the context is created, it represents and manages the connection to a specific instance of the provider, and
 * manages the different versions of that provider. This allows a single provider definition to configure different
 * contexts that may be connected to different instances that are different versions of the provider. The client may
 * request any number of contexts. Each context is used to access a specific instance of the provider. Additionally,
 * multiple contexts can be created to the same instance, if desired.
 * </p>
 * <h2>Model Navigation</h2>
 * <p>
 * This API provides an abstract data model that represents Cloud infrastructure components. This data model is
 * abstract, and is mapped to the actual implementation of the service provider. The model is the same for all service
 * providers. This means that clients only need to understand how to use this API and its data model to be able to
 * interact with any supported cloud provider.
 * </p>
 * <p>
 * Model navigation can be done in two different ways. The client can call services (obtained from the context) and
 * associate objects itself, or the client may obtain a model object from the context and use it to obtain other model
 * objects.
 * </p>
 * <h3>Services-Based</h3>
 * <p>
 * This API is model-based, meaning that the abstractions for interaction with the service providers are all abstracted
 * to a common, generic class model that represents the resources that can be manipulated. There are two ways that a
 * caller can uses these services. First, the user can access the services directly (such as compute service) and
 * request a list of servers, create a server, etc. This is the <em>services-based</em> approach to using the API.
 * </p>
 * <h3>Model-Based</h3>
 * <p>
 * An alternative way to work with the API is to walk the data model. This is allowed because any model object returned
 * from the service providers is aware of the service provider context and provides support to navigate the model.
 * </p>
 * <h4>Connected Model Objects</h4>
 * <p>
 * These objects are called <em>connected</em> objects. Only the service provider implementation can create connected
 * objects, and they must initially be obtained by using the services provided. Once any connected object is obtained,
 * other connected objects may be obtained by simply navigating the abstract model relationships. Use methods supplied
 * as part of the model objects to navigate the model.
 * </p>
 * <p>
 * For example, the client could obtain the <code>Tenant</code> object from the context and use it to navigate the
 * model. From the <code>Tenant</code>, you can obtain a list of all <code>Server</code> objects without actually making
 * any service calls (the model makes them for you). This provides a more natural way to process the model and obtain
 * information from the API.
 * </p>
 * <h4>Disconnected Model Objects</h4>
 * <p>
 * Some API services allow the client to create objects. In order to create an object, the client must create a template
 * of the object that they want to create. This template will usually contain the name, description, size, and other
 * characteristics that are needed to define the object. This template object is of the same class as the model objects,
 * but it is created by the client and therefore is not aware of the context. This object is <em>disconnected</em> and
 * cannot be used to navigate the model. After all, it represents an object that does not yet exist!
 * </p>
 * <p>
 * Once the object has been created, the service that creates the object returns a new model object of the same type.
 * This is a <em>connected</em> model object and can be used to navigate the model. The template object <strong>IS
 * NOT</strong> updated, and will remain disconnected. If the user wishes to navigate the model, they must use the
 * returned object from the service that created the object.
 * </p>
 * <h4>Hybrid Mode</h4>
 * <p>
 * If necessary, you can switch from one mode to the other at any time. Every <em>connected</em> model object has the
 * ability to return the context object, as well as the context object always has the ability to return the
 * <code>Tenant</code> (as well as other objects ).
 * </p>
 * 
 * @since Sep 23, 2013
 * @version $Id$
 */

public interface Context extends Closeable {

    /**
     * The name of the compute service. This is the generic name used to identify the service regardless of what the
     * provider actually calls the service. The provider must map its representation of the service to this generic
     * name.
     */
    String COMPUTE_SERVICE_NAME = "compute";

    /**
     * The name of the volume service. This is the generic name used to identify the service regardless of what the
     * provider actually calls the service. The provider must map its representation of the service to this generic
     * name.
     */
    String VOLUME_SERVICE_NAME = "volume";

    /**
     * The name of the network service. This is the generic name used to identify the service regardless of what the
     * provider actually calls the service. The provider must map its representation of the service to this generic
     * name.
     */
    String NETWORK_SERVICE_NAME = "network";

    /**
     * The name of the identity service. This is the generic name used to identify the service regardless of what the
     * provider actually calls the service. The provider must map its representation of the service to this generic
     * name.
     */
    String IDENTITY_SERVICE_NAME = "identity";

    /**
     * The name of the image service. This is the generic name used to identify the service regardless of what the
     * provider actually calls the service. The provider must map its representation of the service to this generic
     * name.
     */
    String IMAGE_SERVICE_NAME = "image";

    /**
     * The name of the object service. This is the generic name used to identify the service regardless of what the
     * provider actually calls the service. The provider must map its representation of the service to this generic
     * name.
     */
    String OBJECT_SERVICE_NAME = "object";

    /**
     * The name of the stack service. This is the generic name used to identify the service regardless of what the
     * provider actually calls the service. The provider must map its representation of the service to this generic
     * name.
     */
    String STACK_SERVICE_NAME = "stack";

    /**
     * Calls to this method return a reference to the Compute service implementation for this context. Repeated calls on
     * the same context return the same Compute implementation object.
     * 
     * @return the reference to the Compute service for this context.
     */
    ComputeService getComputeService();

    /**
     * Calls to this method return a reference to the identity service implementation for this context. Repeated calls
     * on the same context return the same Identity implementation object.
     * 
     * @return the reference to the identity service for this context.
     */
    IdentityService getIdentityService();

    /**
     * Returns the reference to the Image service for this cloud provider. Calls to the same method on the same context
     * return the same Image service implementation.
     * 
     * @return The Image service provider.
     * @throws NotLoggedInException
     *             If the caller is not logged in
     */
    ImageService getImageService() throws NotLoggedInException;

    /**
     * Returns the locale that was set for this context.
     * 
     * @return The current locale.
     */
    Locale getLocale();

    /**
     * This method allows providers that support geographical systems or regions to report the region name or other
     * identifier they use for the geographical site. For providers that do not support regions, the region name should
     * be returned as an empty string.
     * 
     * @return The region name or ID for this context, or an empty string if not supported.
     */
    String getRegion();

    /**
     * Returns the reference to the Network service for this cloud provider. Calls to the same method on the same
     * context return the same Network service implementation.
     * 
     * @return The Network service provider.
     */
    NetworkService getNetworkService();

    /**
     * Returns the reference to the Object store service for this cloud provider. Calls to the same method on the same
     * context return the same Object Store service implementation.
     * 
     * @return The BlobStore service provider.
     */
    ObjectService getObjectStoreService();

    /**
     * @return the principal name that was used to authenticate the context if hte context is logged in, null otherwise
     */
    String getPrincipal();

    /**
     * Returns the configuration properties for this context.
     * <p>
     * Providers are used to create contexts. Contexts represent configured "connections" to the back-end services of
     * the cloud. Each context can be configured differently if needed even when created from the same provider. This
     * means that a client can create a context for accessing one OpenStack zone (say "Zone1") in a different way from
     * another context used to access a different OpenStack zone (say "Zone2"). Each of these are managed by separate
     * context objects. This method allows the client to access the properties used to configure the current context.
     * </p>
     * 
     * @return The properties used to configure the context. Note, this is an immutable collection. Any attempt to
     *         change the collection will result in an exception.
     */
    Properties getProperties();

    /**
     * @return the reference to the provider that is supporting this context
     */
    Provider getProvider();

    /**
     * Each provider can supply their own metadata. This metadata can be queried to determine what features the provider
     * supports, versions, and other information. This is different from the context properties, because the provider
     * metadata is the same for all contexts created by the same provider.
     * 
     * @return The metadata for this provider
     */
    ProviderMetadata getProviderMetadata();

    /**
     * Returns the tenant object for this context. This allows the client to navigate the object model rather than
     * requesting services, if so desired.
     * 
     * @return The tenant object
     * @throws ZoneException
     *             If the user is not logged in and no tenant exists
     */
    Tenant getTenant() throws ZoneException;

    /**
     * Returns the name of the tenant that this context is connected to, or null if the context is not connected.
     * 
     * @return The tenant name, or null if the context is not connected.
     */
    String getTenantName();

    /**
     * Returns the reference to the Snapshot service for this cloud provider. Calls to the same method on the same
     * context return the same Snapshot service implementation.
     * 
     * @return The Volume service provider.
     */
    SnapshotService getSnapshotService();

    /**
     * Returns the reference to the Volume service for this cloud provider. Calls to the same method on the same context
     * return the same Volume service implementation.
     * 
     * @return The Volume service provider.
     */
    VolumeService getVolumeService();

    /**
     * Returns the reference to the Orchestration service for this cloud provider, if the cloud provider has an
     * orchestration service installed. Calls to the same method on the same context return the same Stack service
     * implementation. If the provider does not have an orchestration service, then the return value will be null.
     * 
     * @return The orchestration service, or null if not available for this provider.
     */
    StackService getStackService();

    /**
     * @return True if a valid login has been performed and no logout has occurred, false otherwise
     */
    boolean isLoggedIn();

    /**
     * @return True if the context is currently open and usable, false otherwise
     */
    boolean isOpen();

    /**
     * @return True if the context should attempt to re-authenticate, false otherwise.
     */
    boolean isStale();

    /**
     * This method logs in to the context using the provided principal and credentials.
     * 
     * @param principal
     *            The principal that we are logging in as
     * @param credential
     *            The credentials used to authenticate the principal
     * @throws IllegalStateException
     *             If the identity service is not available or cannot be created
     * @throws IllegalArgumentException
     *             If the principal and/or credential are null or empty.
     * @throws ZoneException
     *             If the login cannot be performed because the principal and/or credentials are invalid.
     */
    void login(String principal, String credential) throws IllegalStateException, IllegalArgumentException,
        ZoneException;

    /**
     * This method logs the client out of the context. If the user is not currently logged in, nothing happens.
     */
    void logout();

    /**
     * Re-login to the provider using existing credentials.
     * 
     * @throws IllegalStateException
     *             If the context is not in a valid state to re-login. I.e., the context is currently logged in, or has
     *             never been logged in and logged out.
     * @throws IllegalArgumentException
     *             If the principal and credentials, or the tenant, cannot be resolved
     * @throws ZoneException
     *             if the re-login cannot be processed for some reason
     */
    void relogin() throws IllegalStateException, IllegalArgumentException, ZoneException;

    /**
     * Sets the locale for the current connection. Used to ensure that the proper message resources are loaded.
     * 
     * @param locale
     *            The locale for all resources.
     */
    void setLocale(Locale locale);

    /**
     * @return The defined, or defaulted, number of retries allowed to perform some operation. If undefined, the default
     *         is 1.
     */
    int getRetryLimit();

    /**
     * @return The defined, or defaulted, delay that must be inserted between retries. If undefined, the default is zero
     *         (0).
     */
    int getRetryDelay();
    
    /**
     * ReloadKeyPair to the provider using existing key pair from stack.
     * 
     * @throws ZoneException
     *             if the reloadKeyPair cannot be processed
     */   
    void reloadKeyPair(String name, String publicKey, String privateKey,String fingerprint) throws ZoneException;
}
