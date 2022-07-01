package prgrms.neoike.service;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.common.util.RandomTicketIdCreator;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.draw.DrawTicket;
import prgrms.neoike.domain.sneaker.SneakerItem;
import prgrms.neoike.domain.sneaker.SneakerStock;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.repository.DrawTicketRepository;
import prgrms.neoike.repository.SneakerItemRepository;
import prgrms.neoike.service.converter.DrawConverter;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;

@Service
@RequiredArgsConstructor
public class DrawWinnnerService {

    private final DrawRepository drawRepository;
    private final DrawTicketRepository drawTicketRepository;
    private final SneakerItemRepository sneakerItemRepository;

    @Transactional
    public DrawTicketsResponse drawWinner(Long drawId) {
        Draw draw = drawRepository.findById(drawId)
            .orElseThrow(() -> new EntityNotFoundException(
                format("Draw 엔티티를 찾을 수 없습니다. drawId : {0}", drawId)));

        List<SneakerItem> sneakerItems = sneakerItemRepository.findByDraw(draw);
        List<DrawTicket> drawTickets = drawTicketRepository.findByDraw(draw);

        List<DrawTicketResponse> successDrawTickets = new ArrayList<>();
        Map<Integer, SneakerItem> sizeToSneakerItem = new HashMap<>();

        sneakerItems.forEach(
            sneakerItem -> sizeToSneakerItem.put(sneakerItem.getSize(), sneakerItem));

        for (Map.Entry<Integer, SneakerItem> entry : sizeToSneakerItem.entrySet()) {
            Integer size = entry.getKey();
            SneakerItem sneakerItem = entry.getValue();

            List<DrawTicket> ticketsBySize = drawTickets.stream()
                .filter(drawTicket -> drawTicket.getSize() == size)
                .toList();

            drawWinnerBySize(successDrawTickets, sneakerItem, ticketsBySize);
        }

        drawTickets.forEach(DrawTicket::drawQuit);

        return new DrawTicketsResponse(successDrawTickets);
    }

    private List<DrawTicketResponse> drawWinnerBySize(
        List<DrawTicketResponse> successDrawTickets,
        SneakerItem sneakerItem,
        List<DrawTicket> ticketsBySize
    ) {
        int ticketQuantity = ticketsBySize.size();

        if (sneakerItem.isLargerOrEqualThan(ticketQuantity)) {
            ticketsBySize.forEach(DrawTicket::changeToWinner);
            ticketsBySize.forEach(drawTicket -> {
                    drawTicket.changeToWinner();
                    successDrawTickets.add(DrawConverter.toDrawTicketResponse(drawTicket));
                }
            );

            // sneakerItem 의 재고를 감소시키고
            sneakerItem.reduceQuantity(ticketQuantity);
            SneakerStock sneakerStock = sneakerItem.getSneakerStock();
            // 감소하고 남은 개수는 다시 SneakerStock 재고에 추가한다.
            sneakerStock.addQuantity(sneakerItem.getQuantity());
            // 재고 추가 후 최종 개수는 0이다.
            sneakerItem.changeQuantityZero();

            return successDrawTickets;
        }

        Set<Integer> randomSet = RandomTicketIdCreator
            .noDuplicationIdSet(sneakerItem.getQuantity(), ticketsBySize.size());

        randomSet.forEach(id -> {
                DrawTicket winTicket = ticketsBySize.get(id);
                winTicket.changeToWinner();
                successDrawTickets.add(DrawConverter.toDrawTicketResponse(winTicket));
            }
        );
        // sneakerItem 의 재고를 0 으로 바꾼다.
        sneakerItem.changeQuantityZero();

        return successDrawTickets;
    }
}
