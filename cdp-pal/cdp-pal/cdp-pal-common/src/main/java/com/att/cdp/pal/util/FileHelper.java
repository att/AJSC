/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.pal.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.Properties;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class contains methods to assist in manipulating files, file names, and paths.
 * 
 * @since Aug 24, 2011
 * @version $Id: FileHelper.java 60787 2012-01-17 20:37:34Z dh868g $
 */

public final class FileHelper {

    /**
     * Private constructor to prevent anyone from instantiating this class.
     */
    private FileHelper() {
        /* intentionally empty */
    }

    /**
     * This method <em>normalizes</em> a path string by removing all self and parent references, changing all backward
     * slashes to forward slashes, and removing any trailing "/".
     * <p>
     * This method must work with simple paths, as well as path lists.
     * </p>
     * 
     * @param path
     *            The path string to be normalized
     * @return The normalized path string
     */
    public static String normalizePath(final String path) {
        if (path == null) {
            return null;
        }

        StringBuffer buffer = new StringBuffer(path);
        Pattern pattern = Pattern.compile("\\\\");
        Matcher matcher = pattern.matcher(buffer);
        int position = 0;

        /*
         * Change all back-slashes to forward-slashes (to make the rest easier)
         */
        while (matcher.find(position)) {
            position = matcher.start();
            buffer.setCharAt(position, '/');
        }

        /*
         * The odd-ball case of a path from a URL ends up with a leading slash, drive letter, and colon. So, look for
         * that and remove it if present.
         */
        pattern = Pattern.compile("^/[a-zA-Z]:");
        matcher = pattern.matcher(buffer);
        if (matcher.find()) {
            buffer.delete(0, 1);
        }

        /*
         * Remove any parent path specifiers (..)
         */
        pattern = Pattern.compile("/[^/]+/\\.\\.|[^/]+/\\.\\./");
        matcher = pattern.matcher(buffer);
        position = 0;
        while (matcher.find(position)) {
            position = matcher.start();
            buffer.delete(matcher.start(), matcher.end());
        }

        /*
         * Remove any self specifiers (.), such as ./path, path/., or path/./path
         */
        pattern = Pattern.compile("/\\./");
        matcher = pattern.matcher(buffer);
        position = 0;
        while (matcher.find(position)) {
            position = matcher.start();
            buffer.delete(matcher.start(), matcher.end() - 1);
        }

        pattern = Pattern.compile("^\\./");
        matcher = pattern.matcher(buffer);
        position = 0;
        while (matcher.find(position)) {
            position = matcher.start();
            buffer.delete(matcher.start(), matcher.end());
        }

        pattern = Pattern.compile("/\\.$");
        matcher = pattern.matcher(buffer);
        position = 0;
        while (matcher.find(position)) {
            position = matcher.start();
            buffer.delete(matcher.start(), matcher.end());
        }

        /*
         * Remove trailing slash if it exists
         */
        if (buffer.charAt(buffer.length() - 1) == '/') {
            buffer.delete(buffer.length() - 1, buffer.length());
        }
        return buffer.toString();
    }

