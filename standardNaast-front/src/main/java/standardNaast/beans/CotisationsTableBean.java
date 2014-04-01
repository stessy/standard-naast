package standardNaast.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import standardNaast.entities.Cotisation;
import standardNaast.entities.PersonneCotisation;
import standardNaast.service.PersonneCotisationsService;

@Controller("cotisationsTable")
@Scope("session")
public class CotisationsTableBean implements Serializable {

	private static final long serialVersionUID = -4747235749146019196L;

	private List<PersonneCotisation> personneCotisations = new ArrayList<>();

	private Cotisation selectedCotisation;

	@Autowired
	private PersonneCotisationsService cotisationService;

	// @PostConstruct
	// public void init() {
	// System.out.println("Initializing CotisationsTableBean");
	// List<PersonneCotisation> findAllCotisations = this.cotisationService
	// .findAllCotisations();
	// this.personneCotisations = findAllCotisations;
	// }

	public List<PersonneCotisation> getCotisations() {
		return this.personneCotisations;
	}

	public void setCotisations(
			final List<PersonneCotisation> personneCotisations) {
		this.personneCotisations = personneCotisations;
	}

	public Cotisation getSelectedCotisation() {
		return this.selectedCotisation;
	}

	public void setSelectedCotisation(final Cotisation selectedCotisation) {
		this.selectedCotisation = selectedCotisation;
	}

	public void onRowSelect(final SelectEvent event) {

	}
}
