package standardNaast.observer;


public interface MatchSubject {

	public void registerObserver(SeasonObserver observer);

	public void removeObserver(SeasonObserver observer);

	public void notifyObservers();

}
