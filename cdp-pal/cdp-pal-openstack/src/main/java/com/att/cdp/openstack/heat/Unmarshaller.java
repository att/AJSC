/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.yaml.snakeyaml.Yaml;

import com.att.cdp.openstack.exception.UnmarshallException;
import com.att.cdp.openstack.heat.model.Constraint;
import com.att.cdp.openstack.heat.model.CustomConstraint;
import com.att.cdp.openstack.heat.model.LengthConstraint;
import com.att.cdp.openstack.heat.model.ModelObject;
import com.att.cdp.openstack.heat.model.PatternConstraint;
import com.att.cdp.openstack.heat.model.RangeConstraint;
import com.att.cdp.openstack.heat.model.Scalar;
import com.att.cdp.openstack.heat.model.Template;
import com.att.cdp.openstack.heat.model.ValuesConstraint;
import com.att.cdp.pal.util.BeanProperty;
import com.att.cdp.pal.util.ObjectHelper;

/**
 * This is a specialized parser that can convert a heat document specified as either yaml or json into a object graph.
 * <p>
 * This unmarshaller parses the input document and converts it to a Template graph that represents the Heat Template.
 * This unmarshaller uses the same annotations that Jackson uses so that the object graph can be represented as json,
 * and reused to support yaml. The snakeyaml tool does not allow the same level of flexibility directly as Jackson, so
 * instead of using it to directly create the graph, I use it to create a parse event stream and process that stream
 * myself.
 * </p>
 * 
 * @since May 22, 2015
 * @version $Id$
 */
public class Unmarshaller {

    /**
     * A constant collection of the constraint types that can be defined in a heat template
     */
    private static final String[] CONSTRAINT_TYPES = {
        "length", "range", "allowed_values", "allowed_pattern", "custom_constraint"
    };

    /**
     * The class that we use to model the constraints. This collection (array) must be synched positionally to the type
     * names in the CONSTRAINT_TYPES array (i.e., position 1 in this array must correspond to position 1 in that array).
     */
    @SuppressWarnings("rawtypes")
    private static final Class[] CONSTRAINT_CLASSES = {
        LengthConstraint.class, RangeConstraint.class, ValuesConstraint.class, PatternConstraint.class,
        CustomConstraint.class
    };

    /**
     * The list of template(s) unmarshalled from the input event stream
     */
    private List<Template> templates = new ArrayList<Template>();

    /**
     * This method unmarshalls a yaml content string into a Template object graph.
     * <p>
     * This is the main entry point for unmarshalling a YAML document into the template graph. This method will process
     * any number of templates contained in the YAML document and will return a list of <code>Template</code> objects,
     * one per template graph.
     * </p>
     * 
     * @param content
     *            The YAML content of a Heat template
     * @return The list of unmarshalled Template(s) as a collection of graphs, or an empty collection if no templates
     *         were in the document.
     * @throws UnmarshallException
     *             If the document cannot be unmarshalled
     */
    @SuppressWarnings("unchecked")
    public List<Template> unmarshallYaml(String content) throws UnmarshallException {
        Yaml yaml = new Yaml();
        StringReader reader = new StringReader(content);

        /*
         * Ask the yaml parser to load all documents from the input stream, then iterate over the collection of
         * intermediate graphs. Each intermediate graph represents one Template document, and is in a generic format of
         * collections of maps, lists, and strings. This code then transforms that intermediate graph into a template
         * object model.
         */
        for (Object graph : yaml.loadAll(reader)) {
            Map<String, Object> map = (Map<String, Object>) graph;

            /*
             * Dump the intermediate graph for diagnostic purposes
             */
            dump(map, null, 0);

            /*
             * For each intermediate graph, create one new Template object and transform the intermediate graph into a
             * Template model. The intermediate graph always starts with a map.
             */
            templates.add(unmarshall(Template.class, (Map<String, Object>) graph));
        }

        return templates;
    }

