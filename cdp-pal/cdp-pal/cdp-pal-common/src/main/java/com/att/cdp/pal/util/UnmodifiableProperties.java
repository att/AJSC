/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * This utility class is used to wrap a properties object and to delegate all read operations to the property object,
 * while disallowing any write or modification to the property object.
 * 
 * @since Oct 8, 2013
 * @version $Id$
 */
public class UnmodifiableProperties extends Properties implements Cloneable {

    /**
     * Serial number
     */
    private static final long serialVersionUID = 1L;

    private static final String PROPERTY_CANNOT_BE_MODIFIED_MSG = "Property cannot be modified!";

    /**
     * The properties object which we are wrapping
     */
    private Properties properties;

    /**
     * Create the unmodifiable wrapper around the provided properties object
     * 
     * @param properties
     *            The properties to be wrapped and protected from modification
     */
    public UnmodifiableProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * @see java.util.Hashtable#clear()
     */
    @Override
    public synchronized void clear() {
        throw new UnsupportedOperationException(PROPERTY_CANNOT_BE_MODIFIED_MSG);
    }

    /**
     * @see java.util.Hashtable#clone()
     */
    @Override
    // @sonar:off
        public synchronized
        Object clone() {
        throw new UnsupportedOperationException(PROPERTY_CANNOT_BE_MODIFIED_MSG);
    }

    // @sonar:on

    /**
     * @see java.util.Hashtable#contains(java.lang.Object)
     */
    @Override
    public synchronized boolean contains(Object value) {
        return properties.contains(value);
    }

    /**
     * @see java.util.Hashtable#containsKey(java.lang.Object)
     */
    @Override
    public synchronized boolean containsKey(Object key) {
        return properties.containsKey(key);
    }

    /**
     * @see java.util.Hashtable#containsValue(java.lang.Object)
     */
    @Override
    public boolean containsValue(Object value) {
        return properties.containsValue(value);
    }

    /**
     * @see java.util.Hashtable#elements()
     */
    @Override
    public synchronized Enumeration<Object> elements() {
        return properties.elements();
    }

    /**
     * @see java.util.Hashtable#entrySet()
     */
    @Override
    public Set<java.util.Map.Entry<Object, Object>> entrySet() {
        return Collections.unmodifiableSet(properties.entrySet());
    }

    /**
     * @see java.util.Hashtable#equals(java.lang.Object)
     */
    @Override
    public synchronized boolean equals(Object o) {
        return properties.equals(o);
    }

    /**
     * @see java.util.Hashtable#get(java.lang.Object)
     */
    @Override
    public synchronized Object get(Object key) {
        return properties.get(key);
    }

    /**
     * @see java.util.Properties#getProperty(java.lang.String)
     */
    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * @see java.util.Properties#getProperty(java.lang.String, java.lang.String)
     */
    @Override
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * @see java.util.Hashtable#hashCode()
     */
    @Override
    public synchronized int hashCode() {
        return properties.hashCode();
    }

    /**
     * @see java.util.Hashtable#isEmpty()
     */
    @Override
    public synchronized boolean isEmpty() {
        return properties.isEmpty();
    }

    /**
     * @see java.util.Hashtable#keys()
     */
    @Override
    public synchronized Enumeration<Object> keys() {
        return properties.keys();
    }

    /**
     * @see java.util.Hashtable#keySet()
     */
    @Override
    public Set<Object> keySet() {
        return Collections.unmodifiableSet(properties.keySet());
    }

    /**
     * @see java.util.Properties#list(java.io.PrintStream)
     */
    @Override
    public void list(PrintStream out) {
        properties.list(out);
    }

    /**
     * @see java.util.Properties#list(java.io.PrintWriter)
     */
    @Override
    public void list(PrintWriter out) {
        properties.list(out);
    }

