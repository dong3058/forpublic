package com.roulette.roulette.code.repository;

import com.roulette.roulette.entity.Code;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class CodeQueryDSLImpl implements CodeQueryDSL {

    @Override
    public Optional<Code> findOneByMember(Long id) {

        return null;
    }
}
