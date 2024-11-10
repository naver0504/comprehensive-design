package com.example.comprehensivedegisn.adapter.repository.dong;

import com.example.comprehensivedegisn.adapter.domain.DongEntity;
import com.example.comprehensivedegisn.adapter.domain.Gu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DongRepository extends JpaRepository<DongEntity, Integer> {

    @Query("SELECT d FROM DongEntity d WHERE d.guCode = :guCode")
    List<DongEntity> findByGuCode(String guCode);

    @Query("select d from DongEntity d where d.gu = :gu and d.dongName = :dong")
    DongEntity findByGuAndDong(@Param("gu") Gu gu, @Param("dong") String dong);
}
