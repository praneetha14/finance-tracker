package com.finance.tracker.model.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "finance.tracker.aws")
@Getter
@Setter
public class AwsProperties {
    private String region;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private int signedUrlExpiry;
}

