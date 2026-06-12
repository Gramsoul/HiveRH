package com.HiveGroup.HiveRH.Features.Certificate;

import com.HiveGroup.HiveRH.Features.Certificate.DTO.CertificateDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CertificateMapper {
    CertificateEntity toEntity(CertificateDTO certificate);


    CertificateDTO toDTO(CertificateEntity certificate);
}
