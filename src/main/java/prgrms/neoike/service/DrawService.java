package prgrms.neoike.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import prgrms.neoike.repository.DrawRepository;

@Service
@RequiredArgsConstructor
public class DrawService {

    private final DrawRepository drawRepository;
}