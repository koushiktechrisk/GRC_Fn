/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.test.infra;

import com.oracle.idm.agcs.icfconnectors.commons.enums.Operation;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.ResponseTemplateOutput;
import org.junit.Assume;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EpmRequestResponseTemplateValidationTest extends RequestResponseTemplateValidationTest {

    String UserAsAccountEntity = "UserAsAccount";
    String UserAsIdentityEntity = "UserAsIdentity";
    String roleAsEntitlementEntityName = "RoleAsEntitlement";

    static String connectedSystemName;
    static String userAsIdentityUid;
    static String roleUid;
    static String createdUserAsAccountUid;

    @BeforeAll
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
    @Order(1)
    public void validateSearchUserAsIdentityRequestResponseTemplate()
            throws IOException, URISyntaxException, InterruptedException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateOutput requestTemplate =
                getRequestTemplateOutput(connectedSystemName, UserAsIdentityEntity, Operation.SEARCH);
        ResponseTemplateOutput responseTemplate =
                getResponseTemplateOutput(connectedSystemName, UserAsIdentityEntity, Operation.SEARCH);
        HashMap<String, Object> attributesMap = new HashMap<>();
        Map<String, Object> lastRecord =
                processAndValidateRequestAndResponseTemplate(
                        requestTemplate, responseTemplate, attributesMap);
        userAsIdentityUid = ((ArrayList<String>) lastRecord.get("uid")).get(0);
    }


    @Test
    @Order(2)
    public void validateGetUserAsIdentityRequestResponseTemplate()
            throws IOException, URISyntaxException, InterruptedException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateOutput requestTemplate =
                getRequestTemplateOutput(connectedSystemName, UserAsIdentityEntity, Operation.GET);
        ResponseTemplateOutput responseTemplate =
                getResponseTemplateOutput(connectedSystemName, UserAsIdentityEntity, Operation.GET);
        HashMap<String, Object> attributesMap = new HashMap<>();
        attributesMap.put("uid", List.of(userAsIdentityUid));
        processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
    }

    @Test
    @Order(3)
    public void validateSearchRoleAsEntitlementRequestResponseTemplate()
            throws IOException, URISyntaxException, InterruptedException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateOutput requestTemplate =
                getRequestTemplateOutput(
                        connectedSystemName, roleAsEntitlementEntityName, Operation.SEARCH);
        ResponseTemplateOutput responseTemplate =
                getResponseTemplateOutput(
                        connectedSystemName, roleAsEntitlementEntityName, Operation.SEARCH);
        HashMap<String, Object> attributesMap = new HashMap<>();
        Map<String, Object> lastRecord =
                processAndValidateRequestAndResponseTemplate(
                        requestTemplate, responseTemplate, attributesMap);
        roleUid = ((ArrayList<String>) lastRecord.get("uid")).get(0);
    }

    @Test
    @Order(4)
    public void validateCreateUserAsAccountRequestResponseTemplate()
            throws IOException, URISyntaxException, InterruptedException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateOutput requestTemplate =
                getRequestTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.CREATE);
        ResponseTemplateOutput responseTemplate =
                getResponseTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.CREATE);
        HashMap<String, Object> attributesMap = new HashMap<>();
        attributesMap.put("firstName", List.of("abc"));
        attributesMap.put("lastName", List.of("def"));
        attributesMap.put("email", List.of("abc@testmail.com"));
        attributesMap.put("name", List.of("abc"));
        attributesMap.put("password", List.of("Abcdefg@123456"));
        Map<String, Object> lastRecord =
                processAndValidateRequestAndResponseTemplate(
                        requestTemplate, responseTemplate, attributesMap);
        createdUserAsAccountUid = ((ArrayList<String>) lastRecord.get("uid")).get(0);
    }

    @Test
    @Order(5)
    public void validateAddChildDataUserAsAccountRequestResponseTemplate()
            throws IOException, URISyntaxException, InterruptedException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateOutput requestTemplate =
                getRequestTemplateOutput(
                        connectedSystemName,
                        UserAsAccountEntity,
                        Operation.ADD_CHILD_DATA,
                        Map.of(Operation.ADD_CHILD_DATA.name(), "roles"));
        ResponseTemplateOutput responseTemplate =
                getResponseTemplateOutput(
                        connectedSystemName,
                        UserAsAccountEntity,
                        Operation.ADD_CHILD_DATA,
                        Map.of(Operation.ADD_CHILD_DATA.name(), "roles"));
        HashMap<String, Object> attributesMap = new HashMap<>();
        attributesMap.put("uid", List.of(createdUserAsAccountUid));
        HashMap<String, Object> sunAttributesMap = new HashMap<>();
        sunAttributesMap.put("uid", List.of(roleUid));
        attributesMap.put("roles", sunAttributesMap);
        processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
    }

    @Test
    @Order(6)
    public void validateGetUserAsAccountRequestResponseTemplate()
            throws IOException, URISyntaxException, InterruptedException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateOutput requestTemplate =
                getRequestTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.GET);
        ResponseTemplateOutput responseTemplate =
                getResponseTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.GET);
        HashMap<String, Object> attributesMap = new HashMap<>();
        attributesMap.put("uid", List.of(createdUserAsAccountUid));
        processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
    }

    @Test
    @Order(7)
    public void validateRemoveChildDataUserAsAccountRequestResponseTemplate()
            throws IOException, URISyntaxException, InterruptedException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateOutput requestTemplate =
                getRequestTemplateOutput(
                        connectedSystemName,
                        UserAsAccountEntity,
                        Operation.REMOVE_CHILD_DATA,
                        Map.of(Operation.REMOVE_CHILD_DATA.name(), "roles"));
        ResponseTemplateOutput responseTemplate =
                getResponseTemplateOutput(
                        connectedSystemName,
                        UserAsAccountEntity,
                        Operation.REMOVE_CHILD_DATA,
                        Map.of(Operation.REMOVE_CHILD_DATA.name(), "roles"));
        HashMap<String, Object> attributesMap = new HashMap<>();
        attributesMap.put("uid", List.of(createdUserAsAccountUid));
        HashMap<String, Object> sunAttributesMap = new HashMap<>();
        sunAttributesMap.put("uid", List.of(roleUid));
        attributesMap.put("roles", sunAttributesMap);
        processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
    }

    @Test
    @Order(8)
    public void validateDeleteUserAsAccountRequestResponseTemplate()
            throws IOException, URISyntaxException, InterruptedException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateOutput requestTemplate =
                getRequestTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.DELETE);
        HashMap<String, Object> attributesMap = new HashMap<>();
        attributesMap.put("uid", List.of(createdUserAsAccountUid));
        processAndValidateRequestAndResponseTemplate(requestTemplate, null, attributesMap);
    }

    @Test
    @Order(9)
    public void validateSearchUserAsAccountRequestResponseTemplate()
            throws IOException, URISyntaxException, InterruptedException {
        Assume.assumeNotNull(connectedSystemName);
        RequestTemplateOutput requestTemplate =
                getRequestTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.SEARCH);
        ResponseTemplateOutput responseTemplate =
                getResponseTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.SEARCH);
        HashMap<String, Object> attributesMap = new HashMap<>();
        processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
    }

}
