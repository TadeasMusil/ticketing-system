package tadeas_musil.ticketing_system.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import tadeas_musil.ticketing_system.entity.CannedResponse;

@DataJpaTest
public class CannedResponseRepositoryTest {

    @Autowired
    private CannedResponseRepository cannedResponseRepository;

    @Test
    public void findAllByOrderByNameAsc_shouldReturnCannedResponsesInCorrectOrder() {
        CannedResponse firstCannedResponse = new CannedResponse();
        firstCannedResponse.setName("first");
        CannedResponse secondCannedResponse = new CannedResponse();
        secondCannedResponse.setName("second");
        CannedResponse thirdCannedResponse = new CannedResponse();
        thirdCannedResponse.setName("third");
        cannedResponseRepository.saveAll(Set.of(thirdCannedResponse, secondCannedResponse, firstCannedResponse));

        List<CannedResponse> cannedResponses = cannedResponseRepository.findAllByOrderByNameAsc();

        assertThat(cannedResponses)
        .hasSize(3)
        .startsWith(firstCannedResponse, secondCannedResponse, thirdCannedResponse);                        
    }

    @Test
    public void findByName_shouldFindNothing_givenNonExistingResponse() {
        CannedResponse cannedResponse = new CannedResponse();
        cannedResponse.setName("name");
        cannedResponseRepository.save(cannedResponse);

        Optional<CannedResponse> result = cannedResponseRepository.findByName("wrongName");

        assertThat(result).isNotPresent();                      
    }

    @Test
    public void findByName_shouldFindResponse_givenExistingResponse() {
        CannedResponse cannedResponse = new CannedResponse();
        cannedResponse.setName("name");
        cannedResponseRepository.save(cannedResponse);

        Optional<CannedResponse> result = cannedResponseRepository.findByName("name");

        assertThat(result).isPresent();                          
    }

}
