package standardNaast.beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.Personne;
import standardNaast.service.PersonneServiceImpl;

@Named(value = "memberForm")
@SessionScoped
public class MemberFormBean implements Serializable {

	private Personne personne;

	@Inject
	private PersonneServiceImpl personneService;

	@PostConstruct
	public void init() {
		this.personne = new Personne();
		System.out.println("Initializing MemberFormBean");
	}

	public void addMember(final ActionEvent actionEvent) {
		System.out.println("Action catched");
	}

	public Personne getPersonne() {
		return this.personne;
	}

	public void setPersonne(final Personne personne) {
		this.personne = personne;
	}

}
