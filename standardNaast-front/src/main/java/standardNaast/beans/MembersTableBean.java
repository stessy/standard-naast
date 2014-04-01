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

import standardNaast.entities.Personne;
import standardNaast.service.PersonneService;

@Controller("membersTable")
@Scope("session")
public class MembersTableBean implements Serializable {

	private static final long serialVersionUID = 7614568701955199215L;

	private List<Personne> members = new ArrayList<>();

	private List<Personne> filteredMembers = new ArrayList<>();

	private Personne selectedMember;

	@Autowired
	private MemberAbonnementBean memberAbonnementBean;

	@Autowired
	private MemberCotisationsBean memberCotisationsBean;

	@Autowired
	private MemberBenevolatBean memberBenevolatBean;

	@Autowired
	private MemberFormBean memberForm;

	@Autowired
	private PersonneService personneService;

	@Autowired
	private MemberTravelsBean memberTravelsBean;

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
		this.memberAbonnementBean.setAbonnements(this.getSelectedMember()
				.getAbonnementList());
		this.memberCotisationsBean.setCotisations(this.getSelectedMember()
				.getPersonnesCotisations());
		this.memberBenevolatBean.setBenevolats(this.getSelectedMember()
				.getBenevolatList());
		this.memberTravelsBean.setSelectedMember(this.getSelectedMember());
	}

}
