package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;
import prgrms.neoike.service.converter.DrawConverter;

import static java.text.MessageFormat.format;


@Service
@RequiredArgsConstructor
@Transactional
public class DrawService {
    private final DrawRepository drawRepository;
    private final DrawConverter drawConverter;

    public DrawResponse save(ServiceDrawSaveDto drawSaveRequest) {
        Draw draw = drawConverter.toDraw(drawSaveRequest);
        Draw savedDraw = drawRepository.save(draw);
        return drawConverter.toDrawResponseDto(savedDraw.getId());
    }

    @Transactional(readOnly = true)
    public DrawResponse findById(Long drawId) {
        Draw draw = drawRepository.findById(drawId)
                .orElseThrow(() -> new EntityNotFoundException(format("Draw 엔티티를 id 로 찾을 수 없습니다. drawId : {0}", drawId)));
        Draw savedDraw = drawRepository.save(draw);
        return drawConverter.toDrawResponseDto(savedDraw.getId());
    }
}