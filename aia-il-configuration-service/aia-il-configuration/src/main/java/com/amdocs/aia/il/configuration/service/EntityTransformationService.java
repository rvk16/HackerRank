package com.amdocs.aia.il.configuration.service;

import com.amdocs.aia.il.configuration.dto.EntityTransformationDTO;
import com.amdocs.aia.il.configuration.dto.EntityTransformationGridElementDTO;

import java.util.List;

/**
 * Entity Transformation Service
 * E = DTO representing the entity transformation grid item
 */
public interface EntityTransformationService<E extends EntityTransformationGridElementDTO> {
    List<E> list(String projectKey);
    EntityTransformationDTO get(String projectKey, String logicalEntityKey);
    EntityTransformationDTO create(String projectKey, EntityTransformationDTO dto);
    EntityTransformationDTO update(String projectKey, EntityTransformationDTO dto);
    void delete(String projectKey, String logicalEntityKey);
}