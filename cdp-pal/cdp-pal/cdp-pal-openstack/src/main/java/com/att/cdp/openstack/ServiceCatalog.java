/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;

import com.att.cdp.pal.configuration.Configuration;
import com.att.cdp.pal.configuration.ConfigurationFactory;
import com.att.cdp.exceptions.NoRegionFoundException;
import com.att.cdp.openstack.i18n.OSMsg;
import com.att.cdp.pal.util.StreamUtility;
import com.att.cdp.zones.ContextFactory;
import com.att.eelf.i18n.EELFResourceManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Access.Service;

/**
 * The service catalog is an in-memory representation of the service catalog returned from the provider.
 * <p>
 * It is used to capture and recognize the installed services, versions, and endpoints that need to be accessed to use
 * the services of this provider, as well as to reconcile the correct version(s) of the abstraction support to be
 * loaded.
 * </p>
 * <p>
 * This is a fairly involved process of obtaining the service catalog from the provider, and for each service, querying
 * that service to see what version(s) are installed. For each stable version (pre-release and deprecated versions are
 * discarded) reported, the catalog also matches the highest reported stable version with the highest version where we
 * have a coded implementation, and dynamically loads the support. So, we will attempt to load the highest version of
 * each api implementation where there is a match between what the provider reports is installed, and what we have
 * coded. For example, if we have both v1 and v2 support available for a specific service, and the api reports that it
 * supports (as stable) v1 and v2, then we will attempt to load and use v2 support (it is the highest supported AND
 * reported version). On the other hand, if the api reports that it has only v1 available, and we support both v1 and
 * v2, we will load and use the v1 support because it is the highest matching version. On the other hand, if the API
 * reports V3, and we have no V3 support available, we will load the default implementation for that api.
 * </p>
 * <p>
 * What's more, the properties used to configure the connection may specify exact URLs to use for any service. If the
 * client has supplied override URLs for any service, these are used and NOT the endpoints reported by the API. The
 * version is still queried from the override URL and used to dynamically load the correct support however.
 * </p>
 * <p>
 * The service catalog returned from the provider is a list of all services installed, and endpoints where that service
 * can be accessed. Endpoints are usually specific to a particular region, and should be used only for support for the
 * indicated region. Each endpoint could also theoretically have different versions of the API installed, although this
 * has not been seen in practice. However, when the context is created, it will be created for a specific region (if
 * multiple regions exist), and only service endpoints for the specified region will be considered.
 * </p>
 * <p>
 * One additional consideration is that the different installations of OpenStack have not been consistent with respect
 * to the specification of the end point URLs published in the service catalog. The API code needs a URL in a very
 * specific format, so the published endpoint URLs are normalized before being saved in the in-memory catalog. We first
 * remove all extra path information from the endpoint URL, then add the specified path and tenant id to the URL as
 * needed. This normalized URL is then retained for our code's use.
 * </p>
 * 
 * @since Jan 20, 2015
 * @version $Id$
 */

public class ServiceCatalog {

    /**
     * Type type constant that defines the compute service
     */
    public static final String OS_COMPUTE_SERVICE_TYPE = "compute"; //$NON-NLS-1$

    /**
     * The name constant that defines the compute service implementation
     */
    public static final String OS_COMPUTE_SERVICE_NAME = "nova"; //$NON-NLS-1$

    /**
     * Type type constant that defines the network service
     */
    public static final String OS_NETWORK_SERVICE_TYPE = "network"; //$NON-NLS-1$

    /**
     * The name constant that defines the network service implementation
     */
    public static final String OS_NETWORK_SERVICE_NAME = "neutron"; //$NON-NLS-1$

    /**
     * Type type constant that defines the image service
     */
    public static final String OS_IMAGE_SERVICE_TYPE = "image"; //$NON-NLS-1$

    /**
     * The name constant that defines the image service implementation
     */
    public static final String OS_IMAGE_SERVICE_NAME = "glance"; //$NON-NLS-1$

    /**
     * Type type constant that defines the volume service
     */
    public static final String OS_VOLUME_SERVICE_TYPE = "volume"; //$NON-NLS-1$

    /**
     * The name constant that defines the volume service implementation
     */
    public static final String OS_VOLUME_SERVICE_NAME = "cinder"; //$NON-NLS-1$

    /**
     * Type type constant that defines the stack service
     */
    public static final String OS_STACK_SERVICE_TYPE = "orchestration"; //$NON-NLS-1$

    /**
     * The name constant that defines the stack service implementation
     */
    public static final String OS_STACK_SERVICE_NAME = "heat"; //$NON-NLS-1$

    /**
     * Type type constant that defines the identity service
     */
    public static final String OS_IDENTITY_SERVICE_TYPE = "identity"; //$NON-NLS-1$

    /**
     * The name constant that defines the identity service implementation
     */
    public static final String OS_IDENTITY_SERVICE_NAME = "keystone"; //$NON-NLS-1$

    /**
     * The constant for version 1 packages
     */
    public static final String V1 = "v1"; //$NON-NLS-1$

