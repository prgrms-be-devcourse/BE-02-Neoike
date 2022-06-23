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
import static prgrms.neoike.service.mapper.SneakerConverter.toEntity;
import static prgrms.neoike.service.mapper.SneakerConverter.toResponse;

@Service
@RequiredArgsConstructor
public class SneakerService {

    private final SneakerRepository sneakerRepository;
    private final SneakerStockRepository sneakerStockRepository;

    @Transactional
    public SneakerResponse registerSneaker(SneakerRegisterDto registerDto) {
        validateDuplicatedSneaker(registerDto.sneakerDto().code());

        List<SneakerImage> sneakerImages = toEntity(registerDto.imageDto());
        Sneaker sneaker = toEntity(registerDto.sneakerDto());
        sneaker.attachImages(sneakerImages);

        Sneaker retrievedSneaker = sneakerRepository.save(sneaker);
        List<SneakerStock> sneakerStocks = registerDto.stockDto().stream().map(SneakerConverter::toEntity).toList();
        sneakerStocks.forEach(s -> s.setSneaker(retrievedSneaker));
        sneakerStockRepository.saveAll(sneakerStocks);

        return toResponse(retrievedSneaker.getId(), retrievedSneaker.getCode());
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