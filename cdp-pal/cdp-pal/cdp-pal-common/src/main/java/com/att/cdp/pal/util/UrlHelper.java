/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is a general purpose helper class used to manipulate strings representing URI/URL resources.
 * <p>
 * This class can be used to parse, extract, or build URL's from various other parts. This class is designed to not
 * throw exceptions as much as possible, but rather to detect and notify the caller of any errors. Unlike the
 * {@link URL} class which throws an exception if anything is wrong, this class will allow the caller to detect and
 * correct any specific part(s) that may be invalid.
 * </p>
 *
 * @since Sep 28, 2016
 * @version $Id$
 */

public class UrlHelper {
    // URL = protocol :// host [: port] [/ path] [? query]
    private static final String PROTOCOL_REGEX = "(\\p{Alpha}+)";
    private static final String HOST_REGEX = "([^:/\\?]+)";
    private static final String PORT_REGEX = "(?::([0-9]+))?";
    private static final String PATH_REGEX = "(?:/([^\\?]*))?";
    private static final String QUERY_REGEX = "(?:\\?(.*))?";
    private static final String REGEX = PROTOCOL_REGEX + "://" + HOST_REGEX + PORT_REGEX + PATH_REGEX + QUERY_REGEX;

    /**
     * The known protocols that we can validate against. Note, this is not a comprehensive list, it only contains the
     * protocols that **may** be of interest to CDP now or in the future.
     */
    private static final String[] KNOWN_PROTOCOLS = {
        "cvs", "file", "ftp", "http", "https", "jar", "ldap", "ldaps", "mailto", "mvn", "nfs", "nntp", "oid", "pop",
        "rmi", "sftp", "shttp", "sip", "sips", "smb", "smtp", "snmp", "ssh", "svn", "tftp", "udp", "vnc"
    };

    private String protocol;
    private String host;
    private int port;
    private String[] path;
    private Map<String, String> query;

    /**
     * Default no-arg constructor
     */
    public UrlHelper() {

    }

    /**
     * Constructor that takes a string as the initial value
     * 
     * @param url
     *            The url string to be parsed into the helper object
     */
    public UrlHelper(String url) {
        parse(url);
    }