    /**
     * Unmarshal the intermediate graph representation (as a map of maps, lists, or scalars) into the current container.
     * <p>
     * This method is passed the class of the model object to be constructed, and will construct an instance of that
     * class and return it. This is done so that polymorphic mappings can be supported easier, which allows us to
     * determine the actual implementation class by examining the data (lookahead), then instantiate the object and map
     * the data. This method is called recursively for each object being mapped.
     * </p>
     * 
     * @param modelClass
     *            The class of object that must be constructed to contain the mapped data
     * @param data
     *            the data to be unmarshalled
     * @return The containing object of the unmarshalled data
     * @throws UnmarshallException
     *             If the data cannot be unmarshalled for some reason
     */
    @SuppressWarnings("unchecked")
    private <T extends ModelObject> T unmarshall(Class<T> modelClass, Object data) throws UnmarshallException {
        T model = null;
        try {
            model = modelClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new UnmarshallException(String.format("Error creating instance of %s", modelClass.getSimpleName()), e);
        }

        if (data instanceof Map) {
            unmarshallMap(model, (Map<String, Object>) data);
        } else if (data instanceof List) {
            unmarshallList(model, (List<Object>) data);
        } else {
            unmarshallObject(model, data);
        }
        return model;
    }

    /**
     * Unmarshalls a map data object into the model object
     * 
     * @param model
     *            The model object to be updated with the contents of the map
     * @param map
     *            The map to be unmarshalled
     * @throws UnmarshallException
     *             If the map cannot be matched to the model
     */
    private <T extends ModelObject> void unmarshallMap(T model, Map<String, Object> map) throws UnmarshallException {
        if (model instanceof Scalar) {
            unmarshallMapScalar((Scalar) model, map);
        } else {
            unmarshallMapObject(model, map);
        }
    }

    /**
     * Unmarshalling a map into a scalar
     * 
     * @param scalar
     *            The scalar object to be mapped into
     * @param map
     *            The map of objects to be unmarshalled
     * @throws UnmarshallException
     *             If the map does not match the model object
     */
    private void unmarshallMapScalar(Scalar scalar, Map<String, Object> map) throws UnmarshallException {
        String key = (String) map.keySet().toArray()[0];
        if (Scalar.isIntrinsic(key)) {
            scalar.setFunction(key);
            Object obj = map.get(key);
            List<String> args = new ArrayList<String>();
            if (obj instanceof List) {
                args.addAll((List) obj);
            } else {
                args.add(obj.toString());
            }
            scalar.setArguments(args);
        } else {
            scalar.setValue(map.toString());
        }
    }

    /**
     * Unmarshalls a map into a non-scalar model object
     * 
     * @param model
     *            The non-scalar model object
     * @param map
     *            The map to be unmarshalled
     * @throws UnmarshallException
     *             If the map does not match the model object
     */
    private <T extends ModelObject> void unmarshallMapObject(T model, Map<String, Object> map)
        throws UnmarshallException {
        Class<? extends ModelObject> implClass = null;
        /*
         * The data we are processing is a map. In that case we iterate the map keys and use that to locate properties
         * that correspond to the key
         */
        for (String key : map.keySet()) {
            try {
                Object obj = map.get(key);
                BeanProperty beanProperty = BeanProperty.getBeanProperty(model, key);

                /*
                 * If we can't find a property, then check if it's ok to ignore unknown properties. If not, its an
                 * exception.
                 */
                if (beanProperty == null) {
                    if (!ObjectHelper.isJsonIgnoreUnknownProperty(model.getClass(), key)) {
                        throw new UnmarshallException(String.format("Expected property %s was not found on %s", key,
                            model.getClass().getSimpleName()));
                    }
                    continue;
                }

                /*
                 * Determine the implementation class of the object we need to map into
                 */
                implClass = determineImplementationClass(beanProperty, obj);

                if (beanProperty.isMap()) {
                    /*
                     * If the property is a map, then we are creating a map of the implementation objects.
                     */
                    Map<String, Object> values = (Map<String, Object>) obj;
                    Map<String, Object> collection = (Map<String, Object>) beanProperty.get();
                    if (collection == null) {
                        collection = new HashMap<String, Object>();
                        beanProperty.set(collection);
                    }
                    for (String valueKey : values.keySet()) {
                        collection.put(valueKey, unmarshall(implClass, values.get(valueKey)));
                    }

                } else if (beanProperty.isList()) {
                    /*
                     * If the property is a list, then we are creating a list of the implementation objects
                     */
                    List<Object> values = (List<Object>) obj;
                    List<Object> collection = (List<Object>) beanProperty.get();
                    if (collection == null) {
                        collection = new ArrayList<Object>();
                        beanProperty.set(collection);
                    }
                    for (Object value : values) {
                        collection.add(unmarshall(implClass, value));
                    }

                } else {
                    /*
                     * Otherwise we are creating a scalar object
                     */
                    beanProperty.set(unmarshall(Scalar.class, obj));
                }

            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new UnmarshallException(String.format("Exception instantiating model object of class %s",
                    implClass.getSimpleName()), e);
            }
        }
    }

