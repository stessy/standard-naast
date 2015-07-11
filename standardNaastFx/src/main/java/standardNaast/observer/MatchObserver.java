package standardNaast.observer;

import java.util.List;

import standardNaast.model.SeasonModel;

/**
 * @author stessy
 *
 */
public interface MatchObserver {

	void update(final List<SeasonModel> seasons);

}
