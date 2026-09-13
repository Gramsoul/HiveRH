package com.HiveGroup.HiveRH.Features.License;

import com.HiveGroup.HiveRH.Features.License.DTO.LicenseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface LicenseMapper {

    @Mapping(source = "id_license", target = "id")
    @Mapping(source = "employee.dni", target = "dniEmployee")
    @Mapping(source = "reviewedBy.id_account", target = "reviewedByAccountId")
    @Mapping(target = "idCertificates", ignore = true)
    LicenseDTO toDTO(LicenseEntity license);
}
