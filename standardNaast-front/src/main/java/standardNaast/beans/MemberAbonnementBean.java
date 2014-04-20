package standardNaast.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import standardNaast.entities.Abonnement;

//@Controller("memberAbonnement")
//@Scope("session")
@Named(value = "memberAbonnement")
@SessionScoped
public class MemberAbonnementBean implements Serializable {

	private static final long serialVersionUID = 423300322596970656L;

	private List<Abonnement> abonnements;

	public List<Abonnement> getAbonnements() {
		return this.abonnements;
	}

	public void setAbonnements(List<Abonnement> abonnements) {
		Collections.reverse(abonnements);
		this.abonnements = abonnements;
	}
}
