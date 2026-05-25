package com.HiveGroup.HiveRH.Features.Certificate;

import com.HiveGroup.HiveRH.Features.Certificate.DTO.CertificateDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CertificateMapper {
    public CertificateEntity toEntity(CertificateDTO certificate);
    public CertificateDTO toDTO(CertificateEntity certificate);
}
