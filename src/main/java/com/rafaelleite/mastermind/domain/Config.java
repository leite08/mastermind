package com.rafaelleite.mastermind.domain;

import java.util.Collection;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Config {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ElementCollection(fetch=FetchType.EAGER)
	private Collection<Character> options;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Collection<Character> getOptions() {
		return options;
	}

	public void setOptions(Collection<Character> options) {
		this.options = options;
	}
	
	
}
