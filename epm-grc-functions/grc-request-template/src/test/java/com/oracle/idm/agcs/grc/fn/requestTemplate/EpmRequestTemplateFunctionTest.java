/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.requestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oracle.idm.agcs.icfconnectors.commons.enums.Operation;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.RequestTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class EpmRequestTemplateFunctionTest extends RequestTemplateFunctionTest {

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
    public void shouldReturnCreateUserAsAccountRequestTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateInput input =
                RequestTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsAccountEntity)
                        .operationName(Operation.CREATE)
                        .build();
        RequestTemplateOutput output = getRequestTemplateOutput(input);
        assertEquals("Create User", output.getName());
    }

    @Test
    public void shouldReturnGetUserAsAccountRequestTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateInput input =
                RequestTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsAccountEntity)
                        .operationName(Operation.GET)
                        .build();
        RequestTemplateOutput output = getRequestTemplateOutput(input);
        assertEquals("Get User By ID", output.getName());
    }

    @Test
    public void shouldReturnSearchUserAsAccountRequestTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateInput input =
                RequestTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsAccountEntity)
                        .operationName(Operation.SEARCH)
                        .build();
        RequestTemplateOutput output = getRequestTemplateOutput(input);
        assertEquals("Search Users", output.getName());
    }

    @Test
    public void shouldReturnDeleteUserAsAccountRequestTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateInput input =
                RequestTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsAccountEntity)
                        .operationName(Operation.DELETE)
                        .build();
        RequestTemplateOutput output = getRequestTemplateOutput(input);
        assertEquals("Delete User", output.getName());
    }

    @Test
    public void shouldReturnAddChildDataUserAsAccountRequestTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateInput input =
                RequestTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsAccountEntity)
                        .operationName(Operation.ADD_CHILD_DATA)
                        .contextData(Map.of(Operation.ADD_CHILD_DATA.name(), "roles"))
                        .build();
        RequestTemplateOutput output = getRequestTemplateOutput(input);
        assertEquals("User Add Role Membership", output.getName());
    }

    @Test
    public void shouldReturnRemoveChildDataUserAsAccountRequestTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateInput input =
                RequestTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsAccountEntity)
                        .operationName(Operation.REMOVE_CHILD_DATA)
                        .contextData(Map.of(Operation.REMOVE_CHILD_DATA.name(), "roles"))
                        .build();
        RequestTemplateOutput output = getRequestTemplateOutput(input);
        assertEquals("User Remove Role Membership", output.getName());
    }

    @Test
    public void shouldReturnSearchRoleAsEntitlementRequestTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateInput input =
                RequestTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(roleAsEntitlementEntityName)
                        .operationName(Operation.SEARCH)
                        .build();
        RequestTemplateOutput output = getRequestTemplateOutput(input);
        assertEquals("Search Roles", output.getName());
    }

    @Test
    public void shouldReturnGetUserAsIdentityRequestTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateInput input =
                RequestTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsIdentityEntity)
                        .operationName(Operation.GET)
                        .build();
        RequestTemplateOutput output = getRequestTemplateOutput(input);
        assertEquals("Get User As Identity By ID", output.getName());
    }

    @Test
    public void shouldReturnSearchUserAsIdentityRequestTemplateForEPMConnectedSystem()
            throws JsonProcessingException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateInput input =
                RequestTemplateInput.builder()
                        .connectedSystemName(connectedSystemName)
                        .entityName(UserAsIdentityEntity)
                        .operationName(Operation.SEARCH)
                        .build();
        RequestTemplateOutput output = getRequestTemplateOutput(input);
        assertEquals("Search Users As Identity", output.getName());
    }
}
