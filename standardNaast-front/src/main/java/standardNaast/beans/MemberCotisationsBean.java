package standardNaast.beans;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import standardNaast.entities.PersonneCotisation;
import standardNaast.service.PersonneCotisationsService;

@Controller("memberCotisations")
@Scope("session")
public class MemberCotisationsBean {

	private List<PersonneCotisation> cotisations;

	private PersonneCotisation selectedCotisation;

	private boolean isUpdate;

	private PersonneCotisation personneCotisation;

	@Autowired
	private PersonneCotisationsService cotisationService;

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
