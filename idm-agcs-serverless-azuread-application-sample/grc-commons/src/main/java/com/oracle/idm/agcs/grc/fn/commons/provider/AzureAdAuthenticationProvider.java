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

public class AzureAdAuthenticationProvider extends AuthenticationProvider {
  @Override
  public String getAuthorizationValue(ConnectedSystemConfig connectedSystemConfig) {
    String url =
        connectedSystemConfig
            .getAuthenticationDetail()
            .get("scheme")
            .concat("://")
            .concat(connectedSystemConfig.getAuthenticationDetail().get("host"))
            .concat(connectedSystemConfig.getAuthenticationDetail().get("path"));
    String requestBody;
    try {
      String vaultJsonValue = VaultUtil.getDataFromVault(connectedSystemConfig.getAuthenticationDetail().get("secretId"), connectedSystemConfig.getAuthenticationDetail().get("region"));
      requestBody =
              "client_id="
                      + VaultUtil.getAttributeValueFromJson(vaultJsonValue, "clientCode")
                      + "&client_secret="
                      + VaultUtil.getAttributeValueFromJson(vaultJsonValue, "clientSecret")
                      + "&grant_type="
                      + connectedSystemConfig.getAuthenticationDetail().get("grantType")
                      + "&scope="
                      + connectedSystemConfig.getAuthenticationDetail().get("scope");
    } catch (UnsupportedEncodingException | JsonProcessingException e) {
      System.err.println("Exception occurred while getting secret from vault. " + e.getMessage());
      throw new ProcessingFailedException(
              "Exception occurred while getting secret from vault", e);
    }
    final HttpClient client = getHttpClient(connectedSystemConfig);
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header(AuthenticationProvider.HEADER_NAME_CONTENT_TYPE, AuthenticationProvider.HEADER_VALUE_CONTENT_TYPE_FORM_URL_ENCODED)
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();
    HttpResponse<String> response;
    try {
      response = client.send(request, HttpResponse.BodyHandlers.ofString());
    } catch (IOException e) {
      System.err.println(
          "AzureAdAuthenticationProvider Auth Token API HttpRequest failed due to IOException."
              + e.getMessage());
      throw new ProcessingFailedException(
          "AzureAdAuthenticationProvider Auth Token API HttpRequest failed due to IOException.", e);
    } catch (InterruptedException e) {
      System.err.println(
          "AzureAdAuthenticationProvider Auth Token API HttpRequest failed due to InterruptedException."
              + e.getMessage());
      throw new ProcessingFailedException(
          "AzureAdAuthenticationProvider Auth Token API HttpRequest failed due to InterruptedException.",
          e);
    }
    System.err.println(
        "AzureAdAuthenticationProvider Auth Token API HttpResponse is :: " + response.body());
    JsonNode jsonNode;
    try {
      jsonNode = AuthenticationProvider.objectMapper.readTree(response.body());
    } catch (JsonProcessingException e) {
      System.err.println(
          "AzureAdAuthenticationProvider Auth Token API HttpResponse parsing to json is failed.");
      throw new ProcessingFailedException(
          "AzureAdAuthenticationProvider Auth Token API HttpResponse parsing to json is failed.",
          e);
    }
    return AuthenticationProvider.TOKEN_PREFIX_BEARER.concat(jsonNode.get(AuthenticationProvider.ACCESS_TOKEN_ATTRIBUTE).textValue());
  }

  private HttpClient getHttpClient(ConnectedSystemConfig connectedSystemConfig) {
    if (null == connectedSystemConfig.getAuthenticationDetail().get("proxyHost")
        || null == connectedSystemConfig.getAuthenticationDetail().get("proxyPort")) {
      return HttpClient.newHttpClient();
    }
    return HttpClient.newBuilder()
        .proxy(
            ProxySelector.of(
                new InetSocketAddress(
                    connectedSystemConfig.getAuthenticationDetail().get("proxyHost"),
                    Integer.parseInt(connectedSystemConfig.getAuthenticationDetail().get("proxyPort")))))
        .build();
  }
}
