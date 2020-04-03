package tadeas_musil.ticketing_system.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import tadeas_musil.ticketing_system.entity.BaseToken;

@Service
public interface TokenService<T extends BaseToken> {

  T createToken(Long id);

  boolean validateToken(T token);

  T getByToken(UUID token);

}
