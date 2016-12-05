/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.

 *******************************************************************************/

package com.att.cdp.pal.i18n;

import com.att.eelf.i18n.EELFResolvableErrorEnum;
import com.att.eelf.i18n.EELFResourceManager;

/**
 * @since Nov 6, 2015
 * @version $Id$
 */

public enum Msg implements EELFResolvableErrorEnum {

    /**
     * Cloud Delivery Platform (CDP) initialization started at {0}
     */
    CONFIGURATION_STARTED,

    /**
     * Cloud Delivery Platform (CDP) initialization started at {0}
     */
    CONFIGURATION_CLEARED,

    /**
     * Configuration defaults loaded from resource file "{0}"
     */
    LOADING_DEFAULTS,

    /**
     * Property "{0}" ="{1}"
     */
    PROPERTY_VALUE,

    /**
     * Configuration defaults loaded from resource file "{0}"
     */
    NO_DEFAULTS_FOUND,

    /**
     * Searching path "{0}" for configuration settings "{1}";
     */
    SEARCHING_CONFIGURATION_OVERRIDES,

    /**
     * Loading configuration properties from file "{0}"
     */
    LOADING_CONFIGURATION_OVERRIDES,

    /**
     * No configuration file named [{0}] was found on the configuration search path [{1}]. If a configuration file
     * should have been loaded, check the file name and search path specified. CDP will proceed using the default values
     * and command-line overrides (if any).
     */
    NO_OVERRIDE_PROPERTY_FILE_LOADED,

    /**
     * Loading application-specific override properties
     */
    LOADING_APPLICATION_OVERRIDES,

    /**
     * No application-specific override properties were provided!
     */
    NO_APPLICATION_OVERRIDES,

    /**
     * Merging system properties into configuration
     */
    MERGING_SYSTEM_PROPERTIES,

    /**
     * Setting property "{0}={1}" in system properties
     */
    SETTING_SPECIAL_PROPERTY,

    /**
     * Loading resource bundle "{0}"
     */
    LOADING_RESOURCE_BUNDLE,

    /**
     * Logging has already been initialized, check the container logging definitions to ensure they represent your
     * desired logging configuration.
     */
    LOGGING_ALREADY_INITIALIZED,

    /**
     * Searching path "{0}" for log configuration file "{1}"
     */
    MESSAGE_SEARCHING_LOG_CONFIGURATION,

    /**
     * Loading default logging configuration from system resource file "{0}"
     */
    LOADING_DEFAULT_LOG_CONFIGURATION,

    /**
     * No log configuration could be found or defaulted!
     */
    NO_LOG_CONFIGURATION,

    /**
     * An unsupported logging framework is bound to SLF4J. Only Logback or Log4J are supported.
     */
    UNSUPPORTED_LOGGING_FRAMEWORK,

    /**
     * Loading logging configuration from file "{0}"
     */
    LOADING_LOG_CONFIGURATION,

    /**
     * Argument {0} may not be null!
     */
    NULL_ARGUMENT,

    /**
     * Invalid request, argument {0} is {1}
     */
    INVALID_ARGUMENT_FROM_CLIENT,

    /**
     * Not authenticated with provider {0}
     */
    NOT_LOGGED_INTO_PROVIDER,

    /**
     * Provider {0} context is closed.
     */
    PROVIDER_CONTEXT_IS_CLOSED,

    /**
     * Context created for provider {0}
     */
    ZONE_CONTEXT_CREATED,

    /**
     * No implementation for provider {0} was found! Check classpath to ensure that
     * META-INF/services/com.att.cdp.zones.Provider specifies a fully-qualified class name that implements a Provider
     * and can be loaded!
     */
    ZONE_PROVIDER_NOT_FOUND,

    /**
     * Context closed for provider {0} accessing tenant {1}
     */
    ZONE_CONTEXT_CLOSED,

    /**
     * Unable to load service {0} for provider {1} with version {2}
     */
    LOAD_ZONE_SERVICE_FAILED,

    /**
     * No {0} service is available for provider {1}
     */
    NO_PROVIDER_SERVICE,

    /**
     * Principal {0} is invalid for login to provider {1}
     */
    INVALID_PRINCIPAL,

    /**
     * Credentials are invalid for login to provider {0}
     */
    INVALID_CREDENTIAL,

    /**
     * User {0} has logged in to provider {1}
     */
    PROVIDER_LOGIN,

    /**
     * One or more required properties have not been specified to configure provider {0}. The list of missing properties
     * are: [{1}].
     */
    ZONE_BAD_CONFIGURATION,

    /**
     * Floating IP was requested but no floating ip pools exist or they are all empty.
     */
    NO_FLOATING_IP_POOL,

    /**
     * The polling time interval specified ({0}) is not valid.
     */
    INVALID_POLL_INTERVAL,

    /**
     * The poll timeout value ({0}) must be greater than or equal to the poll interval ({1})
     */
    INVALID_POLL_TIMEOUT,

    /**
     * The argument "{0}" is not a valid integer, the value is "{1}".
     */
    INVALID_INTEGER_ARGUMENT,

    /**
     * Object is not connected to a context or the method is not implemented, no model navigation is possible!
     */
    NOT_NAVIGABLE,

    /**
     * The state of server "{0}" with id "{1}" did not change to an expected state within the provided timeout ({2}
     * seconds). The last state of the server is "{3}", and the expected state(s) are "{4}".
     */
    SERVER_TIMEOUT,
    
    /**
     * The state of image "{0}" with id "{1}" did not change to an expected state within the provided timeout ({2}
     * seconds). The last state of the image is "{3}", and the expected state(s) are "{4}".
     */
    IMAGE_TIMEOUT,

    /**
     * Attempt to connect to {0} failed with {1} ({2}), attempt {3} of {4}, waiting {5} seconds
     */
    RETRY_PROVIDER_CONNECTION,

    /**
     * Principal [{0}] has been authenticated on provider [{1}] to tenant [{2}]
     */
    PRINCIPAL_HAS_BEEN_AUTHENTICATED;

    static {
        EELFResourceManager.loadMessageBundle("com/att/cdp/pal_common_messages"); //$NON-NLS-1$
    }

}
