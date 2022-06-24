package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.common.util.RandomCreator;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.draw.DrawTicket;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerItem;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.repository.DrawTicketRepository;
import prgrms.neoike.repository.SneakerItemRepository;
import prgrms.neoike.repository.SneakerRepository;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;
import prgrms.neoike.service.converter.DrawConverter;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;

import java.util.*;

import static java.text.MessageFormat.format;

@Service
@RequiredArgsConstructor
public class DrawService {
    private final DrawRepository drawRepository;
    private final DrawTicketRepository drawTicketRepository;
    private final SneakerRepository sneakerRepository;
    private final SneakerItemRepository sneakerItemRepository;
    private final DrawConverter drawConverter;

    @Transactional
    public DrawResponse save(ServiceDrawSaveDto drawSaveRequest) {
        Long sneakerId = drawSaveRequest.sneakerId();
        Sneaker sneaker = sneakerRepository.findById(sneakerId)
                .orElseThrow(() -> new EntityNotFoundException(format("Sneaker 엔티티를 id 로 찾을 수 없습니다. drawId : {0}", sneakerId)));

        Draw draw = drawConverter.toDraw(drawSaveRequest, sneaker);
        Draw savedDraw = drawRepository.save(draw);

        return drawConverter.toDrawResponseDto(savedDraw.getId());
    }

    @Transactional
    public DrawTicketsResponse drawWinner(Long drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new EntityNotFoundException(format("Draw 엔티티를 id 로 찾을 수 없습니다. drawId : {0}", drawId)));


        List<SneakerItem> sneakerItems = sneakerItemRepository.findByDraw(draw);
        List<DrawTicket> drawTickets = drawTicketRepository.findByDraw(draw);

        List<DrawTicketResponse> successDrawTickets = new ArrayList<>();
        Set<Integer> randomSet = RandomCreator.noDuplication(draw.getQuantity(), drawTickets.size());

        Map<Integer, Integer> map = new HashMap<>();
        sneakerItems.forEach(
                (sneakerItem) -> map.put(sneakerItem.getSize(), sneakerItem.getQuantity())
        );

        randomSet.forEach(
                (id) -> {
                    DrawTicket winTicket = drawTickets.get(id);
                    int size = winTicket.getSize();
                    if (map.get(size) > 0) {
                        winTicket.changeToWinner();
                        map.put(size , map.get(size)-1);
                        successDrawTickets.add(drawConverter.toDrawTicketResponse(winTicket));
                    }
                    // 당첨자에게 당첨되었다는 알람 전송
                    // 로직구현
                }
        );
        drawTickets.forEach(DrawTicket::drawQuit);

        return new DrawTicketsResponse(successDrawTickets);
    }
}