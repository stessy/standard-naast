/**
 *
 */
package standardNaast.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
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
import standardNaast.model.AccountingModel;
import standardNaast.model.MemberAbonnementModel;
import standardNaast.model.PersonModel;
import standardNaast.model.PurchasableAbonnements;
import standardNaast.model.SeasonModel;
import standardNaast.types.AbonnementStatus;
import standardNaast.types.AccountingType;
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

	private final PersonDAO personDAO = new PersonDAOImpl();

	private final PriceDAO priceDAO = new PriceDAOImpl();

	private final AccountingService accountingService = new AccountingService();

	public void setAsPurchased(final List<MemberAbonnementModel> abonnements) {
		for (final MemberAbonnementModel model : abonnements) {
			this.entityManager.getTransaction().begin();
			final Abonnement mergedAbonnement = this.entityManager.find(Abonnement.class, model.getAbonnementId());
			mergedAbonnement.setAbonnementStatus(AbonnementStatus.PURCHASED);
			this.entityManager.merge(mergedAbonnement);
			this.entityManager.getTransaction().commit();
		}
	}

	public void setAsNotYetPaid(final List<MemberAbonnementModel> abonnements) {
		for (final MemberAbonnementModel model : abonnements) {
			this.entityManager.getTransaction().begin();
			final Abonnement mergedAbonnement = this.entityManager.find(Abonnement.class, model.getAbonnementId());
			mergedAbonnement.setPaye(false);
			this.entityManager.merge(mergedAbonnement);
			this.entityManager.getTransaction().commit();
		}
	}

	public void setAsReceived(final List<MemberAbonnementModel> abonnements) {
		for (final MemberAbonnementModel model : abonnements) {
			this.entityManager.getTransaction().begin();
			final Abonnement mergedAbonnement = this.entityManager.find(Abonnement.class, model.getAbonnementId());
			mergedAbonnement.setAbonnementStatus(AbonnementStatus.RECEIVED);
			this.entityManager.merge(mergedAbonnement);
			this.entityManager.getTransaction().commit();
		}
	}

	public void setAsPaid(final List<MemberAbonnementModel> abonnements) {
		for (final MemberAbonnementModel model : abonnements) {
			this.entityManager.getTransaction().begin();
			final Abonnement mergedAbonnement = this.entityManager.find(Abonnement.class, model.getAbonnementId());
			mergedAbonnement.setPaye(true);
			this.entityManager.merge(mergedAbonnement);
			this.entityManager.getTransaction().commit();
			final AccountingModel accountingModel = new AccountingModel();
			accountingModel.setAccountingDate(LocalDate.now());
			accountingModel.setDescription("Abonnement de [" + model.getPerson().getFirstName() + " "
					+ model.getPerson().getName() + "] payé.");
			final Long acompte = model.getAcompte();
			final Long reduction = model.getReduction();
			final Long abonnementPrice = model.getAbonnementPrice().getPrice();
			accountingModel.setMontant(new BigDecimal(abonnementPrice - reduction - acompte));
			accountingModel.setType(AccountingType.ENTRY);
			this.accountingService.addAccounting(accountingModel);
		}
	}

	public void setAsDistributed(final List<MemberAbonnementModel> abonnements) {
		for (final MemberAbonnementModel model : abonnements) {
			this.entityManager.getTransaction().begin();
			final Abonnement mergedAbonnement = this.entityManager.find(Abonnement.class, model.getAbonnementId());
			mergedAbonnement.setAbonnementStatus(AbonnementStatus.DISTRIBUTED);
			this.entityManager.merge(mergedAbonnement);
			this.entityManager.getTransaction().commit();
		}
	}

	public AbonnementsModel getAbonnementsPerSeason(final SeasonModel selectedSeason) {
		final Season season = this.seasonDao.getSeasonById(selectedSeason.getId());
		final List<Abonnement> abonnementsPerSeason = this.abonnementDao.getAbonnementsPerSeason(season);
		Collections.sort(abonnementsPerSeason);
		final List<MemberAbonnementModel> paidAbonnements = new ArrayList<>();
		final List<MemberAbonnementModel> unpaidAbonnements = new ArrayList<>();
		final List<MemberAbonnementModel> newAbonnements = new ArrayList<>();
		final List<MemberAbonnementModel> purchasedAbonnements = new ArrayList<>();
		final List<MemberAbonnementModel> receivedAbonnements = new ArrayList<>();
		final List<MemberAbonnementModel> distributedAbonnements = new ArrayList<>();

		for (final Abonnement abonnement : abonnementsPerSeason) {
			if (abonnement.getPaye()) {
				paidAbonnements.add(MemberAbonnementModel.toModel(abonnement));
			} else {
				unpaidAbonnements.add(MemberAbonnementModel.toModel(abonnement));
			}
			final AbonnementStatus abonnementStatus = abonnement.getAbonnementStatus();
			switch (abonnementStatus) {
			case DISTRIBUTED:
				distributedAbonnements.add(MemberAbonnementModel.toModel(abonnement));
				break;
			case PURCHASED:
				purchasedAbonnements.add(MemberAbonnementModel.toModel(abonnement));
				break;
			case RECEIVED:
				receivedAbonnements.add(MemberAbonnementModel.toModel(abonnement));
				break;
			case NEW:
				if (abonnement.getPaye()) {
					newAbonnements.add(MemberAbonnementModel.toModel(abonnement));
				}
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

	public List<PurchasableAbonnements> printAbonnements(final SeasonModel season) {
		final List<Abonnement> abonnements = this.abonnementDao
				.getPurchasableAbonnements(season.getId());
		final List<PurchasableAbonnements> purchasableAbonnements = new ArrayList<>();
		for (final Abonnement abonnement : abonnements) {
			final Personne person = abonnement.getPersonne();
			final Date birthdate = person.getBirthdate();
			final String formattedBirthdate = new SimpleDateFormat(DateFormat.DDSMMSYYYY).format(birthdate);
			final AbonnementPrices abonnementPrice = abonnement.getAbonnementPrice();
			final PurchasableAbonnements purchasableAbonnement = new
					PurchasableAbonnements(person.getName(), person.getFirstname(),
							formattedBirthdate, person.getFullAddress(),
							person.getIdentityCardNumber(),
							abonnementPrice.getBloc(), abonnement.getRang(),
							abonnement.getPlace(), String.valueOf(abonnementPrice.getPrice()),
							String.valueOf(abonnementPrice.getTypePersonne().getTarif()));
			purchasableAbonnements.add(purchasableAbonnement);
		}
		return purchasableAbonnements;
	}

	public List<MemberAbonnementModel> getMemberAbonnements(final PersonModel personModel) {
		final Personne person = this.entityManager.find(Personne.class, personModel.getPersonneId());
		final List<Abonnement> abonnementList = this.abonnementDao.getMemberAbonnements(person);
		// abonnementList.stream().forEach(a -> this.entityManager.refresh(a));
		return abonnementList.stream().filter(new Predicate<Abonnement>() {

			@Override
			public boolean test(final Abonnement t) {
				return t.getAbonnementPrice() != null;
			}
		}).map(a -> MemberAbonnementModel.toModel(a)).collect(Collectors.toList());
	}

	public MemberAbonnementModel getMemberAbonnement(final MemberAbonnementModel model) {
		final Abonnement foundAbonnement = this.entityManager.find(Abonnement.class, model.getAbonnementId());
		this.entityManager.refresh(foundAbonnement);
		return MemberAbonnementModel.toModel(foundAbonnement);
	}

	public MemberAbonnementModel getPreviousAbonnement(final SeasonModel previousSeasonModel, final Long memberId,
			final CompetitionType competitionType) {
		final Personne person = this.personDAO.getPerson(memberId);
		final Season previousSeason = this.seasonDao.getSeasonById(previousSeasonModel.getId());
		final List<Abonnement> previousAbonnement = this.abonnementDao.getPreviousAbonnement(previousSeason, person,
				competitionType);
		if (previousAbonnement.isEmpty()) {
			return null;
		}
		else {
			return MemberAbonnementModel.toModel(previousAbonnement.get(0));
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

	public MemberAbonnementModel updateAbonnement(final MemberAbonnementModel model) {
		final PersonModel personModel = model.getPerson();
		this.entityManager.getTransaction().begin();
		final Abonnement foundAbonnement = this.entityManager.find(Abonnement.class, model.getAbonnementId());

		final boolean oldPaid = foundAbonnement.getPaye();
		final boolean newPaid = model.getPaye();
		final Long oldAcompte = foundAbonnement.getAcompte();

		foundAbonnement.setAcompte(model.getAcompte());
		foundAbonnement.setPlace(model.getPlace());
		foundAbonnement.setPaye(model.getPaye());
		final AbonnementPrices foundPrice = this.entityManager.find(AbonnementPrices.class, model.getAbonnementPrice()
				.getId());
		foundAbonnement.setAbonnementPrice(foundPrice);
		foundAbonnement.setRang(model.getRang());
		this.entityManager.merge(foundAbonnement);
		this.entityManager.getTransaction().commit();
		if (newPaid && !oldPaid) {
			final AccountingModel accountingModel = new AccountingModel();
			accountingModel.setAccountingDate(LocalDate.now());
			accountingModel.setType(AccountingType.ENTRY);

			final StringBuilder builder = new StringBuilder();
			builder.append("Solde abonnement payé pour [" + personModel.getFirstName() + " " + personModel.getName()
					+ "].");
			builder.append("\n");
			builder.append("Total : ");
			builder.append(foundPrice.getPrice()).append("€");
			builder.append("\n");
			builder.append("Réduction : ");
			builder.append(model.getReduction()).append("€").append("\n");
			builder.append("Premier acompte : ");
			builder.append(oldAcompte).append("€").append("\n");
			builder.append("Solde : ");
			builder.append(foundPrice.getPrice() - foundAbonnement.getReduction() - oldAcompte).append("€");
			accountingModel.setDescription(builder.toString());
			accountingModel.setMontant(new BigDecimal(foundPrice.getPrice() - foundAbonnement.getReduction()
					- oldAcompte));
			this.accountingService.addAccounting(accountingModel);
		}
		return MemberAbonnementModel.toModel(foundAbonnement);
	}

	public MemberAbonnementModel addAbonnement(final MemberAbonnementModel model) {
		final SeasonModel seasonModel = model.getSaison();
		final Season season = this.seasonDao.getSeasonById(seasonModel.getId());
		final PersonModel personModel = model.getPerson();
		final Personne person = this.personDAO.getPerson(personModel.getPersonneId());
		final AbonnementPriceChampionshipModel priceModel = model.getAbonnementPrice();
		final AbonnementPrices price = this.priceDAO.getPrice(priceModel.getId());
		final Abonnement entity = MemberAbonnementModel.toEntity(model);
		entity.setSaison(season);
		entity.setAbonnementPrice(price);
		entity.setPersonne(person);
		this.entityManager.getTransaction().begin();
		this.entityManager.persist(entity);
		this.entityManager.getTransaction().commit();
		if (model.getPaye() || model.getAcompte() > 0) {
			final AccountingModel accountingModel = new AccountingModel();
			accountingModel.setAccountingDate(LocalDate.now());
			accountingModel.setType(AccountingType.ENTRY);
			if (model.getPaye()) {
				final StringBuilder builder = new StringBuilder();
				builder.append("Abonnement payé pour [" + personModel.getFirstName() + " " + personModel.getName()
						+ "].");
				builder.append("\n");
				builder.append("Total : ");
				builder.append(price.getPrice()).append("€");
				builder.append("\n");
				builder.append("Réduction : ");
				builder.append(model.getReduction()).append("€");
				accountingModel.setDescription(builder.toString());
				accountingModel.setMontant(new BigDecimal(price.getPrice() - model.getReduction()));
			}
			else {
				final StringBuilder builder = new StringBuilder();
				builder.append("Acompte de ");
				builder.append(model.getAcompte()).append("€");
				builder.append(" pour l'abonnement de [" + personModel.getFirstName() + " " + personModel.getName()
						+ "].");
				accountingModel.setDescription(builder.toString());
				accountingModel.setMontant(new BigDecimal(model.getAcompte()));
			}
			this.accountingService.addAccounting(accountingModel);

		}
		return MemberAbonnementModel.toModel(entity);
	}

}
