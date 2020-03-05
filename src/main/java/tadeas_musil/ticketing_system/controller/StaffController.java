package tadeas_musil.ticketing_system.controller;

import com.querydsl.core.types.Predicate;

import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.Department;
import tadeas_musil.ticketing_system.entity.User;
import tadeas_musil.ticketing_system.entity.User.UpdateStaffMember;
import tadeas_musil.ticketing_system.repository.bindings.StaffMemberListBindings;
import tadeas_musil.ticketing_system.service.DepartmentService;
import tadeas_musil.ticketing_system.service.StaffService;
import tadeas_musil.ticketing_system.service.UserService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/staff")
public class StaffController {

	private final UserService userService;

	private final DepartmentService departmentService;

	private final StaffService staffService;

	@GetMapping
	public String showStaffMembers(Model model,
			@QuerydslPredicate(root = User.class, bindings = StaffMemberListBindings.class) Predicate predicate,
			@PageableDefault(sort = "lastName") Pageable pageable) {

		model.addAttribute("page", userService.getAllByRole("STAFF", predicate, pageable));
		model.addAttribute("user", new User());
		model.addAttribute("departments", departmentService.getAllDepartments());
		return "user-list/staff-members";
	}

	@GetMapping("/{id}")
	public String showStaffMember(Model model, @PathVariable Long id) {

		model.addAttribute("staffMember", userService.getById(id));
		model.addAttribute("departments", departmentService.getAllDepartments());
		return "fragments/create-ticket";
	}

	@PostMapping("/{id}/department/add")
	@ResponseBody
	public void addToDepartment(@PathVariable Long id, @RequestBody Department department) {
		staffService.addToDepartment(id, department);
	}

	@PostMapping("/{id}/department/remove")
	@ResponseBody
	public void removeFromDepartment(@PathVariable Long id, @RequestBody Department department) {
		staffService.removeFromDepartment(id, department);
	}

	@PostMapping("/{id}")
	public String updateStaffMember(@Validated(UpdateStaffMember.class) @ModelAttribute("staffMember") User staffMember,
			BindingResult bindingResult) {
		if (!bindingResult.hasErrors()) {
			staffService.updateStaffMember(staffMember);
		}
		return "redirect:/staff/" + staffMember.getId();
	}

}