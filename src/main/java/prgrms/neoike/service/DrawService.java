package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.common.util.RandomCreator;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.domain.draw.DrawTicket;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.repository.DrawTicketRepository;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;
import prgrms.neoike.service.converter.DrawConverter;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketListResponse;
import prgrms.neoike.service.dto.drawticketdto.DrawTicketResponse;

import java.util.*;

import static java.text.MessageFormat.format;

@Service
@RequiredArgsConstructor
public class DrawService {
    private final DrawRepository drawRepository;
    private final DrawTicketRepository drawTicketRepository;
    private final DrawConverter drawConverter;

    @Transactional
    public DrawResponse save(ServiceDrawSaveDto drawSaveRequest) {
        Draw draw = drawConverter.toDraw(drawSaveRequest);
        Draw savedDraw = drawRepository.save(draw);

        return drawConverter.toDrawResponseDto(savedDraw.getId());
    }

    @Transactional
    public DrawTicketListResponse drawWinner(Long drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new EntityNotFoundException(format("Draw 엔티티를 id 로 찾을 수 없습니다. drawId : {0}", drawId)));

        // 신발 재고 가져오기
        // SneakerItem sneakerItem =  SneakerItemRepository.findByDraw(draw);
        // int sneakerItemQuantity = sneakerItem.getQuantity();
        int sneakerItemQuantity = 3; // 임의 설정

        List<DrawTicket> draws = drawTicketRepository.findByDraw(draw);
        List<DrawTicketResponse> successDraws = new ArrayList<>();
        int[] randomArray = RandomCreator.makeRandomArray(sneakerItemQuantity, draws.size());

        for (int winId : randomArray) {
            DrawTicket winTicket = draws.get(winId);
            winTicket.changeToWinner();
            draws.set(winId, winTicket);
            successDraws.add(drawConverter.toDrawTicketResponse(winTicket.getId()));

            // 당첨자에게 당첨되었다는 알람 전송
            // 로직구현
        }

        // 나머지 인원들의 당첨 대기를 당첨 실패로 변경한다.
        for (DrawTicket drawTicket : draws) {
            drawTicket.drawQuit();
        }

        return new DrawTicketListResponse(successDraws);
    }
}