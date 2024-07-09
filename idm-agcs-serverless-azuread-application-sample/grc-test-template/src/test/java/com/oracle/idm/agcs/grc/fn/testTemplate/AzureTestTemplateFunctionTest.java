/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.testTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.TestTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

public class AzureTestTemplateFunctionTest extends TestTemplateFunctionTest {

  static String azureAdConnectedSystemName;

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

  @Test
  public void shouldReturnTestTemplateForAzureAd() throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    TestTemplateInput input =
        TestTemplateInput.builder().connectedSystemName(azureAdConnectedSystemName).build();
    RequestTemplateOutput output = getTestTemplateOutput(input);
    assertEquals("Test connectivity invoking get Users API", output.getName());
  }
}
