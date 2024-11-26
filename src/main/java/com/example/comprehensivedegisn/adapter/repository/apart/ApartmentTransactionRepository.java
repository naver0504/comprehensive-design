package com.example.comprehensivedegisn.adapter.repository.apart;

import com.example.comprehensivedegisn.adapter.domain.ApartmentTransaction;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ApartmentTransactionRepository extends JpaRepository<ApartmentTransaction, Long> {

    @Query("""
        select apt from ApartmentTransaction apt
        join fetch apt.dongEntity d
        where apt.id = :id
    """)
    Optional<ApartmentTransaction> findApartmentTransactionById(long id);

    @Query("""
        select apt from ApartmentTransaction apt
        inner join apt.dongEntity d
        where d.gu = :gu and d.dongName = :dongName and  apt.apartmentName = :apartmentName and apt.areaForExclusiveUse = :areaForExclusiveUse and apt.dealDate between :startDate and :endDate
        order by apt.dealAmount desc
    """)
    List<ApartmentTransaction> findApartmentTransactionsForGraph(Gu gu, String dongName, String apartmentName, double areaForExclusiveUse, LocalDate startDate, LocalDate endDate);
}