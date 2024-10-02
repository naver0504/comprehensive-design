package com.example.comprehensivedegisn.domain;
import com.example.comprehensivedegisn.batch.open_api.dto.ApartmentDetail;
import jakarta.persistence.*;
import lombok.*;

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
    private int roadNameSeq;
    private int roadNameBasementCode;
    private int roadNameCode;
    private String dong;
    private int bonbun;
    private int bubun;
    private int landCode;
    private String apartmentName;
    private int dealMonth;
    private int dealDay;
    private double areaForExclusiveUse;
    private String jibun;
    private int floor;
    private String registrationDate;
    private String rdealerLawDnm;

    private String x;
    private String y;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dong_entity_id")
    private DongEntity dongEntity;

    public static ApartmentTransaction from(ApartmentDetail apartmentDetail, DongEntity dongEntity) {
        return ApartmentTransaction.builder()
                .dealAmount(apartmentDetail.dealAmount())
                .buildYear(apartmentDetail.buildYear())
                .dealYear(apartmentDetail.dealYear())
                .roadName(apartmentDetail.roadName())
                .roadNameSeq(apartmentDetail.roadNameSeq())
                .roadNameBasementCode(apartmentDetail.roadNameBasementCode())
                .roadNameCode(apartmentDetail.roadNameCode())
                .dong(apartmentDetail.dong())
                .bonbun(apartmentDetail.bonbun())
                .bubun(apartmentDetail.bubun())
                .landCode(apartmentDetail.landCode())
                .apartmentName(apartmentDetail.apartmentName())
                .dealMonth(apartmentDetail.dealMonth())
                .dealDay(apartmentDetail.dealDay())
                .areaForExclusiveUse(apartmentDetail.areaForExclusiveUse())
                .jibun(apartmentDetail.jibun())
                .floor(apartmentDetail.floor())
                .registrationDate(apartmentDetail.registrationDate())
                .rdealerLawDnm(apartmentDetail.rdealerLawDnm())
                .dongEntity(dongEntity)
                .build();
    }

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }
}