/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.test.infra;

import com.oracle.idm.agcs.icfconnectors.commons.enums.Operation;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.ResponseTemplateOutput;
import org.junit.Assume;
import org.junit.jupiter.api.Assumptions;
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
public class AzureRequestResponseTemplateValidationTest extends RequestResponseTemplateValidationTest {

  String UserAsAccountEntity = "Account";
  String UserAsIdentityEntity = "User";

  static String officeGroupUidforAzure;
  static String securityGroupUidforAzure;
  static String roleUidforAzure;
  static String licenseUidforAzure;
  static String licenseSkuIdforAzure;

  String officeGroupAsEntitlementEntityName = "OfficeGroup";
  String securityGroupAsEntitlementEntityName = "SecurityGroup";
  String roleAsEntitlementEntityName = "Role";
  String licenseAsEntitlementEntityName = "License";

  static String azureAdConnectedSystemName;
  static String userAsIdentityUidforAzure;
  static String userAsAccountUidforAzure;
  static String createdUserAsAccountUidforAzure;

  @BeforeAll
  public static void loadConfig() {
    Assumptions.assumeTrue(Boolean.parseBoolean(null == System.getProperty("azureTestEnabled") ? "true" : System.getProperty("azureTestEnabled")));
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

  //SearchUserAsIdentity
  @Test
  @Order(1)
  public void validateSearchUserAsIdentityRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsIdentityEntity, Operation.SEARCH);
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsIdentityEntity, Operation.SEARCH);
    HashMap<String, Object> attributesMap = new HashMap<>();
    Map<String, Object> lastRecord =
            processAndValidateRequestAndResponseTemplate(
                    requestTemplate, responseTemplate, attributesMap);
    userAsIdentityUidforAzure = ((ArrayList<String>) lastRecord.get("uid")).get(0);
  }
  //GetUserAsIdentity
  @Test
  @Order(2)
  public void validateGetUserAsIdentityRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsIdentityEntity, Operation.GET);
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsIdentityEntity, Operation.GET);
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(userAsIdentityUidforAzure));
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }
  //   SearchOfficeGroupAsEntitlement
  @Test
  @Order(3)
  public void validateSearchOfficeGroupAsEntitlementRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(
                    azureAdConnectedSystemName, officeGroupAsEntitlementEntityName, Operation.SEARCH); // name as per schema templates
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(
                    azureAdConnectedSystemName, officeGroupAsEntitlementEntityName, Operation.SEARCH);
    HashMap<String, Object> attributesMap = new HashMap<>();
    Map<String, Object> lastRecord = processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
    officeGroupUidforAzure = ((ArrayList<String>) lastRecord.get("uid")).get(0);
  }
  //GetOfficeGroupAsEntitlement
  @Test
  @Order(4)
  public void validateGetOfficeGroupAsEntitlementRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(
                    azureAdConnectedSystemName, officeGroupAsEntitlementEntityName, Operation.GET);
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(
                    azureAdConnectedSystemName, officeGroupAsEntitlementEntityName, Operation.GET);
    HashMap<String, Object> attributesMap = new HashMap<>();
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("uid",List.of(officeGroupUidforAzure));
    attributesMap.put("officeGroups", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //   SearchSecurityGroupAsEntitlement
  @Test
  @Order(5)
  public void validateSearchSecurityGroupAsEntitlementRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(
                    azureAdConnectedSystemName, securityGroupAsEntitlementEntityName, Operation.SEARCH); // name as per schema templates
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(
                    azureAdConnectedSystemName, securityGroupAsEntitlementEntityName, Operation.SEARCH);
    HashMap<String, Object> attributesMap = new HashMap<>();
    Map<String, Object> lastRecord = processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
    securityGroupUidforAzure = ((ArrayList<String>) lastRecord.get("uid")).get(0);
  }

  //GetSecurityGroupAsEntitlement
  @Test
  @Order(6)
  public void validateGetSecurityGroupAsEntitlementRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(
                    azureAdConnectedSystemName, securityGroupAsEntitlementEntityName, Operation.GET);
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(
                    azureAdConnectedSystemName, securityGroupAsEntitlementEntityName, Operation.GET);
    HashMap<String, Object> attributesMap = new HashMap<>();
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("uid",List.of(securityGroupUidforAzure));
    attributesMap.put("securityGroups", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //   SearchRoleAsEntitlement
  @Test
  @Order(7)
  public void validateSearchRoleAsEntitlementRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(
                    azureAdConnectedSystemName, roleAsEntitlementEntityName, Operation.SEARCH); // name as per schema templates
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(
                    azureAdConnectedSystemName, roleAsEntitlementEntityName, Operation.SEARCH);
    HashMap<String, Object> attributesMap = new HashMap<>();
    Map<String, Object> lastRecord = processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
    roleUidforAzure = ((ArrayList<String>) lastRecord.get("uid")).get(0);
  }

  //GetRoleAsEntitlement
  @Test
  @Order(8)
  public void validateGetRoleAsEntitlementRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(
                    azureAdConnectedSystemName, roleAsEntitlementEntityName, Operation.GET);
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(
                    azureAdConnectedSystemName, roleAsEntitlementEntityName, Operation.GET);
    HashMap<String, Object> attributesMap = new HashMap<>();
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("uid",List.of(roleUidforAzure));
    attributesMap.put("roles", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //   SearchLicenseAsEntitlement
  //@Test
  @Order(9)
  public void validateSearchLicenseAsEntitlementRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(
                    azureAdConnectedSystemName, licenseAsEntitlementEntityName, Operation.SEARCH); // name as per schema templates
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(
                    azureAdConnectedSystemName, licenseAsEntitlementEntityName, Operation.SEARCH);
    HashMap<String, Object> attributesMap = new HashMap<>();
    Map<String, Object> lastRecord = processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
    licenseUidforAzure = ((ArrayList<String>) lastRecord.get("uid")).get(0);
    licenseSkuIdforAzure = ((ArrayList<String>) lastRecord.get("skuId")).get(0);
  }

  //GetLicenseAsEntitlement
  //@Test
  @Order(10)
  public void validateGetLicenseAsEntitlementRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(
                    azureAdConnectedSystemName, licenseAsEntitlementEntityName, Operation.GET);
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(
                    azureAdConnectedSystemName, licenseAsEntitlementEntityName, Operation.GET);
    HashMap<String, Object> attributesMap = new HashMap<>();
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("uid",List.of(licenseUidforAzure));
    attributesMap.put("licenses", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //SearchUserAsAccount as a target
  @Test
  @Order(11)
  public void validateSearchUserAsAccountRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.SEARCH);
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.SEARCH);
    HashMap<String, Object> attributesMap = new HashMap<>();
    Map<String, Object> lastRecord =
            processAndValidateRequestAndResponseTemplate(
                    requestTemplate, responseTemplate, attributesMap);
    userAsAccountUidforAzure = ((ArrayList<String>) lastRecord.get("uid")).get(0);
  }

  //GetUserAsAccount as a target
  @Test
  @Order(12)
  public void validateGetUserAsAccountRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.GET);
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.GET);
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(userAsAccountUidforAzure));
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //CreateUserAsAccount as a target
  @Test
  @Order(13)
  public void validateCreateUserAsAccountRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.CREATE);
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.CREATE);
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("name", List.of("Harshit63@idmBcone.onmicrosoft.com"));
    attributesMap.put("firstName", List.of("Harshit63"));
    attributesMap.put("lastName", List.of("Gupta63"));
    attributesMap.put("mailNickname", List.of("Harshit63"));
    attributesMap.put("password", List.of("Welcome@123"));
    attributesMap.put("displayName", List.of("Harshit63"));
    attributesMap.put("usageLocation",List.of("IN"));
    attributesMap.put("status", List.of(true));
    Map<String, Object> lastRecord = processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
    createdUserAsAccountUidforAzure = ((ArrayList<String>) lastRecord.get("uid")).get(0);
  }

  //UpdateUserAsAccount as a target
  @Test
  @Order(14)
  public void validateUpdateUserAsAccountRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.UPDATE);
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.UPDATE);
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUidforAzure));
    attributesMap.put("name", List.of("Harshit64@idmBcone.onmicrosoft.com"));
    attributesMap.put("firstName", List.of("Harshit64"));
    attributesMap.put("lastName", List.of("Gupta64"));
    attributesMap.put("mailNickname", List.of("Harshit64"));
    //attributesMap.put("password", List.of("Welcome@12345"));
    attributesMap.put("displayName", List.of("Harshit64"));
    attributesMap.put("status", List.of(true));
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //DisableUserAsAccount as a target
  @Test
  @Order(15)
  public void validateDisableUserAsAccountRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.DISABLE);
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.DISABLE);
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUidforAzure));
    attributesMap.put("status", List.of(false));
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //EnableUserAsAccount as a target
  @Test
  @Order(16)
  public void validateEnableUserAsAccountRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.ENABLE);
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.ENABLE);
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUidforAzure));
    attributesMap.put("status", List.of(true));
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //UserAsAccountAddOfficeGroups as a target
  @Test
  @Order(17)
  public void validateUserAsAccountAddOfficeGroupsRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.ADD_CHILD_DATA, Map.of(Operation.ADD_CHILD_DATA.name(),"officeGroups"));
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.ADD_CHILD_DATA, Map.of(Operation.ADD_CHILD_DATA.name(),"officeGroups"));
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUidforAzure));
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("uid", List.of(officeGroupUidforAzure));
    attributesMap.put("officeGroups", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //UserAsAccountRemoveOfficeGroups as a target
  @Test
  @Order(18)
  public void validateUserAsAccountRemoveOfficeGroupsRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.REMOVE_CHILD_DATA, Map.of(Operation.REMOVE_CHILD_DATA.name(),"officeGroups"));
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.REMOVE_CHILD_DATA, Map.of(Operation.REMOVE_CHILD_DATA.name(),"officeGroups"));
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUidforAzure));
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("uid", List.of(officeGroupUidforAzure));
    attributesMap.put("officeGroups", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //UserAsAccountAddRoles as a target
  @Test
  @Order(19)
  public void validateUserAsAccountAddRolesRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.ADD_CHILD_DATA, Map.of(Operation.ADD_CHILD_DATA.name(),"roles"));
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.ADD_CHILD_DATA, Map.of(Operation.ADD_CHILD_DATA.name(),"roles"));
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUidforAzure));
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("uid", List.of(roleUidforAzure));
    attributesMap.put("roles", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //UserAsAccountRemoveRoles as a target
  @Test
  @Order(20)
  public void validateUserAsAccountRemoveRolesRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.REMOVE_CHILD_DATA, Map.of(Operation.REMOVE_CHILD_DATA.name(),"roles"));
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.REMOVE_CHILD_DATA, Map.of(Operation.REMOVE_CHILD_DATA.name(),"roles"));
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUidforAzure));
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("uid", List.of(roleUidforAzure));
    attributesMap.put("roles", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //UserAsAccountAddLicenses as a target
  //@Test
  //@Order(21)
  public void validateUserAsAccountAddLicensesRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.ADD_CHILD_DATA, Map.of(Operation.ADD_CHILD_DATA.name(),"licenses"));
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.ADD_CHILD_DATA, Map.of(Operation.ADD_CHILD_DATA.name(),"licenses"));
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUidforAzure));
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("skuId", List.of(licenseSkuIdforAzure));
    attributesMap.put("licenses", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //UserAsAccountRemoveLicenses as a target
  //@Test
  //@Order(22)
  public void validateUserAsAccountRemoveLicensesRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.REMOVE_CHILD_DATA, Map.of(Operation.REMOVE_CHILD_DATA.name(),"licenses"));
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.REMOVE_CHILD_DATA, Map.of(Operation.REMOVE_CHILD_DATA.name(),"licenses"));
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUidforAzure));
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("skuId", List.of(licenseSkuIdforAzure));
    attributesMap.put("licenses", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //UserAsAccountAddSecurityGroups as a target
  @Test
  @Order(23)
  public void validateUserAsAccountAddSecurityGroupsRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.ADD_CHILD_DATA, Map.of(Operation.ADD_CHILD_DATA.name(),"securityGroups"));
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.ADD_CHILD_DATA, Map.of(Operation.ADD_CHILD_DATA.name(),"securityGroups"));
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUidforAzure));
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("uid", List.of(securityGroupUidforAzure));
    attributesMap.put("securityGroups", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //UserAsAccountRemoveSecurityGroups as a target
  @Test
  @Order(24)
  public void validateUserAsAccountRemoveSecurityGroupsRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.REMOVE_CHILD_DATA, Map.of(Operation.REMOVE_CHILD_DATA.name(),"securityGroups"));
    ResponseTemplateOutput responseTemplate =
            getResponseTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.REMOVE_CHILD_DATA, Map.of(Operation.REMOVE_CHILD_DATA.name(),"securityGroups"));
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUidforAzure));
    HashMap<String, Object> sunAttributesMap = new HashMap<>();
    sunAttributesMap.put("uid", List.of(securityGroupUidforAzure));
    attributesMap.put("securityGroups", sunAttributesMap);
    processAndValidateRequestAndResponseTemplate(requestTemplate, responseTemplate, attributesMap);
  }

  //DeleteUserAsAccount as a target
  @Test
  @Order(25)
  public void validateDeleteUserAsAccountRequestResponseTemplateAzureAd()
          throws IOException, URISyntaxException, InterruptedException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    RequestTemplateOutput requestTemplate =
            getRequestTemplateOutput(azureAdConnectedSystemName, UserAsAccountEntity, Operation.DELETE);
    HashMap<String, Object> attributesMap = new HashMap<>();
    attributesMap.put("uid", List.of(createdUserAsAccountUidforAzure));
    processAndValidateRequestAndResponseTemplate(requestTemplate, null, attributesMap);
  }
}
