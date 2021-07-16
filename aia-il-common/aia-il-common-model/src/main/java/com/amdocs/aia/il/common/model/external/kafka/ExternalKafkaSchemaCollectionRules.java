package com.amdocs.aia.il.common.model.external.kafka;

import com.amdocs.aia.il.common.model.external.AbstractExternalSchemaCollectionRules;
import com.amdocs.aia.il.common.model.external.ExternalSchemaStoreTypes;

public class ExternalKafkaSchemaCollectionRules extends AbstractExternalSchemaCollectionRules {
    private static final long serialVersionUID = -2091732328515217076L;

    private String inputDataChannel;
    private String skipNodeFromParsing;
    private String deleteEventJsonPath;
    private String deleteEventOperation;
    private String implicitHandlerPreviousNode;
    private String implicitHandlerCurrentNode;

    public ExternalKafkaSchemaCollectionRules() {
        super(ExternalSchemaStoreTypes.KAFKA);
    }

    public String getInputDataChannel() {
        return inputDataChannel;
    }

    public void setInputDataChannel(String inputDataChannel) {
        this.inputDataChannel = inputDataChannel;
    }

    public String getSkipNodeFromParsing() {
        return skipNodeFromParsing;
    }

    public void setSkipNodeFromParsing(String skipNodeFromParsing) {
        this.skipNodeFromParsing = skipNodeFromParsing;
    }

    public String getDeleteEventJsonPath() {
        return deleteEventJsonPath;
    }

    public void setDeleteEventJsonPath(String deleteEventJsonPath) {
        this.deleteEventJsonPath = deleteEventJsonPath;
    }

    public String getDeleteEventOperation() {
        return deleteEventOperation;
    }

    public void setDeleteEventOperation(String deleteEventOperation) {
        this.deleteEventOperation = deleteEventOperation;
    }

    public String getImplicitHandlerPreviousNode() {
        return implicitHandlerPreviousNode;
    }

    public void setImplicitHandlerPreviousNode(String implicitHandlerPreviousNode) {
        this.implicitHandlerPreviousNode = implicitHandlerPreviousNode;
    }

    public String getImplicitHandlerCurrentNode() {
        return implicitHandlerCurrentNode;
    }

    public void setImplicitHandlerCurrentNode(String implicitHandlerCurrentNode) {
        this.implicitHandlerCurrentNode = implicitHandlerCurrentNode;
    }
}
