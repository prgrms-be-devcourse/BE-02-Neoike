package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.common.exception.EntityNotFoundException;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerImage;
import prgrms.neoike.domain.sneaker.SneakerStock;
import prgrms.neoike.repository.SneakerRepository;
import prgrms.neoike.repository.SneakerStockRepository;
import prgrms.neoike.service.converter.SneakerConverter;
import prgrms.neoike.service.dto.page.PageResponse;
import prgrms.neoike.service.dto.page.PageableDto;
import prgrms.neoike.service.dto.sneaker.*;

import javax.persistence.EntityExistsException;
import java.util.List;

import static java.text.MessageFormat.format;
import static prgrms.neoike.service.converter.PageConverter.toPageable;
import static prgrms.neoike.service.converter.PageConverter.toSneakerResponses;
import static prgrms.neoike.service.converter.SneakerConverter.*;

@Service
@RequiredArgsConstructor
public class SneakerService {

    private final SneakerRepository sneakerRepository;
    private final SneakerStockRepository sneakerStockRepository;

    @Transactional
    public SneakerIdResponse registerSneaker(SneakerRegisterDto registerDto) {
        validateDuplicatedSneaker(registerDto.sneakerDto().code());

        Sneaker retrievedSneaker = saveSneaker(registerDto);
        saveSneakerStocks(registerDto, retrievedSneaker);

        return toSneakerIdResponse(retrievedSneaker.getId(), retrievedSneaker.getCode());
    }

    @Transactional(readOnly = true)
    public SneakerDetailResponse getSneakerDetail(SneakerDetailDto detailDto) {
        List<SneakerStock> sneakerStocks = validateEmptySneakerDetails(detailDto);

        return toSneakerDetailResponse(sneakerStocks);
    }

    @Transactional(readOnly = true)
    public PageResponse<SneakerResponse> getSneakers(PageableDto pageableDto) {
        Page<Sneaker> sneakers = sneakerRepository.findAll(toPageable(pageableDto));

        return toSneakerResponses(sneakers);
    }

    private List<SneakerStock> validateEmptySneakerDetails(SneakerDetailDto detailDto) {
        List<SneakerStock> sneakerStocks = sneakerStockRepository
            .findByIdAndCode(detailDto.sneakerId(), detailDto.code());

        if (sneakerStocks.isEmpty()) {
            throw new EntityNotFoundException(
                format("요청한 신발 정보를 찾을 수 없습니다. (신발코드: {0})", detailDto.code())
            );
        }

        return sneakerStocks;
    }

    private void saveSneakerStocks(SneakerRegisterDto registerDto, Sneaker retrievedSneaker) {
        List<SneakerStock> sneakerStocks = toSneakerStockEntities(registerDto.stockDto());
        sneakerStocks.forEach(s -> s.setSneaker(retrievedSneaker));
        sneakerStockRepository.saveAll(sneakerStocks);
    }

    private Sneaker saveSneaker(SneakerRegisterDto registerDto) {
        List<SneakerImage> sneakerImages = SneakerConverter.toSneakerImageEntity(registerDto.imageDto());
        Sneaker sneaker = toSneakerEntity(registerDto.sneakerDto());
        sneaker.attachImages(sneakerImages);

        return sneakerRepository.save(sneaker);
    }

    private void validateDuplicatedSneaker(String code) {
        sneakerRepository
            .findByCode(code)
            .ifPresent(
            sneaker -> {
                throw new EntityExistsException(
                    format("동일한 신발이 이미 존재합니다. (신발코드: {0})", code)
                );
            }
        );
    }
}