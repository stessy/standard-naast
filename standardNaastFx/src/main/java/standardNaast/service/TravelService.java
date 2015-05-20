/**
 * 
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import standardNaast.dao.SeasonDAO;
import standardNaast.dao.SeasonDAOImpl;
import standardNaast.dao.TravelDAO;
import standardNaast.dao.TravelDAOImpl;
import standardNaast.entities.Season;
import standardNaast.entities.TravelPrice;
import standardNaast.model.SeasonModel;
import standardNaast.model.TravelModel;

/**
 * @author stessy
 * 
 */
public class TravelService implements Serializable {

	TravelDAO travelDao = new TravelDAOImpl();

	SeasonDAO seasonDao = new SeasonDAOImpl();

	public List<TravelModel> getTravelsPerSeason(final SeasonModel model) {
		final Season season = this.seasonDao.getSeasonById(model.getId());
		final List<TravelPrice> travels = this.travelDao.getTravelsPerSeason(season);
		Collections.sort(travels, new Comparator<TravelPrice>() {

			@Override
			public int compare(final TravelPrice o1, final TravelPrice o2) {
				return o1.getMontant().compareTo(o2.getMontant());
			}
		});
		final List<TravelModel> travelsModels = new ArrayList<>();
		travels.stream().forEach(t -> travelsModels.add(TravelModel.toModel(t)));

		return travelsModels;
	}
}
