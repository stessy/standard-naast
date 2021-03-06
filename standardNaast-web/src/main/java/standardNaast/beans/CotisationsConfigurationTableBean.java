package standardNaast.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import standardNaast.entities.Cotisation;
import standardNaast.service.CotisationsService;

@Named(value = "cotisationsConfigurationTable")
@SessionScoped
public class CotisationsConfigurationTableBean implements Serializable {

	private static final long serialVersionUID = -4747235749146019196L;

	private List<Cotisation> cotisations = new ArrayList<>();

	private Cotisation selectedCotisation;

	@Inject
	private CotisationsService cotisationService;

	@PostConstruct
	public void init() {
		System.out.println("Initializing CotisationsTableBean");
		List<Cotisation> findAllCotisations = this.cotisationService
				.findAllCotisations();
		Collections.sort(findAllCotisations);
		Collections.reverse(findAllCotisations);
		this.cotisations = findAllCotisations;
	}

	public List<Cotisation> getCotisations() {
		return this.cotisations;
	}

	public void setCotisations(final List<Cotisation> cotisations) {
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