    /**
     * The constant for version 2 packages
     */
    public static final String V2 = "v2"; //$NON-NLS-1$

    /**
     * The constant for version 3 packages
     */
    public static final String V3 = "v3"; //$NON-NLS-1$

    /**
     * A constant used when the http protocol is referenced
     */
    public static final String HTTP = "http";  //$NON-NLS-1$

    /**
     * A constant used when the https protocol is referenced
     */
    public static final String HTTPS = "https";  //$NON-NLS-1$

    /**
     * The name of a JSon node which contains the id of the service in the catalog
     */
    public static final String JSON_NODE_ID = "id"; //$NON-NLS-1$

    /**
     * The name of the JSon node that contains the status of the version definition for a specific service
     */
    public static final String JSON_NODE_STATUS = "status"; //$NON-NLS-1$

    /**
     * The name of the JSon node that contains the list of versions of a service
     */
    public static final String JSON_NODE_VERSIONS = "versions"; //$NON-NLS-1$

    /**
     * The name of the JSon node that contains a specific version of a service
     */
    public static final String JSON_NODE_VERSION = "version"; //$NON-NLS-1$

    /**
     * The definition constant for a version that is stable
     */
    public static final String VERSION_STATUS_STABLE = "stable"; //$NON-NLS-1$

    /**
     * The definition constant for a version that is currently supported (active version)
     */
    public static final String VERSION_STATUS_CURRENT = "current"; //$NON-NLS-1$

    /**
     * The definition constant for a version that is supported
     */
    public static final String VERSION_STATUS_SUPPORTED = "supported"; //$NON-NLS-1$

    /**
     * A constant collection of the supported service versions information. This map is organized by the OpenStack
     * service name so that it can be directly processed when reading the service catalog from the identity service.
     * This map is used as a lookup source of information as to what versions of each OpenStack service are currently
     * supported, and supplies the URL version and package version. The resolved version is then used to actually load
     * the requested service version by supplying the package qualification and URL version. From that point forward,
     * only that version of the service is used for the current context. This does not restrict different contexts from
     * loading different versions however.
     */
    public static final Map<String, List<SupportedVersion>> SUPPORTED_MAP;

    /**
     * A map of the OpenStack service types to the abstraction service URL properties. This is needed to detect and use
     * supplied override URLs that may have been set in the connection properties.
     */
    public static final Map<String, String> SERVICE_URL_MAP;

    /**
     * A map of the OpenStack service types to the abstraction service version. This is needed to detect and use
     * supplied override version that may have been set in the connection properties.
     */
    public static final Map<String, String> SERVICE_VERSION_MAP;

    /**
     * An optional list of trusted host name patterns used when the protocol is https and a trusted host names list is
     * provided.
     */
    private List<Pattern> trustedHostPatterns = new ArrayList<>();

    /**
     * The region that we are connecting to, or null
     */
    private String region;

