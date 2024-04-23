package org.example.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bucket-settings")
@Getter
@Setter
public class ConfigBucket {
    private long capacity;
    private long tokens;
    private long time;
}
