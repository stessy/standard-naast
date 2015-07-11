package standardNaast.observer;

import java.util.ArrayList;
import java.util.List;

import standardNaast.model.SeasonModel;
import standardNaast.service.SeasonService;
import standardNaast.service.SeasonServiceImpl;

public class MatchSubjectImpl implements MatchSubject {

	private final SeasonService seasonService = new SeasonServiceImpl();

	private List<SeasonModel> seasons = new ArrayList<>();

	private final List<SeasonObserver> observers = new ArrayList<>();

	private static final MatchSubjectImpl SUBJECT = new MatchSubjectImpl();

	private MatchSubjectImpl() {
		this.seasons = this.seasonService.findAllSaison();
	}

	public static MatchSubjectImpl getInstance() {
		return MatchSubjectImpl.SUBJECT;
	}

	@Override
	public void registerObserver(final SeasonObserver observer) {
		this.observers.add(observer);
	}

	@Override
	public void removeObserver(final SeasonObserver observer) {
		this.observers.remove(observer);

	}

	@Override
	public void notifyObservers() {
		this.seasons = this.seasonService.findAllSaison();
		for (final SeasonObserver observer : this.observers) {
			observer.update(this.seasons);
		}
	}

	public List<SeasonModel> getSeasons() {
		return this.seasons;
	}

}