    /**
     * @see java.util.Properties#load(java.io.InputStream)
     */
    @Override
    public synchronized void load(InputStream inStream) throws IOException {
        throw new UnsupportedOperationException(PROPERTY_CANNOT_BE_MODIFIED_MSG);
    }

    /**
     * @see java.util.Properties#load(java.io.Reader)
     */
    @Override
    public synchronized void load(Reader reader) throws IOException {
        throw new UnsupportedOperationException(PROPERTY_CANNOT_BE_MODIFIED_MSG);
    }

    /**
     * @see java.util.Properties#loadFromXML(java.io.InputStream)
     */
    @Override
    public synchronized void loadFromXML(InputStream in) throws IOException, InvalidPropertiesFormatException {
        throw new UnsupportedOperationException(PROPERTY_CANNOT_BE_MODIFIED_MSG);
    }

    /**
     * @see java.util.Properties#propertyNames()
     */
    @Override
    public Enumeration<?> propertyNames() {
        return properties.propertyNames();
    }

    /**
     * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized Object put(Object key, Object value) {
        throw new UnsupportedOperationException(PROPERTY_CANNOT_BE_MODIFIED_MSG);
    }

    /**
     * @see java.util.Hashtable#putAll(java.util.Map)
     */
    @Override
    public synchronized void putAll(Map<? extends Object, ? extends Object> t) {
        throw new UnsupportedOperationException(PROPERTY_CANNOT_BE_MODIFIED_MSG);
    }

    /**
     * @see java.util.Hashtable#rehash()
     */
    @Override
    protected void rehash() {
        throw new UnsupportedOperationException(PROPERTY_CANNOT_BE_MODIFIED_MSG);
    }

    /**
     * @see java.util.Hashtable#remove(java.lang.Object)
     */
    @Override
    public synchronized Object remove(Object key) {
        throw new UnsupportedOperationException(PROPERTY_CANNOT_BE_MODIFIED_MSG);
    }

    /**
     * @see java.util.Properties#save(java.io.OutputStream, java.lang.String)
     */
    @Override
    @Deprecated
    public synchronized void save(OutputStream out, String comments) {
        properties.save(out, comments);
    }

    /**
     * @see java.util.Properties#setProperty(java.lang.String, java.lang.String)
     */
    @Override
    public synchronized Object setProperty(String key, String value) {
        throw new UnsupportedOperationException(PROPERTY_CANNOT_BE_MODIFIED_MSG);
    }

    /**
     * @see java.util.Hashtable#size()
     */
    @Override
    public synchronized int size() {
        return properties.size();
    }

    /**
     * @see java.util.Properties#store(java.io.OutputStream, java.lang.String)
     */
    @Override
    public void store(OutputStream out, String comments) throws IOException {
        properties.store(out, comments);
    }

    /**
     * @see java.util.Properties#store(java.io.Writer, java.lang.String)
     */
    @Override
    public void store(Writer writer, String comments) throws IOException {
        properties.store(writer, comments);
    }

    /**
     * @see java.util.Properties#storeToXML(java.io.OutputStream, java.lang.String)
     */
    @Override
    public synchronized void storeToXML(OutputStream os, String comment) throws IOException {
        properties.storeToXML(os, comment);
    }

    /**
     * @see java.util.Properties#storeToXML(java.io.OutputStream, java.lang.String, java.lang.String)
     */
    @Override
    public synchronized void storeToXML(OutputStream os, String comment, String encoding) throws IOException {
        properties.storeToXML(os, comment, encoding);
    }

    /**
     * @see java.util.Properties#stringPropertyNames()
     */
    @Override
    public Set<String> stringPropertyNames() {
        return properties.stringPropertyNames();
    }

    /**
     * @see java.util.Hashtable#toString()
     */
    @Override
    public synchronized String toString() {
        return properties.toString();
    }

    /**
     * @see java.util.Hashtable#values()
     */
    @Override
    public Collection<Object> values() {
        return Collections.unmodifiableCollection(properties.values());
    }
}
