/**
 * 
 */
package standardNaast.model;

import java.beans.PropertyChangeSupport;
import java.util.Collections;

import org.apache.commons.collections.CollectionUtils;
import org.jdesktop.observablecollections.ObservableCollections;
import org.jdesktop.observablecollections.ObservableList;

import standardNaast.entities.Personne;
import standardNaast.service.PersonneService;
import standardNaast.service.PersonneServiceImpl;

/**
 * @author stessy
 * 
 */
public class PersonnesModel {

	private ObservableList<Personne> personnesList;

	private Personne personne;

	private PersonneService personneService;

	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);


	public ObservableList<Personne> getPersonnesList() {
		if (CollectionUtils.isEmpty(this.personnesList)) {
			if (this.personneService == null) {
				this.personneService = new PersonneServiceImpl();
			}
			this.personnesList = ObservableCollections.observableList(this.personneService.findAllPerson());
		}
		Collections.sort(this.personnesList);
		return this.personnesList;
	}


	public Personne getSelectedPersonne() {
		return this.personne;
	}


	public void setSelectedPersonne(final Personne personne) {
		Personne oldPersonne = this.personne;
		this.personne = personne;
		this.changeSupport.firePropertyChange("personne", oldPersonne, this.personne);
	}
}