    /**
     * Unmarshalls a list of data into the model object provided
     * 
     * @param model
     *            The model object to contain the unmarshalled data
     * @param list
     *            The list of data to be unmarshalled
     * @throws UnmarshallException
     *             If the map does not match the model object
     */
    private <T extends ModelObject> void unmarshallList(T model, List<Object> list) throws UnmarshallException {
        if (model instanceof Scalar) {
            Scalar scalar = (Scalar) model;
            StringBuffer buffer = new StringBuffer();
            for (Object value : list) {
                buffer.append(value.toString());
                buffer.append(',');
            }
            if (buffer.length() > 0) {
                buffer.delete(buffer.length() - 1, buffer.length());
            }
            scalar.setValue(buffer.toString());
        } else {
            throw new UnmarshallException(String.format("Expecting scalar, but found %s", model.getClass()
                .getSimpleName()));
        }
    }

    /**
     * Unmarshalls a non-map and non-list (i.e., vector or scalar object) into the model object
     * 
     * @param model
     *            The model object to contain the unmarshalled data
     * @param data
     *            The scalar object to be unmarshalled
     * @throws UnmarshallException
     *             If the map does not match the model object
     */
    private <T extends ModelObject> void unmarshallObject(T model, Object data) throws UnmarshallException {
        if (model instanceof Scalar) {
            Scalar scalar = (Scalar) model;
            if (data instanceof Map) {
                Map<String, Object> values = (Map<String, Object>) data;
                String function = (String) values.keySet().toArray()[0];
                scalar.setFunction(function);
                Object args = values.get(function);

                List<String> arguments = scalar.getArguments();
                if (arguments == null) {
                    arguments = new ArrayList<String>();
                    scalar.setArguments(arguments);
                }

                if (args instanceof String) {
                    arguments.add((String) args);
                } else if (args instanceof List) {
                    for (Object element : (List) args) {
                        arguments.add(element.toString());
                    }
                }
            } else {
                scalar.setValue(data.toString());
            }
        } else {
            throw new UnmarshallException(String.format("Expecting scalar, but found %s", model.getClass()
                .getSimpleName()));
        }

    }

