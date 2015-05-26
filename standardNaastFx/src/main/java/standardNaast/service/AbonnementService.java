/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import standardNaast.constants.DateFormat;
import standardNaast.dao.AbonnementDAO;
import standardNaast.dao.AbonnementDAOImpl;
import standardNaast.dao.PersonDAO;
import standardNaast.dao.PersonDAOImpl;
import standardNaast.dao.PriceDAO;
import standardNaast.dao.PriceDAOImpl;
import standardNaast.dao.SeasonDAO;
import standardNaast.dao.SeasonDAOImpl;
import standardNaast.entities.Abonnement;
import standardNaast.entities.AbonnementPrices;
import standardNaast.entities.Personne;
import standardNaast.entities.Season;
import standardNaast.model.AbonnementPriceChampionshipModel;
import standardNaast.model.AbonnementsModel;
import standardNaast.model.MemberAbonnementsModel;
import standardNaast.model.PersonModel;
import standardNaast.model.PurchasableAbonnements;
import standardNaast.model.SeasonModel;
import standardNaast.types.AbonnementStatus;
import standardNaast.types.CompetitionType;
import standardNaast.types.PersonType;
import standardNaast.utils.AbonnementPricesImporter;
import standardNaast.utils.AbonnementPricesImporter.BlocAbonnementPrices;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class AbonnementService implements Serializable {

	private final EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();

	private static final long serialVersionUID = -7174502180535914551L;

	private final AbonnementDAO abonnementDao = new AbonnementDAOImpl();

	private final SeasonDAO seasonDao = new SeasonDAOImpl();

	private final PersonneService personneService = new PersonneServiceImpl();

	private final PersonDAO personDAO = new PersonDAOImpl();

	private final PriceDAO priceDAO = new PriceDAOImpl();

	public void setAsPurchased(final List<Abonnement> abonnements) {
		for (final Abonnement abonnement : abonnements) {
			this.abonnementDao.setAsPurchased(abonnement);
		}
	}

	public void setAsNotYetPaid(final List<Abonnement> abonnements) {
		for (final Abonnement abonnement : abonnements) {
			this.abonnementDao.setAsNotYetPaid(abonnement);
		}
	}

	public void setAsReceived(final List<Abonnement> abonnements) {
		for (final Abonnement abonnement : abonnements) {
			this.abonnementDao.setAsReceived(abonnement);
		}
	}

	public void setAsPaid(final List<Abonnement> abonnements) {
		for (final Abonnement abonnement : abonnements) {
			this.abonnementDao.setAsPaid(abonnement);
		}
	}

	public void setAsDistributed(final List<Abonnement> abonnements) {
		for (final Abonnement abonnement : abonnements) {
			this.abonnementDao.setAsDistributed(abonnement);
		}
	}

	public AbonnementsModel getAbonnementsPerSeason(final String selectedSeason) {
		final Season season = this.seasonDao.getSeasonById(selectedSeason);
		final List<Abonnement> abonnementsPerSeason = this.abonnementDao.getAbonnementsPerSeason(season);
		final SortedSet<Abonnement> paidAbonnements = new TreeSet<>();
		final SortedSet<Abonnement> unpaidAbonnements = new TreeSet<>();
		final SortedSet<Abonnement> newAbonnements = new TreeSet<>();
		final SortedSet<Abonnement> purchasedAbonnements = new TreeSet<>();
		final SortedSet<Abonnement> receivedAbonnements = new TreeSet<>();
		final SortedSet<Abonnement> distributedAbonnements = new TreeSet<>();

		for (final Abonnement abonnement : abonnementsPerSeason) {
			if (abonnement.getPaye()) {
				paidAbonnements.add(abonnement);
			} else {
				unpaidAbonnements.add(abonnement);
			}
			final AbonnementStatus abonnementStatus = abonnement.getAbonnementStatus();
			switch (abonnementStatus) {
			case DISTRIBUTED:
				distributedAbonnements.add(abonnement);
				break;
			case PURCHASED:
				purchasedAbonnements.add(abonnement);
				break;
			case RECEIVED:
				receivedAbonnements.add(abonnement);
				break;
			case NEW:
				newAbonnements.add(abonnement);
				break;
			default:
				throw new IllegalStateException("L'abonnement a un statut inexistant[" + abonnementStatus + "]");
			}

		}

		final AbonnementsModel abonnementsModel = new AbonnementsModel();
		abonnementsModel.setDistributedAbonnements(distributedAbonnements);
		abonnementsModel.setNewAbonnements(newAbonnements);
		abonnementsModel.setPaidAbonnements(paidAbonnements);
		abonnementsModel.setPurchasedAbonnements(purchasedAbonnements);
		abonnementsModel.setReceivedAbonnements(receivedAbonnements);
		abonnementsModel.setUnpaidAbonnements(unpaidAbonnements);

		return abonnementsModel;

	}

	public void printAbonnements(final List<Abonnement> abonnementsToPurchase) {
		final List<PurchasableAbonnements> purchasableAbonnements = new ArrayList<>();
		for (final Abonnement abonnement : abonnementsToPurchase) {
			final Personne person = abonnement.getPersonne();
			final Date birthdate = person.getBirthdate();
			final String formattedBirthdate = new SimpleDateFormat(DateFormat.DDSMMSYYYY).format(birthdate);
			// final PurchasableAbonnements purchasableAbonnement = new
			// PurchasableAbonnements(person.getName(), person.getFirstname(),
			// birthdate, person.getFullAddress(),
			// person.getIdentityCardNumber(),
			// abonnement.getBloc()., abonnement.getRang(),
			// abonnement.getPlace(), amount)
		}
	}

	public List<MemberAbonnementsModel> getMemberAbonnements(final PersonModel personModel) {
		// this.entityManager.getTransaction().begin();
		final Personne person = this.personDAO.getPerson(personModel.getPersonneId());
		final List<Abonnement> abonnementList = person.getAbonnementList();
		return abonnementList.stream().filter(new Predicate<Abonnement>() {

			@Override
			public boolean test(final Abonnement t) {
				return t.getAbonnementPrice() != null;
			}
		}).map(a -> MemberAbonnementsModel.toModel(a)).collect(Collectors.toList());
	}

	public MemberAbonnementsModel getPreviousAbonnement(final SeasonModel previousSeasonModel, final Long memberId) {
		final Personne person = this.personDAO.getPerson(memberId);
		final Season previousSeason = this.seasonDao.getSeasonById(previousSeasonModel.getId());
		final List<Abonnement> previousAbonnement = this.abonnementDao.getPreviousAbonnement(previousSeason, person);
		if (previousAbonnement.isEmpty()) {
			return null;
		}
		else {
			return MemberAbonnementsModel.toModel(previousAbonnement.get(0));
		}
	}

	public AbonnementPriceChampionshipModel getAbonnementPrice(final SeasonModel seasonModel, final PersonModel model,
			final String bloc,
			final CompetitionType competitionType) {
		final Boolean isStudent = model.getStudent();
		final LocalDate birthdate = model.getBirthdate();
		final LocalDate dateFirstMatch = seasonModel.getFirstMatchDate();
		final Period yearsBetween = Period.between(birthdate, dateFirstMatch);
		PersonType personType = null;
		final PersonType[] personTypes = PersonType.values();
		for (final PersonType type : personTypes) {
			final int minAge = type.getMinAge();
			final int maxAge = type.getMaxAge();
			if (yearsBetween.getYears() >= minAge && yearsBetween.getYears() <= maxAge) {
				if (type == PersonType.STUDENT && !isStudent) {
					personType = PersonType.ADULT;
				}
				else if (type == PersonType.ADULT && isStudent) {
					personType = PersonType.STUDENT;
				}
				else {
					personType = type;
				}
				break;
			}
		}

		final Season season = this.entityManager.find(Season.class, seasonModel.getId());
		final TypedQuery<AbonnementPrices> query = this.entityManager.createNamedQuery("findAbonnementPrice",
				AbonnementPrices.class);
		query.setParameter("season", season);
		query.setParameter("competitionType", competitionType);
		query.setParameter("bloc", bloc);
		query.setParameter("personType", personType);
		final List<AbonnementPrices> resultList = query.getResultList();
		return AbonnementPriceChampionshipModel.toModel(resultList.get(0));
	}

	public List<String> getBlocList(final SeasonModel seasonModel, final CompetitionType competitionType) {
		final Season season = this.seasonDao.getSeasonById(seasonModel.getId());
		final TypedQuery<String> query = this.entityManager.createNamedQuery("findBlocsPerSeason",
				String.class);
		query.setParameter("season", season);
		query.setParameter("competitionType", competitionType);
		final List<String> resultList = query.getResultList();
		return resultList;
	}

	public void addAbonnementPrices(final List<AbonnementPricesImporter.BlocAbonnementPrices> abonnementPrices,
			final CompetitionType competitionType, final SeasonModel season) {
		final Season managedSeason = this.seasonDao.getSeasonById(season.getId());
		for (final BlocAbonnementPrices blocAbonnementPrices : abonnementPrices) {
			final AbonnementPrices prices = new AbonnementPrices();
			prices.setBloc(blocAbonnementPrices.getBloc());
			prices.setSeason(managedSeason);
			prices.setTypeCompetition(competitionType);
			prices.setPrice((long) blocAbonnementPrices.getValue());
			prices.setTypePersonne(blocAbonnementPrices.getPersonType());
			this.priceDAO.addPrice(prices);
		}

	}

	public MemberAbonnementsModel addAbonnement(final MemberAbonnementsModel model) {
		final SeasonModel seasonModel = model.getSaison();
		final Season season = this.seasonDao.getSeasonById(seasonModel.getId());
		final PersonModel personModel = model.getPerson();
		final Personne person = this.personDAO.getPerson(personModel.getPersonneId());
		final AbonnementPriceChampionshipModel priceModel = model.getAbonnementPrice();
		final AbonnementPrices price = this.priceDAO.getPrice(priceModel.getId());
		final Abonnement entity = MemberAbonnementsModel.toEntity(model);
		entity.setSaison(season);
		entity.setAbonnementPrice(price);
		entity.setPersonne(person);
		this.entityManager.getTransaction().begin();
		this.entityManager.persist(entity);
		this.entityManager.getTransaction().commit();
		return MemberAbonnementsModel.toModel(entity);
	}

}
