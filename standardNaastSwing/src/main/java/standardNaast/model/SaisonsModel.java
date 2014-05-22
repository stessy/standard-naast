/**
 * 
 */
package standardNaast.model;

import java.beans.PropertyChangeSupport;
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;

import standardNaast.entities.Season;
import standardNaast.service.SaisonService;
import standardNaast.service.SaisonServiceImpl;

/**
 * @author stessy
 * 
 */
public class SaisonsModel {

	private ObservableList<Season> saisonsList;

	private Season saison;

	private SaisonService saisonService;

	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);


	public ObservableList<Season> getSaisonsList() {
		if (CollectionUtils.isEmpty(this.saisonsList)) {
			if (this.saisonService == null) {
				this.saisonService = new SaisonServiceImpl();
			}
			this.saisonsList = ObservableCollections.observableList(this.saisonService.findAllSaison());
		}
		Collections.sort(this.saisonsList);
		return this.saisonsList;
	}


	public Season getSelectedSaison() {
		return this.saison;
	}


	public void setSelectedSaison(final Season saison) {
		Season oldSaison = this.saison;
		this.saison = saison;
		this.changeSupport.firePropertyChange("saison", oldSaison, this.saison);
	}
}
