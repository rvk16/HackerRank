package com.amdocs.aia.il.devtools.tools;

import com.amdocs.aia.il.common.model.AbstractIntegrationLayerSchemaStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.physical.AbstractPhysicalSchemaStore;
import com.amdocs.aia.il.common.model.physical.kafka.KafkaSchemaStore;
import com.amdocs.aia.repo.client.ElementsProvider;
import com.amdocs.aia.repo.client.local.LocalFileSystemElementsProvider;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
@ComponentScan(basePackages = {"com.amdocs.aia.repo.client"})
public class CollectorConfigMapTool implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectorConfigMapTool.class);

    @Value("${aia.repo.elements.local.path}")
    private String repoElementsLocalPath;
    KubernetesClient kubernetesClient;

    @Autowired
    DeployerConfiguration configuration;

    public static void main(String[] args) {
        SpringApplication.run(CollectorConfigMapTool.class, args);
    }

    @Override
    public void run(String... args) {
        deployConfigMaps();
    }

    private void deployConfigMaps() {
        ElementsProvider elementsProvider = getElementsProvider(repoElementsLocalPath);
        List<AbstractPhysicalSchemaStore> schemaStores = getSchemaStores(elementsProvider);
        schemaStores.stream().map(AbstractPhysicalSchemaStore::getSchemaStoreKey).collect(Collectors.toList());
        if (schemaStores.isEmpty()) {
            LOGGER.info("No Schemas to Configure !!!"); // NOSONAR
        } else {
            schemaStores.sort(Comparator.comparing(AbstractPhysicalSchemaStore::getSchemaName));
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("List of schemas to be configured : {}", schemaStores.stream().map(AbstractPhysicalSchemaStore::getSchemaName).collect(Collectors.joining(",")));
            }
            Map<String, AbstractIntegrationLayerSchemaStoreModel> schemaNames = schemaStores.stream()
                    .collect(Collectors.toMap(AbstractIntegrationLayerSchemaStoreModel::getSchemaName, store -> store ));

            kubernetesClient = getKubernetesClient();

            schemaNames.forEach(this::createAndApplyConfigMap);

            kubernetesClient.close();
        }

    }

    private void createAndApplyConfigMap(String schemaName, AbstractIntegrationLayerSchemaStoreModel store) {
        schemaName = removeSpecialCharacter(schemaName);
        List<String> collectorModes = Arrays.asList("bulk", "ongoing");
        for(String mode : collectorModes ) {
            String templateConfigMapName = String.format("aia-il-%s-%s-%s-configmap",
                    configuration.schemaTypeToAliasMap.get(store.getExternalSchemaType().name()), mode , "source-name");

            ConfigMap configMap = kubernetesClient.configMaps().inNamespace(configuration.namespace).withName(templateConfigMapName).get();

            if(configMap == null) {
                LOGGER.warn("No ConfigMap Template found : {}", templateConfigMapName);
            } else {
                String configMapName = String.format("aia-il-%s-%s-%s-configmap",
                        configuration.schemaTypeToAliasMap.get(store.getExternalSchemaType().name()), mode , schemaName.toLowerCase());
                String propertiesFileName = configMap.getData().keySet().stream().filter(key ->
                        key.contains("service.yaml")).collect(Collectors.toList()).get(0);

                List<String> serviceYamlList = Arrays.asList(configMap.getData().get(propertiesFileName).split("\n"));
                Map<String, String> serviceYamlMap = new LinkedHashMap<>();
                serviceYamlList.forEach(row -> serviceYamlMap.put(row.split(":")[0], row.split(":")[1]));

                if (configuration.schemaToPropertiesMap.get(schemaName + "_" + mode) != null) {
                    configuration.schemaToPropertiesMap.get(schemaName + "_" + mode).forEach((key, value) ->
                            serviceYamlMap.put(key, " \"" + value + "\""));
                    configMap.getData().put(propertiesFileName, mapToYaml(serviceYamlMap));
                    configMap.getMetadata().setName(configMapName);
                    configMap.getMetadata().setResourceVersion("");

                    kubernetesClient.configMaps().inNamespace(configuration.namespace).delete(configMap);
                    kubernetesClient.configMaps().inNamespace(configuration.namespace).create(configMap);
                }
            }
        }
    }

    private static ElementsProvider getElementsProvider(final String repoElementsLocalPath) {
        return new LocalFileSystemElementsProvider(repoElementsLocalPath);
    }

    public List<AbstractPhysicalSchemaStore> getSchemaStores(final ElementsProvider elementsProvider) {
        List<AbstractPhysicalSchemaStore> elements = elementsProvider.getElements(ConfigurationConstants.PRODUCT_KEY,
                KafkaSchemaStore.ELEMENT_TYPE, AbstractPhysicalSchemaStore.class);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Total number of kafka entity configurations: {}", elements.size());
        }
        return elements;
    }

    private static String mapToYaml(Map<String, String> serviceYamlMap) {
        String output = "";
        for(String key: serviceYamlMap.keySet()) { //NOSONAR
            output += key + ":" + serviceYamlMap.get(key) + "\n"; //NOSONAR
        }
        return output;
    }

    private static String removeSpecialCharacter(String schemaName) {
        return schemaName.replace("_", "-");
    }

    private KubernetesClient getKubernetesClient() {
        Config kubeConfig = new ConfigBuilder()
                .withMasterUrl(configuration.k8sServer)
                .withClientCertData(configuration.clientCertData)
                .withClientKeyData(configuration.clientKeyData)
                .withCaCertData(configuration.certAuthData)
                .withNamespace(configuration.namespace)
                .build();
        return new DefaultKubernetesClient(kubeConfig);

    }

    @Configuration
    @ConfigurationProperties(prefix = "deployer")
    public static class DeployerConfiguration {
        String namespace;
        String k8sServer;
        String certAuthData;
        String clientCertData;
        String clientKeyData;
        Map<String, String> schemaTypeToAliasMap;
        Map<String, Map<String, String>> schemaToPropertiesMap;

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }

        public void setK8sServer(String k8sServer) {
            this.k8sServer = k8sServer;
        }

        public void setCertAuthData(String certAuthData) {
            this.certAuthData = certAuthData;
        }

        public void setClientCertData(String clientCertData) {
            this.clientCertData = clientCertData;
        }

        public void setClientKeyData(String clientKeyData) {
            this.clientKeyData = clientKeyData;
        }

        public void setSchemaTypeToAliasMap(Map<String, String> schemaTypeToAliasMap) {
            this.schemaTypeToAliasMap = schemaTypeToAliasMap;
        }

        public void setSchemaToPropertiesMap(Map<String, Map<String, String>> schemaToPropertiesMap) {
            this.schemaToPropertiesMap = schemaToPropertiesMap;
        }
    }
}
