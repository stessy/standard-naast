package standardNaast.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import standardNaast.entities.Benevolat;
import standardNaast.entities.Personne;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class BenevolatDAOImpl implements BenevolatDAO {

	private final EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();

	@Override
	public List<Benevolat> getMemberBenevolats(final Personne member) {
		final TypedQuery<Benevolat> query = this.entityManager.createNamedQuery("getMemberBenevolats",
				Benevolat.class);
		query.setParameter("person", member);
		final List<Benevolat> resultList = query.getResultList();
		return resultList;
	}

}
