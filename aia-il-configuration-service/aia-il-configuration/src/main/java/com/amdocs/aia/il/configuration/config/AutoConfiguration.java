package com.amdocs.aia.il.configuration.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
        "com.amdocs.aia.il.configuration",
        "io.swagger",
        "com.amdocs.aia.common.core.web",
        "com.amdocs.aia.common.model.extensions"})
@Import({com.amdocs.aia.common.core.configuration.AutoConfiguration.class, ServiceHealthIndicatorConfiguration.class})
@EnableAspectJAutoProxy
public class AutoConfiguration {
}
