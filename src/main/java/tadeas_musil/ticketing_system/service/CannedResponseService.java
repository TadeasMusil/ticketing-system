package tadeas_musil.ticketing_system.service;

import java.util.List;

import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.CannedResponse;

@Service
public interface CannedResponseService {

    List<CannedResponse> getAllResponses();

	CannedResponse getResponseByName(String name);

	CannedResponse saveResponse(CannedResponse cannedResponse);
}