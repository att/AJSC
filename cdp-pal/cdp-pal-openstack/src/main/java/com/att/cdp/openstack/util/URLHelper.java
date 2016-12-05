/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @since May 14, 2014
 * @version $Id$
 */

public final class URLHelper {

    /**
     * This private default constructor prevents instantiation
     */
    private URLHelper() {

    }

    /**
     * This method is used to translate a URL to indicate the OpenStack service implementation and not include any
     * tenant id or version encoding in the URL. This is used primarily to access the OpenStack service's API to obtain
     * supported versions.
     * 
     * @param url
     *            The URL to be processed
     * @param tenantId
     *            The id of the tenant which may be encoded in the URL and needs to be removed
     * @return The url without any tenant and version, so we can access the service without regard to any specific
     *         tenant
     */
    public static String serviceOnlyURL(String url /* , String tenantId */) {

        if (/* tenantId == null || */url == null) {
            return url;
        }

        /*
         * Use a regular expression to parse the URL and recognize the scheme, ://, host name or ip address, and
         * optional port. Do not recognize anything after the port. Use a capture group (capture group 0 in this case)
         * to extract the recognized portion, which will be the basic URL only less any version, tenant id, or path
         * information.
         */
        Pattern urlPattern = Pattern.compile("^http[s]?://[^:/]+(?::[^/]+)");
        Matcher urlMatcher = urlPattern.matcher(url);
        if (urlMatcher.find()) {
            return urlMatcher.group(0);
        }
        return url;
        //
        // StringBuffer buffer = new StringBuffer(url.trim());
        // int index = buffer.lastIndexOf(tenantId);
        // if (index > -1) {
        // buffer.delete(index, buffer.length());
        // }
        //
        // Pattern pattern = Pattern.compile("v[0-9]+(\\.[0-9]*)?(/)?$", Pattern.CASE_INSENSITIVE);
        // Matcher matcher = pattern.matcher(buffer);
        // if (matcher.find()) {
        // buffer.delete(matcher.start(), buffer.length());
        // }
        //
        // return buffer.toString();
        //
    }
}
