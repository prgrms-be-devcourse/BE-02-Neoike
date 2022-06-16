package prgrms.neoike.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import prgrms.neoike.domain.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}