    /**
     * Parses a URL and extracts all of the constituent parts from it.
     * 
     * @param url
     *            The URL string to be parsed
     * @return true if the URI/URL is valid, false otherwise
     */
    public boolean parse(String url) {
        boolean result = false;
        if (url != null) {
            Pattern pattern = Pattern.compile(REGEX);
            Matcher matcher = pattern.matcher(url.trim());
            if (matcher.matches()) {
                result = true;
                setProtocol(matcher.group(1));
                setHost(matcher.group(2));
                if (matcher.group(3) == null) {
                    setPort(0);
                } else {
                    setPort(Integer.parseInt(matcher.group(3)));
                }
                String pathString = matcher.group(4);
                setPath(parsePathString(pathString));
                String queryString = matcher.group(5);
                if (queryString == null || queryString.length() == 0) {
                    query = null;
                } else {
                    String[] tokens = queryString.split("&");
                    query = new HashMap<>(tokens.length);
                    for (String token : tokens) {
                        String[] parts = token.split("=", 2);
                        if (parts != null && parts.length != 0) {
                            String name = parts[0].trim();
                            String value = parts.length == 1 ? "" : parts[1];

                            query.put(name, value);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * @return the value of protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     *            the value for protocol
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return True if the protocol of the URL string is a known protocol and supported. Note that false may not mean it
     *         is not a valid protocol, just not one this class recognizes.
     */
    public boolean isProtocolKnown() {
        if (protocol != null) {
            for (String knownProtocol : KNOWN_PROTOCOLS) {
                if (knownProtocol.equalsIgnoreCase(protocol)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @return the value of host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host
     *            the value for host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the value of port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port
     *            the value for port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return True if the port number is a valid port number, in the range 0-65535 inclusive.
     */
    public boolean isPortValid() {
        return port >= 0 && port <= 65535;
    }

    /**
     * @return True if the port is a well-known port (within the first 1024 port number range, or 0-1023 inclusive).
     */
    public boolean isPortWellKnown() {
        return port >= 0 && port <= 1023;
    }

    /**
     * @return the value of path
     */
    public String[] getPath() {
        return path;
    }

    /**
     * @param path
     *            A delimited path (/) that can be broken up into segments
     */
    public void setPath(String path) {
        setPath(parsePathString(path));
    }

    /**
     * @param path
     *            the value for path
     */
    public void setPath(String[] path) {
        this.path = path;
    }

    /**
     * @return the value of query
     */
    public Map<String, String> getQuery() {
        return query;
    }

    /**
     * @param query
     *            the value for query
     */
    public void setQuery(Map<String, String> query) {
        this.query = query;
    }

    /**
     * Formats the protocol, host, and port portions of the URL only. Basically, supplies only the part of the URL that
     * represents the basic connection to the host.
     * 
     * @return The protocol, host, and optionally port portion of the URL
     */
    public String hostOnly() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(protocol);
        buffer.append("://");
        buffer.append(host);
        if (port != 0) {
            buffer.append(":");
            buffer.append(port);
        }

        return buffer.toString();
    }

    /**
     * Formats the full URL component parts as a String and returns it to the caller
     * 
     * @return The full URL as a String
     */
    public String asString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(hostOnly());
        if (path != null && path.length > 0) {
            for (String element : path) {
                buffer.append("/");
                buffer.append(element.trim());
            }
        }
        if (query != null && !query.isEmpty()) {
            boolean first = true;
            for (Map.Entry<String, String> entry : query.entrySet()) {
                if (first) {
                    buffer.append("?");
                    first = false;
                } else {
                    buffer.append("&");
                }
                buffer.append(entry.getKey());
                buffer.append("=");
                buffer.append(entry.getValue().trim());
            }
        }
        return buffer.toString();
    }

    /**
     * @return The parsed and manipulated url representation as an actual URL object
     * @throws MalformedURLException
     *             If the URL string is not valid
     */
    public URL asURL() throws MalformedURLException {
        return new URL(asString());
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        UrlHelper other = (UrlHelper) obj;
        boolean result = true;  // assume equal

        if (protocol != null && other.protocol != null) {
            result = protocol.equals(other.protocol);
        } else {
            if (protocol != null || other.protocol != null) {
                result = false;
            }
        }

        /*
         * Only do this comparison if the previous comparison(s) are all true. If any one results in a false (unequal),
         * then there is no need to check further.
         */
        if (result) {
            if (host != null && other.host != null) {
                result = host.equals(other.host);
            } else {
                if (host != null || other.host != null) {
                    result = false;
                }
            }
        }

        /*
         * Only do this comparison if the previous comparison(s) are all true. If any one results in a false (unequal),
         * then there is no need to check further.
         */
        if (result) {
            result = port == other.port;
        }

        /*
         * Only do this comparison if the previous comparison(s) are all true. If any one results in a false (unequal),
         * then there is no need to check further.
         */
        if (result) {
            if (path != null && other.path != null) {
                result = Arrays.equals(path, other.path);
            } else {
                if (path != null || other.path != null) {
                    result = false;
                }
            }
        }

        /*
         * Only do this comparison if the previous comparison(s) are all true. If any one results in a false (unequal),
         * then there is no need to check further.
         */
        if (result) {
            if (query != null && other.query != null) {
                result = query.equals(other.query);
            } else {
                if (query != null || other.query != null) {
                    result = false;
                }
            }
        }

        return result;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return asString();
    }

    /**
     * @param pathString
     * @return
     */
    private String[] parsePathString(String pathString) {
        String[] result = null;
        if (pathString != null && pathString.length() != 0) {
            StringBuffer buffer = new StringBuffer(pathString);
            if (pathString.startsWith("/")) {
                buffer.delete(0, 1);
            }
            if (pathString.endsWith("/")) {
                buffer.delete(buffer.length() - 1, buffer.length());
            }
            result = buffer.toString().trim().split("/");
            for (int index = 0; index < result.length; index++) {
                result[index] = result[index].trim();
            }
        }

        return result;
    }

}
