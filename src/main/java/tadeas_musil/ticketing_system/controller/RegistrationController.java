package tadeas_musil.ticketing_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.entity.User.Registration;
import tadeas_musil.ticketing_system.service.UserService;


@Controller
public class RegistrationController {
	
	@Autowired
	private UserService userService;

	@RequestMapping("/registration")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		return "registration";
	}

	@PostMapping("/processRegistration")
	public String processRegistration(@Validated(Registration.class) @ModelAttribute("user") User user,
			BindingResult bindingResult) {
		
		if (bindingResult.hasErrors()) {
			return "registration";
		}
		
		userService.createUser(user);
		return "index";
	}
}