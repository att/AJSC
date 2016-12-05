/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones;

import com.att.cdp.exceptions.NotLoggedInException;

/**
 * This interface is implemented by the provider and allows the client to obtain information about the provider.
 * 
 * @since Sep 30, 2013
 * @version $Id$
 */

public interface ProviderMetadata {

    /**
     * The symbolic name of the component that provides compute services
     */
    String COMPUTE = "compute";

    /**
     * The symbolic name of the component that provides identity services
     */
    String IDENTITY = "identity";

    /**
     * The symbolic name of the component that provides image services
     */
    String IMAGE = "image";

    /**
     * The symbolic name of the component that provides network services
     */
    String NETWORK = "network";

    /**
     * The symbolic name of the component that provides object services
     */
    String OBJECT = "object";

    /**
     * The symbolic name of the component that provides volume services
     */
    String VOLUME = "volume";

    /**
     * The symbolic name of the component that provides volume services
     */
    String SNAPSHOT = "snapshot";

    /**
     * The symbolic name of the component that provides stack (orchestration) services
     */
    String STACK = "stack";

    /**
     * @return Returns the version of the provider client.
     */
    String getClientVersion();

    /**
     * Returns the version for the compute service.
     * 
     * @return The version identifier for the service
     */
    String getComputeVersion();

    /**
     * Returns the version for the identity service.
     * 
     * @return The version identifier for the service
     */
    String getIdentityVersion();

    /**
     * Returns the version for the image service.
     * 
     * @return The version identifier for the service
     * @throws NotLoggedInException
     *             If the caller is not logged in
     */
    String getImageVersion() throws NotLoggedInException;

    /**
     * Returns the version for the network service.
     * 
     * @return The version identifier for the service
     */
    String getNetworkVersion();

    /**
     * Returns the version for the Object service.
     * 
     * @return The version identifier for the service
     */
    String getObjectVersion();

    /**
     * Returns the version for the Stack Orchestration service
     * 
     * @return The version identifier for the service.
     */
    String getStackVersion();

    /**
     * @return The name of the provider
     */
    String getProviderName();

    /**
     * Returns the version for the volume service.
     * 
     * @return The version identifier for the service
     */
    String getVolumeVersion();

    /**
     * @return True if the provider supports the Compute abstraction
     */
    boolean isComputeSupported();

    /**
     * @return True if the provider supports the Identity abstraction
     */
    boolean isIdentitySupported();

    /**
     * @return True if the provider supports the Image abstraction
     */
    boolean isImageSupported();

    /**
     * @return True if the provider supports the network abstraction
     */
    boolean isNetworkSupported();

    /**
     * @return True if the provider supports the Object abstraction
     */
    boolean isObjectSupported();

    /**
     * @return True if the provider supports an orchestration service
     */
    boolean isStackSupported();

    /**
     * If the implementation supports the compute abstraction, does it also allow the client to resume a server.
     * 
     * @return True if a server can be resumed
     */
    boolean isResumeServerSupported();

    /**
     * If the implementation supports the compute abstraction, does it also allow the client to start a server.
     * 
     * @return True if a server can be started.
     */
    boolean isStartServerSupported();

    /**
     * If the implementation allows a server to be moved from one physical host to another physical host. Moving a
     * server allows a server that is down (likely because of a failed physical host) to be moved to a new physical
     * host. The server to be moved must be down before it can be moved.
     * 
     * @return True if the provider allows the client to specify a server to be moved, false otherwise.
     */
    boolean isMoveServerSupported();

    /**
     * If the implementation allows a server to be migrated from one host to another, possibly within policy or strategy
     * constraints defined by the provider. The caller cannot specify the host that the server will be placed on, that
     * is determined by the provider. Also, the instance can be either shut down or running.
     * 
     * @return True if the provider allows a server to be migrated.
     */
    boolean isMigrateServerSupported();

    /**
     * If the implementation supports the compute abstraction, does it also allow the client to stop a server.
     * 
     * @return True if a server can be stopped.
     */
    boolean isStopServerSupported();

    /**
     * If the implementation supports the compute abstraction, does it also allow the client to suspend a server.
     * 
     * @return True if a server can be suspended
     */
    boolean isSuspendServerSupported();

    /**
     * @return True if the provider supports the Volume abstraction
     */
    boolean isVolumeSupported();

    /**
     * @return True if the provider is actually a bare-metal (i.e., direct connection to low-level services)
     *         implementation and not a provider such as OpenStack or VMWare.
     */
    boolean isBareMetalImplementation();

    /**
     * @return True if the provider supports the Snapshot abstraction
     */
    boolean isSnapshotSupported();

    /**
     * @return True if the provider supports Snapshot abstraction
     */
    String getSnapshotVersion();

    /**
     * @returnTrue if the provider supports load balancers
     */
    boolean isLoadBalancerSupported();

    /**
     * @return True if the provider supports firewalls
     */
    boolean isFirewallSupported();
    
    /**
     * 
     * @return True if the provider supports keypair read
     */
    boolean isKeypairReadSupported();
    
    /**
     * 
     * @return True if the provider supports FloatingIpPools
     */
    boolean isFloatingIpPoolsSupported(); 
    
    /**
     * Returns the list of provider configuration immutable keys which needs to be preserved in the provisioned stack
     * 
     * @return The provider configuration keys
     */
    String[] getImmutablePropertiesKeys();
    
    /**
     * @return True if the provider supports ssh
     */
    boolean isSSHSupported();
    
    /**
     * @return True if the provider needs hostname to be generated
     */
    boolean isHostNameToBeGenerated();
    
    boolean isNetworkMetadataSupported();
}
