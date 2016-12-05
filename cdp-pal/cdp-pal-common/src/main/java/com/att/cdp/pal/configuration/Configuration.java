/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.configuration;

import java.util.Properties;

import org.slf4j.Logger;

/**
 * Copyright (C) 2013, AT&T Inc. All rights reserved. Proprietary materials, property of AT&T. For internal use only,
 * not for disclosure to parties outside of AT&T or its affiliates.
 */

/**
 * This interface defines the common configuration support that is available to the application.
 * <p>
 * Where properties are common to all CDP components (server, coordinator, and EPM), the property symbolic values are
 * defined as part of this interface. Where they are unique to each component, they must be defined within that
 * component.
 * </p>
 * 
 */
public interface Configuration {

    /**
     * The name of the property used to define the filename for the logback configuration
     */
    String PROPERTY_LOGGING_FILE_NAME = "com.att.cdp.logging.file";

    /**
     * The name of the property used to define the logger to be used by this process for application coordinator logging
     */
    String PROPERTY_COORDINATOR_LOGGER_NAME = "com.att.cdp.coordinator.logger";

    /**
     * The name of the logger to be used by this process for general application logging
     */
    String PROPERTY_GENERAL_LOGGER_NAME = "com.att.cdp.logger";

    /**
     * The name of the property used to define the logger to be used by this process for application gui logging
     */
    String PROPERTY_GUI_LOGGER_NAME = "com.att.cdp.gui.logger";

    /**
     * The name of the property used to define the logger to be used by this process for application node data logging
     */
    String PROPERTY_NODEDATA_LOGGER_NAME = "com.att.aft.metrics.logger";

    /**
     * The name of the property used to define the logger to be used by this process for application performance metrics
     * logging
     */
    String PROPERTY_PERF_LOGGER_NAME = "com.att.cdp.perf.logger";

    /**
     * The name of the property used to define the logger to be used by this process for application policy logging
     */
    String PROPERTY_POLICY_LOGGER_NAME = "com.att.cdp.policy.logger";

    /**
     * The name of the property used to define the logger to be used by this process for application security logging
     */
    String PROPERTY_SECURITY_LOGGER_NAME = "com.att.cdp.security.logger";

    /**
     * The name of the property used to define the logger to be used by this process for application server logging
     */
    String PROPERTY_SERVER_LOGGER_NAME = "com.att.cdp.server.logger";

    /**
     * Indicates that the processing of blueprints and stacks must be simulated.
     */
    String PROPERTY_SIMULATION = "com.att.cdp.simulation";

    /**
     * The name of the SWM agent install script
     */
    String SWM_AGENT_SCRIPT = "cdp.swm.init.script";

    /**
     * The path to the SWM agent install script
     */
    String SWM_AGENT_SCRIPT_PATH = "cdp.swm.init.script.path";

    /**
     * Property that indictes cdp-server should not start if this property is set
     */
    String CDP_BOOTSTRAP_FAIL_ON_ERROR = "cdp.bootstrap.script.failOnError";

    /**
     * Returns the logger to be used to record general application log events. This logger is the same logger that is
     * represented by the {@link #getApplicationLoggerName()} method.
     * 
     * @return The logger to be used
     */
    Logger getApplicationLogger();

    /**
     * The application logger is used for all general-purpose log events. This application log is used to record
     * operations being performed, debug and trace entries, errors and warnings, and the like. It contains all
     * general-purpose log entries.
     * 
     * @return The name of the logger to be used
     */
    String getApplicationLoggerName();

    /**
     * This method is called to obtain a property expressed as a boolean value (true or false). The standard rules for
     * {@link Boolean#valueOf(String)} are used.
     * 
     * @param key
     *            The property key
     * @return The value of the property expressed as a boolean, or false if it does not exist.
     */
    boolean getBooleanProperty(String key);

    /**
     * This method is called to obtain a property expressed as a boolean value (true or false). The standard rules for
     * {@link Boolean#valueOf(String)} are used.
     * 
     * @param key
     *            The property key
     * @param defaultValue
     *            The default value to be returned if the property does not exist
     * @return The value of the property expressed as a boolean, or false if it does not exist.
     */
    boolean getBooleanProperty(String key, boolean defaultValue);

    /**
     * Returns the logger to be used to record coordinator-based general application log events. This logger is the same
     * logger that is represented by the {@link #getCoordinatorLoggerName()} method.
     * 
     * @return The logger to be used
     */
    Logger getCoordinatorLogger();

    /**
     * The coordinator logger is used to segregate events originating within the cdp coordinator from other application
     * events. These are events that are general application logging related events (not security or performance) and
     * which may be directed to different appenders where needed. Application code in the cdp coordinator component
     * should use this logger for all general logging.
     * 
     * @return The name of the coordinator logger
     */
    String getCoordinatorLoggerName();

