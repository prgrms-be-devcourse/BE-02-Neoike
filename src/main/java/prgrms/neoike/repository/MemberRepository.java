package prgrms.neoike.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import prgrms.neoike.domain.member.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("SELECT m FROM Member m WHERE m.email.email = ?1")
    Optional<Member> findByEmail(String email);

    @EntityGraph(attributePaths = "authorities")
    @Query("SELECT m FROM Member m WHERE m.email.email = ?1")
    Optional<Member> findOneWithAuthoritiesByEmail(String email);
}