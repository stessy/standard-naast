/**
 * 
 */
package standardNaast.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import standardNaast.entities.Saison;

/**
 * @author stessy
 * 
 */
public class SeasonUtils {

	public static List<Saison> getCotisationsEuropeanSeasons(final List<Saison> seasonList) {
		Date today = new Date();
		Collections.sort(seasonList);
		Collections.reverse(seasonList);
		List<Saison> subSeasonList = new ArrayList<Saison>();
		boolean passedOnce = false;
		for (Saison season : seasonList) {
			if (today.compareTo(season.getDateStart()) == 1) {
				if (season.isEuropean()) {
					if (passedOnce) {
						break;
					} else {
						subSeasonList.add(season);
					}
				} else {
					subSeasonList.add(season);
				}
				passedOnce = true;
			}
		}
		return subSeasonList;
	}
}
