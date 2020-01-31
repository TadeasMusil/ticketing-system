package tadeas_musil.ticketing_system.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import tadeas_musil.ticketing_system.entity.CannedResponse;
import tadeas_musil.ticketing_system.service.CannedResponseService;

@RequiredArgsConstructor
@Controller
@RequestMapping("/cannedResponse")
public class CannedResponseController {

	private final CannedResponseService cannedResponseService;

	@GetMapping(params = "name")
	@ResponseBody
	public CannedResponse getResponse(@RequestParam String name) {
		return cannedResponseService.getResponseByName(name);
	}

	@GetMapping("/form")
	public String showCannedResponseForm(Model model) {
		model.addAttribute("cannedResponses", cannedResponseService.getAllResponses());
		model.addAttribute("cannedResponse", new CannedResponse());
		return "canned-response-form";
	}

	@PostMapping("/form")
	public String proccessResponseForm(@Valid @ModelAttribute CannedResponse cannedResponse, BindingResult bindingResult, Model model) {
		if(bindingResult.hasErrors()){
			model.addAttribute("cannedResponses", cannedResponseService.getAllResponses());
			return "canned-response-form";
		}
		cannedResponseService.saveResponse(cannedResponse);
		return "redirect:/cannedResponse/form";
	}

}