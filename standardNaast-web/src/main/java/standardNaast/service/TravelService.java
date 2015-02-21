/**
 * 
 */
package standardNaast.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

import standardNaast.dao.TravelDAO;
import standardNaast.entities.Travel;

/**
 * @author stessy
 * 
 */
@Stateless
@LocalBean
public class TravelService implements Serializable {

	@Inject
	TravelDAO travelDao;

	public List<Travel> getAllTravels() {
		List<Travel> travels = this.travelDao.getAllTravels();
		Collections.sort(travels, new Comparator<Travel>() {

			@Override
			public int compare(Travel o1, Travel o2) {
				return o1.getAnnee().compareTo(o2.getAnnee()) * -1;
			}
		});
		return travels;
	}

}
