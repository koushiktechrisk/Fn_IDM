/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.testTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnproject.fn.testing.FnResult;
import com.fnproject.fn.testing.FnTestingRule;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.TestTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;
import org.junit.Rule;

import java.util.Properties;

public class TestTemplateFunctionTest {

  @Rule public final FnTestingRule testing = FnTestingRule.createDefault();

  ObjectMapper mapper = new ObjectMapper();
  String functionMethodName = "handleRequest";

  RequestTemplateOutput getTestTemplateOutput(TestTemplateInput input)
      throws JsonProcessingException {
    String requestBody = mapper.writeValueAsString(input);
    testing.givenEvent().withBody(requestBody).enqueue();
    testing.thenRun(TestTemplateFunction.class, functionMethodName);

    FnResult result = testing.getOnlyResult();
    RequestTemplateOutput output =
        mapper.readValue(result.getBodyAsString(), RequestTemplateOutput.class);
    return output;
  }

  static String getPropertyValue(Properties props, String key) {
    return props.getProperty(key).trim().isEmpty() ? null : props.getProperty(key).trim();
  }
}
