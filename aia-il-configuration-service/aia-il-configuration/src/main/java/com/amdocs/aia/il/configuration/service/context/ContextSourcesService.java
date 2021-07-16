package com.amdocs.aia.il.configuration.service.context;

import com.amdocs.aia.il.configuration.dto.BaseEntityDTO;
import com.amdocs.aia.il.configuration.dto.ContextEntityRefDTO;
import com.amdocs.aia.il.configuration.dto.ContextSourceDTO;
import com.amdocs.aia.il.configuration.dto.EntitiesDTO;

import javax.validation.Valid;
import java.util.List;

public interface ContextSourcesService {

    List<ContextSourceDTO> getContextSources(String projectKey, String schemaType);

    List<BaseEntityDTO> searchContextEntitiesMetadata(String projectKey, EntitiesDTO entities);

}
