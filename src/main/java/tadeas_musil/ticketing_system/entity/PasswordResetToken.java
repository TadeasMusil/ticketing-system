package tadeas_musil.ticketing_system.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class PasswordResetToken extends BaseToken {

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

}
