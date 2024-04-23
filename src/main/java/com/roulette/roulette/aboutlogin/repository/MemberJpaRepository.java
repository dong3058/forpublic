package com.roulette.roulette.aboutlogin.repository;

import com.roulette.roulette.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberJpaRepository extends JpaRepository<Member, Long>,TestRepository {

    //----> 추가로 필요한 로직은 인퍼페이스로 구현한후 인터페이스는 인터페이스를 다중상속이 되므로
    //상속받아서 메서드를 이용하면된다.

}