    /**
     * This method checks the specified bean property to locate the implementation class to be used. If the
     * implementation class supports polymorphic mappings, then the implementation class is further inspected for
     * JsonTypeInfo annotations. If the annotation is present, then the annotation directs the processing. Because the
     * processing of the annotation may be data-dependent, the mapped data to be unmarshalled must be provided as well.
     * 
     * @param beanProperty
     *            The bean property we are mapping the contents into
     * @param obj
     *            The object that contains the mappings
     * @return The class to be constructed to contain the mappings
     * @throws UnmarshallException
     *             If the json type info uses unsupported metadata determination
     */
    @SuppressWarnings("unchecked")
    private Class<? extends ModelObject> determineImplementationClass(BeanProperty beanProperty, Object obj)
        throws UnmarshallException {

        Class<? extends ModelObject> implClass = null;
        JsonTypeInfo typeInfo = null;
        JsonSubTypes subTypes = null;
        Class<?> defaultImplClass = null;

        /*
         * First, try to determine the implementation class to be created based on the bean property type. If the bean
         * property is a scalar type, then use it as is. If it is a list, then use the first generic type. If it is a
         * map, use the second generic type (maps are always assumed to be keyed by strings).
         */
        implClass = (Class<? extends ModelObject>) beanProperty.getPropertyType();
        if (beanProperty.isList()) {
            implClass = (Class<? extends ModelObject>) beanProperty.getCollectionTypes()[0];
        } else if (beanProperty.isMap()) {
            implClass = (Class<? extends ModelObject>) beanProperty.getCollectionTypes()[1];
        }

        /*
         * HACK: If the implClass is Constraint, then we have a special case. We need to examine the first key in the
         * map to determine the type of constraint and return that name.
         */
        if (implClass.equals(Constraint.class)) {
            if (obj instanceof List) {
                Map<String, Object> map = (Map<String, Object>) ((List<Object>) obj).get(0);
                String constraintType = (String) map.keySet().toArray()[0];

                for (int index = 0; index < CONSTRAINT_TYPES.length; index++) {
                    if (CONSTRAINT_TYPES[index].equals(constraintType)) {
                        implClass = CONSTRAINT_CLASSES[index];
                        return implClass;
                    }
                }
            }
        }

        /*
         * If typeInfo annotation is present on the property type class, then check it to get the actual implementation
         * class. Otherwise, we will instantiate the property defined.
         */
        typeInfo = (JsonTypeInfo) ObjectHelper.getClassAnnotation(implClass, JsonTypeInfo.class);
        if (typeInfo != null) {
            subTypes = (JsonSubTypes) ObjectHelper.getClassAnnotation(implClass, JsonSubTypes.class);
            defaultImplClass = typeInfo.defaultImpl();
            JsonTypeInfo.Id use = typeInfo.use();
            JsonTypeInfo.As include = typeInfo.include();

            if (use.equals(JsonTypeInfo.Id.NAME) && include.equals(JsonTypeInfo.As.PROPERTY)) {
                if (obj instanceof Map) {
                    Map<String, Object> map = (Map<String, Object>) obj;
                    String property = typeInfo.property();
                    String propertyValue = (String) map.get(property);
                    implClass = (Class<? extends ModelObject>) defaultImplClass;
                    if (propertyValue != null) {
                        JsonSubTypes.Type[] types = subTypes.value();
                        for (JsonSubTypes.Type type : types) {
                            if (type.name().equals(propertyValue)) {
                                implClass = (Class<? extends ModelObject>) type.value();
                                break;
                            }
                        }
                    }
                }
            } else if (use.equals(JsonTypeInfo.Id.CUSTOM)) {
                JsonDeserialize deserializeAnnotation =
                    (JsonDeserialize) ObjectHelper.getClassAnnotation(implClass, JsonDeserialize.class);
                Class<? extends JsonDeserializer> deserializer = deserializeAnnotation.using();

            } else {
                throw new UnmarshallException(String.format("Only JsonTypeInfo use=\"NAME\" and include=\"PROPERTY\" "
                    + " or use=\"CUSTOM\" with custom deserializer are supported.  The mapping specified "
                    + "use=\"%s\" and include=\"%s\"", use.name(), include.name()));
            }
        }

        return implClass;
    }

    /**
     * Diagnostic method to dump the intermediary structure created by the YAML parser. This structure consists of
     * collections of maps and lists.
     * 
     * @param map
     *            The map to be dumped
     * @param name
     *            The name of the map, if it is a named structure, or null
     * @param level
     *            The current nesting level of the structure
     */
    @SuppressWarnings("unchecked")
    private void dump(Map<String, Object> map, String name, int level) {
        System.out.printf("%s%s\n", prefix(level, map), name == null ? "" : name);
        for (String key : map.keySet()) {
            Object value = map.get(key);
            if (value instanceof Map) {
                dump((Map<String, Object>) value, key, level + 1);
            } else if (value instanceof List) {
                dump((List<Object>) value, key, level + 1);
            } else {
                dump(value, key, level + 1);
            }
        }
    }

    /**
     * Diagnostic method to dump the intermediary structure created by the YAML parser. This structure consists of
     * collections of maps and lists.
     * 
     * @param list
     *            The list to be dumped
     * @param name
     *            The name of the list, if it is a named structure, or null
     * @param level
     *            The current nesting level of the structure
     */
    @SuppressWarnings("unchecked")
    private void dump(List<Object> list, String name, int level) {
        System.out.printf("%s%s\n", prefix(level, list), name == null ? "" : name);
        for (Object value : list) {
            if (value instanceof Map) {
                dump((Map<String, Object>) value, null, level + 1);
            } else if (value instanceof List) {
                dump((List<Object>) value, null, level + 1);
            } else {
                dump(value, null, level + 1);
            }
        }
    }

