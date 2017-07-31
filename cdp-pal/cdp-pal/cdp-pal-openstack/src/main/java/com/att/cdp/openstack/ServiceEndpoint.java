/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import com.att.cdp.pal.util.StringHelper;

/**
 * This class represents the definition of a service instance provided by OpenStack and which is registered in the
 * service catalog.
 * 
 * @since Jan 20, 2015
 * @version $Id$
 */
public class ServiceEndpoint {

    /**
     * The publicly-accessible URL to access this service
     */
    private String publicUrl;

    /**
     * The privately-accessible URL to access this service as an administrator
     */
    private String adminUrl;

    /**
     * The privately-accessible URL to access this service
     */
    private String internalUrl;

    /**
     * The region that this service endpoint supports
     */
    private String region;

    /**
     * @param publicUrl
     *            The public URL
     * @param adminUrl
     *            The administration URL
     * @param internalUrl
     *            The internal URL
     * @param region
     *            The region
     */
    public ServiceEndpoint(String publicUrl, String adminUrl, String internalUrl, String region) {
        this.publicUrl = publicUrl;
        this.internalUrl = internalUrl;
        this.adminUrl = adminUrl;
        this.region = region;
    }

    /**
     * @return the value of publicUrl
     */
    public String getPublicUrl() {
        return publicUrl;
    }

    /**
     * @return the value of adminUrl
     */
    public String getAdminUrl() {
        return adminUrl;
    }

    /**
     * @return the value of internalUrl
     */
    public String getInternalUrl() {
        return internalUrl;
    }

    /**
     * @return the value of region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        ServiceEndpoint other = (ServiceEndpoint) obj;
        return StringHelper.equals(getPublicUrl(), other.getPublicUrl())
            && StringHelper.equals(getInternalUrl(), other.getInternalUrl())
            && StringHelper.equals(getAdminUrl(), other.getAdminUrl())
            && StringHelper.equals(getRegion(), other.getRegion());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return String.format("Endpoint: Region %s, public: %s", region, publicUrl);
    }

}
