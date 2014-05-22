/**
 * 
 */
package standardNaast.viewNew;

/**
 * @author stessy
 * 
 */
public interface Subject {

	public void registerObserver(Observer observer);


	public void removeObserver(Observer observer);


	public void notifyObservers();

}
