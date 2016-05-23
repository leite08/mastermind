package com.rafaelleite.mastermind.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GuessRequest {
	
	public String code;
	public String game_key;
	public String user;

	public void setCode(String code) {
		this.code = code;
	}

	public void setGame_key(String game_key) {
		this.game_key = game_key;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
