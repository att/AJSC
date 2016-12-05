/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.configuration;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import com.att.cdp.pal.i18n.Msg;
import com.att.cdp.pal.util.StreamUtility;
import com.att.cdp.pal.util.StringHelper;
import com.att.eelf.i18n.EELFResourceManager;

/**
 * The configuration factory is used to obtain access to an already created and initialized singleton configuration
 * object as well as to create and initialize the singleton if not already set up.
 * <p>
 * This class is responsible for the creation of the configuration object used to manage the configuration of the
 * application. The configuration object implementation must implement the <code>Configuration</code> interface. This
 * allows for the factory to create different specializations in the future if needed and not break any application
 * code.
 * </p>
 * <p>
 * The configuration object is basically a wrapper around a properties object. The configuration is therefore specified
 * as a set of properties that are loaded and processed from different sources with different precedences. It is
 * important that the configuration object always be able to supply default values for any configuration properties that
 * must be supplied, and not rely on the user always supplying these values. This also relieves the application itself
 * from having to interpret missing or invalid properties and applying defaults. By having all of the defaults in one
 * place, the application code can be simpler (not having to worry about defaults or invalid properties), and the
 * defaults can be changed much easier (they are all in one place and not distributed throughout the codebase).
 * </p>
 * <p>
 * Since the configuration is managed as a property object, we can use a characteristic of the <code>Properties</code>
 * class to our advantage. Namely, if we put a property into a <code>Properties</code> object that already exists, the
 * <code>Properties</code> object replaces it with the new value. This does not affect any other properties that may
 * already be defined in the properties object. This gives us the ability to initialize the properties with default
 * values for all of the application settings, then override just those that we need to override, possibly from multiple
 * sources and in increasing order of precedence.
 * </p>
 * <p>
 * This means that properties are in effect "merged" together from multiple sources in a prescribed precedence order. In
 * fact, the precedence order that this factory implements is defined as:
 * </p>
 * <ol>
 * <li>Default values from a system resource file.</li>
 * <li>User-supplied properties file, if any.</li>
 * <li>Application-supplied properties, if any.</li>
 * <li>Command-line properties (if any)</li>
 * </ol>
 * <p>
 * The name and location of the properties file that is loaded can also be set, either in the defaults or overridden by
 * the system command line. There are two properties that can be specified to define the name and path. These are:
 * </p>
 * <dl>
 * <dt>com.att.cdp.bootstrap.file</dt>
 * <dd>This property defines the name of the file that will be loaded. If not specified, the default value is
 * "cdp.properties". This can be specified in either (or both) the default properties or the command line. The command
 * line specification will always override.</dd>
 * <dt>com.att.cdp.bootstrap.path</dt>
 * <dd>This is a comma-delimited (,) path of directories to be searched to locate the specified file. The first
 * occurrence of the file is the one loaded, and no additional searching is performed. The path can be specified in
 * either, or both, the default values and the command line specification. If specified on the command line, the value
 * overrides the default values. If omitted, the default path is <code>${user.home},etc,../etc</code></dd>
 * </dl>
 *
 */
