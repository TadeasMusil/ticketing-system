package tadeas_musil.ticketing_system.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.CannedResponse;
import tadeas_musil.ticketing_system.repository.CannedResponseRepository;
import tadeas_musil.ticketing_system.service.CannedResponseService;

@Service
@RequiredArgsConstructor
public class CannedResponseServiceImpl implements CannedResponseService {

    private final CannedResponseRepository cannedResponseRepository;

    @Override
    public List<CannedResponse> getAllResponses() {
        return cannedResponseRepository.findAllByOrderByNameAsc();
    }

    @Override
    public CannedResponse getResponseByName(String name) {
        return cannedResponseRepository.findByName(name)
        .orElseThrow(() -> new IllegalArgumentException("Response not found"));

    }

    @Override
    public CannedResponse saveResponse(CannedResponse cannedResponse) {
        return cannedResponseRepository.save(cannedResponse);
    }
}
