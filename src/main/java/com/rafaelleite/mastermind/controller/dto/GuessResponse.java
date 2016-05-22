package com.rafaelleite.mastermind.controller.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuessResponse {
	public String game_key;
	public int num_guesses;
	public List<PastResult> past_results = new ArrayList<>();
	public String guess;
	public boolean solved;
	public long time_taken;
	public List<Character> colors = new ArrayList<>();
	public int code_length;
	public Result result;

	public String getGame_key() {
		return game_key;
	}

	public void setGame_key(String game_key) {
		this.game_key = game_key;
	}

	public int getNum_guesses() {
		return num_guesses;
	}

	public void setNum_guesses(int num_guesses) {
		this.num_guesses = num_guesses;
	}

	public List<PastResult> getPast_results() {
		return past_results;
	}

	public void setPast_results(List<PastResult> past_results) {
		this.past_results = past_results;
	}

	public String getGuess() {
		return guess;
	}

	public void setGuess(String guess) {
		this.guess = guess;
	}

	public boolean isSolved() {
		return solved;
	}

	public void setSolved(boolean solved) {
		this.solved = solved;
	}

	public long getTime_taken() {
		return time_taken;
	}

	public void setTime_taken(long time_taken) {
		this.time_taken = time_taken;
	}

	public List<Character> getColors() {
		return colors;
	}

	public void setColors(List<Character> colors) {
		this.colors = colors;
	}

	public int getCode_length() {
		return code_length;
	}

	public void setCode_length(int code_length) {
		this.code_length = code_length;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}
}