package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.domain.draw.Draw;
import prgrms.neoike.repository.DrawRepository;


@Service
@RequiredArgsConstructor
@Transactional
public class DrawService {
    private final DrawRepository drawRepository;

    public Long save(Draw draw) {
        Draw savedDraw = drawRepository.save(draw);
        return savedDraw.getId();
    }

    @Transactional(readOnly = true)
    public Draw findById(Long drawId) {
        return drawRepository.findById(drawId)
                .orElseThrow(() -> new EntityNotFoundException("Draw 엔티티를 id 로 찾을 수 없습니다 : drawId : " + drawId));
    }
}