    /**
     * Diagnostic method to dump the intermediary structure created by the YAML parser. This structure consists of
     * collections of maps and lists.
     * 
     * @param value
     *            The unknown object (probably a string) to be dumped
     * @param name
     *            The name of the object, if it is a named structure, or null
     * @param level
     *            The current nesting level of the structure
     */
    private void dump(Object value, String name, int level) {
        System.out.printf("%s %s %s\n", prefix(level, value), name == null ? "" : name + ":", value.toString());
    }

    /**
     * Generates the standard prefix for the diagnostic dump output line(s)
     * 
     * @param level
     *            The current nesting level
     * @param obj
     *            The object being dumped
     * @return The prefix to be inserted at the first of each line
     */
    private String prefix(int level, Object obj) {
        String className = obj.getClass().getSimpleName();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < level; i++) {
            buffer.append(' ');
            buffer.append(' ');
        }
        buffer.append("[");
        buffer.append(className);
        buffer.append("] ");

        return buffer.toString();
    }

    /**
     * This is a diagnostic method used to dump the object graph created from unmarshalling the YAML document into it's
     * intermediate form.
     * 
     * @param map
     *            The map object to be dumped
     * @param level
     *            The current nesting level, starting at 0 and incrementing by 1
     */
    @SuppressWarnings("unchecked")
    private void dumpGraph(Map<String, Object> map, String key, int level) {
        System.out.printf("%s%s\n", generatePrefix(level, map.getClass()), key);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String name = (String) entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                System.out.printf("%s%s = %s\n", generatePrefix(level, String.class, String.class), name, value);
            } else {
                if (value instanceof Map) {
                    dumpGraph((Map<String, Object>) value, name, level + 1);
                } else if (value instanceof List) {
                    dumpGraph((List<Object>) value, name, level + 1);
                } else {
                    System.out.printf("**** %s ****\n", value.getClass().getSimpleName());
                }
            }
        }
    }

    /**
     * This is a diagnostic method used to dump the object graph created from unmarshalling the YAML document into it's
     * intermediate form.
     * 
     * @param map
     *            The list object to be dumped
     * @param level
     *            The current nesting level, starting at 0 and incrementing by 1
     */
    @SuppressWarnings("unchecked")
    private void dumpGraph(List<Object> list, String key, int level) {
        System.out.printf("%s%s\n", generatePrefix(level, list.getClass()), key);
        for (Object value : list) {
            if (value instanceof String) {
                System.out.printf("%s%s\n", generatePrefix(level, value.getClass()), value);
            } else {
                if (value instanceof Map) {
                    dumpGraph((Map<String, Object>) value, "", level + 1);
                } else if (value instanceof List) {
                    dumpGraph((List<Object>) value, "", level + 1);
                } else {
                    System.out.printf("*** %s ***\n", value.getClass().getSimpleName());
                }
            }
        }
    }

    /**
     * Generates a prefix for the formatted line created by each diagnostic dump method
     * 
     * @param level
     *            The current nesting level
     * @param clazz
     *            The class of the value being dumped
     * @return The string prefix to be prefixed to the output line(s)
     */
    private String generatePrefix(int level, Class<?> clazz) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(clazz.getSimpleName());
        int pad = 30 - buffer.length();
        for (int index = 0; index < pad; index++) {
            buffer.append(" ");
        }
        buffer.append(": ");
        for (int index = 0; index < level; index++) {
            buffer.append("  ");
        }
        return buffer.toString();
    }

    /**
     * Generates a prefix for the formatted line created by each diagnostic dump method
     * 
     * @param level
     *            The current nesting level
     * @param valueClass
     *            The class of the value being dumped
     * @return The string prefix to be prefixed to the output line(s)
     */
    private String generatePrefix(int level, Class<?> keyClass, Class<?> valueClass) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(keyClass.getSimpleName());
        buffer.append(",");
        buffer.append(valueClass.getSimpleName());
        int pad = 30 - buffer.length();
        for (int index = 0; index < pad; index++) {
            buffer.append(" ");
        }
        buffer.append(": ");
        for (int index = 0; index < level; index++) {
            buffer.append("  ");
        }
        return buffer.toString();
    }
}
