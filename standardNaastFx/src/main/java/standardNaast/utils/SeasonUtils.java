/**
 * 
 */
package standardNaast.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import standardNaast.model.SeasonModel;

/**
 * @author stessy
 * 
 */
public class SeasonUtils {

	public static List<SeasonModel> getCotisationsEuropeanSeasons(final List<SeasonModel> seasonList) {
		final LocalDate today = LocalDate.now();
		Collections.sort(seasonList);
		Collections.reverse(seasonList);
		final List<SeasonModel> subSeasonList = new ArrayList<>();
		boolean passedOnce = false;
		for (final SeasonModel season : seasonList) {
			if (today.compareTo(season.getStartDate()) > 0) {
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