    /**
     * Initialize the data structures used to determine the list of supported versions by service
     */
    static {
        SUPPORTED_MAP = new HashMap<>();
        SERVICE_URL_MAP = new HashMap<>();
        SERVICE_VERSION_MAP = new HashMap<>();

        ArrayList<SupportedVersion> versions = new ArrayList<>();

        versions.add(new SupportedVersion("V2(\\.[0-9]+)?", V2, "v2.0")); //$NON-NLS-1$ //$NON-NLS-2$
        SUPPORTED_MAP.put(OS_IDENTITY_SERVICE_TYPE, versions);

        versions = new ArrayList<>();
        versions.add(new SupportedVersion("V2(\\.[0-9]+)?", V2, "v2")); //$NON-NLS-1$ //$NON-NLS-2$
        SUPPORTED_MAP.put(OS_COMPUTE_SERVICE_TYPE, versions);

        versions = new ArrayList<>();
        versions.add(new SupportedVersion("V2(\\.[0-9]+)?", V2, "v2.0")); //$NON-NLS-1$ //$NON-NLS-2$
        SUPPORTED_MAP.put(OS_NETWORK_SERVICE_TYPE, versions);

        versions = new ArrayList<>();
        versions.add(new SupportedVersion("V2(\\.[0-9]+)?", V2, "v2")); //$NON-NLS-1$ //$NON-NLS-2$
        versions.add(new SupportedVersion("V1(\\.[0-9]+)?", V1, "v1")); //$NON-NLS-1$ //$NON-NLS-2$
        SUPPORTED_MAP.put(OS_IMAGE_SERVICE_TYPE, versions);

        versions = new ArrayList<>();
        versions.add(new SupportedVersion("V2(\\.[0-9]+)?", V2, "v2")); //$NON-NLS-1$ //$NON-NLS-2$
        versions.add(new SupportedVersion("V1(\\.[0-9]+)?", V1, "v1")); //$NON-NLS-1$ //$NON-NLS-2$
        SUPPORTED_MAP.put(OS_VOLUME_SERVICE_TYPE, versions);

        versions = new ArrayList<>();
        versions.add(new SupportedVersion("V1(\\.[0-9]+)?", V1, "v1")); //$NON-NLS-1$ //$NON-NLS-2$
        SUPPORTED_MAP.put(OS_STACK_SERVICE_TYPE, versions);

        SERVICE_URL_MAP.put(OS_COMPUTE_SERVICE_TYPE, ContextFactory.PROPERTY_COMPUTE_URL);
        SERVICE_URL_MAP.put(OS_IDENTITY_SERVICE_TYPE, ContextFactory.PROPERTY_IDENTITY_URL);
        SERVICE_URL_MAP.put(OS_IMAGE_SERVICE_TYPE, ContextFactory.PROPERTY_IMAGE_URL);
        SERVICE_URL_MAP.put(OS_NETWORK_SERVICE_TYPE, ContextFactory.PROPERTY_NETWORK_URL);
        SERVICE_URL_MAP.put(OS_STACK_SERVICE_TYPE, ContextFactory.PROPERTY_STACK_URL);
        SERVICE_URL_MAP.put(OS_VOLUME_SERVICE_TYPE, ContextFactory.PROPERTY_VOLUME_URL);

        SERVICE_VERSION_MAP.put(OS_COMPUTE_SERVICE_TYPE, ContextFactory.PROPERTY_COMPUTE_VERSION);
        SERVICE_VERSION_MAP.put(OS_IDENTITY_SERVICE_TYPE, ContextFactory.PROPERTY_IDENTITY_VERSION);
        SERVICE_VERSION_MAP.put(OS_IMAGE_SERVICE_TYPE, ContextFactory.PROPERTY_IMAGE_VERSION);
        SERVICE_VERSION_MAP.put(OS_NETWORK_SERVICE_TYPE, ContextFactory.PROPERTY_NETWORK_VERSION);
        SERVICE_VERSION_MAP.put(OS_STACK_SERVICE_TYPE, ContextFactory.PROPERTY_STACK_VERSION);
        SERVICE_VERSION_MAP.put(OS_VOLUME_SERVICE_TYPE, ContextFactory.PROPERTY_VOLUME_VERSION);

    }

    /**
     * The resolved version information for each service discovered and which is supported by the abstraction. The
     * resolved version is the actual version, by service, of the implementation code that will be loaded.
     */
    private Map<String, SupportedVersion> resolvedVersionMap;

    /**
     * A map of the resolved services to the re-constructed (normalized) URL to be used to connect to that service.
     */
    private Map<String, String> resolvedUrlMap;

    /**
     * The installed services that we discover. As we process the service catalog, we record the entries in this
     * collection and use that information to query the API for each service to discover the versions supported.
     */
    private Map<String, ServiceEntry> discoveredServices;

    /**
     * A Json object mapper used to convert json responses from OpenStack to object maps
     */
    private ObjectMapper om = new ObjectMapper();

    /**
     * The context that we are servicing. Note that each context will have its own instance of a service catalog.
     * However, the static constant map of supported versions is exactly the same from one context to another. The
     * resolved version map may be different however, because different contexts may connect to different OpenStack
     * installations with different configurations.
     */
    private OpenStackContext context;

    /**
     * The CDP configuration settings
     */
    private Configuration configuration;

    /**
     * The proxy host address, or null if no proxy defined
     */
    private String proxyHost;

    /**
     * The proxy port
     */
    private Integer proxyPort;

    /**
     * The logger to be used
     */
    private Logger logger;

    /**
     * Create the service catalog with an empty set of entries. The entries are registered only when the context is
     * ready to do so. This is because the service catalog is not available to the API until after the user has
     * authenticated.
     * 
     * @param context
     *            The context that we are servicing
     */
    @SuppressWarnings("nls")
    public ServiceCatalog(OpenStackContext context) {
        this.context = context;
        logger = context.getLogger();
        discoveredServices = new HashMap<>();
        resolvedVersionMap = new HashMap<>();
        resolvedUrlMap = new HashMap<>();
        // configuration = ConfigurationFactory.getConfiguration();

        Properties properties = context.getProperties();
        proxyHost = properties.getProperty(ContextFactory.PROPERTY_PROXY_HOST);
        String port = properties.getProperty(ContextFactory.PROPERTY_PROXY_PORT, "8080"); //$NON-NLS-1$

        if (port != null) {
            try {
                proxyPort = Integer.parseInt(port);
                if (proxyPort < 0 || proxyPort > 65535) {
                    proxyPort = 8080;
                }
            } catch (NumberFormatException e) {
                proxyPort = 8080;
            }
        }
        if (proxyHost == null) {
            logger.debug("Connecting without using a proxy");
        } else {
            logger.debug(String.format("Connecting using proxy %s:%d", proxyHost, proxyPort));
        }
    }

