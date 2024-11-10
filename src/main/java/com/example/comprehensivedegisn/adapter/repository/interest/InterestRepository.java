package com.example.comprehensivedegisn.adapter.repository.interest;

import com.example.comprehensivedegisn.adapter.domain.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Integer> {
}
