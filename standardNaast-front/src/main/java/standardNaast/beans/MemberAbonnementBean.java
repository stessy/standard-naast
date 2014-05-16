package standardNaast.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import standardNaast.entities.Abonnement;

@Named(value = "memberAbonnement")
@RequestScoped
public class MemberAbonnementBean implements Serializable {

	private static final long serialVersionUID = 423300322596970656L;

	private List<Abonnement> abonnements;

	private Abonnement selectedAbonnement;

	private boolean rowSelected;

	public List<Abonnement> getAbonnements() {
		return this.abonnements;
	}

	public void setAbonnements(List<Abonnement> abonnements) {
		Collections.reverse(abonnements);
		this.abonnements = abonnements;
	}

	public Abonnement getSelectedAbonnement() {
		return this.selectedAbonnement;
	}

	public void setSelectedAbonnement(Abonnement selectedAbonnement) {
		this.selectedAbonnement = selectedAbonnement;

	}

	public boolean isRowSelected() {
		return this.rowSelected;
	}

	public void setRowSelected(boolean rowSelected) {
		this.rowSelected = rowSelected;
	}

	public void onRowSelect(SelectEvent event) {
		this.setRowSelected(true);
	}
}
