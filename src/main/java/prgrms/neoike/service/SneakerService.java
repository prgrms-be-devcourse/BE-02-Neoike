package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prgrms.neoike.domain.sneaker.Image;
import prgrms.neoike.domain.sneaker.Sneaker;
import prgrms.neoike.domain.sneaker.SneakerImage;
import prgrms.neoike.domain.sneaker.SneakerStock;
import prgrms.neoike.service.image.ImageFileManager;
import prgrms.neoike.repository.SneakerRepository;
import prgrms.neoike.repository.SneakerStockRepository;
import prgrms.neoike.service.dto.sneaker.SneakerRegisterDto;
import prgrms.neoike.service.dto.sneaker.SneakerResponse;

import javax.persistence.EntityExistsException;
import java.util.List;
import java.util.Set;

import static java.text.MessageFormat.format;
import static prgrms.neoike.service.mapper.SneakerConverter.toEntity;
import static prgrms.neoike.service.mapper.SneakerConverter.toResponse;

@Service
@RequiredArgsConstructor
public class SneakerService {

    private final SneakerRepository sneakerRepository;
    private final SneakerStockRepository sneakerStockRepository;
    private final ImageFileManager fileManager;

    @Transactional
    public SneakerResponse registerSneaker(SneakerRegisterDto registerDto) {
        validateDuplicatedSneaker(registerDto.sneakerDto().code(), registerDto.sneakerDto().name());

        Sneaker sneaker = saveSneaker(registerDto);
        saveSneakerStocks(registerDto, sneaker);

        return toResponse(sneaker.getId(), sneaker.getName(), sneaker.getCode());
    }

    private void saveSneakerStocks(SneakerRegisterDto registerDto, Sneaker sneaker) {
        List<SneakerStock> sneakerStocks = toEntity(registerDto.stockDtos());
        sneakerStocks.forEach(s -> s.setSneaker(sneaker));
        sneakerStockRepository.saveAll(sneakerStocks);
    }

    private Sneaker saveSneaker(SneakerRegisterDto registerDto) {
        Set<Image> images = fileManager.storeImages(registerDto.imageDtos());
        Set<SneakerImage> sneakerImages = toEntity(images);
        Sneaker sneaker = toEntity(registerDto.sneakerDto());
        sneakerImages.forEach(i -> i.uploadSneakerImage(sneaker));

        return sneakerRepository.save(sneaker);
    }

    private void validateDuplicatedSneaker(String code, String name) {
        sneakerRepository
            .findByCodeOrName(code, name)
            .ifPresent(
            sneaker -> {
                throw new EntityExistsException(
                    format("동일한 신발이 이미 존재합니다. (코드: {0}, 이름: {1})", code, name)
                );
            }
        );
    }
}