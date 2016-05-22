package com.rafaelleite.mastermind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MasterMindApplication {

	private static final Logger log = LoggerFactory.getLogger(MasterMindApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MasterMindApplication.class, args);
	}
}
