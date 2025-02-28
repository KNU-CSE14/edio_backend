package com.edio.common.properties;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ConfigurationProperties(prefix = "aws")
@Component
public class AwsProperties {
    private String bucketName;
    private String region;
    private String accessKey;
    private String secretKey;
}
