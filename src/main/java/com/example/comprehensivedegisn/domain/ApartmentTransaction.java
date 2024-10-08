package com.example.comprehensivedegisn.domain;
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
    private int roadNameBonbun;
    private int roadNameBubun;
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

    @Override
    public String toString() {
        return String.valueOf(this.id);
    }

    public String getRoadNameWithGu(Gu gu) {
        return gu + " " + getRoadName();
    }

    public String getRoadName() {
        return roadName + " " + roadNameBonbun + getRoadNameBubun();
    }

    private String getRoadNameBubun() {
        return this.roadNameBubun == 0 ? "" : "-" + this.roadNameBubun;
    }
}