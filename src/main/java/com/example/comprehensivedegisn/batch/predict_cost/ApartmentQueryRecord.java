package com.example.comprehensivedegisn.batch.predict_cost;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import lombok.Getter;

@Getter
public class ApartmentQueryRecord extends ApartmentTransaction {

    public ApartmentQueryRecord(ApartmentTransaction apartmentTransaction, double interest, Gu gu, String dongName) {
        super(apartmentTransaction);
        // querydslReader에서 id 인식 못함 ApartmentTransaction에 감싸져있는 id 인식못함
        this.id = apartmentTransaction.getId();
        this.interestRate = interest;
        this.gu = gu;
        this.dongName = dongName;
    }

    private long id;
    private double interestRate;
    private Gu gu;
    private String dongName;

    @Override
    public Gu getGu() {
        return gu;
    }

    @Override
    public String getDongName() {
        return dongName;
    }
}
