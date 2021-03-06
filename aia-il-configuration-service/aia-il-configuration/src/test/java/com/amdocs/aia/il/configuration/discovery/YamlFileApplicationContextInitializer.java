package com.amdocs.aia.il.configuration.discovery;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

public class YamlFileApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    try {
        Resource resource = applicationContext.getResource("classpath:aia-il-configuration-service.yaml");
        YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
        final List<PropertySource<?>> yamlTestProperties = sourceLoader.load("yamlTestProperties", resource);
        applicationContext.getEnvironment().getPropertySources().addFirst(yamlTestProperties.get(0));
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
  }
}