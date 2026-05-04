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
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_certificate;

    @Column(name = "archive", columnDefinition = "TEXT")
    private String archive;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_license", nullable = false)
    private LicenseEntity license;
}
