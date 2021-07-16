package com.amdocs.aia.il.configuration.mapper;

import com.amdocs.aia.il.common.model.EntityReferentialIntegrity;
import com.amdocs.aia.il.common.model.Relation;
import com.amdocs.aia.il.configuration.dto.EntityReferentialIntegrityDTO;
import com.amdocs.aia.il.configuration.dto.RelationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EntityReferentialIntegrityMapper implements ModelDtoMapper<EntityReferentialIntegrity, EntityReferentialIntegrityDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityReferentialIntegrityMapper.class);
    @Override
    public EntityReferentialIntegrity toModel(String projectKey, EntityReferentialIntegrityDTO dto) {

        EntityReferentialIntegrity entityReferentialIntegrity =  new EntityReferentialIntegrity()
                .setLogicalSchemaKey(dto.getSchemaStoreKey())
                .setLogicalEntityKey(dto.getEntityStoreKey())
                .setRelations(getRelations(dto.getRelations()));
        entityReferentialIntegrity.setProjectKey(projectKey);
        return entityReferentialIntegrity;

    }

    @Override
    public List<EntityReferentialIntegrity> toModel(String projectKey, List<EntityReferentialIntegrityDTO> dtos) {
        return dtos.stream()
                .peek(dto->  LOGGER.info("EntityReferentialIntegrity toModel: {}", dto.getEntityStoreKey() ))
                .map( group -> toModel(projectKey,group))
                .collect(Collectors.toList());
    }

    @Override
    public EntityReferentialIntegrityDTO toDTO(EntityReferentialIntegrity model) {
       EntityReferentialIntegrityDTO dto = new EntityReferentialIntegrityDTO();
       dto.setEntityStoreKey(model.getLogicalEntityKey());
       dto.setSchemaStoreKey(model.getLogicalSchemaKey());
       dto.setRelations(getRelationDTOs(model.getRelations()));
       return dto;
    }


    public RelationDTO toDTO(Relation model) {

        RelationDTO dto = new RelationDTO();
        dto.setAttributeKey(model.getAttributeKey());
        dto.setParentAttributeKey(model.getAttributeKey());
        dto.setParentEntityKey(model.getParentEntityKey());
        dto.setParentSchemaKey(model.getParentSchemaKey());
        return dto;
    }

    public Relation toModel(RelationDTO dto) {
        Relation model = new Relation();
        model.setAttributeKey(dto.getAttributeKey());
        model.setParentAttributeKey(dto.getParentAttributeKey());
        model.setParentEntityKey(dto.getParentEntityKey());
        model.setParentSchemaKey(dto.getParentSchemaKey());
        return model;
    }



    private List<Relation> getRelations(List<RelationDTO> relationDTOS) {
        if (relationDTOS == null) {
            return Collections.EMPTY_LIST;
        }
        final Set<Relation> relations = new HashSet<>(relationDTOS.size());
        for (final RelationDTO relationDTO : relationDTOS) {
            relations.add(toModel(relationDTO));
        }
        return relations.stream().collect(Collectors.toList());
    }



    private List<RelationDTO> getRelationDTOs(List<Relation> relations){
        if (relations == null) {
            return Collections.EMPTY_LIST;
        }
        final Set<RelationDTO> relationDTOS = new HashSet<>(relations.size());
        for (final Relation relation : relations) {
            relationDTOS.add(toDTO(relation));
        }
        return relationDTOS.stream().collect(Collectors.toList());
    }

}
