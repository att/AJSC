/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.att.cdp.pal.util.ObjectHelper;

/**
 * The definition of a scalar value.
 * <p>
 * A scalar value is a single value, expressed as either a constant string literal, or an intrinsic function that
 * evaluates at runtime to a scalar value.
 * </p>
 * 
 * @since Jan 28, 2015
 * @version $Id$
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Scalar extends ModelObject implements CharSequence {

    /**
     * The set of all known, recognized intrinsic functions
     */
    private static final String[] FUNCTION_NAMES = {
        "get_attr", "get_file", "get_param", "get_resource", "list_join", "digest", "repeat", "resource_facade",
        "str_replace", "Fn::select"
    };

    /**
     * Determines if the supplied name is that of an intrinsic function or not
     * 
     * @param name
     *            The name to be checked
     * @return True if it is an intrinsic function, false otherwise.
     */
    public static boolean isIntrinsic(String name) {
        for (String functionName : FUNCTION_NAMES) {
            if (functionName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * The arguments to the intrinsic function if the scalar is an intrinsic
     */
    private List<String> arguments;

    /**
     * The name of the intrinsic function if the scalar is a function
     */
    private String function;

    /**
     * The value of the scalar if it is a constant value
     */
    private String value;

    /**
     * Default no-arg constructor
     */
    public Scalar() {
        super();
    }

    /**
     * Constructor of a scalar value
     * 
     * @param value
     *            The constant string value of the scalar
     */
    public Scalar(String value) {
        super();
        this.value = value;
    }

    /**
     * Constructor to create a function (intrinsic) scalar value
     * 
     * @param function
     *            The function name
     * @param arguments
     *            The variable argument list of arguments for the function
     */
    public Scalar(String function, String... arguments) {
        this.function = function;
        this.arguments = new ArrayList<String>();
        if (arguments != null && arguments.length > 0) {
            this.arguments.addAll(Arrays.asList(arguments));
        }
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        return ObjectHelper.equals(this, obj);
    }

    /**
     * @return the value of arguments
     */
    public List<String> getArguments() {
        return arguments;
    }

    /**
     * @return the value of function
     */
    public String getFunction() {
        return function;
    }

    /**
     * @return the value of value
     */
    public String getValue() {
        return value;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 0;
        if (isIntrinsic()) {
            hash = (function == null ? 0 : function.hashCode()) + (arguments == null ? 0 : arguments.hashCode());
        } else {
            hash = value == null ? 0 : value.hashCode();
        }
        return hash;
    }

    /**
     * Returns an indication that the scalar value is obtained by evaluating an intrinsic function.
     * 
     * @return True means the scalar value is obtained using an intrinsic function. False means the scalar value is a
     *         constant.
     */
    public boolean isIntrinsic() {
        return function != null;
    }

    /**
     * @param arguments
     *            the value for arguments
     */
    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    /**
     * @param function
     *            the value for function
     */
    public void setFunction(String function) {
        this.function = function;
    }

    /**
     * @param value
     *            the value for value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {

        StringBuffer buffer = new StringBuffer();
        if (isIntrinsic()) {
            buffer.append(function);
            buffer.append('(');
            if (arguments != null) {
                for (String argument : arguments) {
                    buffer.append(argument);
                    buffer.append(',');
                }
                if (buffer.charAt(buffer.length() - 1) == ',') {
                    buffer.delete(buffer.length() - 1, buffer.length());
                }
            }
            buffer.append(')');
        } else {
            buffer.append(value);
        }

        return buffer.toString();
    }

    /**
     * @see java.lang.CharSequence#length()
     */
    @Override
    public int length() {
        return toString().length();
    }

    /**
     * @see java.lang.CharSequence#charAt(int)
     */
    @Override
    public char charAt(int index) {
        return toString().charAt(index);
    }

    /**
     * @see java.lang.CharSequence#subSequence(int, int)
     */
    @Override
    public CharSequence subSequence(int start, int end) {
        return toString().subSequence(start, end);
    }

}
