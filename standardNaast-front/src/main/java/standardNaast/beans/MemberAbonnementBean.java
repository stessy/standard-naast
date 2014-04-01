package standardNaast.beans;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import standardNaast.entities.Abonnement;

@Controller("memberAbonnement")
@Scope("session")
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
