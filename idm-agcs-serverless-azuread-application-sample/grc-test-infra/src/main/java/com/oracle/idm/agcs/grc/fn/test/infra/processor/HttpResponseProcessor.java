/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.test.infra.processor;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.oracle.idm.agcs.grc.fn.test.infra.handler.GenericRestRecordHandler;
import com.oracle.idm.agcs.grc.fn.test.infra.util.ExpressionParsingUtil;
import com.oracle.idm.agcs.grc.fn.test.infra.util.JsonUtil;
import com.oracle.idm.agcs.icfconnectors.commons.model.ResponseAttribute;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.ResponseTemplateOutput;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minidev.json.JSONArray;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotAccess;

public class HttpResponseProcessor {

        private final ResponseTemplateOutput responseTemplateOutput;
        private final Map<String, Object> attributes;
        private final String parentRequestResponse;
        private final Map<String, List<String>> subRequestsResponses;

        public HttpResponseProcessor(ResponseTemplateOutput responseTemplateOutput, Map<String, Object> attributes, String parentRequestResponse, Map<String, List<String>> subRequestsResponses) {
            this.responseTemplateOutput = responseTemplateOutput;
            this.parentRequestResponse = parentRequestResponse;
            this.subRequestsResponses = subRequestsResponses;
            this.attributes = attributes;
        }

        public Map<String, Object> processResponses(GenericRestRecordHandler recordHandler) {
            System.out.println("Parent Request Response : "+ this.parentRequestResponse);
            System.out.println("Sub Requests Response : "+ this.subRequestsResponses);
            if (this.responseTemplateOutput == null) {
                return new HashMap<>();
            }
            String itemsJsonPath = JsonUtil.getJsonPathFromItem(this.responseTemplateOutput.getItems());
            Long count = 0l;
            if (itemsJsonPath != null && !itemsJsonPath.isEmpty()) {
                count = getItemsCount(parentRequestResponse, itemsJsonPath);
            }
            Map<String, Object> resultRecord ;
            long parentResponseIndex = 0l;
            do {
                GraalJSScriptEngine engine = prepareContextObjectAndCreateScriptEngine(attributes, parentResponseIndex);
                List<ResponseAttribute> responseAttributes = this.responseTemplateOutput.getAttributes();
                resultRecord = new HashMap<>();
                for (ResponseAttribute responseAttribute : responseAttributes) {
                    String subRequestId = responseAttribute.getResponseOfSubRequestId();
                    String subRequestResponseItemPath = JsonUtil.getJsonPathFromItem(responseAttribute.getItems());
                    if (subRequestId != null && !subRequestId.isEmpty()) {
                        String subRequestKey = parentResponseIndex + "::" + subRequestId;
                        List<String> subRequestResponses = subRequestsResponses.get(subRequestKey);
                        List<ResponseAttribute> subAttributes = responseAttribute.getSubAttributes();
                        List<Map<String, List<String>>> attributeResults = new ArrayList<>();
                        if (subRequestsResponses != null && !subRequestResponses.isEmpty()) {
                            for (String subRequestResponse : subRequestResponses) {
                                Long subResponseCount = 0l;
                                if (subRequestResponseItemPath != null && !subRequestResponseItemPath.isEmpty()) {
                                    subResponseCount = getItemsCount(subRequestResponse, subRequestResponseItemPath);
                                }
                                long subResponseIndex = 0;
                                do {
                                    GraalJSScriptEngine subRequestEngine = prepareContextObjectAndCreateScriptEngine(attributes, subResponseIndex);
                                    Map<String, List<String>> resultMap = new HashMap<>();
                                    subAttributes.stream().forEach(subAttr -> {
                                        String subAttrName = subAttr.getName();
                                        String subAttrValue = subAttr.getValue();
                                        Object expressionResult = ExpressionParsingUtil.parseAndExecuteExpressions(subRequestEngine, subRequestResponse, subAttrValue);
                                        List<String> subAttrListValue = new ArrayList<>();
                                        if (expressionResult instanceof ArrayList) {
                                            subAttrListValue.addAll((Collection<? extends String>) expressionResult);
                                        } else if (expressionResult != null) {
                                            subAttrListValue.add(expressionResult.toString());
                                        }
                                        resultMap.put(subAttrName, subAttrListValue);
                                    });
                                    attributeResults.add(resultMap);
                                    subResponseIndex++;
                                } while (subResponseIndex < subResponseCount);
                            }
                        }
                        resultRecord.put(responseAttribute.getName(), attributeResults);
                    } else {
                        String attrName = responseAttribute.getName();
                        String attrValue = responseAttribute.getValue();
                        Object expressionResult = ExpressionParsingUtil.parseAndExecuteExpressions(engine, this.parentRequestResponse, attrValue);
                        List<String> attrListValue = new ArrayList<>();
                        if (expressionResult instanceof List) {
                            attrListValue.addAll((Collection<? extends String>) expressionResult);
                        } else if (expressionResult != null) {
                            attrListValue.add(expressionResult.toString());
                        }
                        resultRecord.put(attrName, attrListValue);
                    }
                }

                if(null != recordHandler) {
                    System.out.println("Calling Handler with result :"+ resultRecord);
                    recordHandler.handle(resultRecord);
                }
                parentResponseIndex++;
            } while (parentResponseIndex < count);
            return resultRecord;
        }

    private Long getItemsCount(String jsonString, String jsonPath) {
        System.out.println("getItemsCount executing JsonPath : "+ jsonPath);
        if (jsonString == null || jsonString.isEmpty()) {
            return 0l;
        }
        Object value = "";
        try {
            value = JsonPath.parse(jsonString).read(jsonPath);
            if (value instanceof JSONArray) {
                return ((JSONArray) value).stream().count();
            }
        } catch (PathNotFoundException pfe) {
            System.out.println("The json path : " + jsonPath + " doesn't exists in the parent request response"+pfe);
        }
        return 0l;
    }

    private GraalJSScriptEngine prepareContextObjectAndCreateScriptEngine(Map<String, Object> attributes, Long currentIndex) {
        return prepareContextObjectAndCreateScriptEngine(attributes, currentIndex, null);
    }

    private GraalJSScriptEngine prepareContextObjectAndCreateScriptEngine(Map<String, Object> attributes, Long currentIndex, Long parentCurrentIndex) {
        GraalJSScriptEngine engine =
                GraalJSScriptEngine.create(
                        null,
                        Context.newBuilder("js")
                                .allowHostAccess(HostAccess.ALL)
                                .allowPolyglotAccess(PolyglotAccess.ALL)
                                .option("js.ecmascript-version", "2021"));
        engine.put("attributes", attributes);
        engine.put("currentIndex", currentIndex);
        engine.put("parentCurrentIndex", parentCurrentIndex);
        return engine;
    }
}
