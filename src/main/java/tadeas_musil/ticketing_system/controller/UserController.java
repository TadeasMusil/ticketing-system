package tadeas_musil.ticketing_system.controller;

import javax.servlet.http.HttpServletRequest;

import com.querydsl.core.types.Predicate;

import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.repository.UserRepository;
import tadeas_musil.ticketing_system.service.TicketEventService;
import tadeas_musil.ticketing_system.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@RequestMapping("/search")
	public String searchUsers(Model model, @RequestParam(required = false) String query, @RequestParam(defaultValue = "0") int page, @QuerydslPredicate(root = User.class, bindings = UserRepository.class) Predicate predicate) {
		model.addAttribute("slice", userService.getAll(query, predicate, page));
		return "user-list/user-list";
	}

	@GetMapping
	public String showUsers(Model model, @RequestParam(defaultValue = "0") int page, @QuerydslPredicate(root = User.class, bindings = UserRepository.class) Predicate predicate) {
		model.addAttribute("user", new User());
		model.addAttribute("slice", userService.getAll(null, predicate, page));
		return "user-list/user-list";
	}

	@PostMapping("/{id}")
	@PreAuthorize("hasPermission(@userServiceImpl.getById(#user.getId()), 'edit_account_status')")
	public String updateAccountStaus(@ModelAttribute User user, HttpServletRequest request) {
		userService.updateAccountStatus(user.getId(), user.isDisabled());
		return "redirect:" + request.getHeader("Referer");
	}
}