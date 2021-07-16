package com.amdocs.aia.il.common.model.physical.kafka;

import com.amdocs.aia.il.common.model.physical.AbstractPhysicalSchemaStore;

public class KafkaSchemaStore extends AbstractPhysicalSchemaStore {
    private static final long serialVersionUID = 3073895642943452882L;

    public static final String ELEMENT_TYPE = "KafkaSchemaStore";

    private String inputDataChannel;
    private String skipNodeFromParsing;
    private String deleteEventJsonPath;
    private String deleteEventOperation;
    private String implicitHandlerPreviousNode;
    private String implicitHandlerCurrentNode;

    public KafkaSchemaStore() {
        super(ELEMENT_TYPE);
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
