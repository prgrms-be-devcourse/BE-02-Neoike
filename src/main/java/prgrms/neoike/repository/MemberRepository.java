package prgrms.neoike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import prgrms.neoike.domain.member.Member;

import java.util.Optional;
import java.util.OptionalInt;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.email = ?1")
    Optional<Member> findByEmail(String email);
}