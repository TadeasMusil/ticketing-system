package tadeas_musil.ticketing_system.controller;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.querydsl.core.types.Predicate;

import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.repository.bindings.UserListBindings;
import tadeas_musil.ticketing_system.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@GetMapping
	public String showUsers(Model model, @PageableDefault(sort = "lastName") Pageable pageable,
			@QuerydslPredicate(root = User.class, bindings = UserListBindings.class) Predicate predicate, Authentication a) {
a.getAuthorities().forEach(au -> System.out.println(au.getAuthority()));
		model.addAttribute("page", userService.getAllByRole("USER", predicate, pageable));
		model.addAttribute("user", new User());
		return "user-list/user-list";
	}

	@PostMapping("/{id}")
	@PreAuthorize("hasPermission(@userServiceImpl.getById(#user.getId()), 'edit_account_status')")
	public String updateAccountStatus(@ModelAttribute User user, HttpServletRequest request) {
		userService.updateAccountStatus(user.getId(), user.isDisabled());
		return "redirect:" + request.getHeader("Referer");
	}
}