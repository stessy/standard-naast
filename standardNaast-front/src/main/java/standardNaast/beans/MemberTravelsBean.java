package standardNaast.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.Personne;
import standardNaast.service.SaisonService;

//@Controller("memberTravelsBean")
//@Scope("session")
@Named(value = "memberTravelsBean")
@SessionScoped
public class MemberTravelsBean implements Serializable {

	private static final long serialVersionUID = -6906759511098537955L;

	private String selectedSeason;

	private Personne selectedMember;

	private int home;

	private int away;

	private int total;

	// @Autowired
	@Inject
	private SaisonService saisonService;

	public MemberTravelsBean() {
		System.out.println("Initializing MemberTravelsBean");
	}

	public String getSelectedSeason() {
		return this.selectedSeason;
	}

	public void setSelectedSeason(String selectedSeason) {
		this.selectedSeason = selectedSeason;
	}

	public int getHome() {
		return this.home;
	}

	public void setHome(int home) {
		this.home = home;
	}

	public int getAway() {
		return this.away;
	}

	public void setAway(int away) {
		this.away = away;
	}

	public int getTotal() {
		return this.total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public Personne getSelectedMember() {
		return this.selectedMember;
	}

	public void setSelectedMember(Personne selectedMember) {
		this.selectedMember = selectedMember;
	}

	public void setSeasonTravels() {
		Travels travelsPerSeason = this.saisonService.getTravelsPerSeason(
				this.getSelectedSeason(), this.getSelectedMember());
		int awayResult = travelsPerSeason.getAway();
		this.setAway(awayResult);
		int homeResult = travelsPerSeason.getHome();
		this.setHome(homeResult);
		this.setTotal(homeResult + awayResult);
	}

}
