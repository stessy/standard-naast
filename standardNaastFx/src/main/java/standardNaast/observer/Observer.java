package standardNaast.observer;

import java.util.List;

import standardNaast.entities.Season;

/**
 * @author stessy
 *
 */
public interface Observer {

	void update(final List<Season> seasons);

}
