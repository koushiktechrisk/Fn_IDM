/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.schemaTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.SchemaTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.SchemaTemplateOutput;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class AzureSchemaTemplateFunctionTest extends SchemaTemplateFunctionTest {

  static String azureAdConnectedSystemName;

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

  @Test
  public void shouldReturnEightEntityInSchemaTemplateForazureadConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(azureAdConnectedSystemName);
    SchemaTemplateInput input =
        SchemaTemplateInput.builder().connectedSystemName(azureAdConnectedSystemName).build();
    SchemaTemplateOutput output = getSchemaTemplateOutput(input);
    assertEquals(8, output.getSchemaTemplates().size());
  }
}
