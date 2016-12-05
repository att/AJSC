/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.model;

import java.util.ArrayList;
import java.util.List;

import com.att.cdp.exceptions.NotNavigableException;
import com.att.cdp.exceptions.ZoneException;
import com.att.cdp.zones.Context;

/**
 * A network represents a defined ethernet network that can be defined and associated with a server.
 * 
 * @since Sep 30, 2013
 * @version $Id$
 */
public class Network extends ModelObject {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * If true, the network is external in that it allows a instance connected to this network to access the internet,
     * or at least external to the tenant.
     */
    private boolean externalNetwork;
  

    /**
     * The id of the network definition
     */
    private String id;

    /**
     * The name of the network definition
     */
    private String name;

    /**
     * The physical network name or id. In the case of OpenStack, this is mapped to the ProviderPhysicalNetwork
     */
    private String physicalNet;

    /**
     * The name of the router, if one is defined. The router is optional, and if one is defined, only one is allowed per
     * network.
     */
    private String router;

    /**
     * A list of vlan segments, if any exist, or an empty list
     */
    private List<VLanSegment> segments = new ArrayList<>();

    /**
     * Indicates if the network is a shared network across multiple tenants or domains in the provider.
     */
    private boolean shared;

    /**
     * True if the router is to use Secure Network Address Translation (SNAT, false otherwise. If no router is defined
     * this attribute is ignored.
     */
    private boolean snat;

    /**
     * The status of the network
     */
    private String status;

    /**
     * A list of the subnets of this network
     */
    private List<Subnet> subnets = new ArrayList<>();

    /**
     * The type of network. This is typically an enumerated value such as "flat", "vlan", etc. In the case of openstack,
     * this is mapped to the ProviderNetworkType
     */
    private String type;

    /**
     * The VLan ID if the nextwork is a VLAN. This is mapped to the OpenStack providerSegmentationId
     */
    private int vlanId;

    /**
     * True if the vlan is transparent, false if not a vlan or it is not transparent
     */
    private boolean vlanTransparent;
    
    /**
     * The CIDR block. In the case of aws,
     * this is mapped to the cidr block for the VPC.
     */
        
    private String cidrBlock = null;
    
    /**
     * 
     */

    /**
     * This is a default constructor
     */
    public Network() {
    }

    /**
     * this constructor allows a user to create a model network that could be used to create a network definition
     * 
     * @param name
     *            The name of the network
     */
    public Network(String name) {
        this.name = name;
    }

    /**
     * This protected constructor allows the creation of a connected model object that is connected to a specific
     * context
     * 
     * @param context
     *            The context we are connected to
     */
    protected Network(Context context) {
        super(context);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        Network other = (Network) obj;
        return id.equals(other.id);
    }

    /**
     * JavaBean accessor to obtain the value of id
     * 
     * @return the id value
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
     * @return the value of physicalNet
     */
    public String getPhysicalNet() {
        return physicalNet;
    }

    /**
     * JavaBean accessor to obtain the value of router
     * 
     * @return the router value
     */
    public String getRouter() {
        return router;
    }

    /**
     * @return the value of segments
     */
    public List<VLanSegment> getSegments() {
        return segments;
    }

    /**
     * JavaBean accessor to obtain the value of status
     * 
     * @return the status value
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the subnet definition for the specified subnet of this network.
     * 
     * @param id
     *            The subnet id to be obtained
     * @return The subnet definition
     * @throws ZoneException
     *             If the model cannot be navigated because it is disconnected
     */
    public Subnet getSubnet(String id) throws ZoneException {
        notConnectedError();
        return null; // for the compiler
    }

    /**
     * @return the list of subnets attached to this network
     */
    public List<Subnet> getSubnets() {
        return subnets;
    }

    /**
     * JavaBean accessor to obtain the value of type
     * 
     * @return the type value
     */
    public String getType() {
        return type;
    }

