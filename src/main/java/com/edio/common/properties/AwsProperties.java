package com.edio.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
public record AwsProperties(
        String bucketName,
        String region,
        String accessKey,
        String secretKey
) {
}
