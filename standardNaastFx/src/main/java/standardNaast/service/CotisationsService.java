/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import standardNaast.dao.CotisationDAO;
import standardNaast.dao.CotisationDAOImpl;
import standardNaast.dao.PersonDAO;
import standardNaast.dao.PersonDAOImpl;
import standardNaast.dao.SeasonDAO;
import standardNaast.dao.SeasonDAOImpl;
import standardNaast.entities.Cotisation;
import standardNaast.entities.Personne;
import standardNaast.entities.PersonneCotisation;
import standardNaast.entities.Season;
import standardNaast.model.AccountingModel;
import standardNaast.model.CotisationViewModel;
import standardNaast.model.CotisationsOverviewModel;
import standardNaast.model.MemberCotisationsModel;
import standardNaast.model.PersonModel;
import standardNaast.model.PersonnesCotisationsModel;
import standardNaast.model.SeasonModel;
import standardNaast.types.AccountingType;
import standardNaast.utils.DateUtils;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class CotisationsService implements Serializable {

	private static final long serialVersionUID = -3608269572256839874L;

	private EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();

	private PersonDAO personDAO = new PersonDAOImpl();

	private SeasonDAO seasonDAO = new SeasonDAOImpl();

	private CotisationDAO cotisationDAO = new CotisationDAOImpl();

	private AccountingService accountingService = new AccountingService();

	private static final Logger LOGGER = LogManager
			.getLogger(CotisationsService.class);

	public List<Cotisation> findAllCotisations() {
		CotisationsService.LOGGER.debug("Getting list of cotisations");
		final CriteriaQuery<Cotisation> queryAll = this.entityManager
				.getCriteriaBuilder().createQuery(Cotisation.class);
		queryAll.from(Cotisation.class);
		return this.entityManager.createQuery(queryAll).getResultList();
	}

	public MemberCotisationsModel addMemberCotisation(final PersonModel member, final SeasonModel season,
			final LocalDate paymentDate)
			throws Exception {
		final Personne person = this.personDAO.getPerson(member.getPersonneId());
		final Season selectedSeason = this.seasonDAO.getSeasonById(season.getId());
		final List<PersonneCotisation> personneCotisation = this.cotisationDAO.getMemberCotisation(person,
				selectedSeason);
		if (!personneCotisation.isEmpty()) {
			throw new Exception("Cotisation déjà existante pour le membre [" + person.getFirstname() + " "
					+ person.getName() + "] pour la saison [" + selectedSeason.getId() + "]");
		}
		final PersonneCotisation cotisation = new PersonneCotisation();
		cotisation.setSeason(selectedSeason);
		cotisation.setPersonne(person);
		cotisation.setDatePaiement(DateUtils.toDate(paymentDate));
		cotisation.setCarteMembreEnvoyee(false);
		final PersonneCotisation memberCotisation = this.cotisationDAO.addMemberCotisation(cotisation);
		final AccountingModel accountingModel = new AccountingModel();
		accountingModel.setAccountingDate(paymentDate);
		accountingModel.setDescription("Cotisation pour la saison [" + season.getId() + "] de ["
				+ person.getFirstname() + " " + person.getName() + "]");
		accountingModel.setMontant(new BigDecimal(season.getCotisationAMount()));
		accountingModel.setType(AccountingType.ENTRY);
		this.accountingService.addAccounting(accountingModel);
		final Long maxMemberNumber = this.personDAO.getMaxMemberNumber();
		person.setMemberNumber(maxMemberNumber + 1);
		this.personDAO.updatePerson(person);
		return MemberCotisationsModel.toModel(memberCotisation);

	}

	public Cotisation getCotisationPerYear(final long year) {
		return this.entityManager.find(Cotisation.class, year);
	}

	public void setCotisationsAsPaid(
			final List<PersonnesCotisationsModel> personnesCotisationsModel,
			final SeasonModel seasonModel) {
		for (final PersonnesCotisationsModel personnesCotisationsModel2 : personnesCotisationsModel) {
			final Personne personne = this.personDAO.getPersonneByMemberNumber(personnesCotisationsModel2
					.getMemberNumber());
			final Season season = this.seasonDAO.getSeasonById(seasonModel.getId());
			final PersonneCotisation personneCotisation = new PersonneCotisation();
			personneCotisation.setPersonne(personne);
			personneCotisation.setSeason(season);
			personneCotisation.setDatePaiement(new Date());
			personne.getPersonnesCotisations().add(personneCotisation);
		}

	}

	public List<MemberCotisationsModel> getMemberCotisations(final PersonModel personModel) {
		final Personne person = this.personDAO.getPerson(personModel.getPersonneId());
		final List<PersonneCotisation> personnesCotisations = this.cotisationDAO.getMemberCotisations(person);
		return personnesCotisations.stream().map(c -> MemberCotisationsModel.toModel(c)).collect(Collectors.toList());
	}

	public void setMemberCardAsSent(
			final List<PersonnesCotisationsModel> personnesCotisationsModel,
			final SeasonModel seasonModel) {
		for (final PersonnesCotisationsModel personnesCotisationsModel2 : personnesCotisationsModel) {
			final Personne personne = this.personDAO.getPersonneByMemberNumber(personnesCotisationsModel2
					.getMemberNumber());
			final List<PersonneCotisation> personneCotisations = personne
					.getPersonnesCotisations();
			for (final PersonneCotisation personneCotisation : personneCotisations) {
				if (personneCotisation.getSeason().equals(seasonModel.getId())) {
					personneCotisation.setCarteMembreEnvoyee(true);
					break;
				}
			}
		}
	}

	public CotisationViewModel getPaiedCotisationsPerSeason(
			final SeasonModel seasonModel) {

		boolean found = false;
		final List<Personne> allMembers = this.personDAO.getAllPersons(false);
		final List<CotisationsOverviewModel> unpaidPersonnesCotisationsModel = new ArrayList<>();
		final List<CotisationsOverviewModel> paidCotisationMemberCardSent = new ArrayList<>();
		final List<CotisationsOverviewModel> paidCotisationMemberCardNotSent = new ArrayList<>();
		for (final Personne personne : allMembers) {
			final List<PersonneCotisation> personnesCotisations = personne
					.getPersonnesCotisations();
			for (final PersonneCotisation personneCotisation : personnesCotisations) {
				final Season season = personneCotisation
						.getSeason();
				final CotisationsOverviewModel model = new CotisationsOverviewModel();
				model.setMember(PersonModel.toModel(personne));
				final boolean memberCardSent = personneCotisation
						.getCarteMembreEnvoyee();
				if ((season != null)
						&& (season.getId().equals(seasonModel.getId()))) {
					model.setPaymentDate(DateUtils.toLocalDate(personneCotisation.getDatePaiement()));
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
				final CotisationsOverviewModel model = new CotisationsOverviewModel();
				model.setMember(PersonModel.toModel(personne));
				unpaidPersonnesCotisationsModel.add(model);
			}
			found = false;
		}
		Collections.sort(paidCotisationMemberCardSent);
		Collections.sort(paidCotisationMemberCardNotSent);
		Collections.sort(unpaidPersonnesCotisationsModel);
		final CotisationViewModel cotisationViewModel = new CotisationViewModel(
				paidCotisationMemberCardSent, paidCotisationMemberCardNotSent,
				unpaidPersonnesCotisationsModel, seasonModel);
		return cotisationViewModel;
	}

}
