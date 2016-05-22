package com.rafaelleite.mastermind.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Game {

	@Id
	@GeneratedValue
	@JsonIgnore
	private Long id;

	@Column(nullable = false, unique = true, name = "game_key")
	private String key;
	
	@Column(nullable = false)
	@JsonIgnore
	private String solution;
	
	@ElementCollection
	@JsonProperty("colors")
	private Collection<Character> options = new ArrayList<>();
	
	@Column(nullable = false)
	@JsonIgnore
	private LocalDateTime createdAt;
	
	@Column(nullable = false)
	@JsonProperty("user")
	private String userName;

	/*
	 * As stated in Google JSON Style Guide (https://google.github.io/styleguide/jsoncstyleguide.xml):
	 * "... string must be surrounded by double quotes. Other value types (like boolean ...) should not be surrounded by double quotes."
	 */
	@Column(columnDefinition="INT(1)")
	private boolean solved;
	
	/*
	 * Time taken for the user to win the game, IN SECONDS.
	 */
	@Column
	@JsonIgnore
	private long timeTaken;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "game")
	@Fetch(FetchMode.JOIN)
	@JsonIgnore
	private Collection<Guess> guesses = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Collection<Character> getOptions() {
		return options;
	}

	public void setOptions(Collection<Character> options) {
		this.options = options;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String val) {
		this.userName = val;
	}

	@JsonProperty("num_guesses")
	public int getNumGuesses() {
		return (guesses==null ? 0 : guesses.size());
	}

	public boolean isSolved() {
		return solved;
	}

	public void setSolved(boolean solved) {
		this.solved = solved;
	}

	public Collection<Guess> getGuesses() {
		return guesses;
	}

	public void setGuesses(Collection<Guess> guesses) {
		this.guesses = guesses;
	}

	public long getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(long timeTaken) {
		this.timeTaken = timeTaken;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

}
