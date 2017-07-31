/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import com.att.cdp.zones.Context;

/**
 * The definition of a load balancer member on a network
 * 
 * @since Sep 24, 2014
 * @version $Id$
 */
public class LoadBalancerMember extends ModelObject {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Administrative state of the load balancer member
     */
    private boolean adminStateUp;

    /**
     * The id of the load balancer member definition
     */
    private String id;

    /**
     * The name of the load balancer member
     */
    private String name;

    /**
     * The pool id that this member belongs too
     */
    private String poolId;

    /**
     * The status of the load balancer member
     */
    private LoadBalancerStatusType status;
    
    /**
     * The IP address of the member
     */
    private String address;

    /**
     * The port on which the application is hosted
     */
    private int protocolPort;
    
    /**
     * The subnet id in which to access this member
     */
    private String subnetId;
    
   	

    /**
     * The Weight of member
     */
    private int weight;
   

	/**
     * Default constructor
     */
    public LoadBalancerMember() {

    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected LoadBalancerMember(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        LoadBalancerMember other = (LoadBalancerMember) obj;
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
     * @return ip address
     */
    public String getAddress() {
		return address;
	}

    /**
     * @param address
     *            the value for ip address
     */
	public void setAddress(String address) {
		this.address = address;
	}
	
	
	/**
     * @return protocol port
     */
	public int getProtocolPort() {
		return protocolPort;
	}

	    /**
     * @param protocolPort
     *            the port
     */
	public void setProtocolPort(int protocolPort) {
		this.protocolPort = protocolPort;
	}
	
	/**
     * @return subnetId
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
     * @return weight 
     */ 
	public int getWeight() {
		return weight;
	}

	    /**
     * @param weight
     *            The weight
     */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("LoadBalancerMember id(%s), name(%s)", id, name);
    }
}
