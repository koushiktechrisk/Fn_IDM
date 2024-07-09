/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.test.infra.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.oracle.idm.agcs.grc.fn.test.infra.handler.GenericRestRecordHandler;
import com.oracle.idm.agcs.grc.fn.test.infra.util.ExpressionParsingUtil;
import com.oracle.idm.agcs.grc.fn.test.infra.util.JsonUtil;
import com.oracle.idm.agcs.icfconnectors.commons.enums.PaginationType;
import com.oracle.idm.agcs.icfconnectors.commons.enums.RequestMethod;
import com.oracle.idm.agcs.icfconnectors.commons.model.KeyValue;
import com.oracle.idm.agcs.icfconnectors.commons.model.ResponseAttribute;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.RequestTemplateOutput;
import com.oracle.idm.agcs.icfconnectors.commons.model.output.ResponseTemplateOutput;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import net.minidev.json.JSONArray;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotAccess;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class HttpRequestProcessor {
  public static final String HEADERS = "HEADERS";
  public static final String BODY = "BODY";
  public static final String STATUS_CODE = "STATUS_CODE";
  public static final String CONNECTOR_PROXY_HOST="CONNECTOR_PROXY_HOST";
  public static final String CONNECTOR_PROXY_PORT="CONNECTOR_PROXY_PORT";
  private final Integer pageSize;
  private final Duration connectTimeout;
  private final Duration readResponseTimeout;
  private RequestTemplateOutput requestTemplateOutput;
  private ResponseTemplateOutput responseTemplateOutput;

  public HttpRequestProcessor(
      Integer pageSize,
      Integer readResponseTimeoutInSeconds,
      Integer connectTimeoutInSeconds,
      RequestTemplateOutput requestTemplateOutput,
      ResponseTemplateOutput responseTemplateOutput) {
    this.pageSize = pageSize;
    this.connectTimeout = Duration.of(connectTimeoutInSeconds, ChronoUnit.SECONDS);
    this.readResponseTimeout = Duration.of(readResponseTimeoutInSeconds, ChronoUnit.SECONDS);
    this.requestTemplateOutput = requestTemplateOutput;
    this.responseTemplateOutput = responseTemplateOutput;
  }

  private static HttpClient.Version HTTP_PROTOCOL_VERSION = HttpClient.Version.HTTP_2;

  public Map<String, Object> processRequest(
      Map<String, Object> attributes, GenericRestRecordHandler recordHandler)
      throws IOException, URISyntaxException, InterruptedException {
    Objects.requireNonNull(requestTemplateOutput);
    Objects.requireNonNull(attributes);
    String requestId = requestTemplateOutput.getId();
    String requestName = requestTemplateOutput.getName();
    PaginationType paginationType = requestTemplateOutput.getPaginationType();
    String itemsJsonPath =
            responseTemplateOutput != null
                    ? JsonUtil.getJsonPathFromItem(responseTemplateOutput.getItems())
                    : null;
    List<KeyValue> responseValues = responseTemplateOutput != null ? responseTemplateOutput.getResponseValues() : null;
    Map<String, Object> currentValues = new HashMap<>();
    Map<String, List<String>> previousRequestResponseHeaders = new HashMap<>();
    Map<String, List<String>> parentResponseHeaders = new HashMap<>();
    boolean shouldContinue;
    Map<String, Object> resultRecord = null;
    long parentRequestCount = 0;
    do {
      shouldContinue = false;
      GraalJSScriptEngine scriptEngine =
              this.prepareContextObjectAndCreateScriptEngine(
                      attributes,
                      parentResponseHeaders,
                      previousRequestResponseHeaders,
                      paginationType,
                      currentValues);
      String path = requestTemplateOutput.getUri().getPath();
      System.out.println("Raw Path : " + path);
      path =
              ExpressionParsingUtil.parseAndExecuteExpressions(scriptEngine, null, path)
                      .toString();
      List<KeyValue> queryParameters = requestTemplateOutput.getQueryParameters();
      path = appendQueryParametersToPath(scriptEngine, path, queryParameters);
      if (!path.contains("'null'")) {
        URI uri =
                new URL(
                        requestTemplateOutput.getUri().getScheme(),
                        requestTemplateOutput.getUri().getHost(),
                        path)
                        .toURI();
        System.out.println("Expression processed path : " + path);
        System.out.println("URI : " + uri);

        List<KeyValue> headers = requestTemplateOutput.getHeaders();
        Map<String, String> requestHeadersMap = getRequestHeadersMap(scriptEngine, headers);
        System.out.println("Headers Map : " + requestHeadersMap);

        RequestMethod requestMethod = requestTemplateOutput.getMethod();

        Map<String, Object> bodyMap = requestTemplateOutput.getBody();
        String processedBodyJsonString = getProcessedBodyJsonString(scriptEngine, bodyMap);
        System.out.println("Body Json String : " + processedBodyJsonString);

        Map<String, Object> responseMap =
                invokeHttpRequest(
                        uri, requestHeadersMap, requestMethod, processedBodyJsonString);
        System.out.println("Response Map : " + responseMap);
        System.out.println("Executed Parent Request : " + ++parentRequestCount);

        Integer statusCode = (Integer) responseMap.get(STATUS_CODE);
        parentResponseHeaders = (Map<String, List<String>>) responseMap.get(HEADERS);
        previousRequestResponseHeaders.putAll(parentResponseHeaders);
        String jsonBody = (String) responseMap.get(BODY);
        if (statusCode >= 200 && statusCode <= 299) {
          Long itemCount = 0l;
          if (itemsJsonPath != null && !itemsJsonPath.isEmpty()) {
            shouldContinue = isItemPresent(jsonBody, itemsJsonPath, paginationType);
            itemCount = getItemCount(jsonBody, itemsJsonPath);
          }
          if (responseValues != null && !responseValues.isEmpty()) {
            for (KeyValue kv : responseValues) {
              Object responseValue =
                      ExpressionParsingUtil.parseAndExecuteExpressions(
                              scriptEngine, jsonBody, kv.getValue());
              if (responseValue != null) {
                previousRequestResponseHeaders.put(
                        kv.getName(), Arrays.asList(responseValue.toString()));
              }
            }
          }
          if ((itemCount > 0 && itemsJsonPath != null && !itemsJsonPath.isEmpty())
                  || !shouldContinue && (itemsJsonPath == null || itemsJsonPath.isEmpty())) {
            Map<String, List<String>> subRequestResponses = new HashMap<>();
            List<RequestTemplateOutput> subRequests =
                    requestTemplateOutput.getSubRequests();
            long parentCurrentIndex = 0l;
            do {
              if (subRequests != null && !subRequests.isEmpty()) {
                for (RequestTemplateOutput subRequestTemplateOutput : subRequests) {
                  List<String> subRequestResponseBody =
                          processSubRequest(
                                  attributes,
                                  subRequestTemplateOutput,
                                  responseTemplateOutput,
                                  jsonBody,
                                  parentResponseHeaders,
                                  parentCurrentIndex,
                                  parentRequestCount);
                  String subRequestKey =
                          parentCurrentIndex
                                  + "::"
                                  + subRequestTemplateOutput.getId();
                  subRequestResponses.put(subRequestKey, subRequestResponseBody);
                }
              }
              parentCurrentIndex++;
            } while (parentCurrentIndex < itemCount);
            HttpResponseProcessor responseProcessor =
                    new HttpResponseProcessor(
                            responseTemplateOutput,
                            attributes,
                            jsonBody,
                            subRequestResponses);
            resultRecord = responseProcessor.processResponses(recordHandler);
          }
        } else {
          throw new RuntimeException(
                  "Request ID :"
                          + requestId
                          + " Request Name :"
                          + requestName
                          + " failed with status code :"
                          + statusCode
                          + " with message : "
                          + jsonBody);
        }
      }
      else shouldContinue = false;
    }
    while (shouldContinue && parentRequestCount < 2); // only 2 iteration to test pagination
    return resultRecord;
  }

  private List<String> processSubRequest(
      Map<String, Object> attributes,
      RequestTemplateOutput requestTemplateOutput,
      ResponseTemplateOutput responseTemplateOutput,
      String parentResponseJsonString,
      Map<String, List<String>> parentResponseHeaders,
      Long parentCurrentIndex,
      Long parentRequestCount)
      throws IOException, InterruptedException, URISyntaxException {
    List<String> returnValue = new ArrayList<>();
    PaginationType paginationType = requestTemplateOutput.getPaginationType();
    String itemsJsonPath =
            responseTemplateOutput != null
                    ? getItemsPathForSubRequest(
                    requestTemplateOutput.getId(), responseTemplateOutput)
                    : null;
    Map<String, Object> currentValues = new HashMap<>();
    Map<String, List<String>> previousRequestResponseHeader = new HashMap<>();
    boolean shouldContinue;
    long subRequestCount = 0;
    do {
      shouldContinue = false;
      GraalJSScriptEngine scriptEngine =
              this.prepareContextObjectAndCreateScriptEngine(
                      attributes,
                      parentResponseHeaders,
                      previousRequestResponseHeader,
                      paginationType,
                      currentValues,
                      parentCurrentIndex);
      String path = requestTemplateOutput.getUri().getPath();
      System.out.println("Raw Sub request Path : " + path);
      path =
              ExpressionParsingUtil.parseAndExecuteExpressions(
                              scriptEngine, parentResponseJsonString, path)
                      .toString();
      System.out.println("Expression processed sub request path : " + path);
      List<KeyValue> queryParameters = requestTemplateOutput.getQueryParameters();
      path = appendQueryParametersToPath(scriptEngine, path, queryParameters);
      URI uri =
              new URL(
                      requestTemplateOutput.getUri().getScheme(),
                      requestTemplateOutput.getUri().getHost(),
                      path)
                      .toURI();
      System.out.println("Sub Request Path : " + path);
      System.out.println("Sub Request URI : " + uri);

      List<KeyValue> headers = requestTemplateOutput.getHeaders();
      Map<String, String> requestHeadersMap =
              getRequestHeadersMap(parentResponseJsonString, scriptEngine, headers);
      System.out.println("Sub Request Headers Map: " + requestHeadersMap);

      RequestMethod requestMethod = requestTemplateOutput.getMethod();

      Map<String, Object> bodyMap = requestTemplateOutput.getBody();
      String processedBodyJsonString =
              getProcessedBodyJsonString(parentResponseJsonString, scriptEngine, bodyMap);
      System.out.println("Processed Body Json String for Sub request: " + processedBodyJsonString);

      Map<String, Object> responseMap =
              invokeHttpRequest(
                      uri, requestHeadersMap, requestMethod, processedBodyJsonString);
      System.out.println("Sub Request Response Map : " + responseMap);
      System.out.println(
              MessageFormat.format(
                      "Executed Sub request : {0} of Parent Request : {1}/{2}",
                      ++subRequestCount, parentCurrentIndex, parentRequestCount));

      Integer statusCode = (Integer) responseMap.get(STATUS_CODE);
      if (statusCode >= 200 && statusCode <= 299) {
        String jsonBody = (String) responseMap.get(BODY);
        long itemCount = 0l;
        if (itemsJsonPath != null && !itemsJsonPath.isEmpty()) {
          shouldContinue = isItemPresent(jsonBody, itemsJsonPath, paginationType);
          itemCount = getItemCount(jsonBody, itemsJsonPath);
        }
        if ((itemCount > 0 && itemsJsonPath != null && !itemsJsonPath.isEmpty())
                || !shouldContinue && (itemsJsonPath == null || itemsJsonPath.isEmpty())) {
          returnValue.add(jsonBody);
        }
      }
    } while (shouldContinue);
    return returnValue;
  }

  private Map<String, Object> invokeHttpRequest(
      URI uri, Map<String, String> requestHeaders, RequestMethod requestMethod, String bodyText)
      throws IOException, InterruptedException {
    System.out.println(
            MessageFormat.format(
                    "HTTP Request : URI: {0} , Request Headers {1} , Request Method {2} , Body Text {3} ",
                    uri.toString(), requestHeaders, requestMethod.toString(), bodyText));
    Map<String, Object> returnValue = new HashMap<>();
    HttpRequest.Builder builder = HttpRequest.newBuilder();
    builder = builder.uri(uri).version(HTTP_PROTOCOL_VERSION);
    switch (requestMethod) {
      case GET:
        builder = builder.GET();
        break;
      case POST:
        builder = builder.POST(HttpRequest.BodyPublishers.ofString(bodyText));
        break;
      case PUT:
        builder = builder.PUT(HttpRequest.BodyPublishers.ofString(bodyText));
        break;
      case PATCH:
        builder = builder.method("PATCH", HttpRequest.BodyPublishers.ofString(bodyText));
        break;
      case DELETE:
        builder = builder.DELETE();
        break;
    }
    builder.timeout(this.readResponseTimeout);
    HttpRequest.Builder finalBuilder = builder;
    requestHeaders.entrySet().forEach(e -> finalBuilder.header(e.getKey(), e.getValue()));
    HttpRequest httpRequest = finalBuilder.build();
    HttpClient httpClient = HttpClient.newBuilder().connectTimeout(this.connectTimeout).build();
    String connectorProxyHost = System.getProperty(CONNECTOR_PROXY_HOST);
    String connectorProxyPort = System.getProperty(CONNECTOR_PROXY_PORT);
    if (null != connectorProxyHost
            && !connectorProxyHost.trim().isEmpty()
            && null != connectorProxyPort
            && !connectorProxyPort.trim().isEmpty()) {
      System.out.println(
              MessageFormat.format(
                      "connectorProxyHost {0} and connectorProxyPort {1} is available in system property",
                      connectorProxyHost, connectorProxyPort));
      try {
        httpClient =
                HttpClient.newBuilder()
                        .proxy(
                                ProxySelector.of(
                                        new InetSocketAddress(
                                                connectorProxyHost, Integer.parseInt(connectorProxyPort))))
                        .connectTimeout(this.connectTimeout)
                        .build();
      } catch (NumberFormatException exception) {
        System.out.println("connectorProxyPort value is not integer : "+exception);
      }
    }
    HttpResponse<String> httpResponse =
            httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    System.out.println(
            MessageFormat.format(
                    "HTTP Response : {0} , BODY : {1}", httpResponse.toString(), httpResponse.body()));
    HttpHeaders responseHeaders = httpResponse.headers();
    Map<String, List<String>> responseHeadersMap = responseHeaders.map();
    String responseBody = httpResponse.body();
    returnValue.put(HEADERS, responseHeadersMap);
    returnValue.put(BODY, responseBody);
    returnValue.put(STATUS_CODE, httpResponse.statusCode());
    return returnValue;
  }

  private static String getProcessedBodyJsonString(
          GraalJSScriptEngine scriptEngine, Map<String, Object> bodyMap)
          throws JsonProcessingException {
    return getProcessedBodyJsonString(null, scriptEngine, bodyMap);
  }

  private static String getItemsPathForSubRequest(
          String subRequestId, ResponseTemplateOutput responseTemplateOutput) {
    Optional<ResponseAttribute> responseOfSubRequestId =
            responseTemplateOutput.getAttributes().stream()
                    .filter(
                            a ->
                                    a.getResponseOfSubRequestId() != null
                                            && a.getResponseOfSubRequestId()
                                            .equals(subRequestId))
                    .findFirst();
    if (responseOfSubRequestId.isPresent()) {
      return JsonUtil.getJsonPathFromItem(responseOfSubRequestId.get().getItems());
    }
    return null;
  }

  private static String getProcessedBodyJsonString(
          String parentResponsejsonString,
          GraalJSScriptEngine scriptEngine,
          Map<String, Object> bodyMap)
          throws JsonProcessingException {
    String processedBodyJsonString = null;
    if (bodyMap != null && !bodyMap.isEmpty()) {
      Object type = bodyMap.get("type");
      String jsonString = "";
      if ("TEXT".equalsIgnoreCase((String) type)) {
        ObjectMapper objectMapper = new ObjectMapper();
        jsonString = objectMapper.writeValueAsString(bodyMap.get("textBody"));
      } else if ("FORM".equalsIgnoreCase((String) type)) {
        jsonString = (String) bodyMap.get("textBody");
      }
      System.out.println("Raw Body Text : " + jsonString);
      processedBodyJsonString =
              ExpressionParsingUtil.parseAndExecuteExpressions(
                              scriptEngine, parentResponsejsonString, jsonString)
                      .toString();
      System.out.println("EL Processed Body Text: " + processedBodyJsonString);
      processedBodyJsonString =
              processedBodyJsonString.replaceAll("(\"UQ:)([^\"]*)(\")", "$2");
      System.out.println("Unquoted Processed Body Text : " + processedBodyJsonString);
    }
    return processedBodyJsonString;
  }

  private static String appendQueryParametersToPath(
          GraalJSScriptEngine scriptEngine, String path, List<KeyValue> queryParameters) {
    StringBuilder sb = new StringBuilder();
    if (queryParameters != null) {
      queryParameters.stream()
              .forEach(
                      kv -> {
                        System.out.println("Raw Query parameter value : " + kv.getValue());
                        String processedQueryParameterValue =
                                ExpressionParsingUtil.parseAndExecuteExpressions(
                                                scriptEngine, null, kv.getValue())
                                        .toString();
                        sb.append(kv.getName())
                                .append("=")
                                .append(processedQueryParameterValue)
                                .append("&");
                      });
      if (!sb.toString().isEmpty()) {
        path = path + "?" + sb.toString().substring(0, sb.length() - 1);
      }
    }
    return path;
  }

  private static Map<String, String> getRequestHeadersMap(
          String parentResponseJsonString,
          GraalJSScriptEngine scriptEngine,
          List<KeyValue> headers) {
    Map<String, String> requestHeadersMap = new HashMap<>();
    headers.stream()
            .forEach(
                    kv -> {
                      System.out.println("Raw Sub request header value : " + kv.getValue());
                      String processedHeaderValue =
                              ExpressionParsingUtil.parseAndExecuteExpressions(
                                              scriptEngine,
                                              parentResponseJsonString,
                                              kv.getValue())
                                      .toString();
                      System.out.println("Processed Sub request Header Value : " + processedHeaderValue);
                      requestHeadersMap.put(kv.getName(), processedHeaderValue);
                    });
    return requestHeadersMap;
  }

  private static Map<String, String> getRequestHeadersMap(
          GraalJSScriptEngine scriptEngine, List<KeyValue> headers) {
    return getRequestHeadersMap(null, scriptEngine, headers);
  }

  private boolean isItemPresent(
          String jsonString, String jsonPath, PaginationType paginationType) {
    System.out.println("isItemPresent executing JsonPath : " + jsonPath);
    if (paginationType == null) {
      return Boolean.FALSE;
    }
    if (jsonString == null || jsonString.isEmpty()) {
      return Boolean.FALSE;
    }
    Object value;
    try {
      value = JsonPath.parse(jsonString).read(jsonPath);
      if (value instanceof JSONArray) {
        return !((JSONArray) value).isEmpty();
      }
    } catch (PathNotFoundException pfe) {
      System.out.println(
              "The json path : " + jsonPath + " doesn't exists in the parent request response" + pfe);
      throw new RuntimeException(
          String.format("Unable to locate data in response at the given json path : %s", jsonPath));
    }
    return false;
  }

  private Long getItemCount(String jsonString, String jsonPath) {
    if (jsonString == null || jsonString.isEmpty()) {
      return 0l;
    }
    Object value;
    try {
      value = JsonPath.parse(jsonString).read(jsonPath);
      if (value instanceof JSONArray) {
        return ((JSONArray) value).stream().count();
      }
    } catch (PathNotFoundException pfe) {
      System.out.println(
              "The json path : " + jsonPath + " doesn't exists in the parent request response" + pfe);
      throw new RuntimeException(
          String.format("Unable to locate data in response at the given json path : %s", jsonPath));
    }
    return 0l;
  }

  private GraalJSScriptEngine prepareContextObjectAndCreateScriptEngine(
          Map<String, Object> attributes,
          Map<String, List<String>> parentRequestResponseHeaders,
          Map<String, List<String>> previousRequestResponseHeaders,
          PaginationType paginationType,
          Map<String, Object> currentValues) {
    return prepareContextObjectAndCreateScriptEngine(
            attributes,
            parentRequestResponseHeaders,
            previousRequestResponseHeaders,
            paginationType,
            currentValues,
            null);
  }

  private GraalJSScriptEngine prepareContextObjectAndCreateScriptEngine(
          Map<String, Object> attributes,
          Map<String, List<String>> parentRequestResponseHeaders,
          Map<String, List<String>> previousRequestResponseHeaders,
          PaginationType paginationType,
          Map<String, Object> currentValues,
          Long parentCurrentIndex) {
    GraalJSScriptEngine engine =
            GraalJSScriptEngine.create(
                    null,
                    Context.newBuilder("js")
                            .allowHostAccess(HostAccess.ALL)
                            .allowPolyglotAccess(PolyglotAccess.ALL)
                            .option("js.ecmascript-version", "2021"));
    engine.put("attributes", attributes);
    engine.put("parentRequestResponseHeaders", parentRequestResponseHeaders);
    engine.put("previousRequestResponseHeaders", previousRequestResponseHeaders);
    engine.put("parentCurrentIndex", parentCurrentIndex);
    if (paginationType != null) {
      switch (paginationType) {
        case OFFSET:
          Integer currentOffset = (Integer) currentValues.get("CURRENT_OFFSET");
          if (currentOffset == null) {
            // TODO: Use configuration to Zero Based offset (boolean). If true use
            // current offset as 0, else 1
            currentOffset = 1;
            currentValues.put("CURRENT_OFFSET", currentOffset);
          } else {
            currentOffset = currentOffset + this.pageSize;
            currentValues.put("CURRENT_OFFSET", currentOffset);
          }
          engine.put("currentOffset", currentOffset);
          engine.put("limit", pageSize);
          break;
        case PAGE_INCREMENT:
          Integer currentPage = (Integer) currentValues.get("CURRENT_PAGE");
          if (currentPage == null) {
            currentPage = 1;
            currentValues.put("currentPage", currentPage);
          } else {
            currentPage++;
            currentValues.put("CURRENT_PAGE", currentPage);
          }
          engine.put("currentPage", currentPage);
          engine.put("pageSize", pageSize);
          break;
        case PAGE_TOKEN:
          engine.put("pageSize", pageSize);
          break;
      }
    }
    return engine;
  }
}
