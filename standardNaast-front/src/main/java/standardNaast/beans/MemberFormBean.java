package standardNaast.beans;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import standardNaast.entities.Personne;
import standardNaast.service.PersonneService;

@Controller("memberForm")
@Scope("session")
public class MemberFormBean implements Serializable {

	private Personne personne;

	@Inject
	@Named("personneService")
	private PersonneService personneService;

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
