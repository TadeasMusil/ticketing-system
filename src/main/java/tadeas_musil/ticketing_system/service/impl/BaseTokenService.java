package tadeas_musil.ticketing_system.service.impl;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.BaseToken;
import tadeas_musil.ticketing_system.helper.LocalDateTimeHelper;
import tadeas_musil.ticketing_system.service.TokenService;

@RequiredArgsConstructor
public abstract class BaseTokenService<T extends BaseToken> implements TokenService<T> {

  private final LocalDateTimeHelper localDateTimeHelper;

  protected LocalDateTime createExpiryDate(int durationInHours) {
    return localDateTimeHelper.getCurrentDateTime()
                              .plusHours(durationInHours);
  }

  protected boolean tokenNotExpired(LocalDateTime expiryDate) {
    if (localDateTimeHelper.getCurrentDateTime()
                           .compareTo(expiryDate) > 0) {
      return false;
    }
    return true;
  }
}
