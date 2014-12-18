package standardNaast.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import standardNaast.entities.Abonnement;
import standardNaast.entities.Benevolat;
import standardNaast.entities.Personne;
import standardNaast.entities.PersonneCotisation;
import standardNaast.service.PersonneServiceImpl;

@Named(value = "membersTable")
@ViewScoped
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
		final List<Personne> findAllPerson = this.personneService
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

	public void setFilteredMembers(final List<Personne> filteredMembers) {
		this.filteredMembers = filteredMembers;
	}

	public Personne getSelectedMember() {
		return this.selectedMember;
	}

	public void setSelectedMember(final Personne selectedMember) {
		this.selectedMember = selectedMember;
	}

	public void onRowSelect(final SelectEvent event) {
		this.memberForm.setUpdate(true);
		this.memberForm.setSelectedPerson(this.getSelectedMember());
		final Personne person = this.personneService.getPerson(this
				.getSelectedMember().getPersonneId());
		final List<Abonnement> abonnementList = person.getAbonnementList();
		final List<Benevolat> benevolatList = person.getBenevolatList();
		final List<PersonneCotisation> personnesCotisations = person
				.getPersonnesCotisations();
		this.memberAbonnementBean.setAbonnements(abonnementList);
		this.memberCotisationsBean.setCotisations(personnesCotisations);
		this.memberBenevolatBean.setBenevolats(benevolatList);
		this.memberTravelsBean.setSelectedMember(this.getSelectedMember());
		this.memberTravelsBean.setSelectedSeason("");
		this.memberTravelsBean.setSeasonTravels();
	}

}
