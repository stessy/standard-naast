/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.log4j.Logger;

import standardNaast.entities.Cotisation;
import standardNaast.entities.Personne;
import standardNaast.entities.PersonneCotisation;
import standardNaast.model.CotisationViewModel;
import standardNaast.model.PersonnesCotisationsModel;

@Stateless
@LocalBean
public class CotisationsService implements Serializable {

	private static final long serialVersionUID = -3608269572256839874L;

	@Inject
	private PersonneService personneService;

	@PersistenceContext
	private EntityManager entityManager;

	private static final Logger LOGGER = Logger.getLogger(CotisationsService.class);

	public List<Cotisation> findAllCotisations() {
		CotisationsService.LOGGER.debug("Getting list of cotisations");
		CriteriaQuery<Cotisation> queryAll = this.entityManager.getCriteriaBuilder().createQuery(Cotisation.class);
		queryAll.from(Cotisation.class);
		return this.entityManager.createQuery(queryAll).getResultList();
	}

	public Cotisation getCotisationPerYear(final long year) {
		return this.entityManager.find(Cotisation.class, year);
	}

	public void setCotisationsAsPaid(
			final List<PersonnesCotisationsModel> personnesCotisationsModel,
			final Cotisation cotisation) {
		for (PersonnesCotisationsModel personnesCotisationsModel2 : personnesCotisationsModel) {
			Personne personne = this.personneService.getPersonByMemberNumber(personnesCotisationsModel2.getMemberNumber());
			PersonneCotisation personneCotisation = new PersonneCotisation();
			personneCotisation.setPersonne(personne);
			personneCotisation.setCotisation(cotisation);
			personneCotisation.setDatePaiement(new Date());
			personne.getPersonnesCotisations().add(personneCotisation);
		}

	}

	public CotisationViewModel getPaiedCotisationsPerYear(String selectedYear) {
		Cotisation selectedCotisation = this.getCotisationPerYear(Long.valueOf(selectedYear));
		long year = selectedCotisation.getAnneeCotisation();
		boolean found = false;
		List<Personne> allPerson = this.personneService.findAllPerson(false);
		List<PersonnesCotisationsModel> paidPersonnesCotisationsModel = new ArrayList<>();
		List<PersonnesCotisationsModel> unpaidPersonnesCotisationsModel = new ArrayList<>();
		for (Personne personne : allPerson) {
			List<PersonneCotisation> personnesCotisations = personne.getPersonnesCotisations();
			for (PersonneCotisation personneCotisation : personnesCotisations) {
				Cotisation cotisation = personneCotisation.getCotisation();
				PersonnesCotisationsModel model = new PersonnesCotisationsModel();
				model.setFirstName(personne.getFirstname());
				model.setName(personne.getName());
				model.setMemberNumber(personne.getMemberNumber());
				model.setMemberCardSent(personneCotisation.getCarteMembreEnvoyee());
				if ((cotisation != null)
						&& (cotisation.getAnneeCotisation() == year)) {
					model.setPaymentDate(personneCotisation.getDatePaiement());
					paidPersonnesCotisationsModel.add(model);
					found = true;
					break;
				}
			}
			if (!found) {
				PersonnesCotisationsModel model = new PersonnesCotisationsModel();
				model.setFirstName(personne.getFirstname());
				model.setName(personne.getName());
				model.setMemberNumber(personne.getMemberNumber());
				unpaidPersonnesCotisationsModel.add(model);
			}
			found = false;
		}
		Collections.sort(paidPersonnesCotisationsModel);
		Collections.sort(unpaidPersonnesCotisationsModel);
		CotisationViewModel cotisationViewModel = new CotisationViewModel(paidPersonnesCotisationsModel, unpaidPersonnesCotisationsModel, selectedCotisation);
		return cotisationViewModel;
	}

}
