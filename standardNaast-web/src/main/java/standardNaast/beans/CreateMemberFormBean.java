package standardNaast.beans;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.Personne;
import standardNaast.service.PersonneServiceImpl;

@Named(value = "createMemberForm")
@RequestScoped
public class CreateMemberFormBean implements Serializable {

	private Personne personne;

	@Inject
	private PersonneServiceImpl personneService;

	public Personne getPersonne() {
		if (this.personne == null) {
			this.personne = new Personne();
		}
		return this.personne;
	}

	public void setPersonne(final Personne personne) {
		this.personne = personne;
	}

}
