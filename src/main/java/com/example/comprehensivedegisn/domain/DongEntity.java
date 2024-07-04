package com.example.comprehensivedegisn.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dong")
@NoArgsConstructor
@Getter
public class DongEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String dongCode;

    private String dongName;

    @Enumerated(EnumType.STRING)
    private Gu gu;

    private String guCode;

    @Builder
    public DongEntity(String dongCode, String dongName, Gu gu) {
        this.dongCode = dongCode;
        this.dongName = dongName;
        this.gu = gu;
        this.guCode = gu.getRegionalCode();
    }
}
