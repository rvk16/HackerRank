package com.amdocs.aia.il.configuration.service;


import com.amdocs.aia.common.core.web.AiaApiException;
import com.amdocs.aia.il.configuration.dto.ContextDTO;
import com.amdocs.aia.il.configuration.dto.SaveElementsResponseDTO;

import java.util.List;

public interface ContextConfigurationService extends ConfigurationService<ContextDTO> {
    List<String> relationTypesList();
    SaveElementsResponseDTO bulkSave(String projectKey, List<ContextDTO> s);
    public ContextDTO findPublisherContextByPublisherName(String projectKey, String publisherName) throws AiaApiException;
}
