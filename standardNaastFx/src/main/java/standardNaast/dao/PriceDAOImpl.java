package standardNaast.dao;

import javax.persistence.EntityManager;

import standardNaast.entities.Prices;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class PriceDAOImpl implements PriceDAO {

	private final EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();

	@Override
	public void addPrice(final Prices price) {
		this.entityManager.getTransaction().begin();
		this.entityManager.persist(price);
		this.entityManager.getTransaction().commit();
	}

}
