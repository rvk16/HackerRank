package com.amdocs.aia.il.collector.deployer;

import com.amdocs.aia.il.collector.deployer.configuration.DeployerConfiguration;
import com.amdocs.aia.il.common.model.AbstractIntegrationLayerSchemaStoreModel;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.physical.AbstractPhysicalSchemaStore;
import com.amdocs.aia.il.common.model.physical.kafka.KafkaSchemaStore;
import com.amdocs.aia.il.collector.deployer.logs.LogMsg;
import com.amdocs.aia.il.collector.deployer.properties.RuntimeConfiguration;
import com.amdocs.aia.repo.client.ElementsProvider;
import com.amdocs.aia.repo.client.local.LocalFileSystemElementsProvider;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.apiextensions.v1beta1.CustomResourceDefinition;
import io.fabric8.kubernetes.api.model.batch.Job;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import static com.amdocs.aia.il.collector.deployer.constants.ConfigurationConstants.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;


@Component
@ConfigurationProperties(prefix = "collector-map")
public class CollectorDeployer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CollectorDeployer.class);

    String namespace;

    private final RuntimeConfiguration runtimeConfiguration;
    private Map<String, String> schemaTypeToAliasMap;
    private Map<String, String> sourceToImageNameMap;
    private Map<String, String> collectorToApplicationPropertyMap;
    private final List<String> configMapNamesList = new ArrayList<>();
    private int exitCode;
    KubernetesClient kubernetesClient;
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    @Autowired
    DeployerConfiguration deployerConfiguration;

    public CollectorDeployer(RuntimeConfiguration runtimeConfiguration) {
        this.runtimeConfiguration = runtimeConfiguration;
    }

    public ElementsProvider getElementsProvider(final String repoElementsLocalPath) {
        return new LocalFileSystemElementsProvider(repoElementsLocalPath);
    }

    public List<AbstractPhysicalSchemaStore> getSchemaStores(final ElementsProvider elementsProvider) {
        List<AbstractPhysicalSchemaStore> schemaStores = elementsProvider.getElements(ConfigurationConstants.PRODUCT_KEY, AbstractIntegrationLayerSchemaStoreModel.getElementTypeFor(KafkaSchemaStore.class), AbstractPhysicalSchemaStore.class);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(LogMsg.getMessage(MSG_NUMBER_OF_LOADED_SCHEMA_STORE), schemaStores.size());
        }
        return schemaStores;
    }

    public void execute() {
        LOGGER.info(LogMsg.getMessage(MSG_STARTING_IL_DEPLOYER)); // NOSONAR

        namespace = deployerConfiguration.getNamespace();
        LOGGER.info("Running Collector Deployer in Namespace : {}", namespace);
        if(StringUtils.isNotEmpty(namespace)) {
            kubernetesClient = new DefaultKubernetesClient(new ConfigBuilder().withNamespace(namespace).build());
        } else {
            LOGGER.error("Namespace values is null");
            exitCode = 3;
            return;
        }

        ElementsProvider elementsProvider = getElementsProvider(runtimeConfiguration.getRepoElementsLocalPath());
        List<AbstractPhysicalSchemaStore> schemaStores = getSchemaStores(elementsProvider);
        schemaStores.stream().map(AbstractPhysicalSchemaStore::getSchemaStoreKey).collect(Collectors.toList());

        if (schemaStores.isEmpty()) {
            LOGGER.info(LogMsg.getMessage(MSG_NO_TABLES_CONFIGURED)); // NOSONAR
        } else {
            schemaStores.sort(Comparator.comparing(AbstractPhysicalSchemaStore::getSchemaName));
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info(LogMsg.getMessage(MSG_LIST_OF_CONFIGURED_SCHEMA), schemaStores.stream().map(AbstractPhysicalSchemaStore::getSchemaName).collect(Collectors.joining(",")));
            }
            Map<String, String> schemaNames = schemaStores.stream().filter(abstractPhysicalSchemaStore ->
                    schemaTypeToAliasMap.containsKey(abstractPhysicalSchemaStore.getExternalSchemaType().name()))
                    .collect(Collectors.toMap(AbstractIntegrationLayerSchemaStoreModel::getSchemaName, store -> store.getExternalSchemaType().name()));

            configureInitContainerImage();
            populateConfigMapList();
            schemaNames.forEach(this::deployCollector);

            kubernetesClient.close();
            LOGGER.info("IL COLLECTOR DEPLOYER FINISHED");
        }
    }

    private void deployCollector(String schemaName, String schemaType) {
        List<String> collectorModes = Arrays.asList("bulk", "ongoing");
        for(String mode : collectorModes ) {
            String serviceName = getServiceName(schemaName, mode, schemaTypeToAliasMap.get(schemaType));
            String dockerImageName = getImageName(mode, schemaType);
            String applicationPropertyFile = collectorToApplicationPropertyMap.get(schemaTypeToAliasMap.get(schemaType) + "-" + mode);
            if(serviceName == null) {
                LOGGER.warn("ConfigMap not found for Schema : {}, Mode : {}, SchemaType : {}",
                        schemaName, mode, schemaTypeToAliasMap.get(schemaType));
            } else {
                // Deploying Service if ConfigMap is Present
                createAndApplyDeploymentYaml(serviceName, dockerImageName, applicationPropertyFile);
                LOGGER.info("Deployed : {}", serviceName);
            }
        }
    }

    private void createAndApplyDeploymentYaml(String serviceName, String dockerImageName, String applicationPropertyFile) {
        String deploymentFileName = serviceName + "_deployment.yaml";
        String deploymentYaml;
        LOGGER.info("Security Protocol: {}", deployerConfiguration.getKafkaSecurityProtocol());
        if(StringUtils.isNotBlank(deployerConfiguration.getKafkaSecurityProtocol()) && deployerConfiguration.getKafkaSecurityProtocol().equals(SSL)){
            deploymentYaml = readFileAsString("/template/microservice_security_template.yaml");
            deploymentYaml = deploymentYaml.replace(NAMESPACE, namespace)
                    .replace(SERVICE_NAME, serviceName)
                    .replace(IMAGE_REPOSITORY, deployerConfiguration.getImageRepository())
                    .replace(DOCKER_IMAGE_NAME, dockerImageName)
                    .replace(TAG_NAME, deployerConfiguration.getTagName())
                    .replace(APPLICATION_PROPERTY_FILE, applicationPropertyFile)
                    .replace(INIT_CONTAINER_IMAGE, deployerConfiguration.getInitContainerImage())
                    .replace(KAFKA_SECRET, deployerConfiguration.getKafkaSecret());
        }else{
            deploymentYaml = readFileAsString("/template/microservice_template.yaml");
            deploymentYaml = deploymentYaml.replace(NAMESPACE, namespace)
                    .replace(SERVICE_NAME, serviceName)
                    .replace(IMAGE_REPOSITORY, deployerConfiguration.getImageRepository())
                    .replace(DOCKER_IMAGE_NAME, dockerImageName)
                    .replace(TAG_NAME, deployerConfiguration.getTagName())
                    .replace(APPLICATION_PROPERTY_FILE, applicationPropertyFile)
                    .replace(INIT_CONTAINER_IMAGE, deployerConfiguration.getInitContainerImage());
        }

        saveDataToFile(TEMP_DIR + deploymentFileName, deploymentYaml);

        try {
            Map<String, Object> deploymentResult = kubernetesClient.customResource(getMilcyCRDContext(kubernetesClient)).load(stringToInputStream(deploymentYaml));
            deleteDeployment(serviceName);
            kubernetesClient.customResource(getMilcyCRDContext(kubernetesClient)).inNamespace(namespace).withName(serviceName).create(deploymentResult);
        } catch (IOException e) {
            exitCode = 4;
            throw new RuntimeException(e); // NOSONAR
        }
    }

    private void deleteDeployment(String serviceName) {
        kubernetesClient.customResource(getMilcyCRDContext(kubernetesClient)).inNamespace(namespace).withName(serviceName).delete();
        Object microservice = kubernetesClient.apps().deployments().inNamespace(namespace).withName(serviceName).get();
        while (microservice != null) {
            microservice = kubernetesClient.apps().deployments().inNamespace(namespace).withName(serviceName).get();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

    }

    private String getImageName(String mode, String schemaType) {
        String aliasName = schemaTypeToAliasMap.get(schemaType);
        String sourceName = aliasName + "-" + mode;
        return sourceToImageNameMap.getOrDefault(sourceName, "UNKNOWN_IMAGE");
    }

    private String getServiceName(String schemaName, String mode, String schemaTypeAlias) {
        String serviceName = null;
        for(String configMap : configMapNamesList){
            if(configMap.contains(schemaTypeAlias + "-" + mode + "-" + removeSpecialCharacter(schemaName.toLowerCase()))) {
                serviceName = configMap.replace("-configmap", "");
            }
        }
        return serviceName;
    }

    private void saveDataToFile(String fileName, String fileData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) { //NOSONAR
            writer.write(fileData);
        } catch (IOException e) {
            exitCode = 1;
            LOGGER.error("There is an error while writing data to file");
            throw new RuntimeException(e); // NOSONAR
        }
    }

    private String readFileAsString(String fileName) {
        String data = "";
        try (InputStream inputStream = getClass().getResourceAsStream(fileName)) {
            assert inputStream != null : "File content cannot be null";
            data = IOUtils.toString(inputStream);
        } catch (IOException e) {
            exitCode = 2;
            throw new RuntimeException(e); // NOSONAR
        }
        return data;
    }

    private void populateConfigMapList() {
        List<ConfigMap> configMaps = kubernetesClient.configMaps().inNamespace(namespace).list().getItems();
        configMaps.stream().filter(configMap -> {
                    String configMapName = configMap.getMetadata().getName();
                    return (configMapName.contains("-bulk-") || configMapName.contains("-ongoing-")) && !configMapName.contains("-pki-");
                }).map(configMap -> configMap.getMetadata().getName()).forEach(configMapNamesList::add);
    }

    private static InputStream stringToInputStream(String yamlData) {
        return new ByteArrayInputStream(yamlData.getBytes(StandardCharsets.UTF_8));
    }

    private static String removeSpecialCharacter(String schemaName) {
        return schemaName.replace("_", "-");
    }

    private static CustomResourceDefinitionContext getMilcyCRDContext(final KubernetesClient kubernetesClient) {
        // milcy Custom Resource Definition in ks8 is: microservices.app.amdocs.com
        final CustomResourceDefinition definition = kubernetesClient.apiextensions().v1beta1()
                .customResourceDefinitions().withName("microservices.app.amdocs.com").get();
        return CustomResourceDefinitionContext.fromCrd(definition);
    }

    private void configureInitContainerImage() {
        Job aiaCollectorDeployerJob = kubernetesClient.batch().jobs().inNamespace(namespace).withName("aia-il-collector-deployer-job").get();
        deployerConfiguration.setInitContainerImage(aiaCollectorDeployerJob.getSpec().getTemplate().getSpec().getInitContainers().get(0).getImage());
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setSchemaTypeToAliasMap(Map<String, String> schemaTypeToAliasMap) {
        this.schemaTypeToAliasMap = schemaTypeToAliasMap;
    }

    public void setSourceToImageNameMap(Map<String, String> sourceToImageNameMap) {
        this.sourceToImageNameMap = sourceToImageNameMap;
    }

    public void setCollectorToApplicationPropertyMap(Map<String, String> collectorToApplicationPropertyMap) {
        this.collectorToApplicationPropertyMap = collectorToApplicationPropertyMap;
    }
}