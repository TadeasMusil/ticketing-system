package tadeas_musil.ticketing_system.controller;

import javax.mail.MessagingException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import tadeas_musil.exception.InvalidPasswordResetTokenException;
import tadeas_musil.ticketing_system.entity.PasswordResetToken;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.entity.User.ChangePassword;
import tadeas_musil.ticketing_system.service.TokenService;
import tadeas_musil.ticketing_system.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/password-reset")
public class PasswordResetController {

  private final UserService userService;

  private final TokenService<PasswordResetToken> passwordResetTokenService;

  @PostMapping("/{id}")
  public String updatePassword(@Validated(ChangePassword.class) @ModelAttribute User user) {
    userService.updatePassword(user);
    return "redirect:/login";
  }

  @GetMapping
  public String showPasswordResetForm(PasswordResetToken token, Model model) {
    if (passwordResetTokenService.validateToken(token)) {
      model.addAttribute("user", passwordResetTokenService.getByToken(token.getToken())
                                                          .getUser());
      return "password-reset";
    }
    throw new InvalidPasswordResetTokenException();
  }

  @GetMapping("/request")
  public String showPasswordResetRequestForm(Model model) {
    return "password-reset-request";
  }

  @PostMapping("/request")
  public String requestPasswordReset(@RequestParam String email, Model model) throws MessagingException {
    userService.sendPasswordResetLink(email);
    model.addAttribute("emailSent", true);
    return "password-reset-request";
  }

}