    /**
     * This method is used to expand a symbolic path containing wildcards or property references to a set of paths that
     * match the symbolic one.
     * <p>
     * The use of an asterisk (*) in a path is interpreted as a wildcard, and will match zero or more characters. It can
     * occur as prefix, postfix, or infix within a string to be matched, and can occur multiple times in the same string
     * as long as they are separated by at least one non-asterisk.
     * </p>
     * <p>
     * The use of a double asterisk (**) in a path means to match any number of directories, including zero, of any
     * name.
     * </p>
     * <p>
     * Property values may be substituted into the path by supplying a property object containing the values, and a
     * reference in the path using the same syntax as an Ant or Maven symbolic reference. That is, <code>${name}</code>
     * where <code>name</code> is the name of a property supplied. The value of the property replaces the reference in
     * the string. Substitution is recursive, so if the property value is another property reference, it will be
     * substituted as well.
     * </p>
     * 
     * @param properties
     *            A properties object that contains values to be substituted into the path, or null if no property
     *            substitution is to be performed.
     * @param path
     *            The base path which may contain symbolic references and/or wildcards that need to be resolved.
     * @return The list of all possible paths that exist based on the expanded path and wild-card processing
     */
    public static String[] expandSymbolicPath(final Properties properties, final String path) {
        if (path == null) {
            return null;
        }

        String temp = path.trim();
        if (temp.length() == 0) {
            return new String[] { temp };
        }

        /*
         * Expand any symbolic references first
         */
        Pattern pattern = null;
        Matcher matcher = null;
        StringBuffer buffer = new StringBuffer(temp);

        if (properties != null) {
            pattern = Pattern.compile("\\$\\{(\\w)+\\}");
            matcher = pattern.matcher(buffer);
            while (matcher.find()) {
                String name = buffer.substring(matcher.start() + 2, matcher.end() - 1);
                String value = properties.getProperty(name);
                if (value == null) {
                    value = "";
                }
                buffer.replace(matcher.start(), matcher.end(), value);
                matcher.reset();
            }
        }

        /*
         * Check to see if there are any wildcards or not. If there are, then use the path as a pattern to determine all
         * of the paths that match that pattern. If not, then simply return the buffer as an array of one.
         */
        if (buffer.indexOf("*") == -1) {
            return new String[] { buffer.toString() };
        }

        temp = normalizePath(buffer.toString());
        pattern = Pattern.compile("^(\\p{Alpha}:)?/");
        matcher = pattern.matcher(temp);
        File base = null;
        File position = null;
        if (matcher.matches()) {
            base = null;
            position = new File(temp.substring(0, matcher.end()));
            temp = temp.substring(matcher.end());
        } else {
            base = new File(".");
            position = new File(normalizePath(new File(".").getAbsolutePath()));
        }
        TreeSet<String> results = new TreeSet<String>();
        temp = temp.replaceAll("[.]", "\\\\.");
        temp = temp.replaceAll("([^*])\\*([^*])", "$1.*$2");
        String[] parts = temp.split("/");
        int index = 0;
        pathSearch(results, base, position, parts, index);
        String[] array = new String[results.size()];
        return results.toArray(array);
    }

    /**
     * This method is used by the <code>expandSymbolicPath</code> method only and provides recursive path traversal and
     * matching to the path pattern(s).
     * 
     * @param results
     *            The set of all path names that are found that match the specified pattern
     * @param base
     *            The starting point if relative, or null if absolute
     * @param position
     *            The current position in the recursive descent
     * @param parts
     *            The array of pattern elements that are matched
     * @param index
     *            The current position in the pattern array
     */
    protected static void pathSearch(final TreeSet<String> results, final File base, final File position,
                    final String[] parts, final int index) {
        /*
         * See if we have reached the end. Successfully matching will mean we have run out of parts to match, and since
         * we are only called when there is a match, then we know that what we have found along the way is a match. On
         * the other hand, if we find a file and we still have parts left, we have to end as well, but it is not a match
         * at that point.
         */
        if (index + 1 == parts.length) {
            if (base == null) {
                results.add(normalizePath(position.getAbsolutePath()));
            } else {
                String basepath = normalizePath(base.getAbsolutePath());
                String matchedpath = normalizePath(position.getAbsolutePath());
                results.add(matchedpath.substring(basepath.length() + 1));
            }
            return; /* Success */
        } else if (!position.isDirectory()) {
            return; /* Failure */
        }

        /*
         * Get the list of the contents of the current position
         */
        String[] items = position.list();

        /*
         * See if the current pattern is a directory wildcard. If it is, then check to see if the next pattern entry
         * matches any of the contents of the current position. If they do, reset the index to the pattern match and
         * recurse. If they don't, then DO NOT reset the pattern position and just recurse with the same directory
         * wildcard pattern as the current pattern. If we can't sync-up the patterns to the directories then we will not
         * match and will ultimately return. If it is not a directory wildcard, then just perform a straight pattern
         * match on each item and if they match, then recurse, otherwise simply end.
         */
        String regex = parts[index];
        for (String item : items) {
            if ("**".equals(regex)) {
                Pattern pattern = Pattern.compile(parts[index + 1]);
                Matcher matcher = pattern.matcher(item);
                if (matcher.matches()) {
                    pathSearch(results, base, new File(position, item), parts, index + 1);
                } else {
                    pathSearch(results, base, new File(position, item), parts, index);
                }
            } else {
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(item);
                if (matcher.matches()) {
                    pathSearch(results, base, new File(position, item), parts, index + 1);
                }
            }
        }
    }

