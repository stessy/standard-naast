package standardNaast.beans;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import standardNaast.entities.Travel;
import standardNaast.service.TravelService;

@Named("travelsTableBean")
@RequestScoped
public class TravelsTableBean {

	private List<Travel> allTravels;

	private Travel selectedTravel;

	@Inject
	TravelService travelService;

	@PostConstruct
	public void init() {
		this.setAllTravels(this.travelService.getAllTravels());
	}

	public List<Travel> getAllTravels() {
		return this.allTravels;
	}

	public void setAllTravels(List<Travel> allTravels) {
		this.allTravels = allTravels;
	}

	public Travel getSelectedTravel() {
		return selectedTravel;
	}

	public void setSelectedTravel(Travel selectedTravel) {
		this.selectedTravel = selectedTravel;
	}

}
