/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.grc.fn.test.infra.util;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ParseContext;
import com.jayway.jsonpath.PathNotFoundException;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import net.minidev.json.JSONArray;

import javax.script.ScriptException;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class ExpressionParsingUtil {

    private static final Configuration configuration =
            Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();

    public static Object parseAndExecuteExpressions(
            GraalJSScriptEngine scriptEngine, String jsonString, String expressions) {
        if (expressions == null || expressions.length() == 0) {
            return expressions;
        }
        Stack<Integer> elStartIndex = new Stack<>();
        int length = expressions.length();
        for (int i = 0; i < length; i++) {
            if (startsWithEL(expressions, length, i)
                    || startsWithJsonPath(expressions, length, i)) {
                elStartIndex.push(i + 4);
            }

            if (endsWithEL(expressions, length, i)) {
                Integer startIndex = elStartIndex.pop();
                String script = expressions.substring(startIndex, i);
                if(!script.contains("'null'")) {
                    String result = evaluateExpression(scriptEngine, script);
                    expressions =
                            expressions.substring(0, startIndex - 4)
                                    + result
                                    + expressions.substring(i + 5);
                }
                else break;
                length = expressions.length();
                i = startIndex - 5;
            } else if (endsWithJsonPath(expressions, length, i)) {
                Integer startIndex = elStartIndex.pop();
                String script = expressions.substring(startIndex, i);
                    Object result = evaluateJsonPath(jsonString, script);
                    // If the JSON Path returns Array List then we can't reduce the expression further,
                    // hence returning the value as is as result
                    if (result instanceof List) {
                        System.out.println(
                                "Multivalued result can't be used in nested expression. Result of JSON Path :  " +
                                        result);
                        return result;
                    }
                    expressions =
                            expressions.substring(0, startIndex - 4)
                                    + result
                                    + expressions.substring(i + 5);
                    length = expressions.length();
                    i = startIndex - 5;

            }
        }
        return expressions;
    }

    private static boolean endsWithEL(String expressions, int length, int i) {
        return expressions.charAt(i) == '<'
                && i + 4 < length
                && expressions.charAt(i + 1) == '/'
                && expressions.charAt(i + 2) == 'E'
                && expressions.charAt(i + 3) == 'L'
                && expressions.charAt(i + 4) == '>';
    }

    private static boolean endsWithJsonPath(String expressions, int length, int i) {
        return expressions.charAt(i) == '<'
                && i + 4 < length
                && expressions.charAt(i + 1) == '/'
                && expressions.charAt(i + 2) == 'J'
                && expressions.charAt(i + 3) == 'P'
                && expressions.charAt(i + 4) == '>';
    }

    private static boolean startsWithEL(String expressions, int length, int i) {
        return expressions.charAt(i) == '<'
                && i + 3 < length
                && expressions.charAt(i + 1) == 'E'
                && expressions.charAt(i + 2) == 'L'
                && expressions.charAt(i + 3) == '>';
    }

    private static boolean startsWithJsonPath(String expressions, int length, int i) {
        return expressions.charAt(i) == '<'
                && i + 3 < length
                && expressions.charAt(i + 1) == 'J'
                && expressions.charAt(i + 2) == 'P'
                && expressions.charAt(i + 3) == '>';
    }

    private static String evaluateExpression(GraalJSScriptEngine scriptEngine, String script) {
        Object result = "";
        try {
            result = scriptEngine.eval(script);
        } catch (ScriptException e) {
            System.out.println("The script : " + script + " execution failed"+ e);
        }
        return result.toString();
    }

    private static Configuration getConfiguration() {
        return configuration;
    }

    private static ParseContext getParseContext() {
        return JsonPath.using(getConfiguration());
    }

    private static Object evaluateJsonPath(String jsonString, String jsonPath) {
        if (jsonString == null || jsonString.isEmpty()) {
            return jsonPath;
        }
        DocumentContext documentContext =
                getParseContext().parse(jsonString);
        Object value = "";
        try {
            value = documentContext.read(jsonPath);
            if (value instanceof JSONArray) {
                value =
                        Arrays.asList(
                                ((JSONArray) value)
                                        .toArray(new String[((JSONArray) value).size()]));
            } else if (value != null) {
                value = value.toString();
            }
        } catch (PathNotFoundException pfe) {
            System.out.println(
                    "The json path : "
                            + jsonPath
                            + " doesn't exists in the parent request response"+
                    pfe);
        }
        return value;
    }
}
