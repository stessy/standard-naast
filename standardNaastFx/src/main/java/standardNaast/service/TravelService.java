/**
 * 
 */
package standardNaast.service;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import standardNaast.dao.MatchDAO;
import standardNaast.dao.MatchDAOImpl;
import standardNaast.dao.SeasonDAO;
import standardNaast.dao.SeasonDAOImpl;
import standardNaast.dao.TravelDAO;
import standardNaast.dao.TravelDAOImpl;
import standardNaast.entities.Match;
import standardNaast.entities.PersonneTravel;
import standardNaast.entities.Season;
import standardNaast.entities.TravelPrice;
import standardNaast.model.MatchModel;
import standardNaast.model.MemberCotisationsModel;
import standardNaast.model.PersonModel;
import standardNaast.model.PersonTravelModel;
import standardNaast.model.PersonsMatchTravel;
import standardNaast.model.SeasonModel;
import standardNaast.model.TravelModel;
import standardNaast.types.PersonTravelType;

/**
 * @author stessy
 * 
 */
public class TravelService implements Serializable {

	private TravelDAO travelDao = new TravelDAOImpl();

	private SeasonDAO seasonDao = new SeasonDAOImpl();

	private PersonneService personneService = new PersonneServiceImpl();

	private MatchDAO matchDAO = new MatchDAOImpl();

	private CotisationsService cotisationService = new CotisationsService();

	public List<TravelModel> getTravelsPricePerSeason(final SeasonModel model) {
		final Season season = this.seasonDao.getSeasonById(model.getId());
		final List<TravelPrice> travels = this.travelDao.getTravelsPerSeason(season);
		Collections.sort(travels, new Comparator<TravelPrice>() {

			@Override
			public int compare(final TravelPrice o1, final TravelPrice o2) {
				return o1.getMontant().compareTo(o2.getMontant());
			}
		});
		final List<TravelModel> travelsModels = new ArrayList<>();
		travels.stream().forEach(t -> travelsModels.add(TravelModel.toModel(t)));

		return travelsModels;
	}

	public PersonsMatchTravel getMatchTravels(final MatchModel matchModel) {
		// Getting all travel prices for a season. This will be used to
		// calculate the travel amount for each member.
		final List<TravelModel> travelsPricePerSeason = this.getTravelsPricePerSeason(matchModel.getSeason());
		// Get all members
		final Set<PersonModel> allMembers = new HashSet<>(this.personneService.findAllPerson(false));
		// Get match from match model
		final Match match = this.matchDAO.getMatch(matchModel.getId());

		// Get match travels that have already been paid
		final List<PersonneTravel> matchTravels = this.travelDao.getMatchTravels(match);

		final Set<PersonTravelModel> membersWhoPaidForTravel = matchTravels.stream()
				.filter(f -> f.getPersonne().getMemberNumber() < 10000).map(t -> PersonTravelModel.toModel(t))
				.collect(Collectors.toSet());
		membersWhoPaidForTravel.stream().forEach(t -> t.setPaid(true));

		final Set<PersonTravelModel> nonMembersWhoPaidForTravel = matchTravels.stream()
				.filter(f -> f.getPersonne().getMemberNumber() >= 10000).map(t -> PersonTravelModel.toModel(t))
				.collect(Collectors.toSet());
		nonMembersWhoPaidForTravel.stream().forEach(t -> t.setPaid(true));

		// Get members who already paid the travel and subtract that list from
		// all members. this way we know who not yet paid.
		final Set<PersonModel> allMembersToCalculate = new HashSet<>(allMembers);
		final Set<PersonModel> paidMembers = membersWhoPaidForTravel.stream().map(t -> t.getPerson())
				.collect(Collectors.toSet());
		allMembersToCalculate.removeAll(paidMembers);

		final Set<PersonTravelModel> membersWhoNotPaidForTravel = allMembersToCalculate.stream()
				.map(t -> this.buildMatchTravelModelForUnpaid(t, matchModel, travelsPricePerSeason))
				.collect(Collectors.toSet());
		// We have both list of members who paid or not yet paid. Time to create
		// a unique list.
		final List<PersonTravelModel> membersTravel = new ArrayList<>(membersWhoNotPaidForTravel);
		membersTravel.addAll(membersWhoPaidForTravel);
		Collections.sort(membersTravel, new Comparator<PersonTravelModel>() {

			@Override
			public int compare(final PersonTravelModel o1, final PersonTravelModel o2) {
				return o1.getPerson().getMemberNumber().compareTo(o2.getPerson().getMemberNumber());
			}

		});

		final Set<PersonModel> nonMembersWhoNotPaidForTravel = new HashSet<>(this.personneService.findAllPerson(true));
		nonMembersWhoNotPaidForTravel.removeAll(allMembers);
		final List<PersonModel> collect = nonMembersWhoPaidForTravel.stream().map(t -> t.getPerson())
				.collect(Collectors.toList());
		nonMembersWhoNotPaidForTravel.removeAll(collect);

		return new PersonsMatchTravel(membersTravel, new ArrayList<>(nonMembersWhoPaidForTravel), new ArrayList<>(
				nonMembersWhoNotPaidForTravel));
	}

