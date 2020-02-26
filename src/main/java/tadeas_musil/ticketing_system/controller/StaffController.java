package tadeas_musil.ticketing_system.controller;

import com.querydsl.core.types.Predicate;

import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.repository.bindings.StaffMemberListBindings;
import tadeas_musil.ticketing_system.service.DepartmentService;
import tadeas_musil.ticketing_system.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/staff")
public class StaffController {

	private final UserService userService;

	private final DepartmentService departmentService;

	@GetMapping
	public String showStaffMembers(Model model,
			@QuerydslPredicate(root = User.class, bindings = StaffMemberListBindings.class) Predicate predicate,
			@PageableDefault(sort = "lastName") Pageable pageable) {

		model.addAttribute("page", userService.getAllByRole("STAFF", predicate, pageable));
		model.addAttribute("user", new User());
		model.addAttribute("departments", departmentService.getAllDepartments());
		return "user-list/staff-members";
	}
}