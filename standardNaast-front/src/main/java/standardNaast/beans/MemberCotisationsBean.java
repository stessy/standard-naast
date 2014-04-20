package standardNaast.beans;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.PersonneCotisation;
import standardNaast.service.CotisationsService;

//@Controller("memberCotisations")
//@Scope("session")
@Named(value = "memberCotisations")
@SessionScoped
public class MemberCotisationsBean implements Serializable {

	private List<PersonneCotisation> cotisations;

	private PersonneCotisation selectedCotisation;

	private boolean isUpdate;

	private PersonneCotisation personneCotisation;

	@Inject
	private CotisationsService cotisationService;

	public List<PersonneCotisation> getCotisations() {
		return this.cotisations;
	}

	public void setCotisations(List<PersonneCotisation> memberCotisations) {
		this.cotisations = memberCotisations;
	}

	public PersonneCotisation getSelectedCotisation() {
		return this.selectedCotisation;
	}

	public void setSelectedCotisation(PersonneCotisation selectedCotisation) {
		this.selectedCotisation = selectedCotisation;
	}

	public boolean isIsUpdate() {
		return this.isUpdate;
	}

	public void setIsUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public PersonneCotisation getPersonneCotisation() {
		return this.personneCotisation;
	}

	public void setPersonneCotisation(PersonneCotisation personneCotisation) {
		this.personneCotisation = personneCotisation;
	}

}
