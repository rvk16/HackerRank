package com.amdocs.aia.il.configuration.mapper;

import com.amdocs.aia.common.model.repo.ChangeStatus;
import com.amdocs.aia.il.common.model.physical.csv.CsvEntityStore;
import com.amdocs.aia.il.configuration.dto.ChangeStatusDTO;
import com.amdocs.aia.il.configuration.dto.InvalidFilenameActionTypeDTO;

public class MapperUtils {
    public static ChangeStatusDTO toDTO(final ChangeStatus changeStatus) {
        if (changeStatus == null) {
            return ChangeStatusDTO.NOT_EXIST;
        }
        switch (changeStatus) {
            case DRAFT:
                return ChangeStatusDTO.DRAFT;
            case MODIFIED:
                return ChangeStatusDTO.MODIFIED;
            case PUBLISHED:
                return ChangeStatusDTO.PUBLISHED;
            default:
                throw new IllegalArgumentException("Unknown status type: " + changeStatus);
        }
    }

    public static InvalidFilenameActionTypeDTO toDTO(CsvEntityStore.FileInvalidNameAction fileInvalidNameAction) {
        if (fileInvalidNameAction == null) {
            return InvalidFilenameActionTypeDTO.KEEP; // default value
        }
        switch (fileInvalidNameAction) {
            case KEEP:
                return InvalidFilenameActionTypeDTO.KEEP;
            case MOVE:
                return InvalidFilenameActionTypeDTO.MOVE;
            default:
                throw new IllegalStateException("Unexpected value: " + fileInvalidNameAction);
        }
    }


    public static CsvEntityStore.FileInvalidNameAction toModel(InvalidFilenameActionTypeDTO invalidFilenameAction) {
        if (invalidFilenameAction == null) {
            return CsvEntityStore.FileInvalidNameAction.KEEP; // default value
        }
        switch (invalidFilenameAction) {
            case KEEP:
                return CsvEntityStore.FileInvalidNameAction.KEEP;
            case MOVE:
                return CsvEntityStore.FileInvalidNameAction.MOVE;
            default:
                throw new IllegalStateException("Unexpected value: " + invalidFilenameAction);
        }
    }


}
