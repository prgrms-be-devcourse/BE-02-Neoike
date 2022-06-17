package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import prgrms.neoike.repository.SneakerRepository;

@Service
@RequiredArgsConstructor
public class SneakerService {

    private final SneakerRepository sneakerRepository;
}