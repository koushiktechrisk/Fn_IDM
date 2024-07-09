/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.schemaTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnproject.fn.testing.FnResult;
import com.fnproject.fn.testing.FnTestingRule;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.SchemaTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.SchemaTemplateOutput;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SchemaTemplateFunctionTest {

  @Rule public final FnTestingRule testing = FnTestingRule.createDefault();

  ObjectMapper mapper = new ObjectMapper();
  String functionMethodName = "handleRequest";
  static String connectedSystemName = "idcs";

  @Test
  public void shouldReturnFiveEntityInSchemaTemplateForIdcsConnectedSystem()
      throws JsonProcessingException {
    Assume.assumeNotNull(connectedSystemName);
    SchemaTemplateInput input =
        SchemaTemplateInput.builder().connectedSystemName(connectedSystemName).build();
    SchemaTemplateOutput output = getSchemaTemplateOutput(input);
    assertEquals(5, output.getSchemaTemplates().size());
  }

  SchemaTemplateOutput getSchemaTemplateOutput(SchemaTemplateInput input)
      throws JsonProcessingException {
    String requestBody = mapper.writeValueAsString(input);
    testing.givenEvent().withBody(requestBody).enqueue();
    testing.thenRun(SchemaTemplateFunction.class, functionMethodName);

    FnResult result = testing.getOnlyResult();
    SchemaTemplateOutput output =
        mapper.readValue(result.getBodyAsString(), SchemaTemplateOutput.class);
    return output;
  }
}
