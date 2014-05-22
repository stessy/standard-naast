/**
 * 
 */
package standardNaast.model;

import java.beans.PropertyChangeSupport;

import standardNaast.entities.Cotisation;

/**
 * @author stessy
 * 
 */
public class CotisationModel {

	private Cotisation cotisation;

	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);


	public Cotisation getSelectedCotisation() {
		return this.cotisation;
	}


	public void setSelectedCotisation(final Cotisation cotisation) {
		Cotisation oldCotisation = this.cotisation;
		this.cotisation = cotisation;
		this.changeSupport.firePropertyChange("cotisation", oldCotisation, this.cotisation);
	}
}
