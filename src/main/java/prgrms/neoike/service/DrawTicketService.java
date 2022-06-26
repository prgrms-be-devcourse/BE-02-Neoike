package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.draw.DrawTicket;
import prgrms.neoike.domain.member.Member;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.repository.DrawTicketRepository;
import prgrms.neoike.repository.MemberRepository;
import prgrms.neoike.repository.SneakerItemRepository;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;
import prgrms.neoike.service.converter.DrawConverter;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;


@Service
@RequiredArgsConstructor
public class DrawTicketService {
    private final DrawConverter drawConverter;
    private final DrawTicketRepository drawTicketRepository;
    private final DrawRepository drawRepository;
    private final MemberRepository memberRepository;
    private final SneakerItemRepository sneakerItemRepository;

    @Transactional
    public DrawTicketResponse save(Long memberId, Long drawId, int size) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(format("Member 엔티티를 id 로 찾을 수 없습니다. memberId : {0}", drawId)));

        Draw draw = drawRepository.findByIdWithSneakerItem(drawId)
                .orElseThrow(() -> new EntityNotFoundException(format("Draw 엔티티를 id 로 찾을 수 없습니다. drawId : {0}", drawId)));

        Sneaker sneaker = draw.getSneaker();

        validateUniqueTicket(member, draw); // 이미 응모한 사람인지 체크
        draw.validateSpare(); // 응모권 재고가 0 이상인지 체크
        validateSizeInput(draw, size); // 응모권의 사이즈가 SneakerItem 의 사이즈 중에 하나인지 체크

        DrawTicket save = drawTicketRepository.save(
                DrawTicket.builder()
                        .member(member)
                        .draw(draw)
                        .sneakerName(sneaker.getName())
                        .price(sneaker.getPrice())
                        .code(sneaker.getCode())
                        .size(size)
                        .build()
        );

        return drawConverter.toDrawTicketResponse(save);
    }

    @Transactional(readOnly = true)
    public void validateSizeInput(Draw draw, int size) {
        if(!sneakerItemRepository.findByDraw(draw).stream()
                .anyMatch(sneakerItem -> sneakerItem.getSize() == size)
        ){
            throw new IllegalStateException("입력 사이즈에 해당하는 신발이 존재하지 않습니다.");
        }
    }

    @Transactional(readOnly = true)
    public void validateUniqueTicket(Member member, Draw draw) {
        Optional<DrawTicket> drawTicketOptional = drawTicketRepository.findByMemberAndDraw(member, draw);
        drawTicketOptional.ifPresent(d -> {
                    throw new IllegalStateException("티켓응모를 이미 진행하였습니다.");
                }
        );
    }

    @Transactional(readOnly = true)
    public DrawTicketsResponse findByMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(format("Member 엔티티를 id 로 찾을 수 없습니다. memberId : {0}", memberId)));

        List<DrawTicket> drawTickets = drawTicketRepository.findByMember(member);

        return new DrawTicketsResponse(
                drawTickets.stream().map((drawTicket) ->
                        drawConverter.toDrawTicketResponse(drawTicket)
                ).collect(Collectors.toList())
        );
    }
}
