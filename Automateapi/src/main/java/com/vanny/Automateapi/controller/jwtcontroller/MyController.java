package com.vanny.Automateapi.controller.jwtcontroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {
	@GetMapping("/home")
	public String home() {
		 return "redirect:/index.html";
		 }
}
