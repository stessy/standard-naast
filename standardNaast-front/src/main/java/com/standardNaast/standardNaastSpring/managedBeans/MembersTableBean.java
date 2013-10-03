package com.standardNaast.standardNaastSpring.managedBeans;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.Personne;
import standardNaast.service.PersonneService;

@Named(value = "membersTable")
@SessionScoped
public class MembersTableBean implements Serializable {

	private List<Personne> members;

	@Inject
	private PersonneService personneService;

	// @PostConstruct
	// public void init() {
	// if (this.members == null) {
	// List<Personne> findAllPerson = this.personneService.findAllPerson();
	// Collections.sort(findAllPerson);
	// this.members = findAllPerson;
	// }
	// }

	public List<Personne> getMembers() {
		if (this.members == null) {
			List<Personne> findAllPerson = this.personneService.findAllPerson();
			Collections.sort(findAllPerson);
			this.members = findAllPerson;
		}
		return this.members;
	}

}