    /**
     * Returns the indicated property value expressed as a floating point double-precision value (double). The standard
     * rules for {@link Double#valueOf(String)} are used.
     * 
     * @param key
     *            The property to retrieve
     * @return The value of the property, or 0.0 if not found or invalid
     */
    double getDoubleProperty(String key);

    /**
     * Returns the indicated property value expressed as a floating point double-precision value (double). The standard
     * rules for {@link Double#valueOf(String)} are used.
     * 
     * @param key
     *            The property to retrieve
     * @param defaultValue
     *            The default value to be returned if the property does not exist
     * @return The value of the property, or 0.0 if not found or invalid
     */
    double getDoubleProperty(String key, double defaultValue);

    /**
     * Returns the logger to be used to record GUI-based general application log events. This logger is the same logger
     * that is represented by the {@link #getGUILoggerName()} method.
     * 
     * @return The logger to be used
     */
    Logger getGUILogger();

    /**
     * The GUI logger is used to segregate events originating within the cdp GUI from other application events. These
     * are events that are general application logging related events (not security or performance) and which may be
     * directed to different appenders where needed. Application code in the cdp GUI component should use this logger
     * for all general logging.
     * 
     * @return The name of the GUI logger
     */
    String getGUILoggerName();

    /**
     * Returns the property indicated expressed as an integer. The standard rules for
     * {@link Integer#parseInt(String, int)} using a radix of 10 are used.
     * 
     * @param key
     *            The property name to retrieve.
     * @return The value of the property, or 0 if it does not exist or is invalid.
     */
    int getIntegerProperty(String key);

    /**
     * Returns the property indicated expressed as an integer. The standard rules for
     * {@link Integer#parseInt(String, int)} using a radix of 10 are used.
     * 
     * @param key
     *            The property name to retrieve.
     * @param defaultValue
     *            The default value to be returned if the property does not exist
     * @return The value of the property, or 0 if it does not exist or is invalid.
     */
    int getIntegerProperty(String key, int defaultValue);

    /**
     * Returns the specified property as a long integer value, if it exists, or zero if it does not.
     * 
     * @param key
     *            The key of the property desired.
     * @return The value of the property expressed as an integer long value, or zero if the property does not exist or
     *         is not a valid integer long.
     */
    long getLongProperty(String key);

    /**
     * Returns the specified property as a long integer value, if it exists, or the default value if it does not exist
     * or is invalid.
     * 
     * @param key
     *            The key of the property desired.
     * @param defaultValue
     *            the value to be returned if the property is not valid or does not exist.
     * @return The value of the property expressed as an integer long value, or the default value if the property does
     *         not exist or is not a valid integer long.
     */
    long getLongProperty(String key, long defaultValue);

    /**
     * Returns the logger to be used to record policy manager-based general application log events. This logger is the
     * same logger that is represented by the {@link #getNodedataLoggerName()} method.
     * 
     * @return The logger to be used
     */
    Logger getNodeDataLogger();

    /**
     * The policy logger is used to segregate events originating within the cdp node data manager from other application
     * events. These are events that are general application logging related events (not security or performance) and
     * which may be directed to different appenders where needed. Application code in the cdp node data manager
     * component should use this logger for all general logging.
     * 
     * @return The name of the nodedata logger
     */
    String getNodeDataLoggerName();

    /**
     * Returns the logger to be used to record performance log events. This logger is the same logger that is
     * represented by the {@link #getPerformanceLoggerName()} method.
     * 
     * @return The logger to be used
     */
    Logger getPerformanceLogger();

    /**
     * Auditing of the performance of the application may require much more structured log events, such as start and
     * stop times, counts, standard deviations, and other performance data. This information can make the general log
     * much more difficult to read and process if intermixed. Fr this reason, the application records all performance
     * log events to the performance logger (perflog) so that it can be configured separately from the other loggers.
     * 
     * @return The name of the performance logger
     */
    String getPerformanceLoggerName();

    /**
     * Returns the logger to be used to record policy manager-based general application log events. This logger is the
     * same logger that is represented by the {@link #getPolicyLoggerName()} method.
     * 
     * @return The logger to be used
     */
    Logger getPolicyLogger();

    /**
     * The policy logger is used to segregate events originating within the cdp policy manager from other application
     * events. These are events that are general application logging related events (not security or performance) and
     * which may be directed to different appenders where needed. Application code in the cdp policy manager component
     * should use this logger for all general logging.
     * 
     * @return The name of the policy logger
     */
    String getPolicyLoggerName();

    /**
     * This method can be called to retrieve a properties object that is immutable. Any attempt to modify the properties
     * object returned will result in an exception. This allows a caller to view the current configuration as a set of
     * properties.
     * 
     * @return An unmodifiable properties object.
     */
    Properties getProperties();

    /**
     * This method is called to obtain a property as a string value
     * 
     * @param key
     *            The key of the property
     * @return The string value, or null if it does not exist.
     */
    String getProperty(String key);

