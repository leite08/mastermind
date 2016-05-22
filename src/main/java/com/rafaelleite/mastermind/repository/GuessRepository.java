package com.rafaelleite.mastermind.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rafaelleite.mastermind.domain.Guess;

public interface GuessRepository extends JpaRepository<Guess, Long> {


}
