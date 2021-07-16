package com.amdocs.aia.il.configuration.service.external;

import com.amdocs.aia.il.common.model.external.ExternalAttribute;
import com.amdocs.aia.il.common.model.external.ExternalEntity;
import com.amdocs.aia.il.common.model.external.ExternalSchema;
import com.amdocs.aia.il.configuration.message.MessageHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class SerializationIDAssigner {

    private int startingKey;
    private final MessageHelper messageHelper;

    public SerializationIDAssigner(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    public int getStartingKey() {
        return startingKey;
    }


    @Value("${default-serialization-starting-value:4000}")
    public void setStartingKey(int startingKey) {
        this.startingKey = startingKey;
    }

    public boolean autoAssignSerializationID(ExternalSchema schema, ExternalEntity entity) {
        Set<Integer> usedSerializationID = new HashSet<>(schema.getAssignedEntitySerializationIDs().values());
        boolean schemaStoreModified = false;

        Integer serializationID = schema.getAssignedEntitySerializationIDs().get(entity.getEntityKey());
        if (serializationID == null) {
            // we've never assigned a Serialization ID to this entity yet
            if (entity.getSerializationId()==null||entity.getSerializationId() <0) {
                // Serialization ID not set manually - we will auto assign
                serializationID = startingKey;
                while (usedSerializationID.contains(serializationID)) {
                    serializationID++;
                }
            } else {
                if (usedSerializationID.contains(entity.getSerializationId())) {
                    throw messageHelper.notAllowedToModifySerializationIdException(entity.getEntityKey(),String.valueOf(entity.getSerializationId()));
                }
                serializationID = entity.getSerializationId();
            }
            schema.putAssignedEntitySerializationID(entity.getEntityKey(), serializationID);
            schemaStoreModified = true;
        }
        else{
            if (!serializationID.equals(entity.getSerializationId())){
                throw messageHelper.notAllowedToModifySerializationIdException(entity.getEntityKey(),String.valueOf(entity.getSerializationId()));
            }
        }
        entity.setSerializationId(serializationID);
        return schemaStoreModified;
    }



    public void autoAssignAttributesSerializationIDs(ExternalEntity entity) {
        AtomicInteger currentSerializationID = new AtomicInteger(0);
        Set<Integer> usedSerializationID = new HashSet<>(entity.getAssignedAttributeSerializationIDs().values());

        for (ExternalAttribute attribute :  entity.getAttributes()) {
            Integer assignedAttributeSerializationID = entity.getAssignedAttributeSerializationIDs().get(attribute.getAttributeKey());
            if (assignedAttributeSerializationID == null) {
                // we've never assigned a Serialization ID to this attribute yet
                if (attribute.getSerializationId()==null ||attribute.getSerializationId()<0 ) {
                    // WE should assign an automatic key to the attribute
                    do {
                        assignedAttributeSerializationID = currentSerializationID.incrementAndGet();
                    }
                    while (usedSerializationID.contains(assignedAttributeSerializationID));
                } else {
                    // the caller manually assigned a value
                    if (usedSerializationID.contains(attribute.getSerializationId())) {
                        throw messageHelper.serializationIdAlreadyExistsException(attribute.getAttributeKey(),String.valueOf(attribute.getSerializationId()));

                    }
                    assignedAttributeSerializationID = attribute.getSerializationId();
                }
                usedSerializationID.add(assignedAttributeSerializationID);
            } else {
                //check if Serialization ID been change-shuold throw exception
                if (attribute.getSerializationId() != null && attribute.getSerializationId() >= 0 && !assignedAttributeSerializationID.equals(attribute.getSerializationId())){
                    throw messageHelper.notAllowedToModifySerializationIdException(attribute.getAttributeKey(),String.valueOf(attribute.getSerializationId()));
                }
            }
            entity.getAssignedAttributeSerializationIDs().put(attribute.getAttributeKey(), assignedAttributeSerializationID);
            attribute.setSerializationId(assignedAttributeSerializationID);
        }

    }



}
