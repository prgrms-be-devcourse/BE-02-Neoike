package prgrms.neoike.service;

import static java.text.MessageFormat.format;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.common.util.RandomTicketIdCreator;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.draw.DrawTicket;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerItem;
import prgrms.neoike.domain.sneaker.SneakerStock;
import prgrms.neoike.domain.sneaker.Stock;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.repository.DrawTicketRepository;
import prgrms.neoike.repository.SneakerItemRepository;
import prgrms.neoike.repository.SneakerRepository;
import prgrms.neoike.repository.SneakerStockRepository;
import prgrms.neoike.service.converter.DrawConverter;
import prgrms.neoike.service.dto.drawdto.DrawDto;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.DrawSaveDto;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketsResponse;

@Service
@RequiredArgsConstructor
public class DrawService {
    private final DrawRepository drawRepository;
    private final DrawTicketRepository drawTicketRepository;
    private final SneakerRepository sneakerRepository;
    private final SneakerItemRepository sneakerItemRepository;
    private final SneakerStockRepository sneakerStockRepository;

    @Transactional
    @CacheEvict(value = "draws", allEntries = true)
    public DrawResponse save(DrawSaveDto drawSaveRequest) {
        Long sneakerId = drawSaveRequest.sneakerId();
        Sneaker sneaker = sneakerRepository.findById(sneakerId)
                .orElseThrow(() -> new EntityNotFoundException(format("Sneaker 엔티티를 id 로 찾을 수 없습니다. drawId : {0}", sneakerId)));

        Draw draw = DrawConverter.toDraw(drawSaveRequest, sneaker);
        drawRepository.save(draw);

        // 해당 sneakerId 에 해당하는 SneakerItem 들을 생성한 후 저장한다.
        saveSneakerItem(drawSaveRequest, sneakerId, sneaker, draw);

        return DrawConverter.toDrawResponseDto(draw.getId());
    }

    private void saveSneakerItem(DrawSaveDto drawSaveRequest, Long sneakerId, Sneaker sneaker, Draw draw) {
        drawSaveRequest.sneakerStocks().forEach(
                (sneakerItem) -> {
                    int size = sneakerItem.size();
                    SneakerStock sneakerStock = sneakerStockRepository.findBySneakerAndSize(sneaker, size)
                            .orElseThrow(() -> new EntityNotFoundException(
                                    format("SneakerStock 엔티티를 sneaker 와 size 로 찾을 수 없습니다. sneakerId : {0}, size : {1}", sneakerId, size)));

                    // SneakerStock 에서 재고를 가지고와 SneakerItem 을 만든다.
                    Stock stock = sneakerStock.getStock();
                    stock.decreaseQuantityBy(sneakerItem.quantity());
                    sneakerStockRepository.flush();

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
    }

    @Transactional
    public DrawTicketsResponse drawWinner(Long drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new EntityNotFoundException(
                    format("Draw 엔티티를 id 로 찾을 수 없습니다. drawId : {0}", drawId)));

        List<SneakerItem> sneakerItems = sneakerItemRepository.findByDraw(draw);
        List<DrawTicket> drawTickets = drawTicketRepository.findByDraw(draw);

        List<DrawTicketResponse> successDrawTickets = new ArrayList<>();
        Map<Integer, SneakerItem> sizeToSneakerItem = new HashMap<>();

        sneakerItems.forEach((sneakerItem) -> {
            sizeToSneakerItem.put(sneakerItem.getSize(), sneakerItem);
        });

        for (Map.Entry<Integer, SneakerItem> entry : sizeToSneakerItem.entrySet()) {
            Integer size = entry.getKey();
            SneakerItem sneakerItem = entry.getValue();
            List<DrawTicket> ticketsBySize = drawTickets.stream()
                    .filter((drawTicket) -> drawTicket.getSize() == size)
                    .toList();

            successDrawTickets = drawWinnerBySize(successDrawTickets, sneakerItem, ticketsBySize);
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
            ticketsBySize.forEach((drawTicket) -> {
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

        randomSet.forEach((id) -> {
                    DrawTicket winTicket = ticketsBySize.get(id);
                    winTicket.changeToWinner();
                    successDrawTickets.add(DrawConverter.toDrawTicketResponse(winTicket));
                }
        );
        // sneakerItem 의 재고를 0 으로 바꾼다.
        sneakerItem.changeQuantityZero();

        return successDrawTickets;
    }

    @Cacheable(value = "draws")
    @Transactional(readOnly = true)
    public List<DrawDto> getAvailableDraws() {
        List<Draw> availableDraws = drawRepository.findAllByWinningDateAfter(LocalDateTime.now());

        return DrawConverter.toDrawDtos(availableDraws);
    }
}