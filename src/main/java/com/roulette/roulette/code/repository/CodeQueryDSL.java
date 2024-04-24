package com.roulette.roulette.code.repository;

import com.roulette.roulette.entity.Code;

import java.util.Optional;

public interface CodeQueryDSL {

    public Optional<Code> findOneByMember(Long id);
}