    /**
     * Registers all services that are installed on this provider.
     * 
     * @param services
     *            The list of all services that were found installed on this provider
     * @throws NoRegionFoundException
     *             If the endpoints specify multiple regions but no region specification was provided, or if a region
     *             specification was provided but no region matches the name specified.
     */
    public void registerServices(List<Service> services) throws NoRegionFoundException {

        Properties properties = context.getProperties();
        for (Service service : services) {
            String type = service.getType();
            String name = service.getName();
            ServiceEntry entry = new ServiceEntry(name, type);
            discoveredServices.put(type, entry);

            List<Access.Service.Endpoint> endpoints = service.getEndpoints();
            for (Access.Service.Endpoint endpoint : endpoints) {
                String adminUrl = endpoint.getAdminURL();
                String internalUrl = endpoint.getInternalURL();
                String publicUrl = endpoint.getPublicURL();
                logger.info(EELFResourceManager.format(OSMsg.PAL_OS_SERVICE_INSTALLED, type, context.getTenantName(),
                    endpoint.getRegion(), publicUrl));

                /*
                 * If the user supplied a URL to override the service, then replace the one we obtained from the service
                 * catalog with the one the user supplied
                 */
                String property = SERVICE_URL_MAP.get(type);
                if (property != null) {
                    String value = properties.getProperty(property);
                    if (value != null) {
                        publicUrl = value;
                        logger.info(EELFResourceManager.format(OSMsg.PAL_OS_SERVICE_OVERRIDE, type, publicUrl));
                    }
                }

                String region = endpoint.getRegion();
                ServiceEndpoint serviceEndpoint = new ServiceEndpoint(publicUrl, adminUrl, internalUrl, region);
                entry.addEndpoint(serviceEndpoint);
            }
        }

        /*
         * If the user supplied a region, then we use that to filter only the endpoints for the specified region. If
         * they did not supply a region, and there is only one endpoint per service, then we just use that endpoint
         * (even if it does specify a region). If no region was specified, and there are multiple endpoints for
         * different regions, then it is an error.
         */
        region = properties.getProperty(ContextFactory.PROPERTY_REGION);

        /*
         * After the services have been discovered, we then need to resolve all of the versions that are installed and
         * reconcile that with our support, choosing the highest level of support that will work with the versions
         * installed.
         */
        servicesLoop: for (Map.Entry<String, ServiceEntry> discoveredServicesEntry : discoveredServices.entrySet()) {
            String type = discoveredServicesEntry.getKey();
            ServiceEntry service = discoveredServicesEntry.getValue();

            /*
             * Make sure that we support the discovered service. If we don't, the map will return null for the service
             * name. In that case, skip the discovered service because were not interested in it.
             */
            List<SupportedVersion> supportedVersions = SUPPORTED_MAP.get(type);
            if (supportedVersions != null) {
                List<ServiceEndpoint> endpoints = service.getEndpoints();
                if (endpoints.size() > 1 && region == null) {
                    if (!Boolean.parseBoolean(context.getProperties().getProperty(
                        OpenStackContext.ALLOW_UNKNOWN_REGION, "false"))) {
                        String msg =
                            EELFResourceManager.format(OSMsg.PAL_OS_REGION_REQUIRED, type, endpoints.toString());
                        throw new NoRegionFoundException(msg);
                    }
                }

                boolean found = false;
                for (ServiceEndpoint serviceEndpoint : service.getEndpoints()) {
                    if (region == null) {
                        processEndpoint(type, serviceEndpoint, supportedVersions);
                        found = true;
                        continue servicesLoop;
                    }
                    if (region.equals(serviceEndpoint.getRegion())) {
                        processEndpoint(type, serviceEndpoint, supportedVersions);
                        found = true;
                        continue servicesLoop;
                    }
                }

                /*
                 * If we get here it is because we couldn't match the region name that the user specified. In that case,
                 * we throw an error.
                 */
                if (!found) {
                    String msg = EELFResourceManager.format(OSMsg.PAL_OS_REGION_NOT_FOUND, region, type);
                    throw new NoRegionFoundException(msg);
                }
            }
        }
    }

    /**
     * Indicates if the specified service type is available for use. This means that the service must be installed, and
     * we must provide support for it. If both of these conditions are true, then the service is available for use and
     * the methods {@link #resolveServiceURL(String)} and {@link #resolvePackageNode(String)} can be called
     * successfully.
     * 
     * @param serviceType
     *            The type of service we are checking
     * @return True if the service is available for use.
     */
    public boolean isServiceAvailable(String serviceType) {
        return resolvedVersionMap.containsKey(serviceType);
    }

    /**
     * Returns the resolved URL for the specified service type.
     * 
     * @param serviceType
     *            The type of service to be called
     * @return The URL to access that service.
     */
    public String resolveServiceURL(String serviceType) {
        String url = resolvedUrlMap.get(serviceType);
        if (url == null) {
            String property = SERVICE_URL_MAP.get(serviceType);
            if (property != null) {
                SupportedVersion version = resolvedVersionMap.get(serviceType);
                url = context.getProperties().getProperty(property);
                if (url != null) {
                    url = rewriteURL(url, version.getUrlNode());
                    resolvedUrlMap.put(serviceType, url);
                }
            }
        }
        return url;
    }

