/**
 * 
 */
package standardNaast.model;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import org.jdesktop.observablecollections.ObservableCollections;

import standardNaast.entities.Benevolat;

/**
 * @author stessy
 * 
 */
public class BenevolatModel {

	private Benevolat benevolat;

	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	private List<Benevolat> benevolatList = ObservableCollections.observableList(new ArrayList<Benevolat>());


	public Benevolat getSelectedBenevolat() {
		return this.benevolat;
	}


	public void setSelectedBenevolat(final Benevolat benevolat) {
		Benevolat oldBenevolat = this.benevolat;
		this.benevolat = benevolat;
		this.changeSupport.firePropertyChange("benevolat", oldBenevolat, this.benevolat);
	}


	public void setBenevolats(final List<Benevolat> benevolatList) {
		List<Benevolat> oldBenevolatList = this.benevolatList;
		this.benevolatList = ObservableCollections.observableList(benevolatList);
		this.changeSupport.firePropertyChange("benevolatList", oldBenevolatList, benevolatList);
	}


	public List<Benevolat> getBenevolatList() {
		return this.benevolatList;
	}
}
