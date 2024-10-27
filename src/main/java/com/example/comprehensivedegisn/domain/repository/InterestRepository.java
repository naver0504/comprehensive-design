package com.example.comprehensivedegisn.domain.repository;

import com.example.comprehensivedegisn.domain.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Integer> {
}
