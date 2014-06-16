package standardNaast.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import standardNaast.entities.Cotisation;
import standardNaast.entities.PersonneCotisation;
import standardNaast.service.CotisationsService;

@Named(value = "cotisationsTable")
@SessionScoped
public class CotisationsTableBean implements Serializable {

	private static final long serialVersionUID = -4747235749146019196L;

	private List<PersonneCotisation> cotisations = new ArrayList<>();

	private Cotisation selectedCotisation;

	@Inject
	private CotisationsService cotisationService;

	@PostConstruct
	public void init() {

	}

	public List<PersonneCotisation> getCotisations() {
		return this.cotisations;
	}

	public void setCotisations(final List<PersonneCotisation> cotisations) {
		this.cotisations = cotisations;
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