    /**
     * This method is called to obtain a property as a string value
     * 
     * @param key
     *            The key of the property
     * @param defaultValue
     *            The default value to be returned if the property does not exist
     * @return The string value, or null if it does not exist.
     */
    String getProperty(String key, String defaultValue);

    /**
     * Returns the logger to be used to record security log events. This logger is the same logger that is represented
     * by the {@link #getSecurityLoggerName()} method.
     * 
     * @return The logger to be used
     */
    Logger getSecurityLogger();

    /**
     * Because security auditing may contain sensitive information, and because it may need to be segregated from the
     * application general logger and log file(s), the application uses a separate logger for security logging. Security
     * logging includes log events such as login, logout, access violations, resource access, and other information that
     * may be needed to perform a security audit of the application.
     * 
     * @return The specific logger name to be used for security event logging
     */
    String getSecurityLoggerName();

    /**
     * Returns the logger to be used to record server-based general application log events. This logger is the same
     * logger that is represented by the {@link #getServerLoggerName()} method.
     * 
     * @return The logger to be used
     */
    Logger getServerLogger();

    /**
     * The server logger is used to segregate events originating within the server from other application events. These
     * are events that are general application logging related events (not security or performance) and which may be
     * directed to different appenders where needed. Application code in the server component should use this logger for
     * all general logging.
     * 
     * @return The name of the server logger
     */
    String getServerLoggerName();

    /**
     * Returns true if the named property is defined, false otherwise.
     * 
     * @param key
     *            The key of the property we are interested in
     * @return True if the property exists.
     */
    boolean isPropertyDefined(String key);

    /**
     * @return True if the stack builder framework is running in simulation mode
     */
    boolean isSimulation();

    /**
     * Returns an indication of the validity of the boolean property. A boolean property is considered to be valid only
     * if it has the value "true" or "false" (ignoring case).
     * 
     * @param key
     *            The property to be checked
     * @return True if the value is a boolean constant, or false if it does not exist or is not a correct string
     */
    boolean isValidBoolean(String key);

    /**
     * Returns an indication if the indicated property represents a valid double-precision floating point number.
     * 
     * @param key
     *            The property to be examined
     * @return True if the property is a valid representation of a double, or false if it does not exist or contains
     *         illegal characters.
     */
    boolean isValidDouble(String key);

    /**
     * Returns an indication if the property is a valid integer value or not.
     * 
     * @param key
     *            The key of the property to check
     * @return True if the value is a valid integer string, or false if it does not exist or contains illegal
     *         characters.
     */
    boolean isValidInteger(String key);

    /**
     * Determines is the specified property exists and is a valid representation of an integer long value.
     * 
     * @param key
     *            The property to be checked
     * @return True if the property is a valid representation of an integer long value, and false if it either does not
     *         exist or is not valid.
     */
    boolean isValidLong(String key);

    /**
     * This method allows the caller to set all properties from a provided properties object into the configuration
     * property set.
     * <p>
     * The primary difference between this method and the factory method
     * {@link ConfigurationFactory#getConfiguration(Properties)} is that this method does not clear and reload the
     * configuration. Rather, this method merges the provided properties object contents into the existing properties,
     * replacing any same-named keys with the values from this object.
     * </p>
     * 
     * @param properties
     *            The properties object to copy all properties from
     */
    void setProperties(Properties properties);

    /**
     * This method allows a caller to insert a new property definition into the configuration object. This allows the
     * application to adjust or add to the current configuration. If the property already exists, it is replaced with
     * the new value.
     * 
     * @param key
     *            The key of the property to be defined
     * @param value
     *            The value of the property to be defined
     */
    void setProperty(String key, String value);

    /**
     * @return the product version string, in the form V.R.M
     */
    String getVersion();

    /**
     * @return The date the common library was last built, in the form yyyy-mm-ddThh:mm:ss
     */
    String getBuildDate();

    /**
     * Returns the time stamp formatted as a string that the system was started (actually, when the configuration was
     * loaded).
     * 
     * @return Get the date that the system was started, in the format yyyy-mm-dd hh:mm:ss
     */
    String getSystemTimeStartedFormatted();

    /**
     * Get the amount of time that the system has been running and format as a standard string showing the number of
     * days, hours, minutes, and seconds that the system has been running.
     * 
     * @return The system run time (up time) in the format ddd.hh:mm:ss
     */
    String getSystemRunTimeFormatted();

    /**
     * Get system run time in milliseconds
     * 
     * @return The system run time in milliseconds
     */
    Long getSystemRunTime();

    /**
     * Retrieve the SWM Bootstrap script required for installing SWM Node, CLI and platform-init on newly provisioned
     * VMs
     * 
     * @return The initialization script to be used
     */
    String getSWMBootstrapScript();

    /**
     * This method allows a caller to insert the agentScript into the configuration object.
     * 
     * @param key
     *            The contents of the agentScript loaded during startup
     */
    void populateScript(String agentScript);
}
