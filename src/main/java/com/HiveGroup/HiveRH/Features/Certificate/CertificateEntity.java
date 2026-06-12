package com.HiveGroup.HiveRH.Features.Certificate;

import com.HiveGroup.HiveRH.Features.License.LicenseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "certificate")
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CertificateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_certificate;

    @Lob
    @Column(name = "file", columnDefinition = "LONGBLOB")
    private byte[] file;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_license", nullable = false)
    @JsonBackReference
    private LicenseEntity license;
}
