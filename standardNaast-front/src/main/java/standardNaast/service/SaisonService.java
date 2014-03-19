/**
 *
 */
package standardNaast.service;

import java.util.List;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import standardNaast.dao.SaisonDAO;
import standardNaast.entities.Saison;

/**
 * @author stessy
 * 
 */
@Named
@Service("saisonService")
@Transactional(readOnly = true)
public class SaisonService {

	@Autowired
	SaisonDAO saisonDAO;

	private static final Logger LOGGER = Logger.getLogger(SaisonService.class);

	public List<Saison> findAllSaison() {
		return this.saisonDAO.getAllSeasons();
	}
}
