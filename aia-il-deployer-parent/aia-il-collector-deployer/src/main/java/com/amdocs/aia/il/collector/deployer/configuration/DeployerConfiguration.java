package com.amdocs.aia.il.collector.deployer.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "deployer")
public class DeployerConfiguration {
    private String namespace;
    private String imageRepository;
    private String k8sMasterNode;
    private String nfsPath;
    private String k1BrokersList;
    private String initContainerImage;
    private String kafkaSecret;
    private String kafkaSecurityProtocol;

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getImageRepository() {
        return imageRepository;
    }

    public void setImageRepository(String imageRepository) {
        this.imageRepository = imageRepository;
    }

    public String getK8sMasterNode() {
        return k8sMasterNode;
    }

    public void setK8sMasterNode(String k8sMasterNode) {
        this.k8sMasterNode = k8sMasterNode;
    }

    public String getNfsPath() {
        return nfsPath;
    }

    public void setNfsPath(String nfsPath) {
        this.nfsPath = nfsPath;
    }

    public String getK1BrokersList() {
        return k1BrokersList;
    }

    public void setK1BrokersList(String k1BrokersList) {
        this.k1BrokersList = k1BrokersList;
    }

    public String getInitContainerImage() {
        return initContainerImage;
    }

    public void setInitContainerImage(String initContainerImage) {
        this.initContainerImage = initContainerImage;
    }

    public String getKafkaSecret() {
        return kafkaSecret;
    }

    public void setKafkaSecret(String kafkaSecret) {
        this.kafkaSecret = kafkaSecret;
    }

    public String getKafkaSecurityProtocol() {
        return kafkaSecurityProtocol;
    }

    public void setKafkaSecurityProtocol(String kafkaSecurityProtocol) {
        this.kafkaSecurityProtocol = kafkaSecurityProtocol;
    }
}