    /**
     * This method returns the appropriate package node to be used to load the correct support for the specified
     * service. The package node is substituted into the class package name to load the correct class, such as
     * "com.att.cdp.openstack.%s.ComputeService", where the "%s" is replaced with the version package node.
     * 
     * @param serviceType
     *            The type of service to be resolved
     * @return The supported package node, or a null string if not supported.
     */
    public String resolvePackageNode(String serviceType) {
        SupportedVersion version = resolvedVersionMap.get(serviceType);

        if (version == null) {
            List<SupportedVersion> list = SUPPORTED_MAP.get(serviceType);
            if (list != null) {
                version = list.get(list.size() - 1);
                resolvedVersionMap.put(serviceType, version);
            }
        }
        if (version == null) {
            return null;
        }
        return version.getPackageNode();
    }

    /**
     * This method returns the map of service types to URL's that have been resolved for the highest supported version
     * of each api. There will be only one URL for each service, and there will be only services that are supported, not
     * the entire list of discovered services.
     * 
     * @return The map of URL's to each service where the key is the service type, and the value is the URL. These URLs
     *         are to the highest supported version of the API.
     */
    public Map<String, String> getResolvedURLMap() {
        return resolvedUrlMap;
    }

    /**
     * Returns the map of all supported service types to the highest supported version of that API.
     * 
     * @return The map of supported (resolved) services and the highest version of the API that we support.
     */
    public Map<String, SupportedVersion> getResolvedVersionMap() {
        return resolvedVersionMap;
    }

