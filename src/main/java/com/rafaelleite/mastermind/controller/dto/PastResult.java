package com.rafaelleite.mastermind.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PastResult extends Result {
	public String guess;
	public String user;

	public void setGuess(String guess) {
		this.guess = guess;
	}

	public void setUser(String user) {
		this.user = user;
	}
}