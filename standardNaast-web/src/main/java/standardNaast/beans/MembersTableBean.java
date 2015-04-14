package standardNaast.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.event.SelectEvent;

import standardNaast.entities.Abonnement;
import standardNaast.entities.Benevolat;
import standardNaast.entities.Personne;
import standardNaast.entities.PersonneCotisation;
import standardNaast.service.PersonneService;

@Named(value = "membersTable")
@ViewScoped
public class MembersTableBean implements Serializable {

	private static final long serialVersionUID = 9148227569155809725L;

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
	private PersonneService personneService;

	@Inject
	private MemberTravelsBean memberTravelsBean;

	@PostConstruct
	public void init() {
		final List<Personne> findAllPerson = this.personneService
				.findAllPerson(false);
		Collections.sort(findAllPerson);
		this.members = findAllPerson;
		this.filteredMembers = findAllPerson;
	}

	public List<Personne> getMembers() {
		return this.members;
	}

	public void setMembers(final List<Personne> members) {
		this.members = members;
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

	public boolean filterByName(final Object value, final Object filter,
			final Locale locale) {
		return this.filter(value, filter, locale);
	}

	public boolean filterByFirstName(final Object value, final Object filter,
			final Locale locale) {
		return this.filter(value, filter, locale);
	}

	public boolean filter(final Object value, final Object filter,
			final Locale locale) {
		final String filterText = (filter == null) ? null : filter.toString()
				.trim();
		if (StringUtils.isBlank(filterText)) {
			return true;
		}

		if (value == null) {
			return false;
		}

		final String name = value.toString();

		if (StringUtils.containsIgnoreCase(name, filterText)) {
			return true;
		} else {
			return false;
		}
	}

	public void updateMember(final ActionEvent event) {
		this.personneService.savePerson(this.selectedMember);
		this.init();
	}

}