    /**
     * This method uses the Apache HttpComponents library to process the service catalog. The previous versions were
     * using the Jersey library, but the way they handle 300 response status codes was preventing us from reading the
     * entity (i.e., the service versions).
     * 
     * @param type
     *            Service type
     * @param serviceEndpoint
     *            The endpoint to be contacted
     * @param supportedVersions
     *            The list of versions we support
     * @return True if the endpoint was reachable and the version information was resolved, false if there were any
     *         issues in reaching the endpoint.
     */
    private boolean processEndpoint(String type, ServiceEndpoint serviceEndpoint,
        List<SupportedVersion> supportedVersions) {

        boolean result = false;
        Logger logger = context.getLogger();
        
        logger.info(new Date().toString()+" PAL-TEST-5555: ServiceCatalog.processEndpoint: type="+type);
        /*
         * DH: Check if the "use internal" property is set and use the internal URL if so, or the public URL if not.
         */
        boolean useInternal =
            Boolean.parseBoolean(context.getProperties()
                .getProperty(ContextFactory.PROPERTY_USE_INTERNAL_CONNECTION, Boolean.FALSE.toString()).trim());
        String serviceUrl = useInternal ? serviceEndpoint.getInternalUrl() : serviceEndpoint.getPublicUrl();
        logger.info(new Date().toString()+" PAL-TEST-5555: ServiceCatalog.processEndpoint: serviceUrl="+serviceUrl);

        /*
         * Check is service version is overridden
         */
        String property = SERVICE_VERSION_MAP.get(type);
        logger.info(new Date().toString()+" PAL-TEST-5555: ServiceCatalog.processEndpoint: property="+property);
        if (property != null) {
            String userDefinedVersion = context.getProperties().getProperty(property);
            logger.info(new Date().toString()+" PAL-TEST-5555: ServiceCatalog.processEndpoint: userDefinedVersion="+userDefinedVersion);
            if (userDefinedVersion != null && !userDefinedVersion.isEmpty()) {
               
                for (SupportedVersion entry : supportedVersions) {
                	logger.info(new Date().toString()+" PAL-TEST-5555: ServiceCatalog.processEndpoint: supportedVersion="+entry.toString());
                    if (entry.isMatch(userDefinedVersion)) {
                    	logger.info(new Date().toString()+" PAL-TEST-5555: ServiceCatalog.processEndpoint: serviceUrl="+serviceUrl+" | urlnode="+entry.getUrlNode());
                        resolvedUrlMap.put(type, rewriteURL(serviceUrl, entry.getUrlNode()));
                        resolvedVersionMap.put(type, entry);
                        logger.info(EELFResourceManager.format(OSMsg.PAL_OS_SERVICE_VERSION_SUPPORTED, type,
                            entry.getPackageNode()));
                        logger.info(new Date().toString()+" PAL-TEST-5555: returning true");
                        return true;
                    }

                }

                logger.warn(EELFResourceManager.format(OSMsg.PAL_OS_VERSION_OVERRIDE_FAILED, type, userDefinedVersion));
            }
        }

        logger.info(new Date().toString()+" PAL-TEST-5555: ServiceCatalog.processEndpoint: property is null");
        try {
            URL url = new URL(serviceUrl);
            logger.info(new Date().toString()+" PAL-TEST-5555: url="+url.toString());
            

            HttpClientBuilder clientBuilder = HttpClients.custom();
            if (url.getProtocol().equalsIgnoreCase(HTTPS)) {
                processTrustedHostsList();
                SSLConnectionSocketFactory sslsf =
                    new SSLConnectionSocketFactory(getSSLContext(), getHostnameVerifier());
                clientBuilder.setSSLSocketFactory(sslsf);
            }

            if (proxyHost != null) {
                if (proxyPort == 0) {
                    proxyPort = 8080;
                }
                HttpHost proxy = new HttpHost(proxyHost, proxyPort, HTTP);
                clientBuilder.setProxy(proxy);
            }

            HttpClient client = clientBuilder.build();
            HttpHost target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
            RequestConfig.Builder builder = RequestConfig.custom();
            builder.setConnectionRequestTimeout(30000);
            builder.setConnectTimeout(30000);
            builder.setRedirectsEnabled(true);
            RequestConfig rc = builder.build();

            HttpGet request = new HttpGet();
            request.setConfig(rc);

            HttpResponse response = null;
            HttpEntity entity = null;
            int attempts = 0;
            while (attempts < context.getRetryLimit()) {
                try {
                    response = client.execute(target, request);
                    entity = response.getEntity();
                    break;
                } catch (Throwable e) {
                    logger.error(EELFResourceManager.format(OSMsg.PAL_OS_RETRY_OPENSTACK_CONNECTION,
                        url.toExternalForm(), e.getClass().getSimpleName(), e.getMessage(),
                        Integer.toString(attempts + 1), Integer.toString(context.getRetryLimit()),
                        Integer.toString(context.getRetryDelay())));
                    try {
                        Thread.sleep(context.getRetryDelay() * 1000L);
                    } catch (InterruptedException ex) {
                        // ignore
                    }
                    attempts++;
                }
            }

            if (response != null && response.getStatusLine() != null) {
                int status = response.getStatusLine().getStatusCode();

                try (InputStream stream = entity.getContent()) {
                    switch (status) {
                        case HttpStatus.SC_MULTIPLE_CHOICES:
                        case HttpStatus.SC_OK:
                            List<String> versions = getVersionsFromResponse(type, serviceUrl, stream);
                            logger.info(new Date().toString()+" PAL-TEST-5555: SC-OK :ServiceCatalog.processEndpoint: All versions ="+versions);
                            for (SupportedVersion entry : supportedVersions) {
                            	logger.info(new Date().toString()+" PAL-TEST-5555: SC-OK :ServiceCatalog.processEndpoint: supportedVersion ="+entry.toString());
                                for (String version : versions) {
                                    if (entry.isMatch(version)) {
                                    	logger.info(new Date().toString()+" PAL-TEST-5555: SC-OK : ServiceCatalog.processEndpoint: found a match with version ="+version);
                                        resolvedUrlMap.put(type, rewriteURL(serviceUrl, entry.getUrlNode()));
                                        resolvedVersionMap.put(type, entry);
                                        logger.info(EELFResourceManager.format(OSMsg.PAL_OS_SERVICE_VERSION_SUPPORTED,
                                            type, entry.getPackageNode()));
                                        logger.info(new Date().toString()+" PAL-TEST-5555: SC-OK : Returning True");
                                        return true;
                                    }
                                }
                            }
                        default:
                        	logger.info(new Date().toString()+" PAL-TEST-5555: default : selecting default version");
                        	selectDefaultVersion(type, serviceUrl, supportedVersions);
                            logger.info(EELFResourceManager.format(OSMsg.PAL_OS_SERVICE_VERSION_SUPPORTED, type,
                            resolvedVersionMap.get(type).getPackageNode()));
                            logger.info(new Date().toString()+" PAL-TEST-5555: default : returning true");
                            return true;
                    }
                } catch (IOException e) {
                    selectDefaultVersion(type, serviceUrl, supportedVersions);
                }
            } else {
                selectDefaultVersion(type, serviceUrl, supportedVersions);
            }
        } catch (IOException e) {
            String msg =
                EELFResourceManager.format(OSMsg.PAL_OS_SERVICE_CATALOG_FAILURE, type, serviceUrl, e.getClass()
                    .getSimpleName(), e.getMessage());
            context.getLogger().error(msg);
        }
        return result;
    }

