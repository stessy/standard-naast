/**
 * 
 */
package standardNaast.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import standardNaast.entities.Season;

/**
 * @author stessy
 * 
 */
public class SeasonUtils {

	public static List<Season> getCotisationsEuropeanSeasons(final List<Season> seasonList) {
		Date today = new Date();
		Collections.sort(seasonList);
		Collections.reverse(seasonList);
		List<Season> subSeasonList = new ArrayList<Season>();
		boolean passedOnce = false;
		for (Season season : seasonList) {
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
