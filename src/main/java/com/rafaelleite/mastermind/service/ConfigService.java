package com.rafaelleite.mastermind.service;

import static java.lang.String.format;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rafaelleite.mastermind.domain.Config;
import com.rafaelleite.mastermind.repository.ConfigRepository;

@Service
public class ConfigService {

	private static final Logger log = LoggerFactory.getLogger(ConfigService.class);

	private final ConfigRepository repo;
	
	// TODO: move to second-level caching, this design can be problematic when we 
	// have multiple instances of the application
	private Config config;

	@Autowired
	public ConfigService(ConfigRepository value) {
		repo = value;
	}

	public Config getConfig() {
		if (config == null) {
			reloadConfig();
		}
		return config;
	}
	
	public Config reloadConfig() {
		List<Config> configs = repo.findAll();
		if (configs == null || configs.isEmpty()) {
			log.error("There is no configuration in the repository!");
			return null;
		}
		if (configs.size() > 1) {
			log.warn(format("There is more than one configuration in the repository (%d configs)."
					+ " Choosing the first.", configs.size()));
		}
		config = configs.get(0);
		return config;
	}
}
