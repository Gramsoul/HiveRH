package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface LicenseMapper {

    LicenseDTO toDTO(LicenseEntity license);
    LicenseEntity toEntity(LicenseDTO license);
}