    /**
     * This method is used to process the trusted hosts list property the first time it is needed, and to cache the
     * results.
     * <p>
     * If the protocol is SSL, then this code allows for the specification of a trusted hosts list rather than a
     * certificate. It uses the trusted hosts list to accept connections to any of the hosts specified regardless of the
     * certificate used, if any. The hosts may be specified by ip address or name, or may use a regular expression to
     * match the host. However, if an IP address is used, no name resolution is performed, and if a name is used, no
     * conversion to an ip address is performed. This means that however the host is specified in the list must be the
     * same way the host is accessed.
     * </p>
     * <p>
     * The trusted hosts list is a list of name/ip/patterns separated by commas. Any number of entries may be specified.
     * The pattern meta-characters that are supported are the following:
     * <dl>
     * <dt>*</dt>
     * <dd>Matches any number of characters, including zero</dd>
     * <dt>+</dt>
     * <dd>Matches any single character, but must match exactly one.</dd>
     * </dl>
     * </p>
     * <p>
     * Other meta-characters may be used, as long as you do not use period, asterisk, or plus. The presence of a period
     * in the name is interpreted literally and is escaped as a period in the resulting pattern. Asterisk and plus are
     * always interpreted as above. Use of character classes ([abc]) and capturing groups does work. Capturing groups
     * cannot be referenced except within the same pattern and are normally just treated as a regular non-capturing
     * group portion of the pattern.
     * </p>
     * <h2>Examples</h2>
     * <ul>
     * <li>192.*,168.*,txcdtl++++++++.itservices.att.com</li>
     * </ul>
     */
    private void processTrustedHostsList() {
        /*
         * Process the trusted hosts list if provided. In this case, we convert each entry in the comma-delimited list
         * to a regular expression pattern and cache the patterns. We will use the patterns in the host name verifier to
         * determine if the host is trusted or not.
         */
        String temp = context.getProperties().getProperty(ContextFactory.PROPERTY_TRUSTED_HOSTS);
        if (temp != null && temp.length() > 0 && trustedHostPatterns.isEmpty()) {
            String[] tokens = temp.split(","); //$NON-NLS-1$
            for (String token : tokens) {
                if (token != null && token.length() > 0) {
                    StringBuffer buffer = new StringBuffer(token);
                    for (int index = 0; index < buffer.length(); index++) {
                        switch (buffer.charAt(index)) {
                        // Convert * to .*
                            case '*':
                                buffer.insert(index, "."); //$NON-NLS-1$
                                index++;
                                break;
                            // Convert + to .+
                            case '+':
                                buffer.insert(index, "."); //$NON-NLS-1$
                                index++;
                                break;
                            // Convert . to \. (escaped period)
                            case '.':
                                buffer.replace(index, index + 1, "\\."); //$NON-NLS-1$
                                index++;
                                break;
                        }
                    }

                    trustedHostPatterns.add(Pattern.compile(buffer.toString()));
                }
            }
        }
    }

    /**
     * Selects the default (last) entry from the list of supported entries and sets it as the resolved version
     * 
     * @param type
     *            The type of the service
     * @param url
     *            The base url to connect to the service
     * @param supportedVersions
     *            The list of supported versions for this service.
     */
    public void selectDefaultVersion(String type, String url, List<SupportedVersion> supportedVersions) {
        int lastEntry = supportedVersions.size() - 1;
        SupportedVersion defaultVersion = supportedVersions.get(lastEntry);
        resolvedUrlMap.put(type, rewriteURL(url, defaultVersion.getUrlNode()));
        resolvedVersionMap.put(type, defaultVersion);
    }

    /**
     * This method strips the URL down to just the protocol and host (+ port if present) connection-only specification,
     * then adds back the version node that identifies the service api. This is required because the service catalog
     * URL's do not have to match the required format for the API, which always includes the version node as part of the
     * path. Therefore, we cannot rely on the service catalog URL for anything more than protocol, host, and port. We
     * will correct the URL if needed by adding the api version (obtained here) and later the tenant ID.
     * 
     * @param url
     *            The url to be re-written
     * @param versionNode
     *            The version node to be used
     * @return The rewritten URL
     */
    public String rewriteURL(String url, String versionNode) {
        return String.format("%s/%s", connectionOnly(url), versionNode); //$NON-NLS-1$
    }

    /**
     * Returns the map of installed services published by this provider.
     * 
     * @return The installed services.
     */
    public Map<String, ServiceEntry> getServices() {
        return discoveredServices;
    }

    /**
     * Returns the map of all supported versions
     * 
     * @return The map of all supported versions
     */
    public Map<String, List<SupportedVersion>> getSupportedVersionsMap() {
        return SUPPORTED_MAP;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        ServiceCatalog other = (ServiceCatalog) obj;
        return discoveredServices.equals(other.getServices());
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return discoveredServices.hashCode();
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(String.format("Service Catalog (%d services):\n", discoveredServices.size()));
        for (Map.Entry<String, ServiceEntry> entry : discoveredServices.entrySet()) {
            ServiceEntry service = entry.getValue();
            List<ServiceEndpoint> endpoints = service.getEndpoints();
            buffer.append(String.format("\t%s [%s] %d endpoints\n", service.getType(), service.getName(),
                endpoints.size()));
            for (ServiceEndpoint endpoint : endpoints) {
                buffer.append(String.format("\t\tEndpoint: Region %s, PublicURL %s\n", endpoint.getRegion(),
                    endpoint.getPublicUrl()));
            }
        }
        return buffer.toString();
    }

