package standardNaast.observer;

import java.util.ArrayList;
import java.util.List;

import standardNaast.model.SeasonModel;
import standardNaast.service.SeasonService;
import standardNaast.service.SeasonServiceImpl;

public class SubjectImpl implements Subject {

	private final SeasonService seasonService = new SeasonServiceImpl();

	private List<SeasonModel> seasons = new ArrayList<>();

	private final List<Observer> observers = new ArrayList<>();

	private static final SubjectImpl SUBJECT = new SubjectImpl();

	private SubjectImpl() {
		this.seasons = this.seasonService.findAllSaison();
	}

	public static SubjectImpl getInstance() {
		return SubjectImpl.SUBJECT;
	}

	@Override
	public void registerObserver(final Observer observer) {
		this.observers.add(observer);
	}

	@Override
	public void removeObserver(final Observer observer) {
		this.observers.remove(observer);

	}

	@Override
	public void notifyObservers() {
		this.seasons = this.seasonService.findAllSaison();
		for (final Observer observer : this.observers) {
			observer.update(this.seasons);
		}
	}

	public List<SeasonModel> getSeasons() {
		return this.seasons;
	}

}