public final class ConfigurationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFactory.class);

    /**
     * This is a string constant for the comma character. It's intended to be used a common string delimiter.
     */
    private static final String COMMA = ",";

    /**
     * The default Configuration object that implements the <code>Configuration</code> interface and represents our
     * system configuration settings.
     */
    private static DefaultConfiguration config = null;

    /**
     * The default properties resource to be loaded
     */
    private static final String DEFAULT_PROPERTIES = "com/att/cdp/default.properties";

    /**
     * This collection allows for special configurations to be created and maintained, organized by some identification
     * (such as an object reference to the StackBuilder to which they apply), and then obtained from the configuration
     * factory when needed.
     */
    private static HashMap<Object, Configuration> localConfigs = new HashMap<>();

    /**
     * The reentrant shared lock used to serialize access to the properties.
     */
    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * This is a constant array of special property names that will be copied from the configuration back to the System
     * properties object if they are defined in the configuration AND they do not already exist in the System properties
     * object. These are intended as a convenience for setting the AFT properties for the Discovery client where it may
     * be difficult or impossible to set VM arguments for the container.
     */
    private static final String[] specialProperties = {
        "AFT_LATITUDE", "AFT_LONGITUDE", "AFT_ENVIRONMENT", "SCLD_PLATFORM", "AFTSWM_CONTROLLER_URLS",
        "AFTSWM_REPOSITORY_URLS", "com.att.aft.swm.common.bootstrap.debug"
    };

    private ConfigurationFactory() {
    }

    /**
     * @param loggerFactory
     *            the logger factory context
     * @param stream
     *            The input stream to be configured
     */
    private static void configureLogback(final LoggerContext context, final InputStream stream) {
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);

        try {
            configurator.doConfigure(stream);
        } catch (JoranException e) {
            // not much we can do since logger may not be configured yet
            e.printStackTrace(System.out);
        }

        StatusPrinter.print(context);
    }

    /**
     * This method is used to obtain the common configuration object (as well as set it up if not already).
     *
     * @return The configuration object implementation
     */
    public static Configuration getConfiguration() {

        /*
         * First, attempt to access the properties as a read lock holder
         */
        ReadLock readLock = lock.readLock();
        readLock.lock();
        try {

            /*
             * If the properties don't exist, release the read lock and acquire the write lock. Once we get the write
             * lock, we need to re-check to see that the configuration needs to be set up (because another thread may
             * have beat us to it). After we get a configuration set up, release the write lock and re-obtain the read
             * lock to access the properties.
             */
            if (config == null) {
                readLock.unlock();
                WriteLock writeLock = lock.writeLock();
                writeLock.lock();
                try {
                    if (config == null) {
                        config = new DefaultConfiguration();
                        initialize(null);
                    }
                } catch (Exception t) {
                    LOG.error("getConfiguration", t);
                } finally {
                    writeLock.unlock();
                }
                readLock.lock();
            }
            return config;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * This method will obtain the local configuration for the specified object if it exists, or will create it from the
     * current global configuration. This allows the configuration to be tailored for a specific process or operation,
     * and uniquely identified by some value (such as the object that represents the special use of the configuration).
     *
     * @param owner
     *            The owner or identification of the owner of the special configuration
     * @return The special configuration object, or a clone of the global configuration so that it can be altered if
     *         needed.
     */
    public static Configuration getConfiguration(final Object owner) {
        ReadLock readLock = lock.readLock();
        readLock.lock();
        try {
            DefaultConfiguration local = (DefaultConfiguration) localConfigs.get(owner);
            if (local == null) {
                readLock.unlock();
                WriteLock writeLock = lock.writeLock();
                writeLock.lock();
                try {
                    local = (DefaultConfiguration) localConfigs.get(owner);
                    if (local == null) {
                        DefaultConfiguration global = (DefaultConfiguration) getConfiguration();
                        try {
                            local = (DefaultConfiguration) global.clone();
                        } catch (CloneNotSupportedException e) {
                            LOG.error("getConfiguration", e);
                        }
                        localConfigs.put(owner, local);
                    }
                } finally {
                    writeLock.unlock();
                }
                readLock.lock();
            }
            return local;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * This method allows the caller to alter the configuration, supplying the specified configuration properties which
     * override the application default values.
     * <p>
     * The configuration is re-constructed (if already constructed) or created new (if not already created) and the
     * default properties are loaded into the configuration.
     * </p>
     * <p>
     * The primary purpose of this method is to allow the application configuration properties to be reset or refreshed
     * after the application has already been initialized. This method will lock the configuration for the duration
     * while it is being re-built, and should not be called on a regular basis.
     * </p>
     *
     * @param props
     *            The properties used to configure the application.
     * @return Access to the configuration implementation
     */
    public static Configuration getConfiguration(final Properties props) {
        WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            config = new DefaultConfiguration();
            initialize(props);
            return config;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * This method will clear the current configuration and then re-initialize it with the default values,
     * application-specific configuration file, user-supplied properties (if any), and then command-line settings.
     * <p>
     * This method <strong><em>MUST</em></strong> be called holding the configuration lock!
     * </p>
     * <p>
     * This method is a little special in that logging messages generated during the method must be cached and delayed
     * until after the logging framework has been initialized. After that, the delayed logging buffer can be dumped to
     * the log file and cleared.
     * </p>
     *
     * @param props
     *            Application-supplied configuration values, if any
     */
    @SuppressWarnings("nls")
    private static void initialize(final Properties props) {
        ArrayList<String> delayedLogging = new ArrayList<>();
        // ResourceManager.loadMessageBundle("com/att/cdp/Resources");
        DateFormat format = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG);
        Date now = new Date();
        delayedLogging.add("-------------------------------------------------------------------------"); //$NON-NLS-1$
        delayedLogging.add(EELFResourceManager.format(Msg.CONFIGURATION_STARTED, now.toString()));

        /*
         * Clear any existing properties
         */
        config.clear();
        delayedLogging.add(EELFResourceManager.format(Msg.CONFIGURATION_CLEARED));

        /*
         * Load the defaults (if any are present)
         */
        InputStream in =
            Thread.currentThread().getContextClassLoader().getResourceAsStream("com/att/cdp/default.properties");
        if (in != null) {
            delayedLogging.add(EELFResourceManager.format(Msg.LOADING_DEFAULTS, DEFAULT_PROPERTIES));
            try {
                config.setProperties(in);
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    // not much we can do since logger may not be configured yet
                    e.printStackTrace(System.out);
                }
            }
            for (String key : config.getProperties().stringPropertyNames()) {
                delayedLogging.add(EELFResourceManager.format(Msg.PROPERTY_VALUE, key, config.getProperty(key)));
            }
        } else {
            delayedLogging.add(EELFResourceManager.format(Msg.NO_DEFAULTS_FOUND, DEFAULT_PROPERTIES));
        }

        /*
         * Look for application configuration property file. By default, we will look for the file "cdp.properties" on
         * the user home path, then on "./etc" (relative to current path), then on "../etc" (relative to current path).
         * If we do not find any property file, then we continue. Otherwise, we load the first property file we find and
         * then continue. In order to allow default values for the filename and paths to be searched, we first attempt
         * to obtain these from our configuration object (which should be primed with default values and/or overridden
         * with application-specified values). We then use the values obtained from that to get any user supplied values
         * on the command line.
         */
        String filename = config.getProperty("com.att.cdp.bootstrap.file", "cdp.properties");
        filename = System.getProperty("com.att.cdp.bootstrap.file", filename);

        String path = config.getProperty("com.att.cdp.bootstrap.path", "${user.home},etc,../etc");
        path = System.getProperty("com.att.cdp.bootstrap.path", path);

        delayedLogging.add(EELFResourceManager.format(Msg.SEARCHING_CONFIGURATION_OVERRIDES, path, filename));

        String[] pathElements = path.split(COMMA);
        boolean found = false;
        for (String pathElement : pathElements) {
            File file = new File(pathElement, filename);
            if (file.exists() && file.canRead() && !file.isDirectory()) {

                delayedLogging.add(EELFResourceManager.format(Msg.LOADING_CONFIGURATION_OVERRIDES,
                    file.getAbsolutePath()));
                Properties fileProperties = new Properties();
                BufferedInputStream stream = null;
                try {
                    stream = new BufferedInputStream(new FileInputStream(file));
                    fileProperties.load(stream);
                    for (String key : fileProperties.stringPropertyNames()) {
                        delayedLogging.add(EELFResourceManager.format(Msg.PROPERTY_VALUE, key,
                            fileProperties.getProperty(key)));
                        config.setProperty(key, fileProperties.getProperty(key));
                    }
                    found = true;
                    break;
                } catch (FileNotFoundException e) {
                    // not much we can do since logger may not be configured yet
                    e.printStackTrace(System.out);
                } catch (IOException e) {
                    delayedLogging.add(EELFResourceManager.format(e));
                } finally {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        // not much we can do since logger may not be configured yet
                        e.printStackTrace(System.out);
                    }
                }
            }
        }

        if (!found) {
            delayedLogging.add(EELFResourceManager.format(Msg.NO_OVERRIDE_PROPERTY_FILE_LOADED, filename, path));
        }

        /*
         * Apply any application-specified properties
         */
        if (props != null) {
            delayedLogging.add(EELFResourceManager.format(Msg.LOADING_APPLICATION_OVERRIDES));
            for (String key : props.stringPropertyNames()) {
                delayedLogging.add(EELFResourceManager.format(Msg.PROPERTY_VALUE, key, props.getProperty(key)));
                config.setProperty(key, props.getProperty(key));
            }
        } else {
            delayedLogging.add(EELFResourceManager.format(Msg.NO_APPLICATION_OVERRIDES));
        }

        /*
         * Merge in the System.properties to pick-up any command line arguments (-Dkeyword=value)
         */
        delayedLogging.add(EELFResourceManager.format(Msg.MERGING_SYSTEM_PROPERTIES));
        config.setProperties(System.getProperties());

        /*
         * As a convenience, copy the "specialProperties" that are not defined in System.properties from the
         * configuration back to the system properties object.
         */
        for (String key : config.getProperties().stringPropertyNames()) {
            for (String specialProperty : specialProperties) {
                if (key.equals(specialProperty) && !System.getProperties().containsKey(key)) {
                    System.setProperty(key, config.getProperty(key));
                    delayedLogging.add(EELFResourceManager.format(Msg.SETTING_SPECIAL_PROPERTY, key,
                        config.getProperty(key)));
                }
            }
        }

        /*
         * Initialize the resource manager by loading the requested bundles, if any are defined. Resource bundles may be
         * specified as a comma-delimited list of names. These resource names are base names of resource bundles, do not
         * include the language or country code, or the ".properties" extension. The actual loading of the resource
         * bundles is done lazily when requested the first time. If the bundle does not exist, or cannot be loaded, it
         * is ignored.
         */
        String resourcesList = config.getProperty("com.att.cdp.resources");
        if (resourcesList != null) {
            String[] resources = resourcesList.split(",");
            for (String resource : resources) {
                delayedLogging.add(EELFResourceManager.format(Msg.LOADING_RESOURCE_BUNDLE, resource.trim()));
                EELFResourceManager.loadMessageBundle(resource.trim());
            }
        }

        /*
         * Now, we are ready to initialize logging. Check to see if logging has already been initialized and that the
         * application logger exists already. If it does, then skip the logging configuration because it was already set
         * up in the container that is calling us. If not, then we need to set it up.
         */
        ILoggerFactory factory = LoggerFactory.getILoggerFactory();
        if (factory instanceof LoggerContext) {
            LoggerContext loggerContext = (LoggerContext) factory;
            if (config.getApplicationLoggerName() != null
                && loggerContext.exists(config.getApplicationLoggerName()) == null) {
                initializeLogging(delayedLogging);
            } else {
                delayedLogging.add(EELFResourceManager.format(Msg.LOGGING_ALREADY_INITIALIZED));
            }
        }

        /*
         * Copy all delayed logging messages to the logger
         */
        Logger logger = config.getApplicationLogger();
        for (String message : delayedLogging) {
            // All messages are prefixed with a message code of the form CDP####S
            // Where:
            // CDP --- is the product code
            // #### -- Is the message number
            // S ----- Is the severity code (I=INFO, D=DEBUG, W=WARN, E=ERROR)
            char severity = message.charAt(7);
            switch (severity) {
                case 'D':
                    logger.debug(message);
                    break;
                case 'I':
                    logger.info(message);
                    break;
                case 'W':
                    logger.warn(message);
                    break;
                case 'E':
                    logger.error(message);
            }
        }
        delayedLogging.clear();

        if (config.getProperty(Configuration.CDP_BOOTSTRAP_FAIL_ON_ERROR, "false").equalsIgnoreCase("true")) {
            config.populateScript(loadSWMBootstrapScript());
        }
    }

    /**
     * Initialize the logging environment, record all logging messages to the provided list for delayed processing.
     *
     * @param delayedLogging
     *            The list to record logging messages to for delayed processing after the logging environment is
     *            created.
     */
    private static void initializeLogging(final ArrayList<String> delayedLogging) {
        /*
         * See if we can find logback-test.xml first, unless a specific file has been provided
         */
        String filename = config.getProperty(Configuration.PROPERTY_LOGGING_FILE_NAME, "logback-test.xml");
        filename = System.getProperty(Configuration.PROPERTY_LOGGING_FILE_NAME, filename);

        String path = config.getProperty("com.att.cdp.logging.path", "${user.home};etc;../etc");
        path = System.getProperty("com.att.cdp.logging.path", path);

        String msg = EELFResourceManager.format(Msg.MESSAGE_SEARCHING_LOG_CONFIGURATION, path, filename);
        System.out.println(msg);
        delayedLogging.add(msg);

        if (scanAndLoadLoggingConfiguration(path, filename, delayedLogging)) {
            return;
        }

        /*
         * If the first attempt was for logback-test.xml and it failed to find it, look again for logback.xml
         */
        if (filename.equals("logback-test.xml")) {
            filename = config.getProperty(Configuration.PROPERTY_LOGGING_FILE_NAME, "logback.xml");
            filename = System.getProperty(Configuration.PROPERTY_LOGGING_FILE_NAME, filename);

            if (scanAndLoadLoggingConfiguration(path, filename, delayedLogging)) {
                return;
            }
        }

        /*
         * If we reach here, then no external logging configurations were defined or found. In that case, we need to
         * initialize the logging framework from hard-coded default values we load from resources.
         */
        InputStream stream = ConfigurationFactory.class.getClassLoader().getResourceAsStream("com/att/cdp/logback.xml");
        try {
            if (stream != null) {
                delayedLogging.add(EELFResourceManager.format(Msg.LOADING_DEFAULT_LOG_CONFIGURATION,
                    "com/att/cdp/logback.xml"));
                loadLoggingConfiguration(stream, delayedLogging);
            } else {
                delayedLogging.add(EELFResourceManager.format(Msg.NO_LOG_CONFIGURATION));
            }
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // not much we can do since logger may not be configured yet
                    e.printStackTrace(System.out);
                }
            }
        }

    }

    /**
     * Loads the logging configuration from the specified stream.
     *
     * @param stream
     *            The stream that contains the logging configuration document.
     * @param delayedLogging
     */
    private static void loadLoggingConfiguration(final InputStream stream, final ArrayList<String> delayedLogging) {
        ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
        if (loggerFactory instanceof LoggerContext) {
            configureLogback((LoggerContext) loggerFactory, stream);
        } else {
            delayedLogging.add(EELFResourceManager.format(Msg.UNSUPPORTED_LOGGING_FRAMEWORK));
        }
    }

    /**
     * This method scans a set of directories specified by the path for an occurrence of a file of the specified
     * filename, and when found, loads that file as a logging configuration file.
     *
     * @param path
     *            The path to be scanned. This can be one or more directories, separated by the platform specific path
     *            separator character.
     * @param filename
     *            The file name to be located. The file name examined within each element of the path for the first
     *            occurrence of the file that exists and which can be read and processed.
     * @param delayedLogging
     * @return True if a file was found and loaded, false if no files were found, or none were readable.
     */
    private static boolean scanAndLoadLoggingConfiguration(final String path, final String filename,
        final ArrayList<String> delayedLogging) {
        String[] pathElements = path.split(COMMA);
        for (String pathElement : pathElements) {
            File file = new File(pathElement, filename);
            if (file.exists() && file.canRead() && !file.isDirectory()) {
                String msg = EELFResourceManager.format(Msg.LOADING_LOG_CONFIGURATION, file.getAbsolutePath());
                System.out.println(msg);
                delayedLogging.add(msg);

                BufferedInputStream stream = null;
                try {
                    stream = new BufferedInputStream(new FileInputStream(file));
                    delayedLogging.add(String.format("CDP000I Loading logging configuration from %s",
                        file.getAbsolutePath()));
                    loadLoggingConfiguration(stream, delayedLogging);
                } catch (FileNotFoundException e) {
                    delayedLogging.add(EELFResourceManager.format(e));
                } finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        } catch (IOException e) {
                            // not much we can do since logger may not be configured yet
                            e.printStackTrace(System.out);
                        }
                    }
                }

                return true;
            }
        }
        return false;
    }

    /**
     * Retrieve the SWM Bootstrap script required for installing SWM Node, CLI and platform-init on newly provisioned
     * VMs
     * 
     * @return The initialization script to be used
     */
    private static String loadSWMBootstrapScript() {
        String content = "";
        boolean found = false;

        // Retrieve the path to the agentScript file
        String agentScriptPath = config.getProperty(Configuration.SWM_AGENT_SCRIPT_PATH, "${user.home},etc,../etc");
        agentScriptPath = System.getProperty(Configuration.SWM_AGENT_SCRIPT_PATH, agentScriptPath);

        String agentScript = config.getProperty(Configuration.SWM_AGENT_SCRIPT, "install_swm_agent_for_cdp.sh");

        String[] pathElements = agentScriptPath.split(",");

        for (String pathElement : pathElements) {
            File file = new File(pathElement, agentScript);
            if (file.exists() && file.canRead() && !file.isDirectory()) {

                try (InputStream in = new FileInputStream(file)) {

                    if (in != null) {
                        content = StringHelper.toUnixLines(StreamUtility.getStringFromInputStream(in));
                    }
                    found = true;
                    break;

                } catch (FileNotFoundException e) {
                    System.err.println("The script " + agentScript + " is not found.");
                    System.exit(1);

                } catch (IOException e) {
                    System.err.println("The script " + agentScript + " could not be closed.");
                    System.exit(1);
                }
            }
        }

        if (!found) {
            System.err.println("The script " + agentScript + " does not exist in " + agentScriptPath);
            System.exit(1);
        }

        return content;
    }

}
