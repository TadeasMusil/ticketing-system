package tadeas_musil.ticketing_system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {

	@RequestMapping("/")
	public String showHomepage() {

		return "index";
    }
  

}