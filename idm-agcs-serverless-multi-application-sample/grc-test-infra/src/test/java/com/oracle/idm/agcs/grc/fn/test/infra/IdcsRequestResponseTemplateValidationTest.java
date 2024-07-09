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
public class IdcsRequestResponseTemplateValidationTest extends RequestResponseTemplateValidationTest{

  String UserAsAccountEntity = "UserAsAccount";
  String UserAsIdentityEntity = "UserAsIdentity";
  String countriesEntityName = "countries";
  String languagesEntityName = "languages";
  String groupAsEntitlementEntityName = "GroupAsEntitlement";
  String nameAttributeValue = "Anup Kumar Gautam"+System.currentTimeMillis();
  String emailAttributeValue = System.currentTimeMillis()+"anup@testmail.com";
  String lastNameAttributeValue = "Gautam"+System.currentTimeMillis();

  static String connectedSystemName;
  static String userAsIdentityUid;
  static String groupUid;
  static String createdUserAsAccountUid;

  @BeforeAll
  public static void loadConfig() {
    String resourceName = "config.properties";
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Properties props = new Properties();
    try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
      props.load(resourceStream);
      connectedSystemName = getPropertyValue(props, "idcsConnectedSystemName");
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
  public void validateSearchGroupAsEntitlementRequestResponseTemplate()
      throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(connectedSystemName);
    RequestTemplateOutput requestTemplate =
        getRequestTemplateOutput(
            connectedSystemName, groupAsEntitlementEntityName, Operation.SEARCH);
    ResponseTemplateOutput responseTemplate =
        getResponseTemplateOutput(
            connectedSystemName, groupAsEntitlementEntityName, Operation.SEARCH);
    HashMap<String, Object> attributesMap = new HashMap<>();
    Map<String, Object> lastRecord =
        processAndValidateRequestAndResponseTemplate(
            requestTemplate, responseTemplate, attributesMap);
    groupUid = ((ArrayList<String>) lastRecord.get("uid")).get(0);
  }

  @Test
  @Order(4)
  public void validateGetGroupAsEntitlementRequestResponseTemplate()
      throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(connectedSystemName);
    RequestTemplateOutput requestTemplate =
        getRequestTemplateOutput(connectedSystemName, groupAsEntitlementEntityName, Operation.GET);
    ResponseTemplateOutput responseTemplate =
        getResponseTemplateOutput(connectedSystemName, groupAsEntitlementEntityName, Operation.GET);
    HashMap<String, Object> attributesMap = new HashMap<>();
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("uid", List.of(groupUid));
    attributesMap.put("groups", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  @Test
  @Order(5)
  public void validateCreateUserAsAccountRequestResponseTemplate()
      throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(connectedSystemName);
    RequestTemplateOutput requestTemplate =
        getRequestTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.CREATE);
    ResponseTemplateOutput responseTemplate =
        getResponseTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.CREATE);
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("name", List.of(nameAttributeValue));
    attributesMap.put("firstName", List.of("Anup"));
    attributesMap.put("lastName", List.of(lastNameAttributeValue));
    attributesMap.put("middleName", List.of("Kumar"));
    attributesMap.put("password", List.of("Abcdefg@123456"));
    attributesMap.put("email", List.of(emailAttributeValue));
    Map<String, Object> lastRecord =
        processAndValidateRequestAndResponseTemplate(
            requestTemplate, responseTemplate, attributesMap);
    createdUserAsAccountUid = ((ArrayList<String>) lastRecord.get("uid")).get(0);
  }

  @Test
  @Order(6)
  public void validateAddChildDataUserAsAccountRequestResponseTemplate()
      throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(connectedSystemName);
    RequestTemplateOutput requestTemplate =
        getRequestTemplateOutput(
            connectedSystemName,
            UserAsAccountEntity,
            Operation.ADD_CHILD_DATA,
            Map.of(Operation.ADD_CHILD_DATA.name(), "groups"));
    ResponseTemplateOutput responseTemplate =
        getResponseTemplateOutput(
            connectedSystemName,
            UserAsAccountEntity,
            Operation.ADD_CHILD_DATA,
            Map.of(Operation.ADD_CHILD_DATA.name(), "groups"));
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUid));
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("uid", List.of(groupUid));
    attributesMap.put("groups", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  @Test
  @Order(7)
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
  @Order(8)
  public void validateRemoveChildDataUserAsAccountRequestResponseTemplate()
      throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(connectedSystemName);
    RequestTemplateOutput requestTemplate =
        getRequestTemplateOutput(
            connectedSystemName,
            UserAsAccountEntity,
            Operation.REMOVE_CHILD_DATA,
            Map.of(Operation.REMOVE_CHILD_DATA.name(), "groups"));
    ResponseTemplateOutput responseTemplate =
        getResponseTemplateOutput(
            connectedSystemName,
            UserAsAccountEntity,
            Operation.REMOVE_CHILD_DATA,
            Map.of(Operation.REMOVE_CHILD_DATA.name(), "groups"));
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUid));
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("uid", List.of(groupUid));
    attributesMap.put("groups", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  @Test
  @Order(9)
  public void validateDisableUserAsAccountRequestResponseTemplate()
      throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(connectedSystemName);
    RequestTemplateOutput requestTemplate =
        getRequestTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.DISABLE);
    ResponseTemplateOutput responseTemplate =
        getResponseTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.DISABLE);
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUid));
    attributesMap.put("status", List.of("false"));
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  @Test
  @Order(10)
  public void validateEnableUserAsAccountRequestResponseTemplate()
      throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(connectedSystemName);
    RequestTemplateOutput requestTemplate =
        getRequestTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.ENABLE);
    ResponseTemplateOutput responseTemplate =
        getResponseTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.ENABLE);
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUid));
    attributesMap.put("status", List.of("true"));
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  @Test
  @Order(11)
  public void validateUpdateUserAsAccountRequestResponseTemplate()
      throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(connectedSystemName);
    RequestTemplateOutput requestTemplate =
        getRequestTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.ENABLE);
    ResponseTemplateOutput responseTemplate =
        getResponseTemplateOutput(connectedSystemName, UserAsAccountEntity, Operation.ENABLE);
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUid));
    attributesMap.put("name", List.of(nameAttributeValue));
    attributesMap.put("status", List.of("true"));
    attributesMap.put("firstName", List.of("Anup"));
    attributesMap.put("lastName", List.of(lastNameAttributeValue));
    attributesMap.put("middleName", List.of("Kumar1"));
    attributesMap.put("email", List.of(emailAttributeValue));
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  @Test
  @Order(12)
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
  @Order(13)
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

  @Test
  @Order(14)
  public void validateSearchCountriesRequestResponseTemplate()
      throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(connectedSystemName);
    RequestTemplateOutput requestTemplate =
        getRequestTemplateOutput(connectedSystemName, countriesEntityName, Operation.SEARCH);
    ResponseTemplateOutput responseTemplate =
        getResponseTemplateOutput(connectedSystemName, countriesEntityName, Operation.SEARCH);

    HashMap<String, Object> attributesMap = new HashMap<>();
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  @Test
  @Order(15)
  public void validateSearchLanguagesRequestResponseTemplate()
      throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(connectedSystemName);
    RequestTemplateOutput requestTemplate =
        getRequestTemplateOutput(connectedSystemName, languagesEntityName, Operation.SEARCH);
    ResponseTemplateOutput responseTemplate =
        getResponseTemplateOutput(connectedSystemName, languagesEntityName, Operation.SEARCH);

    HashMap<String, Object> attributesMap = new HashMap<>();
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

}
