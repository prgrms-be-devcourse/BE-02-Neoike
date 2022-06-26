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
import prgrms.neoike.domain.sneaker.SneakerStock;
import prgrms.neoike.repository.*;
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
    private final SneakerStockRepository sneakerStockRepository;
    private final DrawConverter drawConverter;

    @Transactional
    public DrawResponse save(ServiceDrawSaveDto drawSaveRequest) {
        Long sneakerId = drawSaveRequest.sneakerId();
        Sneaker sneaker = sneakerRepository.findById(sneakerId)
                .orElseThrow(() -> new EntityNotFoundException(format("Sneaker 엔티티를 id 로 찾을 수 없습니다. drawId : {0}", sneakerId)));

        Draw draw = drawConverter.toDraw(drawSaveRequest, sneaker);
        Draw savedDraw = drawRepository.save(draw);

        // 해당 sneakerId 에 해당하는 SneakerItem 들을 생성한 후 저장한다.
        drawSaveRequest.sneakerItems().forEach(
                (sneakerItem) -> {
                    int size = sneakerItem.size();
                    SneakerStock sneakerStock = sneakerStockRepository.findBySneakerAndSize(sneaker, size)
                            .orElseThrow(() -> new EntityNotFoundException(format("SneakerStock 엔티티를 sneaker 와 size 로 찾을 수 없습니다. sneakerId : {0}, size : {1}", sneakerId, size)));

                    sneakerItemRepository.save(
                            SneakerItem.builder()
                                    .sneakerStock(sneakerStock)
                                    .quantity(sneakerItem.quantity())
                                    .draw(draw)
                                    .size(size)
                                    .build()
                    );
                }
        );

        return drawConverter.toDrawResponseDto(savedDraw.getId());
    }

    @Transactional
    public DrawTicketsResponse drawWinner(Long drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new EntityNotFoundException(format("Draw 엔티티를 id 로 찾을 수 없습니다. drawId : {0}", drawId)));

        List<SneakerItem> sneakerItems = sneakerItemRepository.findByDraw(draw);
        List<DrawTicket> drawTickets = drawTicketRepository.findByDraw(draw);

        List<DrawTicketResponse> successDrawTickets = new ArrayList<>();
        Map<Integer, SneakerItem> sizeToSneakerItem = new HashMap<>();

        sneakerItems.forEach(
                (sneakerItem) -> sizeToSneakerItem.put(sneakerItem.getSize(), sneakerItem)
        );

        sizeToSneakerItem.forEach(
                (size, sneakerItem) -> {
                    List<DrawTicket> ticketsBySize = drawTickets.stream()
                            .filter((drawTicket) -> drawTicket.getSize() == size)
                            .toList();

                    drawWinnerBySize(successDrawTickets, sneakerItem, ticketsBySize);
                }
        );

        drawTickets.forEach(DrawTicket::drawQuit);

        return new DrawTicketsResponse(successDrawTickets);
    }

    public void drawWinnerBySize(List<DrawTicketResponse> successDrawTickets,
                                  SneakerItem sneakerItem,
                                  List<DrawTicket> ticketsBySize
    ) {
        int quantity = sneakerItem.getQuantity();
        Boolean isTicketQuantityLOEThanSneakerItemQuantity = (ticketsBySize.size() <= quantity);

        if (isTicketQuantityLOEThanSneakerItemQuantity) {
            ticketsBySize.forEach(DrawTicket::changeToWinner);
            // sneakerItem.changeQuantity(ticketsBySize.size());
            // sneakerItem 의 재고를 감소시키고 감소하고 남은 개수는 다시 SneakerStock 재고에 추가한다.
            return;
        }

        Set<Integer> randomSet = RandomCreator.noDuplication(quantity, ticketsBySize.size());
        randomSet.forEach(
                (id) -> {
                    DrawTicket winTicket = ticketsBySize.get(id);
                    winTicket.changeToWinner();
                    successDrawTickets.add(drawConverter.toDrawTicketResponse(winTicket));

                    // 당첨자에게 당첨되었다는 알람 전송
                }
        );
        // sneakerItem.changeQuantity();
        // sneakerItem 의 재고를 0 으로 바꾼다.
    }
}