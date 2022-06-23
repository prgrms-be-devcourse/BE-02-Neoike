package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.repository.DrawRepository;
import prgrms.neoike.service.dto.drawdto.DrawResponse;
import prgrms.neoike.service.dto.drawdto.ServiceDrawSaveDto;
import prgrms.neoike.service.converter.DrawConverter;

@Service
@RequiredArgsConstructor
public class DrawService {
    private final DrawRepository drawRepository;
    private final DrawConverter drawConverter;

    @Transactional
    public DrawResponse save(ServiceDrawSaveDto drawSaveRequest) {
        Draw draw = drawConverter.toDraw(drawSaveRequest);
        Draw savedDraw = drawRepository.save(draw);

        return drawConverter.toDrawResponseDto(savedDraw.getId());
    }
}