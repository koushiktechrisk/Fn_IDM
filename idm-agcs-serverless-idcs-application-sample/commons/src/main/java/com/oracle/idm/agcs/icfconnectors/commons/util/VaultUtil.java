/*
 * Copyright (c) 2024, Oracle and/or its affiliates. All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package com.oracle.idm.agcs.icfconnectors.commons.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.bmc.model.BmcException;
import com.oracle.bmc.retrier.RetryCondition;
import com.oracle.bmc.retrier.RetryConfiguration;
import com.oracle.bmc.secrets.SecretsClient;
import com.oracle.bmc.secrets.model.Base64SecretBundleContentDetails;
import com.oracle.bmc.secrets.requests.GetSecretBundleRequest;
import com.oracle.bmc.secrets.responses.GetSecretBundleResponse;
import com.oracle.bmc.waiter.ExponentialBackoffDelayStrategyWithJitter;
import com.oracle.bmc.waiter.MaxAttemptsTerminationStrategy;
import com.oracle.bmc.auth.BasicAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.auth.ResourcePrincipalAuthenticationDetailsProvider;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.ConfigFileReader.ConfigFile;
import java.util.Set;
import lombok.NonNull;

public class VaultUtil {

    private VaultUtil() {
        throw new IllegalStateException("VaultUtil is a utility class");
    }

    public static String getDataFromVault(String secretId, String region) throws UnsupportedEncodingException {
        System.err.println("start getting data from vault util");
        byte[] secretValueByteArray = fetchSecret(secretId, region);
        return new String(secretValueByteArray, String.valueOf(StandardCharsets.UTF_8));
    }

    private static byte[] fetchSecret(String secretId, String region) {
        System.err.println("start fetch secret");
        SecretsClient client = new SecretsClient(VaultUtil.createAuthenticationProvider());
        client.setRegion(region);
        GetSecretBundleRequest getSecretBundleRequest = GetSecretBundleRequest.builder().secretId(secretId).stage(GetSecretBundleRequest.Stage.Current).retryConfiguration(ClientRetryCondition.DEFAULT_RETRY_CONFIGURATION).build();
        GetSecretBundleResponse getSecretBundleResponse = client.getSecretBundle(getSecretBundleRequest);
        Base64SecretBundleContentDetails base64SecretBundleContentDetails = (Base64SecretBundleContentDetails)getSecretBundleResponse.getSecretBundle().getSecretBundleContent();
        System.err.println("finished fetch secret");
        return Base64.getDecoder().decode(base64SecretBundleContentDetails.getContent());
    }

    public static String getAttributeValueFromJson(String vaultJsonValue, String attributeName)
            throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(vaultJsonValue);
        return jsonNode.get(attributeName).asText();
    }

    public static BasicAuthenticationDetailsProvider createAuthenticationProvider() {
        BasicAuthenticationDetailsProvider provider;
        boolean isDevMode = Boolean.parseBoolean(System.getProperty("isDevMode"));
        if (isDevMode) {
            try {
                System.err.println("using configurations provider in dev mode");
                ConfigFile configFile = ConfigFileReader.parse("src/test/resources/config");
                provider = new ConfigFileAuthenticationDetailsProvider(configFile);
            } catch (Exception e) {
                System.err.println("error create instance of configurations provider in dev mode"+ e);
                throw new IllegalStateException("Error to use AuthenticationProvider in DevMode. Please install OCI CLI on your local box.");
            }
        } else {
            System.err.println("using resource principals provider");
            provider = ResourcePrincipalAuthenticationDetailsProvider.builder().build();
        }
        return provider;
    }

    public static class ClientRetryCondition implements RetryCondition {
        public static final RetryCondition DEFAULT_RETRY_CONDITION = new ClientRetryCondition();
        public static final long DEFAULT_MAX_WAIT_TIME;
        public static final RetryConfiguration DEFAULT_RETRY_CONFIGURATION;
        public static final Set<Integer> RETRYABLE_SERVICE_ERRORS;

        static {
            DEFAULT_MAX_WAIT_TIME = TimeUnit.SECONDS.toMillis(30L);
            DEFAULT_RETRY_CONFIGURATION = RetryConfiguration.builder()
                    .terminationStrategy(new MaxAttemptsTerminationStrategy(8))
                    .delayStrategy(new ExponentialBackoffDelayStrategyWithJitter(DEFAULT_MAX_WAIT_TIME))
                    .retryCondition(DEFAULT_RETRY_CONDITION)
                    .build();
            RETRYABLE_SERVICE_ERRORS = Set.of(400, 401, 404, 409, 429, 500);
        }

        public ClientRetryCondition() {
        }

        @Override
        public boolean shouldBeRetried(@NonNull BmcException exception) {
            if (exception == null) {
                throw new NullPointerException("exception is marked non-null but is null");
            } else {
                System.err.println("retry oci error"+ exception);
                return exception.isClientSide() || exception.isTimeout() || exception.getStatusCode() >= 500 || RETRYABLE_SERVICE_ERRORS.contains(exception.getStatusCode()) || isProcessingException(exception);
            }
        }

        public static boolean isProcessingException(BmcException exception) {
            return exception.getStatusCode() == -1 && exception.getMessage().toLowerCase().matches(".*processing(\\s)+exception.*");
        }
    }
}