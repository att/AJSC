/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import com.att.cdp.exceptions.NotLoggedInException;
import com.att.cdp.zones.ContextFactory;
import com.att.cdp.zones.spi.DefaultProviderMetadata;

/**
 * Returns the metadata that describes the OpenStack provider and its services.
 * <p>
 * By extending the class <code>DefaultProviderMetadata</code>, most (if not all) of the metadata can be obtained from
 * the "provider.properties" resource file. Overriding this class allows metadata that cannot be easily accommodated
 * using the resource file to be computed or derived, specific to a provider.
 * </p>
 * <p>
 * In the case of OpenStack, as they have evolved the product they have added new services and refactored existing
 * services to break out operations and to segregate functionality. For example, in the distributions prior to Folsom,
 * the Nova component supplied compute, volume, image, template, and network support. Later, in Folsom and beyond, they
 * have been breaking these functions out into separate services, such as Cinder. These additional services are accessed
 * using a URL that can be obtained from the identity service catalog, or the end-user client can supply that URL. It is
 * placed into the properties object as part of the configuration of the context.
 * </p>
 * <p>
 * Since different services exist at different times, the presence or absence of a component's URL can be used to infer
 * version information. For example, with Folsom the only services that are defined are "volume", "image", "compute",
 * and "identity". Additionally, the version of the URL can be used to "fine tune" the support.
 * </p>
 * <p>
 * The implementation of all of the abstracted services is provided. In different releases, they may get their
 * information from and interact with different OpenStack services. For example, in Folsom, the network functions are
 * directed to Nova. In Grizzly, they are directed to "Neutron" (neutron did not exist in Folsom).
 * </p>
 * 
 * @since Sep 30, 2013
 * @version $Id$
 */

public class OpenStackMetadata extends DefaultProviderMetadata {

    /**
     * Construct the metadata object that describes the provider
     * 
     * @param osContext
     *            OpenStack provider context implementation
     */
    public OpenStackMetadata(OpenStackContext osContext) {
        super(osContext);
    }

    /**
     * Returns the package version for the correct support for the compute service that we are actually connecting to.
     * Note that this method requires a valid login in order to succeed.
     * 
     * @return the package version for the correct support for the compute service
     * @see com.att.cdp.zones.ProviderMetadata#getComputeVersion()
     */
    @Override
    public String getComputeVersion() {

        if (getComponentVersion(COMPUTE) == null) {
            OpenStackContext context = (OpenStackContext) getContext();
            ServiceCatalog catalog = context.getServiceCatalog();
            setComponentVersion(COMPUTE, catalog.resolvePackageNode(ServiceCatalog.OS_COMPUTE_SERVICE_TYPE));
            String url = catalog.resolveServiceURL(ServiceCatalog.OS_COMPUTE_SERVICE_TYPE);
            if (url != null) {
                context.updateProperty(ContextFactory.PROPERTY_COMPUTE_URL, url);
            }
        }

        return super.getComputeVersion();
    }

    /**
     * This method is called to resolve what identity services we need to load. OpenStack has two supported identity
     * service implementations, V2 and V3, and they are very different. In V2, we had to login to access the service
     * catalog, whereas in V3 we can access the service catalog without being loggedin but V3 uses OAuth to authenticate
     * the user. For now, we will punt and only support the V2 version of identity service.
     * 
     * @return the package version for the correct support for the identity service
     * @see com.att.cdp.zones.ProviderMetadata#getIdentityVersion()
     */
    @Override
    public String getIdentityVersion() {
        if (getComponentVersion(IDENTITY) == null) {
            OpenStackContext context = (OpenStackContext) getContext();
            ServiceCatalog catalog = context.getServiceCatalog();
            setComponentVersion(IDENTITY, catalog.resolvePackageNode(ServiceCatalog.OS_IDENTITY_SERVICE_TYPE));
            String url = catalog.resolveServiceURL(ServiceCatalog.OS_IDENTITY_SERVICE_TYPE);
            if (url != null) {
                context.updateProperty(ContextFactory.PROPERTY_IDENTITY_URL, url);
            }
        }
        return super.getIdentityVersion();
    }

