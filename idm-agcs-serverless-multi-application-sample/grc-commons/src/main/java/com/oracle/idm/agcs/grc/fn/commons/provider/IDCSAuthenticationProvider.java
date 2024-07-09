/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.commons.provider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.oracle.idm.agcs.grc.fn.commons.config.ConnectedSystemConfig;
import com.oracle.idm.agcs.grc.fn.commons.exception.ProcessingFailedException;
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

public class IDCSAuthenticationProvider extends AuthenticationProvider {

  @Override
  public String getAuthorizationValue(ConnectedSystemConfig connectedSystemConfig) {
    String url =
        connectedSystemConfig
            .getAuthenticationDetail()
            .get("scheme")
            .concat("://")
            .concat(connectedSystemConfig.getAuthenticationDetail().get("host"))
            .concat(connectedSystemConfig.getAuthenticationDetail().get("path"));
    String requestBody = "grant_type=client_credentials&scope=urn%3Aopc%3Aidm%3A__myscopes__";
    String authHeader;
    try {
      String vaultJsonValue = VaultUtil.getDataFromVault(connectedSystemConfig.getAuthenticationDetail().get("secretId"), connectedSystemConfig.getAuthenticationDetail().get("region"));
      String clientCode =  VaultUtil.getAttributeValueFromJson(vaultJsonValue, "clientCode");
      String clientSecret = VaultUtil.getAttributeValueFromJson(vaultJsonValue, "clientSecret");
      authHeader = clientCode.concat(":").concat(clientSecret);
    } catch (UnsupportedEncodingException | JsonProcessingException e) {
      System.err.println("Exception occurred while getting secret from vault. " + e.getMessage());
      throw new ProcessingFailedException(
              "Exception occurred while getting secret from vault", e);
    }
    HttpClient client = HttpClient.newBuilder().build();
    String connectorProxyHost = System.getProperty(CONNECTOR_PROXY_HOST);
    String connectorProxyPort = System.getProperty(CONNECTOR_PROXY_PORT);
    if (null != connectorProxyHost
            && !connectorProxyHost.trim().isEmpty()
            && null != connectorProxyPort
            && !connectorProxyPort.trim().isEmpty()) {
      System.out.println(
              MessageFormat.format(
                      "connectorProxyHost {0} and connectorProxyPort {1} is available in system property",
                      connectorProxyHost, connectorProxyPort));
      try {
        client =
                HttpClient.newBuilder()
                        .proxy(
                                ProxySelector.of(
                                        new InetSocketAddress(
                                                connectorProxyHost, Integer.parseInt(connectorProxyPort))))
                        .build();
      } catch (NumberFormatException exception) {
        System.err.println("connectorProxyPort value is not integer : "+exception);
      }
    }
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header(HEADER_NAME_CONTENT_TYPE, HEADER_VALUE_CONTENT_TYPE_FORM_URL_ENCODED)
            .header(
                HEADER_NAME_AUTHORIZATION,
                TOKEN_PREFIX_BASIC
                    + Base64.getEncoder()
                        .encodeToString(authHeader.getBytes(StandardCharsets.UTF_8)))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
    HttpResponse<String> response;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      System.err.println(
          "IDCSAuthenticationProvider Auth Token API HttpRequest failed due to IOException."
              + e.getMessage());
      throw new ProcessingFailedException(
          "IDCSAuthenticationProvider Auth Token API HttpRequest failed due to IOException.", e);
    } catch (InterruptedException e) {
      System.err.println(
          "IDCSAuthenticationProvider Auth Token API HttpRequest failed due to InterruptedException."
              + e.getMessage());
      throw new ProcessingFailedException(
          "IDCSAuthenticationProvider Auth Token API HttpRequest failed due to InterruptedException.",
          e);
    }
    System.err.println(
        "IDCSAuthenticationProvider Auth Token API HttpResponse is :: " + response.body());
    JsonNode jsonNode;
    try {
      jsonNode = objectMapper.readTree(response.body());
    } catch (JsonProcessingException e) {
      System.err.println(
          "IDCSAuthenticationProvider Auth Token API HttpResponse parsing to json is failed.");
      throw new ProcessingFailedException(
          "IDCSAuthenticationProvider Auth Token API HttpResponse parsing to json is failed.", e);
    }
    return TOKEN_PREFIX_BEARER.concat(jsonNode.get(ACCESS_TOKEN_ATTRIBUTE).textValue());
  }
}
