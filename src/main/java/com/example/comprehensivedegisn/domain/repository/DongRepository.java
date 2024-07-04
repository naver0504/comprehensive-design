package com.example.comprehensivedegisn.domain.repository;

import com.example.comprehensivedegisn.domain.DongEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DongRepository extends JpaRepository<DongEntity, Integer> {

    @Query("SELECT d FROM DongEntity d WHERE d.guCode = :guCode")
    List<DongEntity> findByGuCode(String guCode);
}
