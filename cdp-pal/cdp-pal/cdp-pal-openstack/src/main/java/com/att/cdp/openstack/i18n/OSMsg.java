/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.i18n;

import com.att.eelf.i18n.EELFResolvableErrorEnum;
import com.att.eelf.i18n.EELFResourceManager;

/**
 * This enumeration is used to manage all messages issued from the OpenStack provider abstraction and uses the same
 * message, description, and resolution resource framework used by the rest of CDP.
 * 
 * @since May 8, 2015
 * @version $Id$
 */
public enum OSMsg implements EELFResolvableErrorEnum {

    /**
     * Unable to authenticate user [{0}] with provider for access to tenant [{1}]
     */
    PAL_OS_FAILED_PROVIDER_AUTHENTICATION,

    /**
     * Not authorized for access to service
     */
    PAL_OS_NOT_AUTHORIZED,

    /**
     * Resource [{0}] with id [{1}] cannot be found on provider [{2}]
     */
    PAL_OS_RESOURCE_NOT_FOUND,

    /**
     * Invalid request
     */
    PAL_OS_INVALID_REQUEST,

    /**
     * Timeout waiting on request
     */
    PAL_OS_REQUEST_TIMED_OUT,

    /**
     * Unexpected exception
     */
    PAL_OS_UNEXPECTED_EXCEPTION,

    /**
     * Network [{0}] cannot be found on provider
     */
    PAL_OS_NETWORK_NOT_FOUND,

    /**
     * Server [{0}], ID [{1}], is in state [{2}] but is expected to be in one of [{3}]
     */
    PAL_OS_INVALID_SERVER_STATE,

    /**
     * Device name [{0}] is invalid or conflicts with an already assigned device
     */
    PAL_OS_INVALID_DEVICE_NAME,

    /**
     * Extension [{0}] is not supported in provider [{1}]
     */
    PAL_OS_UNSUPPORTED_OPERATION,

    /**
     * Resource or extension [{0}] is not available on provider [{1}]
     */
    PAL_OS_RESOURCE_UNAVAILABLE,

    /**
     * Attempt to locate service [{0}] failed
     */
    PAL_OS_LOCATE_SERVICE_FAILED,

    /**
     * Service [{0}] type [{1}], [{2}] endpoints; public = [{3}], internal = [{4}], registered = [{5}]
     */
    PAL_OS_SERVICE_CATALOG,

    /**
     * Exception {0} (with status code {1} = {2}) while processing service request {3}. Original exception message
     * was:\n{4}\nService request state context:\n{5}
     */
    PAL_OS_PROVIDER_EXCEPTION,

    /**
     * No region specified but multiple endpoints were found for service {0} at {1}, please specify a region to select
     * the correct endpoint(s)
     */
    PAL_OS_REGION_REQUIRED,

    /**
     * Region {0} was specified but no endpoints for service {1} were found for this region!
     */
    PAL_OS_REGION_NOT_FOUND,

    /**
     * Failed to resolve versions for service {0} at url {1}, exception {2}, reason {3}
     */
    PAL_OS_SERVICE_CATALOG_FAILURE,

    /**
     * Service catalog discovery found "{0}" service for tenant "{1}" in region "{2}" at public URL "{3}"
     */
    PAL_OS_SERVICE_INSTALLED,

    /**
     * Service {0} URL was overridden with {1} by user properties
     */
    PAL_OS_SERVICE_OVERRIDE,

    /**
     * Service {0} version {1} is supported and will be loaded if referenced
     */
    PAL_OS_SERVICE_VERSION_SUPPORTED,

    /**
     * {0} service api version detection using url {1} generated invalid or unknown response, reverting to default
     * version support for this service.
     */
    PAL_OS_API_VERSION_DETECTION_FAILED,

    /**
     * Connection to provider {0} service {1} at {2} failed, called from {3}:{4}.{5} on thread {6}
     */
    PAL_OS_CONNECTION_FAILED,

    /**
     * An error was returned from the provider attempting to {0}
     */
    PAL_OS_REQUEST_FAILURE,

    /**
     * Attempt to connect to {0} failed with {1} ({2}), attempt {3} of {4}, waiting {5} seconds
     */
    PAL_OS_RETRY_OPENSTACK_CONNECTION,
    
    /**
     * Service {0} version {1} is not supported and the appropriate version will be detected and loaded
     */
    PAL_OS_VERSION_OVERRIDE_FAILED,

    
    /**
     *  Reboot Type [{0}] is invalid 
     */
    PAL_OS_INVALID_REBOOT_TYPE;
    
    /**
     * Static initializer to ensure the resource bundles for this class are loaded...
     */
    static {
        EELFResourceManager.loadMessageBundle("com/att/cdp/openstack/OpenStackMessages");
    }
}
