package prgrms.neoike.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import prgrms.neoike.domain.member.*;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("유효한 email을 통해 member를 조회할 수 있다.")
    void findOneByEmailTest() {
        Member member = Member.builder()
                .email(new Email("test@gmail.com"))
                .name("testMember")
                .phoneNumber(new PhoneNumber(CountryType.KOR, "01012341234"))
                .gender(Gender.FEMALE)
                .address(new Address("city", "street", "12345"))
                .password(new Password("123abcA!!"))
                .birthDay(LocalDate.now())
                .build();
        Member savedMember = memberRepository.save(member);

        Optional<Member> oneByEmail = memberRepository.findOneByEmail(savedMember.getEmail().getAddress());

        assertThat(oneByEmail).isNotEmpty();
        assertThat(oneByEmail.get().getEmail().getAddress()).isEqualTo(member.getEmail().getAddress());
    }

    @Test
    @DisplayName("유효한 email을 통해 member를 조회할 수 있다.")
    void findOneByInValidEmailTest() {
        Optional<Member> oneByEmail = memberRepository.findOneByEmail("invalidTestEmail@gmail.com");

        assertThat(oneByEmail).isEmpty();
    }

}