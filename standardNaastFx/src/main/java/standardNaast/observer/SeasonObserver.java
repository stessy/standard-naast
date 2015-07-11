package standardNaast.observer;

import java.util.List;

import standardNaast.model.SeasonModel;

/**
 * @author stessy
 *
 */
public interface SeasonObserver {

	void update(final List<SeasonModel> seasons);

}
