/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.testTemplate;

import com.oracle.idm.agcs.grc.fn.testTemplate.util.Config;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.TestTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;

import static com.oracle.idm.agcs.grc.fn.testTemplate.util.Util.getAuthorizationValueFromIdcs;
import static com.oracle.idm.agcs.grc.fn.testTemplate.util.Util.getConfig;
import static com.oracle.idm.agcs.grc.fn.testTemplate.util.Util.getTemplateOutputFromResources;
import static com.oracle.idm.agcs.grc.fn.testTemplate.util.Util.replaceAuthorizationHeaderValue;
import static com.oracle.idm.agcs.grc.fn.testTemplate.util.Util.replaceUriHostValue;
import static com.oracle.idm.agcs.grc.fn.testTemplate.util.Util.replaceUriSchemeValue;

public class TestTemplateFunction {

  public RequestTemplateOutput handleRequest(TestTemplateInput input) {
    System.err.println("TestTemplateFunction input is :: " + input);
    // get request template output
    RequestTemplateOutput templateOutputFromResources = getTemplateOutputFromResources();
    /// get configuration from yaml file
    Config config = getConfig();
    // get authorizationToken from IDCS
    String authorizationToken = getAuthorizationValueFromIdcs(config);
    // replace authorizationToken placeholder in template with its value
    replaceAuthorizationHeaderValue(templateOutputFromResources, authorizationToken);
    // replace uri scheme placeholder in template with its value
    replaceUriSchemeValue(
        templateOutputFromResources, config.getApplicationInstanceDetail().get("scheme"));
    // replace host placeholder in template with its value
    replaceUriHostValue(
        templateOutputFromResources, config.getApplicationInstanceDetail().get("host"));
    System.err.println("TestTemplateFunction output is :: " + templateOutputFromResources);
    // return final template as a response
    return templateOutputFromResources;
  }
}
