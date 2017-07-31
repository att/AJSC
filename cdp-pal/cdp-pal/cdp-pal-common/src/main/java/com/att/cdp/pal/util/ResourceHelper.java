/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides the ability to scan the class path for resources that match a provided name pattern.
 * 
 * @since Apr 11, 2014
 * @version $Id$
 */
public final class ResourceHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceHelper.class);

    /**
     * Find resources for a specified class using the provided name pattern.
     * 
     * @param clazz
     *            The class that we will use to determine the code base. The code base is where this particular class
     *            was loaded from. If that was a jar file, then the location will be the jar file that loaded this
     *            class. If the location was a file system directory (as in an open deployment or when testing under
     *            eclipse), then the location will be the directory where the class was loaded from (the classes package
     *            will be relative to that location). In either case, the code base for the specified class is where the
     *            resources will be searched. This is not a general class path search, only the location where the
     *            specified class was loaded from.
     * @param namePattern
     *            The name pattern to be matched, which can include relative path information. This can be a regular
     *            expression or a fixed name.
     * @return The array of URLs that reference all resources that match the specified name in the specified code base.
     */
    public static URL[] findResources(Class<?> clazz, String namePattern) {
        List<URL> list = new ArrayList<URL>();

        CodeSource source = clazz.getProtectionDomain().getCodeSource();
        URL location = source.getLocation();

        if (location.getFile().endsWith(".jar")) {
            scanJarFile(namePattern, location, list);
        } else {
            scanDirectory(namePattern, location, list);
        }

        URL[] array = new URL[list.size()];
        return list.toArray(array);
    }

    /**
     * Scan a file system directory
     * 
     * @param path
     * @param namePattern
     * @param location
     * @param list
     */
    private static void scanDirectory(String namePattern, URL location, List<URL> list) {
        StringBuffer buffer = new StringBuffer(namePattern);
        if (buffer.charAt(0) == '/') {
            buffer.deleteCharAt(0);
        }
        if (buffer.charAt(buffer.length() - 1) == '/') {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        String[] elements = buffer.toString().split("/");
        buffer.setLength(0);
        for (int index = 0; index < elements.length - 1; index++) {
            buffer.append(elements[index] + '/');
        }

        final Pattern pattern = Pattern.compile(elements[elements.length - 1]);
        try {
            File dir = new File(location.toURI());
            dir = new File(dir, buffer.toString());
            String[] filenames = dir.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    Matcher matcher = pattern.matcher(name);
                    if (matcher.matches()) {
                        return true;
                    }
                    return false;
                }
            });

            if (filenames != null) {
                for (String filename : filenames) {
                    File file = new File(dir, filename);
                    try {
                        list.add(file.toURI().toURL());
                    } catch (MalformedURLException e) {
                        LOG.error("scanDirectory", e);
                    }
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace(System.out);
        }

    }

    /**
     * Scan a jar file
     * 
     * @param path
     * @param namePattern
     * @param location
     * @param list
     */
    private static void scanJarFile(String namePattern, URL location, List<URL> list) {

        StringBuffer buffer = new StringBuffer(namePattern);
        if (buffer.charAt(0) == '/') {
            buffer.deleteCharAt(0);
        }
        if (buffer.charAt(buffer.length() - 1) == '/') {
            buffer.deleteCharAt(buffer.length() - 1);
        }

        Pattern pattern = Pattern.compile(buffer.toString());
        ZipInputStream zip = null;
        try {
            zip = new ZipInputStream(location.openStream());
            ZipEntry entry = zip.getNextEntry();
            while (entry != null) {
                String entryName = entry.getName();
                Matcher matcher = pattern.matcher(entryName);
                if (matcher.matches()) {
                    String jarUrl = "jar:" + location.toString() + "!/" + entryName;
                    list.add(new URL(jarUrl));
                }
                entry = zip.getNextEntry();
            }
        } catch (IOException e) {
            LOG.error("scanJarFile", e);
        } finally {
            if (zip != null) {
                try {
                    zip.close();
                } catch (IOException e) {
                    e.printStackTrace(System.out);
                }
            }
        }
    }

    /**
     * Private constructor prevents anyone from instantiating this class
     */
    private ResourceHelper() {

    }
}
