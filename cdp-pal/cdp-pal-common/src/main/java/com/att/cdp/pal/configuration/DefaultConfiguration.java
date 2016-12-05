/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.configuration;

/**
 * Copyright (C) 2013, AT&T Inc. All rights reserved. Proprietary materials, property of AT&T. For internal use only,
 * not for disclosure to parties outside of AT&T or its affiliates.
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.att.cdp.pal.util.UnmodifiableProperties;

/**
 * This class provides the implementation of the <code>Configuration</code> interface. It is created by the
 * ConfigurationFactory and initialized with the configuration values for the process.
 * 
 * @since Mar 18, 2014
 * @version $Id$
 */
public final class DefaultConfiguration implements Configuration, Cloneable {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConfiguration.class);

    /**
     * The logger to be used to record general application log events
     */
    private Logger applicationLogger;

    /**
     * The logger to be used to record coordinator application (non-performance and non-security) events
     */
    private Logger coordinatorLogger;

    /**
     * The logger to be used to record gui application (non-performance and non-security) events
     */
    private Logger guiLogger;

    /**
     * This lock is used to serialize access to create the loggers
     */
    private Object loggerLock = new Object();

    /**
     * The logger to be used to record policy manager application (non-performance and non-security) events
     */
    private Logger nodeDataLogger;

    /**
     * The logger to be used to record performance records
     */
    private Logger performanceLogger;

    /**
     * The logger to be used to record policy manager application (non-performance and non-security) events
     */
    private Logger policyLogger;

    /**
     * The framework configuration properties.
     */
    private Properties properties = new Properties();

    /**
     * The logger to be used to record security events
     */
    private Logger securityLogger;

    /**
     * The logger to be used to record server application (non-performance and non-security) events
     */
    private Logger serverLogger;

    /**
     * The date (timestamp) that the system was started.
     */
    private Date systemStartTime = new Date(System.currentTimeMillis());

    /**
     * SWMBootstrap script will be populated at start time
     */
    private String swmBootstrapScript;

    /**
     * Construct the configuration object.
     */
    public DefaultConfiguration() {
    }

    /**
     * Clears all properties
     */
    public void clear() {
        properties.clear();
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        DefaultConfiguration clone = (DefaultConfiguration) super.clone();

        clone.properties = new Properties(this.properties);
        clone.applicationLogger = this.applicationLogger;
        clone.coordinatorLogger = this.coordinatorLogger;
        clone.guiLogger = this.guiLogger;
        clone.nodeDataLogger = this.nodeDataLogger;
        clone.performanceLogger = this.performanceLogger;
        clone.policyLogger = this.policyLogger;
        clone.securityLogger = this.securityLogger;
        clone.serverLogger = this.serverLogger;

        clone.properties.putAll(this.properties);

        return clone;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        DefaultConfiguration other = (DefaultConfiguration) obj;

        if ( (this.properties.size() == other.properties.size()) && 
        	 (this.properties.entrySet().containsAll(other.properties.entrySet())) && 
        	 (other.properties.entrySet().containsAll(this.properties.entrySet())) ) {
                return true;
        }

        return false;
    }

    /**
     * This method will use the properties object to expand any variables that may be present in the template provided.
     * Variables are represented by the string "${name}", where "name" is the name of a property defined in either the
     * current configuration object, or system properties if undefined. If the value cannot be found, the variable is
     * removed and an empty string is used to replace the variable.
     * 
     * @param template
     *            The template to be expanded
     * @return The expanded template where each variable is replaced with its value
     */
    private String expandVariables(String template) {
        if (template == null) {
            return template;
        }
        StringBuffer buffer = new StringBuffer(template);
        Pattern pattern = Pattern.compile("\\$\\{([^\\}]+)\\}");
        Matcher matcher = pattern.matcher(buffer);
        while (matcher.find()) {
            String variable = matcher.group(1);
            String value = properties.getProperty(variable);
            if (value == null) {
                value = System.getProperty(variable);
            }
            if (value == null) {
                value = "";
            }
            buffer.replace(matcher.start(), matcher.end(), value);

            matcher.reset();
        }
        return buffer.toString().trim();
    }

    /**
     * Returns the logger to be used to record general application log events. This logger is the same logger that is
     * represented by the getApplicationLoggerName() method.
     * 
     * @return The logger to be used
     * @see com.att.cdp.pal.configuration.Configuration#getApplicationLogger()
     */
    @Override
    public Logger getApplicationLogger() {
        synchronized (loggerLock) {
            if (applicationLogger == null) {
                String name = getApplicationLoggerName();
                if (name == null) {
                    name = "CDP";
                }

                applicationLogger = LoggerFactory.getLogger(name);
            }
        }
        return applicationLogger;
    }

    /**
     * The application logger is used for all general-purpose log events. This application log is used to record
     * operations being performed, debug and trace entries, errors and warnings, and the like. It contains all
     * general-purpose log entries.
     * 
     * @return The name of the logger to be used
     * @see com.att.cdp.pal.configuration.Configuration#getApplicationLoggerName()
     */
    @Override
    public String getApplicationLoggerName() {
        return getProperty(PROPERTY_GENERAL_LOGGER_NAME);
    }

    /**
     * This method is called to obtain a property expressed as a boolean value (true or false). The standard rules for
     * Boolean.parseBoolean() are used.
     * 
     * @param key
     *            The property key
     * @return The value of the property expressed as a boolean, or false if it does not exist.
     */
    @Override
    public boolean getBooleanProperty(String key) {
        return Boolean.valueOf(getProperty(key, "false")).booleanValue();
    }

    /**
     * This method is called to obtain a property expressed as a boolean value (true or false). The standard rules for
     * Boolean.valueOf(String) are used.
     * 
     * @param key
     *            The property key
     * @param defaultValue
     *            The default value to be returned if the property does not exist
     * @return The value of the property expressed as a boolean, or false if it does not exist.
     * @see com.att.cdp.pal.configuration.Configuration#getBooleanProperty(java.lang.String, boolean)
     */
    @Override
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        if (isPropertyDefined(key)) {
            return getBooleanProperty(key);
        }
        return defaultValue;
    }

    /**
     * Returns the logger to be used to record coordinator-based general application log events. This logger is the same
     * logger that is represented by the getCoordinatorLoggerName() method.
     * 
     * @return The logger to be used
     * @see com.att.cdp.pal.configuration.Configuration#getCoordinatorLogger()
     */
    @Override
    public Logger getCoordinatorLogger() {
        synchronized (loggerLock) {
            if (coordinatorLogger == null) {
                coordinatorLogger = LoggerFactory.getLogger(getCoordinatorLoggerName());
            }
        }
        return coordinatorLogger;
    }

    /**
     * The coordinator logger is used to segregate events originating within the cdp coordinator from other application
     * events. These are events that are general application logging related events (not security or performance) and
     * which may be directed to different appenders where needed. Application code in the cdp coordinator component
     * should use this logger for all general logging.
     * 
     * @return The name of the coordinator logger
     * @see com.att.cdp.pal.configuration.Configuration#getCoordinatorLoggerName()
     */
    @Override
    public String getCoordinatorLoggerName() {
        return getProperty(PROPERTY_COORDINATOR_LOGGER_NAME);
    }

    /**
     * Returns the indicated property value expressed as a floating point double-precision value (double).
     * 
     * @param key
     *            The property to retrieve
     * @return The value of the property, or 0.0 if not found
     * @see com.att.cdp.pal.configuration.Configuration#getDoubleProperty(java.lang.String)
     */
    @Override
    public double getDoubleProperty(String key) {
        try {
            return Double.valueOf(getProperty(key, "0.0")).doubleValue();
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * This method is called to obtain a property as a string value
     * 
     * @param key
     *            The key of the property
     * @param defaultValue
     *            The default value to be returned if the property does not exist
     * @return The string value, or null if it does not exist.
     * @see com.att.cdp.pal.configuration.Configuration#getDoubleProperty(java.lang.String, double)
     */
    @Override
    public double getDoubleProperty(String key, double defaultValue) {
        if (isPropertyDefined(key)) {
            return getDoubleProperty(key);
        }
        return defaultValue;
    }

    /**
     * Returns the logger to be used to record GUI-based general application log events. This logger is the same logger
     * that is represented by the getGUILoggerName() method.
     * 
     * @return The logger to be used
     * @see com.att.cdp.pal.configuration.Configuration#getGUILogger()
     */
    @Override
    public Logger getGUILogger() {
        synchronized (loggerLock) {
            if (guiLogger == null) {
                guiLogger = LoggerFactory.getLogger(getGUILoggerName());
            }
        }
        return guiLogger;
    }

    /**
     * The GUI logger is used to segregate events originating within the cdp GUI from other application events. These
     * are events that are general application logging related events (not security or performance) and which may be
     * directed to different appenders where needed. Application code in the cdp GUI component should use this logger
     * for all general logging.
     * 
     * @return The name of the GUI logger
     * @see com.att.cdp.pal.configuration.Configuration#getGUILoggerName()
     */
    @Override
    public String getGUILoggerName() {
        return getProperty(PROPERTY_GUI_LOGGER_NAME);
    }

    /**
     * Returns the property indicated expressed as an integer. The standard rules for
     * {@link Integer#parseInt(String, int)} using a radix of 10 are used.
     * 
     * @param key
     *            The property name to retrieve.
     * @returns The value of the property, or 0 if it does not exist or is invalid.
     * @see com.att.cdp.pal.configuration.Configuration#getIntegerProperty(java.lang.String)
     */
    @Override
    public int getIntegerProperty(String key) {
        try {
            return Integer.parseInt(getProperty(key, "0"), 10);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Returns the property indicated expressed as an integer. The standard rules for Integer.parseInt(String, int)
     * using a radix of 10 are used.
     * 
     * @param key
     *            The property name to retrieve.
     * @param defaultValue
     *            The default value to be returned if the property does not exist
     * @return The value of the property, or 0 if it does not exist or is invalid.
     * @see com.att.cdp.pal.configuration.Configuration#getIntegerProperty(java.lang.String, int)
     */
    @Override
    public int getIntegerProperty(String key, int defaultValue) {
        if (isPropertyDefined(key)) {
            return getIntegerProperty(key);
        }
        return defaultValue;
    }

    /**
     * Returns the specified property as a long integer value, if it exists, or zero if it does not.
     * 
     * @param key
     *            The key of the property desired.
     * @return The value of the property expressed as an integer long value, or zero if the property does not exist or
     *         is not a valid integer long.
     * @see com.att.cdp.pal.configuration.Configuration#getLongProperty(java.lang.String)
     */
    @Override
    public long getLongProperty(String key) {
        try {
            return Long.parseLong(getProperty(key, "0"), 10);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

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
     * @see com.att.cdp.pal.configuration.Configuration#getLongProperty(java.lang.String, long)
     */
    @Override
    public long getLongProperty(String key, long defaultValue) {
        if (isPropertyDefined(key)) {
            return getLongProperty(key);
        }
        return defaultValue;
    }

    /**
     * Returns the logger to be used to record policy manager-based general application log events. This logger is the
     * same logger that is represented by the getPolicyLoggerName() method.
     * 
     * @return The logger to be used
     * @see com.att.cdp.pal.configuration.Configuration#getNodeDataLogger()
     */
    @Override
    public Logger getNodeDataLogger() {
        synchronized (loggerLock) {
            if (nodeDataLogger == null) {
                nodeDataLogger = LoggerFactory.getLogger(getNodeDataLoggerName());
            }
        }
        return nodeDataLogger;
    }

    /**
     * The policy logger is used to segregate events originating within the cdp policy manager from other application
     * events. These are events that are general application logging related events (not security or performance) and
     * which may be directed to different appenders where needed. Application code in the cdp policy manager component
     * should use this logger for all general logging.
     * 
     * @return The name of the policy logger
     * @see com.att.cdp.pal.configuration.Configuration#getNodeDataLoggerName()
     */
    @Override
    public String getNodeDataLoggerName() {
        return getProperty(PROPERTY_NODEDATA_LOGGER_NAME);
    }

    /**
     * Returns the logger to be used to record performance log events. This logger is the same logger that is
     * represented by the getPerformanceLoggerName() method.
     * 
     * @return The logger to be used
     * @see com.att.cdp.pal.configuration.Configuration#getPerformanceLogger()
     */
    @Override
    public Logger getPerformanceLogger() {
        synchronized (loggerLock) {
            if (performanceLogger == null) {
                performanceLogger = LoggerFactory.getLogger(getPerformanceLoggerName());
            }
        }
        return performanceLogger;
    }

    /**
     * Auditing of the performance of the application may require much more structured log events, such as start and
     * stop times, counts, standard deviations, and other performance data. This information can make the general log
     * much more difficult to read and process if intermixed. Fr this reason, the application records all performance
     * log events to the performance logger (perflog) so that it can be configured separately from the other loggers.
     * 
     * @return The name of the performance logger
     * @see com.att.cdp.pal.configuration.Configuration#getPerformanceLoggerName()
     */
    @Override
    public String getPerformanceLoggerName() {
        return getProperty(PROPERTY_PERF_LOGGER_NAME);
    }

    /**
     * Returns the logger to be used to record policy manager-based general application log events. This logger is the
     * same logger that is represented by the getPolicyLoggerName() method.
     * 
     * @return The logger to be used
     * @see com.att.cdp.pal.configuration.Configuration#getPolicyLogger()
     */
    @Override
    public Logger getPolicyLogger() {
        synchronized (loggerLock) {
            if (policyLogger == null) {
                policyLogger = LoggerFactory.getLogger(getPolicyLoggerName());
            }
        }
        return policyLogger;
    }

    /**
     * The policy logger is used to segregate events originating within the cdp policy manager from other application
     * events. These are events that are general application logging related events (not security or performance) and
     * which may be directed to different appenders where needed. Application code in the cdp policy manager component
     * should use this logger for all general logging.
     * 
     * @return The name of the policy logger
     * @see com.att.cdp.pal.configuration.Configuration#getPolicyLoggerName()
     */
    @Override
    public String getPolicyLoggerName() {
        return getProperty(PROPERTY_POLICY_LOGGER_NAME);
    }

    /**
     * This method can be called to retrieve a properties object that is immutable. Any attempt to modify the properties
     * object returned will result in an exception. This allows a caller to view the current configuration as a set of
     * properties.
     * 
     * @return An unmodifiable properties object.
     * @see com.att.cdp.pal.configuration.Configuration#getProperties()
     */
    @Override
    public Properties getProperties() {
        return new UnmodifiableProperties(properties);
    }

    /**
     * This method is called to obtain a property as a string value
     * 
     * @param key
     *            The key of the property
     * @return The string value, or null if it does not exist.
     */
    @Override
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            return null;
        }
        return expandVariables(value.trim());
    }

    /**
     * This method is called to obtain a property as a string value
     * 
     * @param key
     *            The key of the property
     * @param defaultValue
     *            The default value to be returned if the property does not exist
     * @return The string value, or null if it does not exist.
     * @see com.att.cdp.pal.configuration.Configuration#getProperty(java.lang.String, java.lang.String)
     */
    @Override
    public String getProperty(String key, String defaultValue) {
        if (isPropertyDefined(key)) {
            return getProperty(key);
        }

        if (defaultValue == null) {
            return null;
        }

        return expandVariables(defaultValue.trim());
    }

    /**
     * Returns the logger to be used to record security log events. This logger is the same logger that is represented
     * by the getSecurityLoggerName() method.
     * 
     * @return The logger to be used
     * @see com.att.cdp.pal.configuration.Configuration#getSecurityLogger()
     */
    @Override
    public Logger getSecurityLogger() {
        synchronized (loggerLock) {
            if (securityLogger == null) {
                securityLogger = LoggerFactory.getLogger(getSecurityLoggerName());
            }
        }
        return securityLogger;
    }

    /**
     * Because security auditing may contain sensitive information, and because it may need to be segregated from the
     * application general logger and log file(s), the application uses a separate logger for security logging. Security
     * logging includes log events such as login, logout, access violations, resource access, and other information that
     * may be needed to perform a security audit of the application.
     * 
     * @return The specific logger name to be used for security event logging
     * @see com.att.cdp.pal.configuration.Configuration#getSecurityLoggerName()
     */
    @Override
    public String getSecurityLoggerName() {
        return getProperty(PROPERTY_SECURITY_LOGGER_NAME);
    }

    /**
     * Returns the logger to be used to record server-based general application log events. This logger is the same
     * logger that is represented by the getServerLoggerName() method.
     * 
     * @return The logger to be used
     * @see com.att.cdp.pal.configuration.Configuration#getServerLogger()
     */
    @Override
    public Logger getServerLogger() {
        synchronized (loggerLock) {
            if (serverLogger == null) {
                serverLogger = LoggerFactory.getLogger(getServerLoggerName());
            }
        }
        return serverLogger;
    }

    /**
     * The server logger is used to segregate events originating within the server from other application events. These
     * are events that are general application logging related events (not security or performance) and which may be
     * directed to different appenders where needed. Application code in the server component should use this logger for
     * all general logging.
     * 
     * @return The name of the server logger
     * @see com.att.cdp.pal.configuration.Configuration#getServerLoggerName()
     */
    @Override
    public String getServerLoggerName() {
        return getProperty(PROPERTY_SERVER_LOGGER_NAME);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return (properties == null ? 0 : properties.hashCode());
    }

    /**
     * Returns true if the named property is defined, false otherwise.
     * 
     * @param key
     *            The key of the property we are interested in
     * @return True if the property exists.
     */
    @Override
    public boolean isPropertyDefined(String key) {
        return properties.containsKey(key);
    }

    /**
     * @return True if the stack builder framework is running in simulation mode
     */
    @Override
    public boolean isSimulation() {
        return getBooleanProperty(PROPERTY_SIMULATION);
    }

    /**
     * Returns an indication of the validity of the boolean property. A boolean property is considered to be valid only
     * if it has the value "true" or "false" (ignoring case).
     * 
     * @param key
     *            The property to be checked
     * @returns True if the value is a boolean constant, or false if it does not exist or is not a correct string
     * @see com.att.cdp.pal.configuration.Configuration#isValidBoolean(java.lang.String)
     */
    @Override
    public boolean isValidBoolean(String key) {
        String value = getProperty(key);
        if (value != null) {
            value = value.toLowerCase();
            return value.matches("true|false");
        }
        return false;
    }

    /**
     * Returns an indication if the indicated property represents a valid double-precision floating point number.
     * 
     * @param key
     *            The property to be examined
     * @returns True if the property is a valid representation of a double, or false if it does not exist or contains
     *          illegal characters.
     * @see com.att.cdp.pal.configuration.Configuration#isValidDouble(java.lang.String)
     */
    @Override
    public boolean isValidDouble(String key) {
        String value = getProperty(key);
        if (value != null) {
            try {
                Double.valueOf(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Returns an indication if the property is a valid integer value or not.
     * 
     * @param key
     *            The key of the property to check
     * @returns True if the value is a valid integer string, or false if it does not exist or contains illegal
     *          characters.
     * @see com.att.cdp.pal.configuration.Configuration#isValidInteger(java.lang.String)
     */
    @Override
    public boolean isValidInteger(String key) {
        String value = getProperty(key);
        if (value != null) {
            try {
                Integer.parseInt(value.trim(), 10);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Determines is the specified property exists and is a valid representation of an integer long value.
     * 
     * @param key
     *            The property to be checked
     * @return True if the property is a valid representation of an integer long value, and false if it either does not
     *         exist or is not valid.
     * @see com.att.cdp.pal.configuration.Configuration#isValidLong(java.lang.String)
     */
    @Override
    public boolean isValidLong(String key) {
        String value = getProperty(key);
        if (value != null) {
            try {
                Long.parseLong(value.trim(), 10);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * This method allows an implementation to load configuration properties that may override default values.
     * 
     * @param is
     *            An input stream that contains the properties to be loaded
     */
    public void setProperties(InputStream is) {
        try {
            properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method allows an implementation to load configuration properties that may override default values.
     * 
     * @param props
     *            An optional Properties object to be merged into the configuration, replacing any same-named
     *            properties.
     * @see com.att.cdp.pal.configuration.Configuration#setProperties(java.util.Properties)
     */
    @Override
    public void setProperties(Properties props) {
        properties.putAll(props);
    }

    /**
     * This method allows a caller to insert a new property definition into the configuration object. This allows the
     * application to adjust or add to the current configuration. If the property already exists, it is replaced with
     * the new value.
     * 
     * @param key
     *            The key of the property to be defined
     * @param value
     *            The value of the property to be defined
     * @see com.att.cdp.pal.configuration.Configuration#setProperty(java.lang.String, java.lang.String)
     */
    @Override
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("Configuration: %d properties, keys:[%s]", properties.size(), properties.keySet()
            .toString());
    }

    /**
     * Returns the version of the system as a string
     * 
     * @see com.att.cdp.pal.configuration.Configuration#getVersion()
     */
    @Override
    public String getVersion() {
        Manifest manifest = getManifest();
        /*
         * If no manifest, fall back to properties
         */
        if (manifest == null) {
            return getProperty("CDP_VERSION");
        }

        Map<String, Attributes> entries = manifest.getEntries();
        Attributes attributes = entries.get("CDP");

        return attributes.getValue("version");
    }

    /**
     * Returns the date that the system was built as a string
     * 
     * @see com.att.cdp.pal.configuration.Configuration#getBuildDate()
     */
    @Override
    public String getBuildDate() {
        Manifest manifest = getManifest();
        /*
         * If no manifest, fall back to properties
         */
        if (manifest == null) {
            return getProperty("CDP_BUILD");
        }

        Map<String, Attributes> entries = manifest.getEntries();
        Attributes attributes = entries.get("CDP");

        return attributes.getValue("buildDate");
    }

    /**
     * Returns the date that the system started formatted as a string.
     * 
     * @see com.att.cdp.pal.configuration.Configuration#getSystemTimeStartedFormatted()
     */
    @Override
    public String getSystemTimeStartedFormatted() {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss-ZZZZ");
        return formatter.format(systemStartTime);
    }

    /**
     * Computes and formats the duration as a string, of the form <days>.<hours>:<minutes>:<seconds>, such as
     * 1.02:45:33.
     * 
     * @see com.att.cdp.pal.configuration.Configuration#getSystemRunTime()
     */
    @Override
    public String getSystemRunTimeFormatted() {
        long duration = getSystemRunTime();

        long days = duration / 86400000L;
        duration %= 86400000L;
        long hours = duration / 3600000L;
        duration %= 3600000L;
        long minutes = duration / 60000L;
        duration %= 60000L;
        long seconds = duration / 1000;

        return String.format("%d.%02d:%02d:%02d", days, hours, minutes, seconds);
    }

    /**
     * Returns the system run time in milliseconds
     * 
     * @see com.att.cdp.pal.configuration.Configuration#getSystemRunTime()
     */
    @Override
    public Long getSystemRunTime() {
        Date now = new Date();
        return (now.getTime() - systemStartTime.getTime());
    }

    /**
     * This is a helper method to read the manifest of the jar file that this class was loaded from. Note that this will
     * only work if the code is packaged in a jar file. If it is an open deployment, such as under eclipse, this will
     * not work and there is code added to detect that case.
     * 
     * @return The manifest object from the jar file, or null if the code is not packaged in a jar file.
     */
    private Manifest getManifest() {
        ProtectionDomain domain = getClass().getProtectionDomain();
        CodeSource source = domain.getCodeSource();
        URL location = source.getLocation();
        String path = location.getPath();
        int index = path.indexOf('!');
        if (index != -1) {
            path = path.substring(0, index);
        }
        if (path.endsWith(".jar")) {
            JarFile jar = null;
            try {
                jar = new JarFile(location.getFile());
                return jar.getManifest();
            } catch (IOException e) {
                LOG.error("getManifest", e);
            } finally {
                if (jar != null) {
                    try {
                        jar.close();
                    } catch (IOException e) {
                        // IGNORE
                        return null;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Retrieve the SWM Bootstrap script required for installing SWM Node, CLI and platform-init on newly provisioned
     * VMs
     * 
     * @return The initialization script to be used
     */
    @Override
    public String getSWMBootstrapScript() {
        return swmBootstrapScript;
    }

    /**
     * This method allows a caller to insert the agentScript into the configuration object.
     * 
     * @param key
     *            The contents of the agentScript loaded during startup
     */
    public void populateScript(String agentScript) {
        this.swmBootstrapScript = agentScript;
    }
}
