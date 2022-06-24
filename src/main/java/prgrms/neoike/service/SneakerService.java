package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerImage;
import prgrms.neoike.domain.sneaker.SneakerStock;
import prgrms.neoike.repository.SneakerRepository;
import prgrms.neoike.repository.SneakerStockRepository;
import prgrms.neoike.service.dto.sneaker.SneakerRegisterDto;
import prgrms.neoike.service.dto.sneaker.SneakerResponse;
import prgrms.neoike.service.mapper.SneakerConverter;

import javax.persistence.EntityExistsException;
import java.util.List;

import static java.text.MessageFormat.format;
import static prgrms.neoike.service.mapper.SneakerConverter.*;

@Service
@RequiredArgsConstructor
public class SneakerService {

    private final SneakerRepository sneakerRepository;
    private final SneakerStockRepository sneakerStockRepository;

    @Transactional
    public SneakerResponse registerSneaker(SneakerRegisterDto registerDto) {
        validateDuplicatedSneaker(registerDto.sneakerDto().code());

        Sneaker retrievedSneaker = saveSneaker(registerDto);
        saveSneakerStocks(registerDto, retrievedSneaker);

        return toSneakerResponse(retrievedSneaker.getId(), retrievedSneaker.getCode());
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
                    format("동일한 신발이 이미 존재합니다. (코드: {0})", code)
                );
            }
        );
    }
}