    /**
     * Return the name of the file only without any path or extension present.
     * 
     * @param file
     *            The file to extract the name from
     * @return The file name less any path information or extension
     */
    public static String nameOnly(final File file) {
        if (file == null) {
            return null;
        }
        return nameOnly(file.getName());
    }

    /**
     * Returns the name without any path or extension, if a path or extension is present.
     * 
     * @param name
     *            The name to be processed
     * @return The name without any extension, or the original name if no extension is present
     */
    public static String nameOnly(final String name) {
        StringBuffer buffer = new StringBuffer(name);
        int index = buffer.lastIndexOf(".");
        if (index != -1) {
            buffer.delete(index, buffer.length());
        }
        index = buffer.lastIndexOf("/");
        if (index != -1) {
            buffer.delete(0, index + 1);
        }
        index = buffer.lastIndexOf("\\");
        if (index != -1) {
            buffer.delete(0, index + 1);
        }
        return buffer.toString();
    }

    /**
     * This method can be used to copy a file from one place to another.
     * 
     * @param input
     *            The file to be read
     * @param output
     *            The file to be written to
     * @throws IOException
     *             if anything fails.
     */
    public static void copyFile(final File input, final File output) throws IOException {
        FileInputStream in = null;
        FileOutputStream out = null;

        try {
            in = new FileInputStream(input);
            out = new FileOutputStream(output);
            FileChannel inChannel = in.getChannel();
            FileChannel outChannel = out.getChannel();
            long size = inChannel.size();
            inChannel.transferTo(0L, size, outChannel);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                /* ignore */
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                /* ignore */
            }
        }
    }

    /**
     * This method creates a memory-mapped file of the given name from an input stream.
     * 
     * @param name
     *            The resulting name of the memory mapped file
     * @param stream
     *            The input stream to be converted
     * @return The <code>File</code> object of the memory mapped file.
     * @throws IOException
     *             If anything goes wrong.
     */
    public static File memoryMapFile(final String name, final InputStream stream) throws IOException {
        File file = File.createTempFile("rgi_", "_cltemp");
        byte[] buffer = StreamHelper.readAndBuffer(stream);
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        FileChannel channel = raf.getChannel();
        ByteBuffer channelBuffer = channel.map(MapMode.READ_WRITE, 0, buffer.length);
        channelBuffer.rewind();
        channelBuffer.put(buffer);
        channelBuffer.flip();
        return file;
    }

    /**
     * This method is used to copy an entire file tree from one location to another. This is a deep copy.
     * 
     * @param source
     *            The source directory to be copied.
     * @param dest
     *            The destination directory to receive the contents of the copy.
     * @throws IOException
     *             if the copy fails
     */
    public static void copyTree(final File source, final File dest) throws IOException {
        if (source != null && dest != null && source.isDirectory() && dest.isDirectory()) {
            String[] children = source.list();
            for (String child : children) {
                File in = new File(source, child);
                File out = new File(dest, child);
                if (in.isDirectory()) {
                    out.mkdir();
                    copyTree(in, out);
                } else {
                    copyFile(in, out);
                }
            }
        }
    }

    /**
     * This method can be used to perform a deep deletion of a directory structure starting at, and including the
     * directory provided.
     * 
     * @param file
     *            the directory to be deep deleted
     */
    public static void deleteTree(final File file) {
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (String child : children) {
                    File childFile = new File(file, child);
                    deleteTree(childFile);
                }
            }
            file.delete();
        }
    }

    /**
     * This method can be called to make sure that the specified directories exist that are specified for the indicated
     * file. The assumption is that the last node in the path string is the file itself.
     * 
     * @param file
     *            The file to be processed.
     */
    public static void ensurePathExists(final File file) {
        String path = normalizePath(file.getAbsolutePath());
        int index = path.lastIndexOf('/');
        if (index != -1) {
            path = path.substring(0, index);
            File check = new File(path);
            if (!check.exists()) {
                check.mkdirs();
            }
        }
    }

    /**
     * Tests the provided path to see if it is an absolute or relative path.
     * 
     * @param reference
     *            The path to be tested.
     * @return <code>true</code> if the path is absolute, or false otherwise.
     */
    public static boolean isAbsolute(final String reference) {
        if (reference == null) {
            return false;
        }
        return reference.matches("([a-zA-Z]:)?(/|\\\\).*");
    }

    /**
     * This method examines the supplied path for a file name (anything with an extension) and removes the name from the
     * path and returns just the path. If the provided path does not contain a filename with an extension, then it is
     * returned as-is.
     * 
     * @param path
     *            The path to be massaged
     * @return The path-only portion
     */
    public static String pathOnly(final String path) {
        String buffer = normalizePath(path);
        Pattern pattern = Pattern.compile("^(.*)/[^/]*[.][^/]*");
        Matcher matcher = pattern.matcher(buffer);
        if (matcher.matches()) {
            return buffer.substring(0, matcher.end(1));
        }
        return path;
    }

    /**
     * This method returns the file name and extension only from a file path, if one exists. If the file path does not
     * contain a filename and extension, then an empty string is returned.
     * 
     * @param path
     *            The path to examine for a filename and extension.
     * @return The filename and extension less the path information if it exists, or an empty string.
     */
    public static String nameAndExtensionOnly(final String path) {
        String buffer = normalizePath(path);

        /*
         * Handle the case where we are presented only a filename and extension without any path info
         */
        if (!buffer.contains("/") && buffer.contains(".")) {
            return buffer.toString();
        }

        /*
         * Extract the filename and extension from the path if it exists, else return an empty string.
         */
        Pattern pattern = Pattern.compile("^(.*)/[^/]*[.][^/]*");
        Matcher matcher = pattern.matcher(buffer);
        if (matcher.matches()) {
            return buffer.substring(matcher.end(1) + 1);
        }
        return "";
    }

    /**
     * This method takes a base path and a relative path and forms an absolute path, where the relative path is made
     * absolute relative to the base path. If the relative path is already absolute, then it is returned unchanged.
     * 
     * @param basePath
     *            The base path to be made relative to. If null, then the current working directory is used.
     * @param relPath
     *            The relative path that is to be made absolute relative to the base path, or if already absolute, is
     *            simply returned as is.
     * @return The absolute path of the relative path part relative to the base path, or the original absolute path.
     */
    public static String makeAbsolutePath(final String basePath, final String relPath) {
        if (FileHelper.isAbsolute(relPath)) {
            return normalizePath(relPath);
        }
        File base = null;
        if (basePath == null) {
            base = new File(".");
        } else {
            base = new File(makeAbsolutePath(null, basePath));
        }

        File file = null;
        if (relPath == null) {
            file = base;
        } else {
            file = new File(base, relPath);
        }
        return FileHelper.normalizePath(file.getAbsolutePath());
    }

    /**
     * This method takes two absolute paths and returns the relative path (difference between the two paths)
     * 
     * @param reference
     *            The base, or reference path that we will "subtract" from the source.
     * @param source
     *            The absolute source path that we want to make relative to the reference path
     * @return The relative path to the reference
     */
    public static String makeRelativePath(final String reference, final String source) {
        StringBuilder relativePath = new StringBuilder();
        String[] referenceNodes = pathOnly(normalizePath(reference)).split("/");
        String[] sourceNodes = pathOnly(normalizePath(source)).split("/");
        String name = nameAndExtensionOnly(source);

        int referenceIndex = 0;
        int sourceIndex = 0;
        for (; referenceIndex < referenceNodes.length; referenceIndex++) {
            String referenceNode = referenceNodes[referenceIndex];
            if (sourceIndex < sourceNodes.length) {
                String sourceNode = sourceNodes[sourceIndex];
                if (referenceNode.equals(sourceNode)) {
                    sourceIndex = referenceIndex + 1;
                    continue;
                }
                relativePath.append("../");
            } else {
                relativePath.append("../");
            }
        }
        for (; sourceIndex < sourceNodes.length; sourceIndex++) {
            relativePath.append(sourceNodes[sourceIndex] + "/");
        }
        /*
         * If no name is present, then we have an extra trailing "/" that we need to eliminate
         */
        if (name == null || name.length() == 0) {
            relativePath.deleteCharAt(relativePath.length() - 1);
        } else {
            relativePath.append(name);
        }

        return relativePath.toString();
    }
}
