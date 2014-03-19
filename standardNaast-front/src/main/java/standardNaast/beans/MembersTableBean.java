package standardNaast.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.event.SelectEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import standardNaast.entities.Abonnement;
import standardNaast.entities.Personne;
import standardNaast.service.PersonneService;

@Controller("membersTable")
@Scope("session")
public class MembersTableBean implements Serializable {

	private static final long serialVersionUID = 7614568701955199215L;

	private List<Personne> members = new ArrayList<>();

	private List<Personne> filteredMembers = new ArrayList<>();

	private Personne selectedMember;

	private List<Abonnement> abonnements;

	@Autowired
	private MemberAbonnementBean memberAbonnementBean;

	@Autowired
	private MemberFormBean memberForm;

	@Autowired
	private PersonneService personneService;

	@PostConstruct
	public void init() {
		System.out.println("Initializing MembersTableBean");
		List<Personne> findAllPerson = this.personneService.findAllPerson();
		Collections.sort(findAllPerson);
		this.members = findAllPerson;
		this.filteredMembers = findAllPerson;
	}

	public List<Personne> getMembers() {
		return this.members;
	}

	public List<Personne> getFilteredMembers() {
		return this.filteredMembers;
	}

	public void setFilteredMembers(final List<Personne> filteredMembers) {
		this.filteredMembers = filteredMembers;
	}

	public Personne getSelectedMember() {
		return this.selectedMember;
	}

	public void setSelectedMember(final Personne selectedMember) {
		this.selectedMember = selectedMember;
		this.setAbonnements(this.getSelectedMember().getAbonnementList());
	}

	public void onRowSelect(final SelectEvent event) {
		this.memberForm.setPersonne(this.getSelectedMember());
		this.memberAbonnementBean.setAbonnements(this.getAbonnements());
	}

	public List<Abonnement> getAbonnements() {
		return this.abonnements;
	}

	public void setAbonnements(final List<Abonnement> abonnements) {
		this.abonnements = abonnements;
	}
}
