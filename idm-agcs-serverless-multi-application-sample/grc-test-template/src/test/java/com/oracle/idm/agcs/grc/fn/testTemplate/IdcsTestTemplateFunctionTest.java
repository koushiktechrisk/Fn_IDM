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

public class IdcsTestTemplateFunctionTest extends TestTemplateFunctionTest {

  static String idcsConnectedSystemName;

  @BeforeClass
  public static void loadConfig() {
    String resourceName = "config.properties";
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Properties props = new Properties();
    try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
      props.load(resourceStream);
      idcsConnectedSystemName = getPropertyValue(props, "idcsConnectedSystemName");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void shouldReturnTestTemplateForIdcs() throws JsonProcessingException {
    Assume.assumeNotNull(idcsConnectedSystemName);
    TestTemplateInput input =
        TestTemplateInput.builder().connectedSystemName(idcsConnectedSystemName).build();
    RequestTemplateOutput output = getTestTemplateOutput(input);
    assertEquals("Test connectivity invoking schema API", output.getName());
  }
}
