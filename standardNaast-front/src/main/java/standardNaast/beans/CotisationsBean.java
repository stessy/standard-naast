package standardNaast.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.Cotisation;
import standardNaast.service.CotisationService;

@Named(value = "cotisation")
@SessionScoped
public class CotisationsBean implements Serializable {

	private List<Cotisation> cotisations;

	@Inject
	private CotisationService cotisationService;

	@PostConstruct
	public void init() {
		if (this.cotisations == null) {
			List<Cotisation> findAllPerson = this.cotisationService
					.findAllCotisations();
			Collections.sort(findAllPerson);
			this.cotisations = findAllPerson;
		}
	}

	public List<Cotisation> getCotisations() {
		return this.cotisations;
	}

}
