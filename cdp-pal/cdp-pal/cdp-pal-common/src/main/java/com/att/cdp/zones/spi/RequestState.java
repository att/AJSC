/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import java.util.HashMap;
import java.util.Map;

/**
 * This class provides a thread-local storage implementation to track the current request state and make it available
 * for exception handling and logging purposes.
 * 
 * @since Jan 6, 2015
 * @version $Id$
 */

public final class RequestState {

    /**
     * The class name of the service method being called
     */
    public static final String CLASS = "CLASS";

    /**
     * If applicable, the device name
     */
    public static final String DEVICE = "DEVICE";

    /**
     * If applicable, the hypervisor name
     */
    public static final String HYPERVISOR = "HYPERVISOR";

    /**
     * If applicable, the image name
     */
    public static final String IMAGE = "IMAGE";

    /**
     * If applicable, the IP Address
     */
    public static final String IPADDRESS = "IPADDRESS";

    /**
     * If applicable, the keypair name
     */
    public static final String KEYPAIR = "KEYPAIR";

    /**
     * The request method being called
     */
    public static final String METHOD = "METHOD";

    /**
     * The source code line number
     */
    public static final String LINE_NUMBER = "LINE_NUMBER";

    /**
     * The provider service being called
     */
    public static final String SERVICE = "SERVICE";

    /**
     * The URL of the provider service being called
     */
    public static final String SERVICE_URL = "SERVICE_URL";

    /**
     * If applicable, the name/id of the network
     */
    public static final String NETWORK = "NETWORK";

    /**
     * If applicable, the name of a resource pool
     */
    public static final String POOL = "POOL";

    /**
     * The principal used to authenticate the connection
     */
    public static final String PRINCIPAL = "PRINCIPAL";

    /**
     * The provider being used
     */
    public static final String PROVIDER = "PROVIDER";

    /**
     * If applicable, the server name
     */
    public static final String SERVER = "SERVER";

    /**
     * If applicable, the size of some resource
     */
    public static final String SIZE = "SIZE";

    /**
     * If applicable, the snapshot id or name
     */
    public static final String SNAPSHOT = "SNAPSHOT";

    /**
     * If applicable, the status of whatever object we are working on
     */
    public static final String STATUS = "STATUS";

    /**
     * If applicable, the name of the subnet
     */
    public static final String SUBNET = "SUBNET";

    /**
     * If applicable, the template name
     */
    public static final String TEMPLATE = "TEMPLATE";

    /**
     * The tenant being used
     */
    public static final String TENANT = "TENANT";

    /**
     * The thread name of the thread running the service request
     */
    public static final String THREAD = "THREAD";

    /**
     * If applicable, the volume id or name
     */
    public static final String VOLUME = "VOLUME";

    /**
     * If applicable, the load balancer listener id or name
     */
    public static final String LOADBALANCERLISTENER = "LOADBALANCERLISTENER";

    /**
     * If applicable, the load balancer pool id or name
     */
    public static final String LOADBALANCERPOOL = "LOADBALANCERPOOL";

    /**
     * If applicable, the load balancer health monitor id or name
     */
    public static final String LOADBALANCERHEALTHMONITOR = "LOADBALANCERHEALTHMONITOR";

    /**
     * If applicable, the load balancer member id or name
     */
    public static final String LOADBALANCERMEMBER = "LOADBALANCERMEMBER";

    /**
     * If applicable, the Port ID or name
     */
    public static final String PORT = "PORT";

    /**
     * 
     */
    private static final ThreadLocal<Map<String, Object>> REQUEST_STATE = new ThreadLocal<Map<String, Object>>();

    /**
     * Clears all saved state for the current thread
     */
    public static void clear() {
        getState().clear();
    }

    /**
     * Returns the value of the request state property for this thread.
     * 
     * @param key
     *            The key of the property to be returned
     * @return The object value, or null if no state is defined with that key
     */
    public static Object get(String key) {
        return getState().get(key);
    }

    /**
     * Returns the thread local state map, if it exists.
     * 
     * @return The state map if it exists, or null if not.
     */
    public static Map<String, Object> getState() {
        if (REQUEST_STATE.get() == null) {
            REQUEST_STATE.set(new HashMap<String, Object>());
        }
        return REQUEST_STATE.get();
    }

    /**
     * Puts the specified key=value pair into the request state, creating the thread local map if needed
     * 
     * @param key
     *            The key of the property to be inserted into the state
     * @param value
     *            The value of the property to be inserted into the state
     */
    public static void put(String key, Object value) {
        getState().put(key, value);
    }

    /**
     * Private default constructor prevents instantiation of this class
     */
    private RequestState() {

    }

}
