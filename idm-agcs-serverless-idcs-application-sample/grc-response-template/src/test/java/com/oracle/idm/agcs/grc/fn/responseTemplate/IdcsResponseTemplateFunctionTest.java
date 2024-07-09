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

public class IdcsResponseTemplateFunctionTest extends ResponseTemplateFunctionTest {

  static String idcsConnectedSystemName;
  String UserAsAccountEntity = "UserAsAccount";
  String UserAsIdentityEntity = "UserAsIdentity";
  String countriesEntityName = "countries";
  String languagesEntityName = "languages";
  String groupAsEntitlementEntityName = "GroupAsEntitlement";

  @BeforeClass
  public static void loadConfig() {
    String resourceName = "config.properties";
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Properties props = new Properties();
    try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
      props.load(resourceStream);
      idcsConnectedSystemName = getPropertyValue(props, "connectedSystemName");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void shouldReturnCreateUserAsAccountResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.CREATE)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<JP>$.id</JP>", output.getAttributes().get(0).getValue());
  }

  @Test
  public void shouldReturnGetUserAsAccountResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.GET)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<JP>$.id</JP>", output.getAttributes().get(0).getValue());
  }

  @Test
  public void shouldReturnSearchUserAsAccountResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.SEARCH)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("<JP>$.Resources[*]</JP>", output.getItems());
  }

  @Test
  public void shouldReturnUpdateUserAsAccountResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.UPDATE)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<EL>attributes.get('uid').get(0)</EL>", output.getAttributes().get(0).getValue());
  }

  @Test
  public void shouldReturnDisableUserAsAccountResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.DISABLE)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<EL>attributes.get('uid').get(0)</EL>", output.getAttributes().get(0).getValue());
  }

  @Test
  public void shouldReturnEnableUserAsAccountResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.ENABLE)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<EL>attributes.get('uid').get(0)</EL>", output.getAttributes().get(0).getValue());
  }

  @Test
  public void shouldReturnAddChildDataUserAsAccountResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.ADD_CHILD_DATA)
            .contextData(Map.of(Operation.ADD_CHILD_DATA.name(), "groups"))
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<EL>attributes.get('uid').get(0)</EL>", output.getAttributes().get(0).getValue());
  }

  @Test
  public void shouldReturnRemoveChildDataUserAsAccountResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(UserAsAccountEntity)
            .operationName(Operation.REMOVE_CHILD_DATA)
            .contextData(Map.of(Operation.REMOVE_CHILD_DATA.name(), "groups"))
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<EL>attributes.get('uid').get(0)</EL>", output.getAttributes().get(0).getValue());
  }

  @Test
  public void shouldReturnGetGroupAsEntitlementResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(groupAsEntitlementEntityName)
            .operationName(Operation.GET)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<JP>$.id</JP>", output.getAttributes().get(0).getValue());
  }

  @Test
  public void shouldReturnSearchGroupAsEntitlementResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(groupAsEntitlementEntityName)
            .operationName(Operation.SEARCH)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("<JP>$.Resources[*]</JP>", output.getItems());
  }

  @Test
  public void shouldReturnGetUserAsIdentityResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(UserAsIdentityEntity)
            .operationName(Operation.GET)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("uid", output.getAttributes().get(0).getName());
    assertEquals("<JP>$.id</JP>", output.getAttributes().get(0).getValue());
  }

  @Test
  public void shouldReturnSearchUserAsIdentityResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(UserAsIdentityEntity)
            .operationName(Operation.SEARCH)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("<JP>$.Resources[*]</JP>", output.getItems());
  }

  @Test
  public void shouldReturnGetCountriesResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(countriesEntityName)
            .operationName(Operation.SEARCH)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("<JP>$.attrValues[*]</JP>", output.getItems());
  }

  @Test
  public void shouldReturnGetLanguagesResponseTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    ResponseTemplateInput input =
        ResponseTemplateInput.builder()
            .connectedSystemName(idcsConnectedSystemName)
            .entityName(languagesEntityName)
            .operationName(Operation.SEARCH)
            .build();
    ResponseTemplateOutput output = getResponseTemplateOutput(input);
    assertEquals("<JP>$.attrValues[*]</JP>", output.getItems());
  }
}
