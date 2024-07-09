/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.test.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnproject.fn.testing.FnResult;
import com.fnproject.fn.testing.FnTestingRule;
import com.oracle.idm.agcs.grc.fn.requestTemplate.RequestTemplateFunction;
import com.oracle.idm.agcs.grc.fn.responseTemplate.ResponseTemplateFunction;
import com.oracle.idm.agcs.grc.fn.test.infra.handler.GenericRestRecordHandler;
import com.oracle.idm.agcs.grc.fn.test.infra.handler.impl.GenericRestRecordHandlerTestImpl;
import com.oracle.idm.agcs.grc.fn.test.infra.processor.HttpRequestProcessor;
import com.oracle.idm.agcs.icfconnectors.commons.enums.Operation;
import com.oracle.idm.agcs.icfconnectors.commons.model.ResponseAttribute;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.RequestTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.input.ResponseTemplateInput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.ResponseTemplateOutput;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;

public class RequestResponseTemplateValidationTest {

    ObjectMapper mapper = new ObjectMapper();
    String functionMethodName = "handleRequest";

    public Map<String, Object> processAndValidateRequestAndResponseTemplate(
            RequestTemplateOutput requestTemplate,
            ResponseTemplateOutput responseTemplate,
            HashMap<String, Object> attributesMap)
            throws IOException, URISyntaxException, InterruptedException {
        // validate all attributes in response template present in result record
        HttpRequestProcessor httpRequestResponseProcessor =
                new HttpRequestProcessor(25, 300, 300, requestTemplate, responseTemplate);
        GenericRestRecordHandler recordHandler = new GenericRestRecordHandlerTestImpl();
        Map<String, Object> resultRecord =
                httpRequestResponseProcessor.processRequest(attributesMap, recordHandler);
        System.out.println("resultRecord for :: " + resultRecord);
        System.out.println("Total record Count :: " + recordHandler.getRecordCount());
        if (null != responseTemplate && responseTemplate.getAttributes().size() > 0) {
            Assertions.assertEquals(
                    responseTemplate.getAttributes().size(),
                    resultRecord.size(),
                    "Result Record does not contain all the attributes present in response template");
            for (ResponseAttribute attribute : responseTemplate.getAttributes()) {
                Assertions.assertTrue(
                        resultRecord.containsKey(attribute.getName()),
                        "Result Record does not contain response attributes");
                if (null != attribute.getSubAttributes() && !attribute.getSubAttributes().isEmpty()) {
                    for (ResponseAttribute subAttributes : attribute.getSubAttributes()) {
                        List attributeValue = (ArrayList) resultRecord.get(attribute.getName());
                        if(null != attributeValue && !attributeValue.isEmpty()) {
                            Predicate<Map> predicate = e -> e.containsKey(subAttributes.getName());
                            Assertions.assertTrue(
                                    attributeValue.stream().anyMatch(predicate),
                                    "Result Record does not contain response sub attributes");
                        }
                    }
                }
            }
        }
        return resultRecord;
    }

    public RequestTemplateOutput getRequestTemplateOutput(
            String connectedSystemName, String entityName, Operation operation)
            throws JsonProcessingException {
        String requestBody =
                mapper.writeValueAsString(
                        RequestTemplateInput.builder()
                                .connectedSystemName(connectedSystemName)
                                .entityName(entityName)
                                .operationName(operation)
                                .build());
        FnTestingRule testing = FnTestingRule.createDefault();
        testing.givenEvent().withBody(requestBody).enqueue();
        testing.thenRun(RequestTemplateFunction.class, functionMethodName);

        FnResult result = testing.getOnlyResult();
        RequestTemplateOutput output =
                mapper.readValue(result.getBodyAsString(), RequestTemplateOutput.class);
        return output;
    }

    public RequestTemplateOutput getRequestTemplateOutput(
            String connectedSystemName,
            String entityName,
            Operation operation,
            Map<String, String> contextData)
            throws JsonProcessingException {
        String requestBody =
                mapper.writeValueAsString(
                        RequestTemplateInput.builder()
                                .connectedSystemName(connectedSystemName)
                                .entityName(entityName)
                                .operationName(operation)
                                .contextData(contextData)
                                .build());
        FnTestingRule testing = FnTestingRule.createDefault();
        testing.givenEvent().withBody(requestBody).enqueue();
        testing.thenRun(RequestTemplateFunction.class, functionMethodName);

        FnResult result = testing.getOnlyResult();
        RequestTemplateOutput output =
                mapper.readValue(result.getBodyAsString(), RequestTemplateOutput.class);
        return output;
    }

    public ResponseTemplateOutput getResponseTemplateOutput(
            String connectedSystemName, String entityName, Operation operation)
            throws JsonProcessingException {
        String requestBody =
                mapper.writeValueAsString(
                        ResponseTemplateInput.builder()
                                .connectedSystemName(connectedSystemName)
                                .entityName(entityName)
                                .operationName(operation)
                                .build());
        FnTestingRule testing = FnTestingRule.createDefault();
        testing.givenEvent().withBody(requestBody).enqueue();
        testing.thenRun(ResponseTemplateFunction.class, functionMethodName);
        FnResult result = getLast(testing.getResults());
        ResponseTemplateOutput output =
                mapper.readValue(result.getBodyAsString(), ResponseTemplateOutput.class);
        return output;
    }

    public ResponseTemplateOutput getResponseTemplateOutput(
            String connectedSystemName,
            String entityName,
            Operation operation,
            Map<String, String> contextData)
            throws JsonProcessingException {
        String requestBody =
                mapper.writeValueAsString(
                        ResponseTemplateInput.builder()
                                .connectedSystemName(connectedSystemName)
                                .entityName(entityName)
                                .operationName(operation)
                                .contextData(contextData)
                                .build());
        FnTestingRule testing = FnTestingRule.createDefault();
        testing.givenEvent().withBody(requestBody).enqueue();
        testing.thenRun(ResponseTemplateFunction.class, functionMethodName);
        FnResult result = getLast(testing.getResults());
        ResponseTemplateOutput output =
                mapper.readValue(result.getBodyAsString(), ResponseTemplateOutput.class);
        return output;
    }

    private static <T> T getLast(List<T> list) {
        return list != null && !list.isEmpty() ? list.get(list.size() - 1) : null;
    }

    public static String getPropertyValue(Properties props, String key) {
        return props.getProperty(key).trim().isEmpty() ? null : props.getProperty(key).trim();
    }
}
