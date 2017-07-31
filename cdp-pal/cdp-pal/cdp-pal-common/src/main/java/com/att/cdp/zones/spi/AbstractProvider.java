/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.zones.spi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;

import com.att.cdp.pal.configuration.Configuration;
import com.att.cdp.pal.configuration.ConfigurationFactory;
import com.att.cdp.zones.Provider;

/**
 * This abstract base class is intended to help off-load the common operations and management of a provider from the
 * implementation classes.
 * 
 * @since Sep 23, 2013
 * @version $Id$
 */
public abstract class AbstractProvider implements Provider {

    /**
     * The CDP configuration
     */
    private Configuration configuration;

    /**
     * An optional properties object containing configuration settings and other information for the provider.
     */
    private Properties defaults;

    /**
     * The application logger
     */
    private Logger logger;

    /**
     * The name that this provider is known by. This value must be supplied by the implementing classes during
     * construction. This is needed because the service loader will call the <code>getName()</code> method (@link
     * #getName()} to compare the name of the provider to that requested by the client.
     */
    private String providerName;

    /**
     * <p>
     * All providers must provide a default constructor to allow the service loader to instantiate the provider. This
     * constructor will load and save any configuration information, initialize resources that it will need, etc.
     * </p>
     * <p>
     * This constructor will attempt to locate and load a default configuration properties file on the class path of the
     * descendant class. If found, the constructor can initialize the provider name and any other common attributes from
     * this property file. Additionally, the implementation class can supply these values during construction as well.
     * </p>
     */
    public AbstractProvider() {
        configuration = ConfigurationFactory.getConfiguration();
        logger = configuration.getApplicationLogger();

        logger.debug(String.format("Loading provider implementation properties and metadata"));
        defaults = new Properties();
        InputStream stream = getClass().getResourceAsStream("provider.properties");
        if (stream != null) {
            try {
                defaults.load(stream);
            } catch (IOException e) {
                logger.debug("Loading Provider properties failed", e);
            } finally {
                try {
                    stream.close();
                } catch (IOException e) {
                    logger.debug("Error closing provider.properties", e);
                }
            }

            providerName = defaults.getProperty(ProviderProperties.PROPERTY_PROVIDER_NAME, null);
        } else {
            logger
                .warn(String
                    .format("Provider implementation properties and metadata resource file provider.properties was not found!"));
        }
    }

    /**
     * @see com.att.cdp.zones.Provider#getName()
     */
    @Override
    public String getName() {
        return providerName;
    }

    /**
     * @return the value of defaults
     */
    protected Properties getDefaults() {
        return defaults;
    }

    /**
     * @return the value of logger
     */
    protected Logger getLogger() {
        return logger;
    }

}