    /**
     * Returns the package version for the correct support for the image service that we are actually connecting to.
     * Note that this method requires a valid login in order to succeed.
     * 
     * @return the package version for the correct support for the image service
     * @throws NotLoggedInException
     *             If the context has not been logged in
     * @see com.att.cdp.zones.ProviderMetadata#getImageVersion()
     */
    @Override
    public String getImageVersion() throws NotLoggedInException {
        if (getComponentVersion(IMAGE) == null) {
            OpenStackContext context = (OpenStackContext) getContext();
            ServiceCatalog catalog = context.getServiceCatalog();
            setComponentVersion(IMAGE, catalog.resolvePackageNode(ServiceCatalog.OS_IMAGE_SERVICE_TYPE));
            String url = catalog.resolveServiceURL(ServiceCatalog.OS_IMAGE_SERVICE_TYPE);
            if (url != null) {
                context.updateProperty(ContextFactory.PROPERTY_IMAGE_URL, url);
            }
        }
        return super.getImageVersion();
    }

    /**
     * Returns the package version for the correct support for the network service that we are actually connecting to.
     * Note that this method requires a valid login in order to succeed.
     * 
     * @return the package version for the correct support for the network service
     * @see com.att.cdp.zones.ProviderMetadata#getNetworkVersion()
     */
    @Override
    public String getNetworkVersion() {
        if (getComponentVersion(NETWORK) == null) {
            OpenStackContext context = (OpenStackContext) getContext();
            ServiceCatalog catalog = context.getServiceCatalog();
            setComponentVersion(NETWORK, catalog.resolvePackageNode(ServiceCatalog.OS_NETWORK_SERVICE_TYPE));
            String url = catalog.resolveServiceURL(ServiceCatalog.OS_NETWORK_SERVICE_TYPE);
            if (url != null) {
                context.updateProperty(ContextFactory.PROPERTY_NETWORK_URL, url);
            }
        }
        return super.getNetworkVersion();
    }

    /**
     * Returns the package version for the correct support for the object service that we are actually connecting to.
     * This method does not actually make an api call to determine the support. The reason for this is that OpenStack
     * recently added this API, and there is only one version available, and the API does not provide a mechanism to
     * determine the correct versions.
     * 
     * @return the package version for the correct support for the object service
     * @see com.att.cdp.zones.ProviderMetadata#getObjectVersion()
     */
    @Override
    public String getObjectVersion() {
        if (getComponentVersion(OBJECT) == null) {
            setComponentVersion(OBJECT, ServiceCatalog.V1);
        }
        return super.getObjectVersion();
    }

    /**
     * Returns the package version for the correct support for the object service that we are actually connecting to.
     * This method does not actually make an api call to determine the support. The reason for this is that OpenStack
     * recently added this API, and there is only one version available, and the API does not provide a mechanism to
     * determine the correct versions.
     * 
     * @return the package version for the correct support for the object service
     * @see com.att.cdp.zones.ProviderMetadata#getObjectVersion()
     */
    @Override
    public synchronized String getStackVersion() {
        if (isStackSupported()) {
            if (getComponentVersion(STACK) == null) {
                OpenStackContext context = (OpenStackContext) getContext();
                ServiceCatalog catalog = context.getServiceCatalog();
                setComponentVersion(STACK, catalog.resolvePackageNode(ServiceCatalog.OS_STACK_SERVICE_TYPE));
                String url = catalog.resolveServiceURL(ServiceCatalog.OS_STACK_SERVICE_TYPE);
                if (url != null) {
                    context.updateProperty(ContextFactory.PROPERTY_STACK_URL, url);
                }
            }
        }
        return super.getObjectVersion();
    }

    /**
     * Returns the package version for the correct support for the volume service that we are actually connecting to.
     * Note that this method requires a valid login in order to succeed.
     * 
     * @return the package version for the correct support for the volume service
     * @see com.att.cdp.zones.ProviderMetadata#getVolumeVersion()
     */
    @Override
    public String getVolumeVersion() {
        if (getComponentVersion(VOLUME) == null) {
            OpenStackContext context = (OpenStackContext) getContext();
            ServiceCatalog catalog = context.getServiceCatalog();
            setComponentVersion(VOLUME, catalog.resolvePackageNode(ServiceCatalog.OS_VOLUME_SERVICE_TYPE));
            String url = catalog.resolveServiceURL(ServiceCatalog.OS_VOLUME_SERVICE_TYPE);
            if (url != null) {
                context.updateProperty(ContextFactory.PROPERTY_VOLUME_URL, url);
            }
        }

        return super.getVolumeVersion();
    }

    /**
     * Returns the package version for the correct support for the snapshot service that we are actually connecting to.
     * Note that this method requires a valid login in order to succeed. NOTE: Since snapshots and volumes
     * 
     * @return the package version for the correct support for the volume service
     * @see com.att.cdp.zones.ProviderMetadata#getVolumeVersion()
     */
    @SuppressWarnings("nls")
    @Override
    public String getSnapshotVersion() {
        // TODO - Figure out how to do this dynamically
        return "v2";
    }
}
