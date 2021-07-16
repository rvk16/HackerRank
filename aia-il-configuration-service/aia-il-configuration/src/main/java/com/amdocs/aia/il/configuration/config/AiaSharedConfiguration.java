package com.amdocs.aia.il.configuration.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"com.amdocs.aia.repo.client", "com.amdocs.aia.shared.client"})
public class AiaSharedConfiguration {

}
