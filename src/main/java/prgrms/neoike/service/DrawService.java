package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerItem;
import prgrms.neoike.domain.sneaker.SneakerStock;
import prgrms.neoike.domain.sneaker.Stock;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.repository.SneakerItemRepository;
import prgrms.neoike.repository.SneakerRepository;
import prgrms.neoike.repository.SneakerStockRepository;
import prgrms.neoike.service.converter.DrawConverter;
import prgrms.neoike.service.dto.drawdto.DrawDto;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.DrawSaveDto;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static java.text.MessageFormat.format;

@Service
@RequiredArgsConstructor
public class DrawService {

    private final DrawRepository drawRepository;
    private final SneakerRepository sneakerRepository;
    private final SneakerItemRepository sneakerItemRepository;
    private final SneakerStockRepository sneakerStockRepository;

    @Transactional
    @CacheEvict(value = "draws", allEntries = true)
    public DrawResponse save(DrawSaveDto drawSaveRequest) {
        Long sneakerId = drawSaveRequest.sneakerId();
        Sneaker sneaker = sneakerRepository.findById(sneakerId)
            .orElseThrow(() -> new EntityNotFoundException(
                format("Sneaker 엔티티를 찾을 수 없습니다. drawId : {0}", sneakerId)));

        Draw draw = DrawConverter.toDraws(drawSaveRequest, sneaker);
        drawRepository.save(draw);

        // 해당 sneakerId 에 해당하는 SneakerItem 들을 생성한 후 저장한다.
        saveSneakerItem(drawSaveRequest, sneakerId, sneaker, draw);

        return DrawConverter.toDrawResponse(draw.getId());
    }

    private void saveSneakerItem(DrawSaveDto drawSaveRequest, Long sneakerId, Sneaker sneaker, Draw draw) {
        drawSaveRequest.sneakerStocks().forEach(
            sneakerStock -> {
                int size = sneakerStock.size();
                SneakerStock foundSneakerStock = sneakerStockRepository.findBySneakerAndSize(sneaker, size)
                    .orElseThrow(() -> new EntityNotFoundException(
                        format(
                            "SneakerStock 엔티티를 찾을 수 없습니다. sneakerId : {0}, size : {1}",
                            sneakerId, size)));

                // SneakerStock 에서 재고를 가지고와 SneakerItem 을 만든다.
                Stock stock = foundSneakerStock.getStock();
                stock.decreaseQuantity(sneakerStock.quantity());
                sneakerStockRepository.flush();

                sneakerItemRepository.save(
                    SneakerItem.builder()
                        .sneakerStock(foundSneakerStock)
                        .quantity(sneakerStock.quantity())
                        .draw(draw)
                        .size(size)
                        .build()
                );
            }
        );
    }

    @Cacheable(value = "draws")
    @Transactional(readOnly = true)
    public List<DrawDto> getAvailableDraws() {
        List<Draw> availableDraws = drawRepository.findAllByWinningDateAfter(LocalDateTime.now());

        return DrawConverter.toDrawDtos(availableDraws);
    }
}