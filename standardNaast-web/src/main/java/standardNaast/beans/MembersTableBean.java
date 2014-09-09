package standardNaast.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import standardNaast.entities.Abonnement;
import standardNaast.entities.Benevolat;
import standardNaast.entities.Personne;
import standardNaast.entities.PersonneCotisation;
import standardNaast.service.PersonneServiceImpl;

@Named(value = "membersTable")
@SessionScoped
public class MembersTableBean implements Serializable {

	private static final long serialVersionUID = 7614568701955199215L;

	private List<Personne> members = new ArrayList<>();

	private List<Personne> filteredMembers = new ArrayList<>();

	private Personne selectedMember;

	@Inject
	private MemberAbonnementBean memberAbonnementBean;

	@Inject
	private MemberCotisationsBean memberCotisationsBean;

	@Inject
	private MemberBenevolatBean memberBenevolatBean;

	@Inject
	private MemberFormBean memberForm;

	@Inject
	private PersonneServiceImpl personneService;

	@Inject
	private MemberTravelsBean memberTravelsBean;

	@PostConstruct
	public void init() {
		System.out.println("Initializing MembersTableBean");
		List<Personne> findAllPerson = this.personneService
				.findAllPerson(false);
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

	public void setFilteredMembers(List<Personne> filteredMembers) {
		this.filteredMembers = filteredMembers;
	}

	public Personne getSelectedMember() {
		return this.selectedMember;
	}

	public void setSelectedMember(Personne selectedMember) {
		this.selectedMember = selectedMember;
	}

	public void onRowSelect(SelectEvent event) {
		this.memberForm.setPersonne(this.getSelectedMember());
		Personne person = this.personneService.getPerson(this
				.getSelectedMember().getPersonneId());
		List<Abonnement> abonnementList = person.getAbonnementList();
		List<Benevolat> benevolatList = person.getBenevolatList();
		List<PersonneCotisation> personnesCotisations = person
				.getPersonnesCotisations();
		this.memberAbonnementBean.setAbonnements(abonnementList);
		this.memberCotisationsBean.setCotisations(personnesCotisations);
		this.memberBenevolatBean.setBenevolats(benevolatList);
		this.memberTravelsBean.setSelectedMember(this.getSelectedMember());
	}

}
