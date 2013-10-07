package com.standardNaast.standardNaastSpring.managedBeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.Personne;
import standardNaast.service.PersonneService;

@Named(value = "membersTable")
@RequestScoped
public class MembersTableBean implements Serializable {

    private List<Personne> members = new ArrayList<>();

    private List<Personne> filteredMembers = new ArrayList<>();

    @Inject
    private PersonneService personneService;

    @PostConstruct
    public void init() {
        List<Personne> findAllPerson = this.personneService.findAllPerson();
        Collections.sort(findAllPerson);
        this.members = findAllPerson;
        this.filteredMembers = findAllPerson;
    }

    public List<Personne> getMembers() {
        return this.members;
    }

    public List<Personne> getFilteredMembers() {
        return filteredMembers;
    }

    public void setFilteredMembers(List<Personne> filteredMembers) {
        this.filteredMembers = filteredMembers;
    }

}
