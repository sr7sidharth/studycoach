package com.jobprep.randomserver.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "studycoach")
@Data
public class InitialParamsConfig {
    private List<String> topics;

    @JsonProperty("trusted sources")
    private List<String> trustedSources;
}
