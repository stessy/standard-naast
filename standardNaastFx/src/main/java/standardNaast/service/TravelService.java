package standardNaast.service;

import standardNaast.dao.*;
import standardNaast.entities.Match;
import standardNaast.entities.PersonneTravel;
import standardNaast.entities.Season;
import standardNaast.entities.TravelPrice;
import standardNaast.model.*;
import standardNaast.types.PersonTravelType;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author stessy
 */
public class TravelService implements Serializable {

	private final TravelDAO travelDao = new TravelDAOImpl();

	private final SeasonDAO seasonDao = new SeasonDAOImpl();

	private final PersonneService personneService = new PersonneServiceImpl();

	private final MatchDAO matchDAO = new MatchDAOImpl();

	private final CotisationsService cotisationService = new CotisationsService();

	public List<TravelModel> getTravelsPricePerSeason(final SeasonModel model) {
		final Season season = this.seasonDao.getSeasonById(model.getId());
		final List<TravelPrice> travels = this.travelDao.getTravelsPerSeason(season);
		travels.sort(Comparator.comparing(TravelPrice::getMontant));
		final List<TravelModel> travelsModels = new ArrayList<>();
		travels.forEach(t -> travelsModels.add(TravelModel.toModel(t)));

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
		final List<PersonneTravel> personsTravel = this.travelDao.getMatchTravels(match);

		final Set<PersonTravelModel> membersWhoPaidForTravel = personsTravel.stream()
				.filter(f -> f.getPersonne().getMemberNumber() < 10000).map(PersonTravelModel::toModel)
				.collect(Collectors.toSet());
		membersWhoPaidForTravel.forEach(t -> t.setPaid(true));

		final Set<PersonTravelModel> nonMembersWhoPaidForTravel = personsTravel.stream()
				.filter(f -> f.getPersonne().getMemberNumber() >= 10000).map(PersonTravelModel::toModel)
				.collect(Collectors.toSet());
		nonMembersWhoPaidForTravel.forEach(t -> t.setPaid(true));

		// Get members who already paid the travel and subtract that list from
		// all members. this way we know who not yet paid.
		final Set<PersonModel> allMembersToCalculate = new HashSet<>(allMembers);
		final Set<PersonModel> paidMembers = membersWhoPaidForTravel.stream().map(PersonTravelModel::getPerson)
				.collect(Collectors.toSet());
		allMembersToCalculate.removeAll(paidMembers);

		// We have both list of members who paid or not yet paid. Time to create
		// a unique list.
		final List<PersonTravelModel> membersTravel = allMembersToCalculate.stream()
				.map(t -> this.buildMatchTravelModelForUnpaid(t, matchModel, travelsPricePerSeason))
				.distinct()
				.collect(Collectors.toList());
		membersTravel.addAll(membersWhoPaidForTravel);
		membersTravel.sort(Comparator.comparing(o -> o.getPerson().getMemberNumber()));

		final Set<PersonModel> nonMembersWhoNotPaidForTravel = new HashSet<>(this.personneService.findAllPerson(true));
		nonMembersWhoNotPaidForTravel.removeAll(allMembers);
		final List<PersonModel> collect = nonMembersWhoPaidForTravel.stream().map(PersonTravelModel::getPerson)
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
		final List<PersonTravelType> travelType = PersonTravelType.getListFor((int) yearsBetween);
		List<TravelModel> travelModels = new ArrayList<>();
		for (TravelModel travelModel : travelsPricePerSeason) {
			if (this.isSameSeason(matchModel, travelModel)
					&& this.isMember(isMember, travelModel)
					&& this.isCorrespondingtraveltypePerson(travelType, travelModel)
					&& this.hasCorrespondingPlace(matchModel, travelModel)) {
				travelModels.add(travelModel);
			}
		}
//		List<TravelModel> travelModels = travelsPricePerSeason
//				.stream()
//				.filter(t -> this.isSameSeason(matchModel, t) && this.isMember(isMember, t)
//						&& this.isCorrespondingtraveltypePerson(travelType, t)
//						&& this.hasCorrespondingPlace(matchModel, t))
//				.collect(Collectors.toList());
		return travelModels.get(0).getAmount();
	}

	private boolean hasCorrespondingPlace(final MatchModel matchModel, final TravelModel t) {
		return t.getPlace() == matchModel.getPlace();
	}

	private boolean isCorrespondingtraveltypePerson(final List<PersonTravelType> personTravelTypes, final TravelModel t) {
		for (PersonTravelType personTravelType : personTravelTypes) {
			if (personTravelType == t.getTypePersonne()) {
				return true;
			}
		}
		return false;
	}

	private boolean isMember(final boolean isMember, final TravelModel t) {
		return t.getMember() == isMember;
	}

	private boolean isSameSeason(final MatchModel matchModel, final TravelModel t) {
		return t.getSeason().equals(matchModel.getSeason());
	}
}