	public PersonTravelModel addMemberMatchTravel(final PersonTravelModel model) {
		final PersonneTravel entity = PersonTravelModel.toEntity(model);
		return PersonTravelModel.toModel(this.travelDao.addMemberMatchTravel(entity));
	}

	public PersonTravelModel addNonMemberMatchTravel(final PersonModel selectedPerson,
			final MatchModel selectedMatch, final SeasonModel selectedSeason) {
		final List<TravelModel> travelsPricePerSeason = this.getTravelsPricePerSeason(selectedSeason);
		final Long travelAmount = this.getMemberTravelPriceForMatch(selectedPerson, selectedMatch,
				travelsPricePerSeason);
		final PersonTravelModel model = new PersonTravelModel();
		model.setAmount(travelAmount);
		model.setMatch(selectedMatch);
		model.setPerson(selectedPerson);
		model.setPaid(true);
		final PersonneTravel entity = PersonTravelModel.toEntity(model);
		return PersonTravelModel.toModel(this.travelDao.addMemberMatchTravel(entity));
	}

	public void removeMemberMatchTravel(final PersonTravelModel model) {
		final PersonneTravel entity = PersonTravelModel.toEntity(model);
		this.travelDao.removeMemberMatchTravel(entity);
	}

	private PersonTravelModel buildMatchTravelModelForUnpaid(final PersonModel t, final MatchModel matchModel,
			final List<TravelModel> travelsPricePerSeason) {
		final PersonTravelModel model = new PersonTravelModel();
		model.setMatch(matchModel);
		model.setPerson(t);
		model.setPaid(false);
		model.setAmount(this.getMemberTravelPriceForMatch(t, matchModel, travelsPricePerSeason));
		return model;
	}

	public Long getMemberTravelPriceForMatch(final PersonModel model, final MatchModel matchModel,
			final List<TravelModel> travelsPricePerSeason) {
		final LocalDate matchDate = matchModel.getMatchDate();
		final LocalDate birthdate = model.getBirthdate();
		final long yearsBetween = ChronoUnit.YEARS.between(birthdate, matchDate);
		// Check that the member has paid his cotisation for the season for
		// which the match is played.
		final List<MemberCotisationsModel> memberCotisations = this.cotisationService.getMemberCotisations(model);
		final boolean isMember = memberCotisations.stream().anyMatch(t -> t.getSeason().equals(matchModel.getSeason()));
		final PersonTravelType travelType = PersonTravelType.MINOR.get((int) yearsBetween) != null ? PersonTravelType.MINOR
				.get((int) yearsBetween)
				: PersonTravelType.MAJOR.get((int) yearsBetween);
		final Optional<TravelModel> travelModel = travelsPricePerSeason
				.stream()
				.filter(t -> this.isSameSeason(matchModel, t) && this.isMember(isMember, t)
						&& this.isCorrespondingtraveltypePerson(travelType, t)
						&& this.hasCorrespondingPlace(matchModel, t)).findFirst();
		travelModel.isPresent();
		return travelModel.get().getAmount();
	}

	private boolean hasCorrespondingPlace(final MatchModel matchModel, final TravelModel t) {
		return t.getPlace() == matchModel.getPlace();
	}

	private boolean isCorrespondingtraveltypePerson(final PersonTravelType travelType, final TravelModel t) {
		return t.getTypePersonne() == travelType;
	}

	private boolean isMember(final boolean isMember, final TravelModel t) {
		return t.getMember() == isMember;
	}

	private boolean isSameSeason(final MatchModel matchModel, final TravelModel t) {
		return t.getSeason().equals(matchModel.getSeason());
	}
}
