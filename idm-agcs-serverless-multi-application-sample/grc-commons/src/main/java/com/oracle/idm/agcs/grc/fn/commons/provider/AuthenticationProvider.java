/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.commons.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.idm.agcs.grc.fn.commons.config.ConnectedSystemConfig;

public abstract class AuthenticationProvider {

  public static final String TOKEN_PREFIX_BASIC = "Basic ";
  public static final String TOKEN_PREFIX_BEARER = "Bearer ";
  public static final String HEADER_NAME_CONTENT_TYPE = "Content-Type";
  public static final String HEADER_NAME_AUTHORIZATION = "Authorization";
  public static final String ACCESS_TOKEN_ATTRIBUTE = "access_token";
  public static final String HEADER_VALUE_CONTENT_TYPE_FORM_URL_ENCODED =
      "application/x-www-form-urlencoded";
  public static final String CONNECTOR_PROXY_HOST="CONNECTOR_PROXY_HOST";
  public static final String CONNECTOR_PROXY_PORT="CONNECTOR_PROXY_PORT";

  public static final ObjectMapper objectMapper = new ObjectMapper();

  public abstract String getAuthorizationValue(ConnectedSystemConfig connectedSystemConfig);
}
