/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.log4j.Logger;

import standardNaast.dao.PersonDAO;
import standardNaast.dao.PersonDAOImpl;
import standardNaast.entities.Cotisation;
import standardNaast.entities.Personne;
import standardNaast.entities.PersonneCotisation;
import standardNaast.model.CotisationViewModel;
import standardNaast.model.PersonModel;
import standardNaast.model.PersonnesCotisationsModel;
import standardNaast.model.SeasonModel;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class CotisationsService implements Serializable {

	private static final long serialVersionUID = -3608269572256839874L;

	private PersonneService personneService;

	private EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();

	private PersonDAO personDAO = new PersonDAOImpl();

	private static final Logger LOGGER = Logger
			.getLogger(CotisationsService.class);

	public List<Cotisation> findAllCotisations() {
		CotisationsService.LOGGER.debug("Getting list of cotisations");
		final CriteriaQuery<Cotisation> queryAll = this.entityManager
				.getCriteriaBuilder().createQuery(Cotisation.class);
		queryAll.from(Cotisation.class);
		return this.entityManager.createQuery(queryAll).getResultList();
	}

	public Cotisation getCotisationPerYear(final long year) {
		return this.entityManager.find(Cotisation.class, year);
	}

	public void setCotisationsAsPaid(
			final List<PersonnesCotisationsModel> personnesCotisationsModel,
			final Cotisation cotisation) {
		for (final PersonnesCotisationsModel personnesCotisationsModel2 : personnesCotisationsModel) {
			final Personne personne = this.personDAO.getPersonneByMemberNumber(personnesCotisationsModel2
					.getMemberNumber());
			final PersonneCotisation personneCotisation = new PersonneCotisation();
			personneCotisation.setPersonne(personne);
			personneCotisation.setCotisation(cotisation);
			personneCotisation.setDatePaiement(new Date());
			personne.getPersonnesCotisations().add(personneCotisation);
		}

	}

	public void setMemberCardAsSent(
			final List<PersonnesCotisationsModel> personnesCotisationsModel,
			final Cotisation cotisation) {
		for (final PersonnesCotisationsModel personnesCotisationsModel2 : personnesCotisationsModel) {
			final Personne personne = this.personDAO.getPersonneByMemberNumber(personnesCotisationsModel2
					.getMemberNumber());
			final List<PersonneCotisation> personneCotisations = personne
					.getPersonnesCotisations();
			for (final PersonneCotisation personneCotisation : personneCotisations) {
				if (personneCotisation.getCotisation().getAnneeCotisation() == cotisation
						.getAnneeCotisation()) {
					personneCotisation.setCarteMembreEnvoyee(true);
					break;
				}
			}
		}
	}

	public CotisationViewModel getPaiedCotisationsPerYear(
			final SeasonModel seasonModel) {

		boolean found = false;
		final List<PersonModel> allPerson = this.personneService
				.findAllPerson(false);
		final List<PersonnesCotisationsModel> unpaidPersonnesCotisationsModel = new ArrayList<>();
		final List<PersonnesCotisationsModel> paidCotisationMemberCardSent = new ArrayList<>();
		final List<PersonnesCotisationsModel> paidCotisationMemberCardNotSent = new ArrayList<>();
		for (final PersonModel personne : allPerson) {
			final List<PersonneCotisation> personnesCotisations = personne
					.getPersonnesCotisations();
			for (final PersonneCotisation personneCotisation : personnesCotisations) {
				final Cotisation cotisation = personneCotisation
						.getCotisation();
				final PersonnesCotisationsModel model = new PersonnesCotisationsModel();
				model.setFirstName(personne.getFirstname());
				model.setName(personne.getName());
				model.setMemberNumber(personne.getMemberNumber());
				final boolean memberCardSent = personneCotisation
						.getCarteMembreEnvoyee();
				if ((cotisation != null)
						&& (cotisation.getAnneeCotisation() == year)) {
					model.setPaymentDate(personneCotisation.getDatePaiement());
					if (memberCardSent) {
						paidCotisationMemberCardSent.add(model);
					} else {
						paidCotisationMemberCardNotSent.add(model);
					}
					found = true;
					break;
				}
			}
			if (!found) {
				final PersonnesCotisationsModel model = new PersonnesCotisationsModel();
				model.setFirstName(personne.getFirstname());
				model.setName(personne.getName());
				model.setMemberNumber(personne.getMemberNumber());
				unpaidPersonnesCotisationsModel.add(model);
			}
			found = false;
		}
		Collections.sort(paidCotisationMemberCardSent);
		Collections.sort(paidCotisationMemberCardNotSent);
		Collections.sort(unpaidPersonnesCotisationsModel);
		final CotisationViewModel cotisationViewModel = new CotisationViewModel(
				paidCotisationMemberCardSent, paidCotisationMemberCardNotSent,
				unpaidPersonnesCotisationsModel, selectedCotisation);
		return cotisationViewModel;
	}

}
