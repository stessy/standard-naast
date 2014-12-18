package standardNaast.beans;

import java.io.Serializable;

import javax.faces.event.ActionEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.Personne;
import standardNaast.service.PersonneServiceImpl;

@Named(value = "memberForm")
@ViewScoped
public class MemberFormBean implements Serializable {

	private Personne person = new Personne();

	private Personne selectedPerson;

	private boolean update;

	@Inject
	private PersonneServiceImpl personneService;

	public void addMember(final ActionEvent actionEvent) {
		System.out.println("Action catched");
	}

	public Personne getPersonne() {
		return this.person;
	}

	public void setPersonne(final Personne person) {
		this.person = person;
	}

	public void updateMember(final Personne personne) {
		this.personneService.savePerson(personne);
	}

	// public void openDialogForNewMember() {
	// this.person = new Personne();
	// final RequestContext requestContext = RequestContext
	// .getCurrentInstance();
	// requestContext.execute("PF('createMemberDlg').show()");
	// }

	public boolean isUpdate() {
		return this.update;
	}

	public void setUpdate(final boolean update) {
		this.update = update;
	}

	public Personne getSelectedPerson() {
		return this.selectedPerson;
	}

	public void setSelectedPerson(final Personne selectedPerson) {
		this.selectedPerson = selectedPerson;
	}

}
