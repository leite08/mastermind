package com.rafaelleite.mastermind.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rafaelleite.mastermind.MasterMindApplication;

@RestController
public class HomeController {

	private static final Logger log = LoggerFactory.getLogger(MasterMindApplication.class);
	
	@RequestMapping(name="/", method = RequestMethod.GET)
	String home() {
		log.debug("Home running...");
		return "<h1>Master Mind Game - API implementation on REST services</h1>"
				+ "<br><br>"
				+ "This version is only available through REST requisitions using Json as data format."
				+ "<br><br>"
				+ "<strong>Author:</strong> Rafael Leite - leite08@gmail.com";
	}
}
