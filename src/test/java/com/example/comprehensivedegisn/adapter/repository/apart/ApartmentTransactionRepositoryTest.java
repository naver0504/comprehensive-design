package com.example.comprehensivedegisn.adapter.repository.apart;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.DongEntity;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import com.example.comprehensivedegisn.adapter.repository.BaseRepositoryTest;
import com.example.comprehensivedegisn.adapter.repository.dong.DongRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;

import static com.example.comprehensivedegisn.adapter.repository.apart.QuerydslApartmentTransactionRepositoryTest.*;
import static org.junit.jupiter.api.Assertions.*;

@BaseRepositoryTest
class ApartmentTransactionRepositoryTest {


    @Autowired
    private ApartmentTransactionRepository apartmentTransactionRepository;

    @Autowired
    private DongRepository dongRepository;

    @Test
    void findApartmentTransactionsForGraph() {
        // given
        Gu gu = Gu.서초구;
        String dong = TEST_DONG;
        String aptName = TEST_APT_NAME;
        double area = TEST_AREA;

        int repeatCount = 10;
        LocalDate endDate = TEST_END_DATE;
        LocalDate startDate = endDate.minusMonths(repeatCount);
        DongEntity dongEntity = dongRepository.save(DongEntity.builder()
                .gu(gu)
                .dongName(dong)
                .build());

        for (int i = 0; i < repeatCount; i++) {
            apartmentTransactionRepository.save(ApartmentTransaction.builder()
                    .dongEntity(dongEntity)
                    .apartmentName(aptName)
                    .areaForExclusiveUse(area)
                    .dealDate(endDate.minusMonths(i))
                    .build());
        }

        // when
        List<ApartmentTransaction> result = apartmentTransactionRepository.findApartmentTransactionsForGraph(gu, dong, aptName, area, startDate, endDate);

        // then
        Assertions.assertThat(result).hasSize(repeatCount);
        Assertions.assertThat(result).extracting(ApartmentTransaction::getApartmentName).allMatch((apartmentName) -> apartmentName.equals(aptName));
        Assertions.assertThat(result).extracting(ApartmentTransaction::getAreaForExclusiveUse).allMatch((areaForExclusiveUse) -> areaForExclusiveUse == area);
        Assertions.assertThat(result).extracting(ApartmentTransaction::getDealDate).allMatch(isBetween(startDate, endDate));
        Assertions.assertThat(result).extracting(ApartmentTransaction::getDealAmount).isSorted();
    }

    private Predicate<LocalDate> isBetween(LocalDate startDate, LocalDate endDate) {
        return (date) -> (date.isAfter(startDate) && date.isBefore(endDate)) || date.isEqual(startDate) || date.isEqual(endDate);
    }
}