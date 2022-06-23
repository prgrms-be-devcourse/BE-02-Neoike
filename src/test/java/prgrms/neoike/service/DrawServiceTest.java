package prgrms.neoike.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.domain.draw.DrawStatus;
import prgrms.neoike.domain.draw.DrawTicket;
import prgrms.neoike.domain.member.*;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.repository.DrawTicketRepository;
import prgrms.neoike.repository.MemberRepository;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketListResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
@Transactional
class DrawServiceTest {
    @Autowired
    DrawService drawService;

    @Autowired
    DrawTicketService drawTicketService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    DrawTicketRepository drawTicketRepository;

    @Autowired
    DrawRepository drawRepository;

    LocalDateTime startDate = LocalDateTime.of(2025, 06, 12, 12, 00, 00);
    LocalDateTime endDate = LocalDateTime.of(2025, 06, 13, 12, 00, 00);
    LocalDateTime winningDate = LocalDateTime.of(2025, 06, 14, 12, 00, 00);


    @Test
    @DisplayName("Draw 엔티티를 저장한다.")
    void saveDrawTest() {
        // given
        ServiceDrawSaveDto drawSaveDto = getDrawSaveDto();

        // when
        DrawResponse save = drawService.save(drawSaveDto);

        // then
        assertThat(drawRepository.count()).isEqualTo(1);
    }


    @Test
    @DisplayName("등록 순서가 잘못된 Draw 엔티티를 저장할때 오류 발생")
    void invalidSaveDrawTest() {
        // given // when // then
        assertThatThrownBy(() -> drawService.save(
                ServiceDrawSaveDto.builder()
                        .sneakerId(1L)
                        .startDate(endDate)
                        .endDate(startDate)
                        .winningDate(winningDate)
                        .quantity(50)
                        .sneakerItems(new ArrayList<>())
                        .build()
        )).isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> drawService.save(
                ServiceDrawSaveDto.builder()
                        .sneakerId(1L)
                        .startDate(startDate)
                        .endDate(winningDate)
                        .winningDate(endDate)
                        .quantity(50)
                        .sneakerItems(new ArrayList<>())
                        .build()
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("추첨을 진행한다")
    void drawWinTest() {
        // given
        ServiceDrawSaveDto drawSaveDto = getDrawSaveDto();
        DrawResponse save = drawService.save(drawSaveDto);

        Member member1 = memberRepository.save(sampleMember());
        Member member2 = memberRepository.save(sampleMember());
        Member member3 = memberRepository.save(sampleMember());
        Member member4 = memberRepository.save(sampleMember());
        Member member5 = memberRepository.save(sampleMember());

        drawTicketService.save(member1.getId(), save.drawId());
        drawTicketService.save(member2.getId(), save.drawId());
        drawTicketService.save(member3.getId(), save.drawId());
        drawTicketService.save(member4.getId(), save.drawId());
        drawTicketService.save(member5.getId(), save.drawId());

        // when
        DrawTicketListResponse drawTicketListResponse = drawService.drawWinner(save.drawId());

        // then
        List<DrawTicket> drawTicketByMember1 = drawTicketRepository.findByMember(member1);
        List<DrawTicket> drawTicketByMember2 = drawTicketRepository.findByMember(member2);
        List<DrawTicket> drawTicketByMember3 = drawTicketRepository.findByMember(member3);
        List<DrawTicket> drawTicketByMember4 = drawTicketRepository.findByMember(member4);
        List<DrawTicket> drawTicketByMember5 = drawTicketRepository.findByMember(member5);

        int result = 0;

        result = findWinning(drawTicketByMember1.get(0).getDrawStatus(), result);
        result = findWinning(drawTicketByMember2.get(0).getDrawStatus(), result);
        result = findWinning(drawTicketByMember3.get(0).getDrawStatus(), result);
        result = findWinning(drawTicketByMember4.get(0).getDrawStatus(), result);
        result = findWinning(drawTicketByMember5.get(0).getDrawStatus(), result);

        Assertions.assertThat(result).isEqualTo(3); // 3(현재 설정값) 에는 sneaker 의 재고가 들어가야 된다. 추후 수정예정
    }

    private int findWinning(DrawStatus drawStatus, int result){
        if(drawStatus==DrawStatus.WINNING){
            return ++result;
        }
        return result;
    }

    private ServiceDrawSaveDto getDrawSaveDto() {
        ServiceDrawSaveDto drawSaveDto = ServiceDrawSaveDto.builder()
                .sneakerId(1L)
                .startDate(startDate)
                .endDate(endDate)
                .winningDate(winningDate)
                .quantity(50)
                .sneakerItems(new ArrayList<>())
                .build();
        return drawSaveDto;
    }

    private Member sampleMember() {
        return Member.builder()
                .name("이용훈")
                .password(new Password("123abcAB!!"))
                .phoneNumber(new PhoneNumber(CountryType.KOR, "01012341566"))
                .address(new Address("도시", "거리", "000222"))
                .birthDay(LocalDateTime.now())
                .email(new Email("test@test.com"))
                .gender(Gender.MALE)
                .build();
    }
}