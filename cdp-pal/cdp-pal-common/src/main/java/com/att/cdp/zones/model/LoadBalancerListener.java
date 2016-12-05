/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import com.att.cdp.zones.Context;


/**
 * The definition of a load balancer listener
 * 
 * @since May 04, 2015
 * @version $Id$
 */
public class LoadBalancerListener extends ModelObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The unique id for the listener
     */
    private String id;

    /**
     * The name for the listener
     */
    private String name;

    /**
     * The description for the listener
     */
    private String description;

    /**
     * The subnet id on which to allocate the listener address
     */
    private String subnetId;
    
    /**
     * The IP address of the listener
     */
    private String ipAddress;
    
    /**
     * The protocol of the listener address
     */
    private ProtocolType protocol;
    
    /**
     * The port on which to listen to client traffic that is associated with the listener address
     */
    private int protocolPort;
    
    /**
     * The pool id which the listener is associated
     */
    private String poolId;
    
    /**
     * The session persistence parameters for the listener.
     */
    private SessionPersistence sessionPersistence;
    
   
	/**
     * The maximum number of connections allowed for the listener
     */
    private int connectionLimit;
    
    /**
     * Administrative state of the listener
     */
    private boolean adminStateUp;

    /**
     * The status of the listener
     */
    private LoadBalancerStatusType status;

    /**
     * Default constructor
     */
    public LoadBalancerListener() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected LoadBalancerListener(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        LoadBalancerListener other = (LoadBalancerListener) obj;
        return id.equals(other.id);
    }

    /**
     * @return the id of the subnet
     */
    public String getId() {
        return id;
    }

    /**
     * JavaBean accessor to obtain the value of name
     * 
     * @return the name value
     */
    public String getName() {
        return name;
    }

    /**
     * @return pool id
     */
    public String getPoolId() {
        return poolId;
    }

    /**
     * @return status
     */
    public LoadBalancerStatusType getStatus() {
        return status;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * @return True if the load balancer is up
     */
    public boolean isAdminStateUp() {
        return adminStateUp;
    }

    /**
     * @param adminStateUp
     *            the value for adminStateUp
     */
    public void setAdminStateUp(boolean adminStateUp) {
        this.adminStateUp = adminStateUp;
    }

    /**
     * @param id
     *            the value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Standard JavaBean mutator method to set the value of name
     * 
     * @param name
     *            the value to be set into name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param poolId
     *            the value for poolId
     */
    public void setPoolId(String poolId) {
        this.poolId = poolId;
    }

    /**
     * @param status
     *            the value for status
     */
    public void setStatus(LoadBalancerStatusType status) {
        this.status = status;
    }

    /**
     * @return The description
     */
	public String getDescription() {
		return description;
	}

    /**
     * @param description
     *            The description
     */
	public void setDescription(String description) {
		this.description = description;
	}

    /**
     * @return The subnet id
     */
	public String getSubnetId() {
		return subnetId;
	}

    /**
     * @param subnetId
     *            The subnet id
     */
	public void setSubnetId(String subnetId) {
		this.subnetId = subnetId;
	}

    /**
     * @return The ip address
     */
	public String getIpAddress() {
		return ipAddress;
	}

    /**
     * @param ipAddress
     *            The ip address
     */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

    /**
     * @return The protocol we are listening on
     */
	public ProtocolType getProtocol() {
		return protocol;
	}

    /**
     * @param protocol
     *            The protocol
     */
	public void setProtocol(ProtocolType protocol) {
		this.protocol = protocol;
	}

    /**
     * @return The port to listen to
     */
	public int getProtocolPort() {
		return protocolPort;
	}

    /**
     * @param protocolPort
     *            The port to listen to
     */
	public void setProtocolPort(int protocolPort) {
		this.protocolPort = protocolPort;
	}

    /**
     * @return How we want to persist sessions
     */
	public SessionPersistence getSessionPersistence() {
		return sessionPersistence;
	}

    /**
     * @param sessionPersistence
     *            How we want to persist user sessions
     */
	public void setSessionPersistence(SessionPersistence sessionPersistence) {
		this.sessionPersistence = sessionPersistence;
	}

    /**
     * @return Maximum connections allowed
     */
	public int getConnectionLimit() {
		return connectionLimit;
	}

    /**
     * @param connectionLimit
     *            maximum connections allowed
     */
	public void setConnectionLimit(int connectionLimit) {
		this.connectionLimit = connectionLimit;
	}
	
    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("LoadBalancerListener id(%s), name(%s)", id, name);
    }
}
