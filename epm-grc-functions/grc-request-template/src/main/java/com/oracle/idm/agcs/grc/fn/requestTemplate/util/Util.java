/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.requestTemplate.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.oracle.idm.agcs.icfconnectors.commons.enums.Operation;
import com.oracle.idm.agcs.icfconnectors.commons.model.KeyValue;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.RequestTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;
import com.oracle.idm.agcs.icfconnectors.commons.util.VaultUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Map;

public class Util {

  public static final String configFilePath = "/request/applications/epm/config.yaml";
  public static final String templateJsonFilePath =
          "/request/applications/epm/<ENTITY_NAME>/<OPERATION_NAME>_TEMPLATE.json";
  public static final String membershipAttributeTemplateJsonFilePath =
          "/request/applications/epm/<ENTITY_NAME>/<MEMBERSHIP_ATTRIBUTE_NAME>/<OPERATION_NAME>_TEMPLATE.json";
  public static final String entityNamePlaceHolder = "<ENTITY_NAME>";
  public static final String operationNamePlaceHolder = "<OPERATION_NAME>";
  public static final String membershipAttributeNamePlaceHolder = "<MEMBERSHIP_ATTRIBUTE_NAME>";

  public static final ObjectMapper objectMapper = new ObjectMapper();

  public static final String TOKEN_PREFIX_BASIC = "Basic ";
  public static final String TOKEN_PREFIX_BEARER = "Bearer ";
  public static final String HEADER_NAME_AUTHORIZATION = "Authorization";
  public static final String HEADER_NAME_CONTENT_TYPE = "Content-Type";
  public static final String ACCESS_TOKEN_ATTRIBUTE = "access_token";
  public static final String HEADER_VALUE_CONTENT_TYPE_FORM_URL_ENCODED =
      "application/x-www-form-urlencoded";
  public static final String CONNECTOR_PROXY_HOST = "CONNECTOR_PROXY_HOST";
  public static final String CONNECTOR_PROXY_PORT = "CONNECTOR_PROXY_PORT";

  private Util() {
    throw new IllegalStateException("Util is a Utility class");
  }

  public static Config getConfig() {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    mapper.findAndRegisterModules();
    try {
      return mapper.readValue(Util.class.getResourceAsStream(configFilePath), Config.class);
    } catch (IOException e) {
      System.err.println(
          "RequestTemplateFunction application configuration initialization is failed while mapping config yaml to config model.");
      throw new RuntimeException(
          "RequestTemplateFunction application configuration initialization is failed while mapping config yaml to config model.",
          e);
    }
  }

  public static RequestTemplateOutput getTemplateOutputFromResources(
      String entityName, Operation operationName, Map<String, String> contextData) {
    try {
      String jsonFilePath =
          templateJsonFilePath
              .replace(entityNamePlaceHolder, entityName)
              .replace(operationNamePlaceHolder, operationName.name());
      if (operationName == Operation.ADD_CHILD_DATA
          || operationName == Operation.REMOVE_CHILD_DATA) {
        jsonFilePath =
            membershipAttributeTemplateJsonFilePath
                .replace(entityNamePlaceHolder, entityName)
                .replace(membershipAttributeNamePlaceHolder, contextData.get(operationName.name()))
                .replace(operationNamePlaceHolder, operationName.name());
      }
      return objectMapper.readValue(
          Util.class.getResourceAsStream(jsonFilePath), RequestTemplateOutput.class);
    } catch (IOException e) {
      System.err.println(
          "RequestTemplateFunction is failed while mapping request template json to output model");
      throw new RuntimeException(
          "RequestTemplateFunction is failed while mapping request template json to output model",
          e);
    }
  }

  public static String getAuthorizationValueFromEpm(Config config) {
    try {
      String vaultJsonValue =
              VaultUtil.getDataFromVault(
                      config.getAuthenticationDetail().get("secretId"),
                      config.getAuthenticationDetail().get("region"));
      String username = VaultUtil.getAttributeValueFromJson(vaultJsonValue, "username");
      String password = VaultUtil.getAttributeValueFromJson(vaultJsonValue, "password");
      String authHeader = username.concat(":").concat(password);
      return TOKEN_PREFIX_BASIC
              + Base64.getEncoder()
              .encodeToString(authHeader.getBytes(StandardCharsets.UTF_8));
    } catch (UnsupportedEncodingException | JsonProcessingException e) {
      System.err.println("Exception occurred while getting secret from vault. " + e.getMessage());
      throw new RuntimeException("Exception occurred while getting secret from vault", e);
    }
  }

  public static void replaceAuthorizationHeaderValue(
      RequestTemplateOutput requestTemplateOutput, String authorizationValue) {
    for (KeyValue obj : requestTemplateOutput.getHeaders()) {
      if (obj.getName().equalsIgnoreCase(HEADER_NAME_AUTHORIZATION)) {
        obj.setValue(authorizationValue);
      }
    }
    if (null != requestTemplateOutput.getSubRequests()
        && !requestTemplateOutput.getSubRequests().isEmpty()) {
      for (RequestTemplateOutput subRequest : requestTemplateOutput.getSubRequests()) {
        for (KeyValue obj : subRequest.getHeaders()) {
          if (obj.getName().equalsIgnoreCase(HEADER_NAME_AUTHORIZATION)) {
            obj.setValue(authorizationValue);
          }
        }
      }
    }
  }

  public static void replaceUriSchemeValue(
      RequestTemplateOutput requestTemplateOutput, String schemeValue) {
    requestTemplateOutput.getUri().setScheme(schemeValue);
    if (null != requestTemplateOutput.getSubRequests()
        && !requestTemplateOutput.getSubRequests().isEmpty()) {
      for (RequestTemplateOutput subRequest : requestTemplateOutput.getSubRequests()) {
        subRequest.getUri().setScheme(schemeValue);
      }
    }
  }

  public static void replaceUriHostValue(
      RequestTemplateOutput requestTemplateOutput, String hostValue) {
    requestTemplateOutput.getUri().setHost(hostValue);
    if (null != requestTemplateOutput.getSubRequests()
        && !requestTemplateOutput.getSubRequests().isEmpty()) {
      for (RequestTemplateOutput subRequest : requestTemplateOutput.getSubRequests()) {
        subRequest.getUri().setHost(hostValue);
      }
    }
  }

  public static void validateInputContextData(RequestTemplateInput input) {
    if (null != input.getOperationName()
        && (input.getOperationName() == Operation.ADD_CHILD_DATA
            || input.getOperationName() == Operation.REMOVE_CHILD_DATA)
        && (null == input.getContextData()
            || input.getContextData().isEmpty()
            || null == input.getContextData().get(input.getOperationName().name()))) {

      System.err.println("RequestTemplateFunction input is invalid. contextData is not present.");
      throw new RuntimeException(
          "RequestTemplateFunction input is invalid. contextData is not present.");
    }
  }
}
