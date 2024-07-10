/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.testTemplate.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.oracle.idm.agcs.icfconnectors.commons.model.KeyValue;
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

public class Util {

  public static final String configFilePath = "/test/applications/epm/config.yaml";
  public static final String templateJsonFilePath = "/test/applications/epm/TEMPLATE.json";
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

  public static RequestTemplateOutput getTemplateOutputFromResources() {
    try {
      return objectMapper.readValue(
          Util.class.getResourceAsStream(templateJsonFilePath), RequestTemplateOutput.class);
    } catch (IOException e) {
      System.err.println(
          "TestTemplateFunction is failed while mapping test template json to output model");
      throw new RuntimeException(
          "TestTemplateFunction is failed while mapping test template json to output model", e);
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
      RequestTemplateOutput testTemplateOutputFromResources, String authorizationValue) {
    for (KeyValue obj : testTemplateOutputFromResources.getHeaders()) {
      if (obj.getName().equalsIgnoreCase(HEADER_NAME_AUTHORIZATION)) {
        obj.setValue(authorizationValue);
      }
    }
  }

  public static void replaceUriSchemeValue(
      RequestTemplateOutput testTemplateOutputFromResources, String schemeValue) {
    testTemplateOutputFromResources.getUri().setScheme(schemeValue);
    if (null != testTemplateOutputFromResources.getSubRequests()
        && testTemplateOutputFromResources.getSubRequests().size() > 0) {
      for (RequestTemplateOutput subRequest : testTemplateOutputFromResources.getSubRequests()) {
        subRequest.getUri().setScheme(schemeValue);
      }
    }
  }

  public static void replaceUriHostValue(
      RequestTemplateOutput testTemplateOutputFromResources, String hostValue) {
    testTemplateOutputFromResources.getUri().setHost(hostValue);
    if (null != testTemplateOutputFromResources.getSubRequests()
        && testTemplateOutputFromResources.getSubRequests().size() > 0) {
      for (RequestTemplateOutput subRequest : testTemplateOutputFromResources.getSubRequests()) {
        subRequest.getUri().setHost(hostValue);
      }
    }
  }
}
