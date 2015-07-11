package standardNaast.observer;


public interface SeasonSubject {

	public void registerObserver(SeasonObserver observer);

	public void removeObserver(SeasonObserver observer);

	public void notifyObservers();

}
