package tadeas_musil.ticketing_system.service.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import tadeas_musil.ticketing_system.entity.PasswordResetToken;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.helper.LocalDateTimeHelper;
import tadeas_musil.ticketing_system.helper.UUIDHelper;
import tadeas_musil.ticketing_system.repository.PasswordResetTokenRepository;
import tadeas_musil.ticketing_system.repository.UserRepository;

@Service
public class PasswordResetTokenService extends BaseTokenService<PasswordResetToken> {

  private final PasswordResetTokenRepository passwordResetTokenRepository;

  private final UUIDHelper uuidHelper;

  private final UserRepository userRepository;

  private static int TOKEN_DURATION_HOURS = 24;

  public PasswordResetTokenService(LocalDateTimeHelper localDateTimeHelper,
      PasswordResetTokenRepository passwordResetTokenRepository, UUIDHelper uuidHelper, UserRepository userRepository) {
    super(localDateTimeHelper);
    this.passwordResetTokenRepository = passwordResetTokenRepository;
    this.uuidHelper = uuidHelper;
    this.userRepository = userRepository;
  }

  @Override
  public PasswordResetToken createToken(Long ticketId) {
    User user = userRepository.findById(ticketId)
                              .orElseThrow();
    PasswordResetToken token = new PasswordResetToken();
    token.setUser(user);
    token.setToken(uuidHelper.randomUUID());
    token.setExpiryDate(createExpiryDate(TOKEN_DURATION_HOURS));
    return passwordResetTokenRepository.save(token);
  }

  @Override
  public boolean validateToken(PasswordResetToken token) {
    Optional<PasswordResetToken> matchingToken = passwordResetTokenRepository.findByToken(token.getToken());

    if (matchingToken.isPresent()
        && tokenNotExpired(matchingToken.get()
                                        .getExpiryDate())) {
      return true;
    }
    return false;
  }

  public PasswordResetToken getByToken(UUID uuidToken) {
    return passwordResetTokenRepository.findByToken(uuidToken)
                                       .orElseThrow();

  }

}
