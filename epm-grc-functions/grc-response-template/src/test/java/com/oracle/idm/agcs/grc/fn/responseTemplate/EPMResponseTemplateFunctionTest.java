/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.responseTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oracle.idm.agcs.icfconnectors.commons.enums.Operation;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.ResponseTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.ResponseTemplateOutput;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class EPMResponseTemplateFunctionTest extends ResponseTemplateFunctionTest {

    static String connectedSystemName;
    String UserAsAccountEntity = "UserAsAccount";
    String UserAsIdentityEntity = "UserAsIdentity";
    String roleAsEntitlementEntityName = "RoleAsEntitlement";

    @BeforeClass
    public static void loadConfig() {
        String resourceName = "config.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            props.load(resourceStream);
            connectedSystemName = getPropertyValue(props, "connectedSystemName");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldReturnCreateUserAsAccountResponseTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        ResponseTemplateInput input =
                ResponseTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsAccountEntity)
                        .operationName(Operation.CREATE)
                        .build();
        ResponseTemplateOutput output = getResponseTemplateOutput(input);
        assertEquals("uid", output.getAttributes().get(0).getName());
        assertEquals("<EL>attributes.get('uid').get(0)</EL>", output.getAttributes().get(0).getValue());
    }

    @Test
    public void shouldReturnGetUserAsAccountResponseTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        ResponseTemplateInput input =
                ResponseTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsAccountEntity)
                        .operationName(Operation.GET)
                        .build();
        ResponseTemplateOutput output = getResponseTemplateOutput(input);
        assertEquals("uid", output.getAttributes().get(0).getName());
        assertEquals("<JP>$.details[0].userlogin</JP>", output.getAttributes().get(0).getValue());
    }

    @Test
    public void shouldReturnSearchUserAsAccountResponseTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        ResponseTemplateInput input =
                ResponseTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsAccountEntity)
                        .operationName(Operation.SEARCH)
                        .build();
        ResponseTemplateOutput output = getResponseTemplateOutput(input);
        assertEquals("<JP>$.details[*]</JP>", output.getItems());
    }

    @Test
    public void shouldReturnAddChildDataUserAsAccountResponseTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        ResponseTemplateInput input =
                ResponseTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsAccountEntity)
                        .operationName(Operation.ADD_CHILD_DATA)
                        .contextData(Map.of(Operation.ADD_CHILD_DATA.name(), "roles"))
                        .build();
        ResponseTemplateOutput output = getResponseTemplateOutput(input);
        assertEquals("uid", output.getAttributes().get(0).getName());
        assertEquals("<EL>attributes.get('uid').get(0)</EL>", output.getAttributes().get(0).getValue());
    }

    @Test
    public void shouldReturnRemoveChildDataUserAsAccountResponseTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        ResponseTemplateInput input =
                ResponseTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsAccountEntity)
                        .operationName(Operation.REMOVE_CHILD_DATA)
                        .contextData(Map.of(Operation.REMOVE_CHILD_DATA.name(), "roles"))
                        .build();
        ResponseTemplateOutput output = getResponseTemplateOutput(input);
        assertEquals("uid", output.getAttributes().get(0).getName());
        assertEquals("<EL>attributes.get('uid').get(0)</EL>", output.getAttributes().get(0).getValue());
    }

    @Test
    public void shouldReturnSearchRoleAsEntitlementResponseTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        ResponseTemplateInput input =
                ResponseTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(roleAsEntitlementEntityName)
                        .operationName(Operation.SEARCH)
                        .build();
        ResponseTemplateOutput output = getResponseTemplateOutput(input);
        assertEquals("<JP>$.details[*]</JP>", output.getItems());
    }

    @Test
    public void shouldReturnGetUserAsIdentityResponseTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        ResponseTemplateInput input =
                ResponseTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsIdentityEntity)
                        .operationName(Operation.GET)
                        .build();
        ResponseTemplateOutput output = getResponseTemplateOutput(input);
        assertEquals("uid", output.getAttributes().get(0).getName());
        assertEquals("<JP>$.details[0].userlogin</JP>", output.getAttributes().get(0).getValue());
    }

    @Test
    public void shouldReturnSearchUserAsIdentityResponseTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        ResponseTemplateInput input =
                ResponseTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsIdentityEntity)
                        .operationName(Operation.SEARCH)
                        .build();
        ResponseTemplateOutput output = getResponseTemplateOutput(input);
        assertEquals("<JP>$.details[*]</JP>", output.getItems());
    }

}
