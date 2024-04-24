package com.roulette.roulette.code.repository;

import com.roulette.roulette.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodeRepository extends JpaRepository<Code, Long>, CodeQueryDSL {

}