    /**
     * @return the value of vlanId
     */
    public int getVlanId() {
        return vlanId;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * @return the value of externalNetwork
     */
    public boolean isExternalNetwork() {
        return externalNetwork;
    }

    /**
     * @return the value of shared
     */
    public boolean isShared() {
        return shared;
    }

    /**
     * JavaBean accessor to obtain the value of snat
     * 
     * @return the snat value
     */
    public boolean isSnat() {
        return snat;
    }

    /**
     * @return the value of vlanTransparent
     */
    public boolean isVlanTransparent() {
        return vlanTransparent;
    }

    /**
     * This method informs the provider implementation to refresh this network object with the current state as defined
     * on the provider.
     * 
     * @throws ZoneException
     *             If the netwrok cannot be refreshed
     */
    public void refresh() throws ZoneException {
        notConnectedError();
    }

    /**
     * @param externalNetwork
     *            the value for externalNetwork
     */
    public void setExternalNetwork(boolean externalNetwork) {
        this.externalNetwork = externalNetwork;
    }

    /**
     * @param id
     *            the value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param name
     *            the value for name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param physicalNet
     *            the value for physicalNet
     */
    public void setPhysicalNet(String physicalNet) {
        this.physicalNet = physicalNet;
    }

    /**
     * Standard JavaBean mutator method to set the value of router
     * 
     * @param router
     *            the value to be set into router
     */
    public void setRouter(String router) {
        this.router = router;
    }

    /**
     * @param segments
     *            the value for segments
     */
    public void setSegments(List<VLanSegment> segments) {
        this.segments = segments;
    }

    /**
     * @param shared
     *            the value for shared
     */
    public void setShared(boolean shared) {
        this.shared = shared;
    }

    /**
     * Standard JavaBean mutator method to set the value of snat
     * 
     * @param snat
     *            the value to be set into snat
     */
    public void setSnat(boolean snat) {
        this.snat = snat;
    }

    /**
     * @param status
     *            the value for status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @param subnets
     *            the value for subnets
     */
    public void setSubnets(List<Subnet> subnets) {
        this.subnets = subnets;
    }

    /**
     * @param type
     *            the value for type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param vlanId
     *            the value for vlanId
     */
    public void setVlanId(int vlanId) {
        this.vlanId = vlanId;
    }

    /**
     * @param vlanTransparent
     *            the value for vlanTransparent
     */
    public void setVlanTransparent(boolean vlanTransparent) {
        this.vlanTransparent = vlanTransparent;
    }
    
    

    public String getCidrBlock() {
		return cidrBlock;
	}

	public void setCidrBlock(String cidrBlock) {
		this.cidrBlock = cidrBlock;
	}

	

	/**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format(
            "Network id(%s), name(%s), status(%s), type(%s), subnets(%s), router(%s), SNAT(%s), vlan(%d), %d subnets",
            id, name, status, type, (subnets == null ? "empty" : subnets.toString()),
            (router == null ? "none" : router), (router == null ? "undefined" : Boolean.toString(snat)), vlanId,
            subnets.size());
    }

    /**
     * @author rl634w
     */
    public enum Status {

        /**
         * The network is active
         */
        ACTIVE
    }

    /**
     * The definition of a vlan segment
     * 
     * @since Sep 15, 2015
     * @version $Id$
     */
    public class VLanSegment {
        /**
         * The physical network name or id. In the case of OpenStack, this is mapped to the ProviderPhysicalNetwork
         */
        private String segmentPhysicalNet;

        /**
         * The type of network. This is typically an enumerated value such as "flat", "vlan", etc. In the case of
         * openstack, this is mapped to the ProviderNetworkType
         */
        private String segmentNetworkType;

        /**
         * The VLan ID if the nextwork is a VLAN. This is mapped to the OpenStack providerSegmentationId
         */
        private int segmentVlanId;

        /**
         * Construct an empty VLAN segment
         */
        public VLanSegment() {

        }

        /**
         * Construct a VLan segment
         * 
         * @param type
         *            The type of segment
         * @param physicalNet
         *            The physical network name
         * @param vlanId
         *            The id for the vlan
         */
        public VLanSegment(String type, String physicalNet, int vlanId) {
            this.segmentNetworkType = type;
            this.segmentPhysicalNet = physicalNet;
            this.segmentVlanId = vlanId;
        }

        /**
         * @return the value of physicalNet
         */
        public String getPhysicalNet() {
            return segmentPhysicalNet;
        }

        /**
         * @return the value of type
         */
        public String getType() {
            return segmentNetworkType;
        }

        /**
         * @return the value of vlanId
         */
        public int getVlanId() {
            return segmentVlanId;
        }

        /**
         * @param physicalNet
         *            the value for physicalNet
         */
        public void setPhysicalNet(String physicalNet) {
            this.segmentPhysicalNet = physicalNet;
        }

        /**
         * @param type
         *            the value for type
         */
        public void setType(String type) {
            this.segmentNetworkType = type;
        }

        /**
         * @param vlanId
         *            the value for vlanId
         */
        public void setVlanId(int vlanId) {
            this.segmentVlanId = vlanId;
        }

    }
}
