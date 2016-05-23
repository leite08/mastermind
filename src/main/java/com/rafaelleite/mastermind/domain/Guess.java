package com.rafaelleite.mastermind.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Guess {

	@Id
	@GeneratedValue
	@JsonIgnore
	private Long id;

	@Column(nullable = false)
	private String guess;

	// If in multiplayer mode, holds the user who made this guess
	@Column(nullable = true)
	private String userName;

	@Column(nullable = false)
	@JsonIgnore
	private LocalDateTime createdAt;

	@Column
	private int exact;

	@Column
	private int near;
	
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	@JoinColumn(name = "game_id", foreignKey=@ForeignKey(name="FK_GAME_GUESS"))
	@JsonBackReference("past_results")
	@JsonIgnore
	private Game game;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGuess() {
		return guess;
	}

	public void setGuess(String guess) {
		this.guess = guess;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public int getExact() {
		return exact;
	}

	public void setExact(int exact) {
		this.exact = exact;
	}

	public int getNear() {
		return near;
	}

	public void setNear(int near) {
		this.near = near;
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
