package com.oracle.idm.agcs.grc.fn.requestTemplate.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Config {
    private Map<String, String> authenticationDetail;
    private Map<String, String> applicationInstanceDetail;
}
