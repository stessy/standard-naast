package standardNaast.dao;

import javax.persistence.EntityManager;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class MatchDAOImpl implements MatchDAO {

	private final EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();
	
	public void addMatch
}
