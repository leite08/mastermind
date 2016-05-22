package com.rafaelleite.mastermind.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rafaelleite.mastermind.domain.Config;

public interface ConfigRepository extends JpaRepository<Config, Long> {

}
