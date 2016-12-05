/*******************************************************************************
 * Copyright (c) 2016 AT&T Intellectual Property. All rights reserved.
 *******************************************************************************/

package com.att.cdp.openstack.heat.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import com.att.cdp.openstack.exception.UnmarshallException;
import com.att.cdp.openstack.heat.Unmarshaller;
import com.att.cdp.pal.util.ResourceHelper;
import com.att.cdp.pal.util.StreamUtility;

/**
 * This class is designed to test the ability to marshall and unmarshall the HOT template between the YAML document and
 * the java object graph.
 * 
 * @since May 18, 2015
 * @version $Id$
 */
public class TestModel {

    private Yaml yaml;

    private ObjectMapper om;

    private List<String> templateStrings;

    @Ignore
    @Before
    public void setup() {
        yaml = new Yaml();
        om = new ObjectMapper();
        templateStrings = new ArrayList<String>();
        URL[] resources = ResourceHelper.findResources(this.getClass(), "/com/att/cdp/templates/.+\\.yml$");
        if (resources != null) {
            for (URL resource : resources) {
                InputStream stream = null;
                try {
                    stream = resource.openStream();
                    String content = StreamUtility.getStringFromInputStream(stream);
                    templateStrings.add(content);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        }

    }

    /**
     * Test that the unmarshalling of the test template worked correctly
     * 
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     * @throws UnmarshallException
     */
    @Ignore
    @Test
    public void testUnmarshall() throws JsonParseException, JsonMappingException, IOException, UnmarshallException {
        assertFalse(templateStrings.isEmpty());
        Unmarshaller unmarshaller = new Unmarshaller();
        for (String templateString : templateStrings) {
            List<Template> templates = unmarshaller.unmarshallYaml(templateString);
            assertNotNull(templates);
            assertFalse(templates.isEmpty());
            assertEquals(1, templates.size());

            for (Template template : templates) {
                printTemplate(template);

                assertEquals("2013-05-23", template.getHeatTemplateVersion().toString());
                assertEquals("Simple template to test heat commands", template.getDescription().toString());
                List<ParameterGroup> parameterGroups = template.getParameterGroups();
                assertNotNull(parameterGroups);
                assertEquals(2, parameterGroups.size());

                ParameterGroup group = parameterGroups.get(0);
                assertEquals("Group 1", group.getLabel().toString());
                assertEquals("Description of group 1", group.getDescription().toString());
                List<Scalar> parameters = group.getParameters();
                assertNotNull(parameters);
                assertEquals(1, parameters.size());
                assertEquals("flavor", parameters.get(0).toString());
            }
        }
    }

    /**
     * Diagnostic routine to format and print out a template object graph
     * 
     * @param template
     */
    public void printTemplate(Template template) {
        System.out.println(template.toString());

        List<ParameterGroup> parameterGroups = template.getParameterGroups();
        if (parameterGroups != null && !parameterGroups.isEmpty()) {
            System.out.printf("Parameter Groups: %d\n", parameterGroups.size());
            for (ParameterGroup group : parameterGroups) {
                Scalar label = group.getLabel();
                Scalar description = group.getDescription();
                List<Scalar> parameters = group.getParameters();

                System.out.printf("\t%s, %s, %s\n", label, description,
                    parameters == null ? "null" : parameters.toString());
            }
        }

        Map<String, Parameter> parameters = template.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            System.out.printf("Parameters: %d\n", parameters.size());
            for (Map.Entry<String, Parameter> entry : parameters.entrySet()) {
                String name = entry.getKey();
                Parameter parameter = entry.getValue();

                System.out.printf("\t%s = %s\n", name, parameter.toString());
                List<Constraint> constraints = parameter.getConstraints();
                if (constraints != null && !constraints.isEmpty()) {
                    for (Constraint constraint : constraints) {
                        System.out.printf("\t\t%s\n", constraint.toString());
                    }
                }
            }
        }

        Map<String, Resource> resources = template.getResources();
        if (resources != null && !resources.isEmpty()) {
            System.out.printf("Resources: %d\n", resources.size());
            for (Map.Entry<String, Resource> entry : resources.entrySet()) {
                String name = entry.getKey();
                Resource resource = entry.getValue();

                System.out.printf("\t%s = %s\n", name, resource.toString());
            }
        }

        Map<String, Output> outputs = template.getOutputs();
        if (outputs != null && !outputs.isEmpty()) {
            System.out.printf("Outputs: %d\n", outputs.size());
            for (Map.Entry<String, Output> entry : outputs.entrySet()) {
                String name = entry.getKey();
                Output output = entry.getValue();

                System.out.printf("\t%s = %s\n", name, output.toString());
            }
        }
    }
}
