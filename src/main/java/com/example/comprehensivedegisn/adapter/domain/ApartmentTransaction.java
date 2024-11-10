package com.example.comprehensivedegisn.adapter.domain;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;

import java.time.LocalDate;
import java.util.Optional;

@Entity
@Table(name = "apartment_transaction")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ApartmentTransaction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String dealAmount;
    private int buildYear;
    private int dealYear;
    private String roadName;
    private int roadNameBonbun;
    private int roadNameBubun;
    private int roadNameCode;
    private int bonbun;
    private int bubun;
    private String apartmentName;
    private int dealMonth;
    private int dealDay;
    private double areaForExclusiveUse;
    private String jibun;
    private int floor;
    private LocalDate dealDate;

    @Column(columnDefinition = "GEOMETRY")
    private Point geography;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dong_entity_id")
    private DongEntity dongEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interest_id")
    private Interest interest;

    public String getRoadNameWithGu(Gu gu) {
        return AddressUtils.getRoadNameWithGu(gu, roadName, roadNameBonbun, roadNameBubun);
    }

    public Optional<String> getRoadNameAddress() {
        return AddressUtils.getRoadName(roadName, roadNameBonbun, roadNameBubun);
    }

}