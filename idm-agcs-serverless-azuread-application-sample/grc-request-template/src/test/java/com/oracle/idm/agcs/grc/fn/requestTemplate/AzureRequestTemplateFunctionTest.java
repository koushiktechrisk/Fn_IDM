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
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

public class AzureRequestTemplateFunctionTest extends RequestTemplateFunctionTest {

  static String azureAdConnectedSystemName;
  String UserAsAccountEntity = "Account";
  String UserAsIdentityEntity = "User";

  @BeforeClass
  public static void loadConfig() {
    assumeTrue(Boolean.parseBoolean(null == System.getProperty("azureTestEnabled") ? "true" : System.getProperty("azureTestEnabled")));
    String resourceName = "config.properties";
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Properties props = new Properties();
    try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
      props.load(resourceStream);
      azureAdConnectedSystemName = getPropertyValue(props, "connectedSystemName");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // Azure Search User as Identity
  @Test
  public void shouldReturnSearchUserAsIdentityRequestTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateInput input =
        RequestTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsIdentityEntity)
            .operationName(Operation.SEARCH)
            .build();
    RequestTemplateOutput output = getRequestTemplateOutput(input);
    assertEquals("Search Users As Identity sort by displayName", output.getName());
  }

  // Azure Get User as Identity
  @Test
  public void shouldReturnGetUserAsIdentityRequestTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateInput input =
        RequestTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsIdentityEntity)
            .operationName(Operation.GET)
            .build();
    RequestTemplateOutput output = getRequestTemplateOutput(input);
    assertEquals("Get User As Identity By ID", output.getName());
  }

  // Azure Create User as Account
  @Test
  public void shouldReturnCreateUserAsAccountRequestTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateInput input =
        RequestTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.CREATE)
            .build();
    RequestTemplateOutput output = getRequestTemplateOutput(input);
    assertEquals("Create User", output.getName());
  }

  // Azure Get User as Account
  @Test
  public void shouldReturnGetUserAsAccountRequestTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    System.out.println("azureAdConnectedSystemName" + azureAdConnectedSystemName);
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateInput input =
        RequestTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.GET)
            .build();
    RequestTemplateOutput output = getRequestTemplateOutput(input);
    assertEquals("Get User By ID", output.getName());
  }

  // Azure Search User as Account
  @Test
  public void shouldReturnSearchUserAsAccountRequestTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateInput input =
        RequestTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.SEARCH)
            .build();
    RequestTemplateOutput output = getRequestTemplateOutput(input);
    assertEquals("Search Users sort by displayName", output.getName());
  }

  // Azure Delete User as Account
  @Test
  public void shouldReturnDeleteUserAsAccountRequestTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateInput input =
        RequestTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.DELETE)
            .build();
    RequestTemplateOutput output = getRequestTemplateOutput(input);
    assertEquals("Delete User", output.getName());
  }

  // Azure Update User as Account
  @Test
  public void shouldReturnUpdateUserAsAccountRequestTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateInput input =
        RequestTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.UPDATE)
            .build();
    RequestTemplateOutput output = getRequestTemplateOutput(input);
    assertEquals("Update User", output.getName());
  }

  // Azure Disable User as Account
  @Test
  public void shouldReturnDisableUserAsAccountRequestTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateInput input =
        RequestTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.DISABLE)
            .build();
    RequestTemplateOutput output = getRequestTemplateOutput(input);
    assertEquals("Disable User", output.getName());
  }

  // Azure Enable User as Account
  @Test
  public void shouldReturnEnableUserAsAccountRequestTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateInput input =
        RequestTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.ENABLE)
            .build();
    RequestTemplateOutput output = getRequestTemplateOutput(input);
    assertEquals("Enable User", output.getName());
  }
}
