/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is used to represent a supported version of a service by OpenStack and is used to match the reported
 * versions from querying the service and to construct the appropriate URL to access the service as well as supply the
 * package qualifier of the code that is to be dynamically loaded.
 * 
 * @since Jan 16, 2015
 * @version $Id$
 */
public class SupportedVersion {
    private Pattern pattern;
    private String pkgNode;
    private String urlNode;

    /**
     * @param patternString
     *            The Version pattern that is used to match the reported versions from querying the API
     * @param pkgNode
     *            The package node (qualifier) of the java package that contains the class to be loaded.
     * @param urlNode
     *            The URL qualifier (version) to be inserted into the service URL for accessing the service on
     *            OpenStack. This is needed because the service catalog may not expose the version as part of the URL,
     *            so we have to reconstruct the URL from the detected version information and the host information we
     *            obtain from the service catalog.
     */
    public SupportedVersion(String patternString, String pkgNode, String urlNode) {
        this.pkgNode = pkgNode;
        this.urlNode = urlNode;
        pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        SupportedVersion other = (SupportedVersion) obj;
        return pattern.equals(other.pattern) && pkgNode.equals(other.pkgNode) && urlNode.equals(other.urlNode);
    }

    /**
     * Returns the package node to be used to dynamically load the support for this service.
     * 
     * @return The package node
     */
    public String getPackageNode() {
        return pkgNode;
    }

    /**
     * @return The regular expression pattern used to match the version
     */
    public String getPattern() {
        return pattern.pattern();
    }

    /**
     * Returns the URL node used to construct the URL to access this service.
     * 
     * @return The URL node.
     */
    public String getUrlNode() {
        return urlNode;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Returns an indication f the supplied value matches the supported version pattern.
     * 
     * @param value
     *            The value of the version from the api query
     * @return True if the version matches this supported version object, false if not.
     */
    public boolean isMatch(String value) {
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("[%s, %s, %s]", pattern, pkgNode, urlNode);
    }
}
