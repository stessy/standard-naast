package standardNaast.beans;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.Personne;
import standardNaast.model.MemberSeasonTravels;
import standardNaast.service.SeasonServiceImpl;

@Named(value = "memberTravelsBean")
@SessionScoped
public class MemberTravelsBean implements Serializable {

	private static final long serialVersionUID = -6906759511098537955L;

	private String selectedSeason;

	private Personne selectedMember;

	private int home;

	private int away;

	private int total;

	@Inject
	private SeasonServiceImpl saisonService;

	public MemberTravelsBean() {
		System.out.println("Initializing MemberTravelsBean");
	}

	public String getSelectedSeason() {
		return this.selectedSeason;
	}

	public void setSelectedSeason(final String selectedSeason) {
		this.selectedSeason = selectedSeason;
	}

	public int getHome() {
		return this.home;
	}

	public void setHome(final int home) {
		this.home = home;
	}

	public int getAway() {
		return this.away;
	}

	public void setAway(final int away) {
		this.away = away;
	}

	public int getTotal() {
		return this.total;
	}

	public void setTotal(final int total) {
		this.total = total;
	}

	public Personne getSelectedMember() {
		return this.selectedMember;
	}

	public void setSelectedMember(final Personne selectedMember) {
		this.selectedMember = selectedMember;
	}

	public void setSeasonTravels() {
		final MemberSeasonTravels travelsPerSeason = this.saisonService
				.getTravelsPerSeason(this.getSelectedSeason(),
						this.getSelectedMember());
		final int awayResult = travelsPerSeason.getAway();
		this.setAway(awayResult);
		final int homeResult = travelsPerSeason.getHome();
		this.setHome(homeResult);
		this.setTotal(homeResult + awayResult);
	}

}
