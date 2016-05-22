package com.rafaelleite.mastermind.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rafaelleite.mastermind.domain.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

	Game findByKey(String gameKey);

}
