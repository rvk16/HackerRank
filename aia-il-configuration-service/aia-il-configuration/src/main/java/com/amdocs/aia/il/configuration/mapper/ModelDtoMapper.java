package com.amdocs.aia.il.configuration.mapper;


import com.amdocs.aia.common.model.ProjectElement;

import java.util.List;

/**
 * @param <M> Model type
 * @param <D> DTO type
 */
public interface ModelDtoMapper<M extends ProjectElement, D> {
    M toModel(final String projectKey, final D dto);
    List<M> toModel(final String projectKey, final List<D> dtos);
    D toDTO(final M model);
}

