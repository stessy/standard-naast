/**
 * 
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import standardNaast.dao.TravelDAO;
import standardNaast.dao.TravelDAOImpl;
import standardNaast.entities.TravelPrice;
import standardNaast.model.SeasonModel;

/**
 * @author stessy
 * 
 */
public class TravelService implements Serializable {

	TravelDAO travelDao = new TravelDAOImpl();

	public List<TravelPrice> getAllTravels() {
		final List<TravelPrice> travels = this.travelDao.getAllTravels();
		Collections.sort(travels, new Comparator<TravelPrice>() {

			@Override
			public int compare(final TravelPrice o1, final TravelPrice o2) {
				return o1.getAnnee().compareTo(o2.getAnnee()) * -1;
			}
		});
		return travels;
	}

	public List<TravelPrice> getTravelsPerSeason(final SeasonModel model) {
		final List<TravelPrice> travels = this.travelDao.getAllTravels();
		Collections.sort(travels, new Comparator<TravelPrice>() {

			@Override
			public int compare(final TravelPrice o1, final TravelPrice o2) {
				return o1.getAnnee().compareTo(o2.getAnnee()) * -1;
			}
		});
		return travels;
	}

}
