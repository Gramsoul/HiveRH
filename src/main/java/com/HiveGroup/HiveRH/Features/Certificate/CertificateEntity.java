package com.HiveGroup.HiveRH.Features.Certificate;

import com.HiveGroup.HiveRH.Features.License.LicenseEntity;
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

    @Column(name = "file", columnDefinition = "TEXT")
    private String file;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_license", nullable = false)
    private LicenseEntity license;
}
