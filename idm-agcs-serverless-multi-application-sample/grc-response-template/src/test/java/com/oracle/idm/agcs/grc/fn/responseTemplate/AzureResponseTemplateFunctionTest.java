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
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class AzureResponseTemplateFunctionTest extends ResponseTemplateFunctionTest {

  static String azureAdConnectedSystemName;
  String UserAsAccountEntity = "Account";
  String UserAsIdentityEntity = "User";

  @BeforeClass
  public static void loadConfig() {
    String resourceName = "config.properties";
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Properties props = new Properties();
    try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
      props.load(resourceStream);
      azureAdConnectedSystemName = getPropertyValue(props, "azureAdConnectedSystemName");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  // Azure Get User as Account
  @Test
  public void shouldReturnGetUserAsAccountResponseTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.GET)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<JP>$.id</JP>", output.getAttributes().get(0).getValue());
  }

  // Azure Create User as Account
  @Test
  public void shouldReturnCreateUserAsAccountResponseTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.CREATE)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<JP>$.id</JP>", output.getAttributes().get(0).getValue());
  }

  // Azure Search User as Account
  @Test
  public void shouldReturnSearchUserAsAccountResponseTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.SEARCH)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("<JP>$.value[*]</JP>", output.getItems());
  }

  // Azure Update User as Account
  @Test
  public void shouldReturnUpdateUserAsAccountResponseTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.UPDATE)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<EL>attributes.get('uid').get(0)</EL>", output.getAttributes().get(0).getValue());
  }

  // Azure Disable User as Account
  @Test
  public void shouldReturnDisableUserAsAccountResponseTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.DISABLE)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<EL>attributes.get('uid').get(0)</EL>", output.getAttributes().get(0).getValue());
  }

  // Azure Enable User as Account
  @Test
  public void shouldReturnEnableUserAsAccountResponseTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.ENABLE)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<EL>attributes.get('uid').get(0)</EL>", output.getAttributes().get(0).getValue());
  }

  // Azure Get User as Identity
  @Test
  public void shouldReturnGetUserAsIdentityResponseTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsIdentityEntity)
            .operationName(Operation.GET)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<JP>$.id</JP>", output.getAttributes().get(0).getValue());
  }

  // Azure Search User as Identity
  @Test
  public void shouldReturnSearchUserAsIdentityResponseTemplateForAzureAdConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(azureAdConnectedSystemName)
            .entityName(UserAsIdentityEntity)
            .operationName(Operation.SEARCH)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("<JP>$.value[*]</JP>", output.getItems());
  }
}
