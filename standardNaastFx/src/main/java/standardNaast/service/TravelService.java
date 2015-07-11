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
import standardNaast.model.MatchTravelsModel;
import standardNaast.model.MemberCotisationsModel;
import standardNaast.model.PersonModel;
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

	public List<MatchTravelsModel> getMatchTravels(final MatchModel matchModel) {
		// Getting all travel prices for a season. This will be used to
		// calculate the travel amount for each member.
		final List<TravelModel> travelsPricePerSeason = this.getTravelsPricePerSeason(matchModel.getSeason());
		// Get all members
		final Set<PersonModel> allMembers = new HashSet<>(this.personneService.findAllPerson(false));
		// Get match from match model
		final Match match = this.matchDAO.getMatch(matchModel.getId());
		// Get match travels that have already been paid
		final List<PersonneTravel> matchTravels = this.travelDao.getMatchTravels(match);
		final Set<MatchTravelsModel> paidTravelSet = matchTravels.stream().map(t -> MatchTravelsModel.toModel(t))
				.collect(Collectors.toSet());
		paidTravelSet.stream().forEach(t -> t.setPaid(true));
		// Get members who already paid the travel and subtract that list from
		// all members. this way we know who not yet paid.
		final Set<PersonModel> membersTravel = paidTravelSet.stream().map(t -> t.getPerson())
				.collect(Collectors.toSet());
		allMembers.removeAll(membersTravel);
		final Set<MatchTravelsModel> unpaidTravelSet = allMembers.stream()
				.map(t -> this.buildMatchTravelModelForUnpaid(t, matchModel, travelsPricePerSeason))
				.collect(Collectors.toSet());
		unpaidTravelSet.addAll(paidTravelSet);
		final List<MatchTravelsModel> unpaidTravelList = new ArrayList<>(unpaidTravelSet);
		Collections.sort(unpaidTravelList, new Comparator<MatchTravelsModel>() {

			@Override
			public int compare(final MatchTravelsModel o1, final MatchTravelsModel o2) {
				return o1.getPerson().getMemberNumber().compareTo(o2.getPerson().getMemberNumber());
			}

		});
		return unpaidTravelList;
	}

	public MatchTravelsModel addMemberMatchTravel(final MatchTravelsModel model) {
		final PersonneTravel entity = MatchTravelsModel.toEntity(model);
		return MatchTravelsModel.toModel(this.travelDao.addMemberMatchTravel(entity));
	}

	public void removeMemberMatchTravel(final MatchTravelsModel model) {
		final PersonneTravel entity = MatchTravelsModel.toEntity(model);
		this.travelDao.removeMemberMatchTravel(entity);
	}

	private MatchTravelsModel buildMatchTravelModelForUnpaid(final PersonModel t, final MatchModel matchModel,
			final List<TravelModel> travelsPricePerSeason) {
		final MatchTravelsModel model = new MatchTravelsModel();
		model.setMatch(matchModel);
		model.setPerson(t);
		model.setPaid(false);
		model.setAmount(this.getMemberTravelPriceForMatch(t, matchModel, travelsPricePerSeason));
		return model;
	}

	private Long getMemberTravelPriceForMatch(final PersonModel model, final MatchModel matchModel,
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
