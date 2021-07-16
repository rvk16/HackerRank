package com.amdocs.aia.il.sqlite.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = {
        "com.amdocs.aia.il.sqlite",
        "io.swagger",
        "com.amdocs.aia.common.core.web"})
@Import({com.amdocs.aia.common.core.configuration.AutoConfiguration.class})
@EnableConfigurationProperties({DataSourceConfiguration.class})
@EnableAspectJAutoProxy
public class AutoConfiguration {
}