    /**
     * This method strips all extraneous encoding from a URL to represent the connection only. All extra path
     * information, encoded values and query parameters are removed.
     * 
     * @param url
     *            The URL to be processed
     * @return The url without any tenant and version, so we can access the service without regard to any specific
     *         tenant
     */
    public static String connectionOnly(String url) {
        if (url == null) {
            return url;
        }
        
        // First try to match on the last occurrence of a API version
		String versionRegx= "(.+?(?=/v[0-9]+))+";
		Pattern versionPattern =Pattern.compile(versionRegx, Pattern.CASE_INSENSITIVE);
		Matcher versionMatcher = versionPattern.matcher(url.trim());
				
		if(versionMatcher.find()) {
			return versionMatcher.group(0);
		}
		
		// Otherwise parse to protocol host and port
		String regex = "(http(s)?://[a-z0-9_.\\-]+(:[0-9]+)?)(/.*)?"; //$NON-NLS-1$
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return url.trim();
    }

    /**
     * Gets a list of the current and/or supported and/or stable api versions from the json object stream and returns
     * the list.
     * <p>
     * This method can handle a json object that represents a list of possible versions as well as the singular version
     * response. All versions that are supported, current, or stable are returned. Any versions that are experimental or
     * obsolete are omitted from the return response.
     * </p>
     * 
     * @param type
     *            The service type we are processing
     * @param url
     *            the URL we have contacted to process the response
     * @param stream
     *            The Json stream from the api
     * @return The list of current and supported versions
     */
    private List<String> getVersionsFromResponse(String type, String url, InputStream stream) {
        ArrayList<String> versions = new ArrayList<>();

        try {
            String content = StreamUtility.getStringFromInputStream(stream);
            JsonNode root = om.readTree(content);
            JsonNode versionsNode = root.get(JSON_NODE_VERSIONS);
            JsonNode versionNode = root.get(JSON_NODE_VERSION);

            if (versionsNode != null) {
                Iterator<JsonNode> it = versionsNode.elements();
                while (it.hasNext()) {
                    JsonNode element = it.next();
                    checkVersionId(element, versions);
                }

            } else if (versionNode != null) {
                checkVersionId(versionNode, versions);
            }
        } catch (IOException | NoSuchElementException e) {
            context.getLogger().error(
                EELFResourceManager.format(OSMsg.PAL_OS_API_VERSION_DETECTION_FAILED, e, type, url));
        }
        return versions;
    }

    /**
     * Checks the current Json node to see if it is a supported, stable, or current version reported
     * 
     * @param node
     *            The node in the versions response from the API
     * @param versions
     *            The list of supported, stable, or current versions
     */
    private static void checkVersionId(JsonNode node, List<String> versions) {
        if (node.isArray()) {
            for (int index = 0; index < node.size(); index++) {
                JsonNode entry = node.get(index);
                processVersionId(entry, versions);
            }
        } else {
            processVersionId(node, versions);
        }
    }

    /**
     * This method examines the list of versions defined for a specified service by examining the JsonNode that was
     * returned from the service catalog, extracts all versions that are listed as "stable", "current", or "supported",
     * and returns them in the list supplied.
     * 
     * @param node
     *            The Json node that defines the service versions in the returned response
     * @param versions
     *            The list of all stable, current, and supported versions. Experimental and other version types are
     *            ignored.
     */
    private static void processVersionId(JsonNode node, List<String> versions) {
        JsonNode idNode = node.get(JSON_NODE_ID);
        JsonNode statusNode = node.get(JSON_NODE_STATUS);
        String value = statusNode.asText();
        if (value.equalsIgnoreCase(VERSION_STATUS_STABLE) || value.equalsIgnoreCase(VERSION_STATUS_CURRENT)
            || value.equalsIgnoreCase(VERSION_STATUS_SUPPORTED)) {
            versions.add(idNode.asText());
        }
    }

    /**
     * This host name verifier is used if the protocol is HTTPS AND a trusted hosts list has been provided. Otherwise,
     * the default behavior is used (requiring valid, unexpired certificates).
     * 
     * @return A host-name verifier that verifies the hosts based on the trusted hosts list. Note, the trusted hosts
     *         list can include wild-card characters * and +.
     */
    private HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {

            @Override
            public boolean verify(String hostName, SSLSession arg1) {
                for (Pattern trustedHostPattern : trustedHostPatterns) {
                    if (trustedHostPattern.matcher(hostName).matches()) {
                        return true;
                    }
                }
                return false;
            }
        };
    }

    /**
     * This SSLContext is used only when the protocol is HTTPS AND a trusted hosts list has been provided. In that case,
     * this SSLContext accepts all certificates, whether they are expired or not, and regardless of the CA that issued
     * them.
     * 
     * @return An SSL context that does not validate certificates
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static SSLContext getSSLContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLSv1.2"); //$NON-NLS-1$
            sslContext.init(null, new TrustManager[] {
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // nop
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // nop
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                }
            }, new java.security.SecureRandom());
        } catch (Exception e) {
            // ignore
        }
        return sslContext;
    }

    /**
     * @return The region that was selected, or an empty string if no region applies (all endpoints are for the same
     *         region and the user did not specify a region).
     */
    public String getRegion() {
        return region == null ? "" : region;
    }
}
