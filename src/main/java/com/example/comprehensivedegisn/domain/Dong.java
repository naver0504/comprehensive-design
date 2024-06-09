package com.example.comprehensivedegisn.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dong")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dong {

    @Id
    private int dongCode;

    private String dongName;

    @Enumerated(EnumType.STRING)
    private Gu gu;
}
