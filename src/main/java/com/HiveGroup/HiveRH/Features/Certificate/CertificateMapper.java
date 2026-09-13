package com.HiveGroup.HiveRH.Features.Certificate;

import com.HiveGroup.HiveRH.Features.Certificate.DTO.CertificateDTO;
import com.HiveGroup.HiveRH.Features.Certificate.DTO.ResponseCertificateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface CertificateMapper {

    @Mapping(source = "id_certificate", target = "idCertificate")
    @Mapping(source = "license.id_license", target = "idLicense")
    CertificateDTO toDTO(CertificateEntity certificate);

    @Mapping(source = "license.id_license", target = "idLicense")
    ResponseCertificateDTO toResponseDTO(CertificateEntity certificate);
}
