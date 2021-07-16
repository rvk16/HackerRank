package com.amdocs.aia.il.configuration.mapper;

import com.amdocs.aia.common.model.OriginProcess;
import com.amdocs.aia.il.common.model.ConfigurationConstants;
import com.amdocs.aia.il.common.model.bulk.BulkGroup;
import com.amdocs.aia.il.common.model.bulk.EntityFilterRef;
import com.amdocs.aia.il.common.model.bulk.GroupFilter;
import com.amdocs.aia.il.configuration.dto.BulkGroupDTO;
import com.amdocs.aia.il.configuration.dto.EntityFilterRefDTO;
import com.amdocs.aia.il.configuration.dto.GroupFilterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BulkGroupMapper implements ModelDtoMapper<BulkGroup, BulkGroupDTO> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BulkGroupMapper.class);
    @Override
    public BulkGroup toModel(String projectKey, BulkGroupDTO dto) {
        final BulkGroup model = new BulkGroup();
        model.setEntityFilters(dto.getEntityFilters() == null? Collections.emptySet() : dto.getEntityFilters().stream().map(this::toModel).collect(Collectors.toSet()));
        model.setGroupFilter(this.toModel(dto.getGroupFilter()));
        model.setName(dto.getBulkGroupName());
        model.setKey(dto.getBulkGroupKey());
        model.setSchemaKey(dto.getSchemaKey());
        model.setProjectKey(projectKey);
        model.setProductKey(ConfigurationConstants.PRODUCT_KEY);
        setOriginValues(dto.getOriginProcess(),model);
        return model;
    }

    @Override
    public List<BulkGroup> toModel(String projectKey, List<BulkGroupDTO> dtos) {
        return dtos.stream()
                .peek(dto->  LOGGER.info("BulkGroup toModel: {}", dto.getBulkGroupKey() ))
                .map( group -> toModel(projectKey,group))
                .collect(Collectors.toList());
    }

    @Override
    public BulkGroupDTO toDTO(BulkGroup model) {
        final BulkGroupDTO dto = new BulkGroupDTO();
        dto.bulkGroupKey(model.getKey())
                .schemaKey(model.getSchemaKey())
                .bulkGroupName(model.getName())
                .groupFilter(toDto(model.getGroupFilter()))
                .originProcess(model.getOriginProcess() == null? null : model.getOriginProcess().toString())
                .entityFilters(model.getEntityFilters() == null? Collections.emptyList() :
                        model.getEntityFilters().stream().map(this::toDto).collect(Collectors.toList()));

        return dto;
    }

    public EntityFilterRef toModel(EntityFilterRefDTO dto){
        if (dto == null){
            return null;
        }
        return new EntityFilterRef(dto.getEntityFilterKey(),dto.getEntityKey());
    }

    public EntityFilterRefDTO toDto(EntityFilterRef model){
        if (model == null){
            return null;
        }
        return new EntityFilterRefDTO().entityFilterKey(model.getEntityFilterKey()).entityKey(model.getEntityKey());
    }


    public GroupFilter toModel(GroupFilterDTO dto){
        if (dto == null){
            return null;
        }

        GroupFilter gf = new GroupFilter();
        gf.setFilter(dto.getFilter());
        return gf;
    }

    public GroupFilterDTO toDto(GroupFilter model){
        if (model == null){
            return null;
        }
        return new GroupFilterDTO().filter(model.getFilter());
    }


    private static void setOriginValues(final String originProcessStr, final BulkGroup model) {
        final OriginProcess originProcess = valueOfLabel(originProcessStr);
        if (originProcess != null) {
            model.setOriginProcess(originProcess);
        } else {
            model.setOriginProcess(OriginProcess.CUSTOM);
            model.setOrigin(originProcessStr);
        }
    }

    public static OriginProcess valueOfLabel(final String originProcessStr) {
        for (final OriginProcess originProcess : OriginProcess.values()) {
            if (originProcess.name().equals(originProcessStr)) {
                return originProcess;
            }
        }
        return null;
    }
}
