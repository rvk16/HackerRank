package com.amdocs.aia.il.busdeployer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.amdocs.aia.il.busdeployer", "com.amdocs.aia.repo.client"})
public class IntegrationLayerBusDeployerApplication implements CommandLineRunner {
    private final IntegrationLayerBusDeployer integrationLayerBusDeployer;

    @Autowired
    public IntegrationLayerBusDeployerApplication(final IntegrationLayerBusDeployer integrationLayerBusDeployer) {
        this.integrationLayerBusDeployer = integrationLayerBusDeployer;
    }

    @Override
    public void run(final String... args) {
        integrationLayerBusDeployer.execute();
    }

    public static void main(final String... args) {
        try (final ConfigurableApplicationContext context = new SpringApplicationBuilder(IntegrationLayerBusDeployerApplication.class)
                .web(WebApplicationType.NONE).run(args)) {
            // empty
        }
    }
}