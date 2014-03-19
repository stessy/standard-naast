package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.stereotype.Repository;

import standardNaast.entities.Saison;

@Repository
public class SaisonDAOImpl implements SaisonDAO {

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public List<Saison> getAllSeasons() {
		CriteriaQuery<Saison> queryAll = this.getEntityManager()
				.getCriteriaBuilder().createQuery(Saison.class);
		queryAll.from(Saison.class);
		return this.getEntityManager().createQuery(queryAll).getResultList();

	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

	public void setEntityManager(final EntityManager entityManager) {
		this.entityManager = entityManager;
	}

}
