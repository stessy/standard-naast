/**
 * 
 */
package standardNaast.service;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.EntityManager;

import standardNaast.entities.Benevolat;
import standardNaast.entities.Personne;
import standardNaast.model.BenevolatModel;
import standardNaast.utils.DateUtils;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

/**
 * @author stessy
 * 
 */
public class BenevolatService implements Serializable {

	public Benevolat addBenevolat(final BenevolatModel model, final Personne personne) {
		final Benevolat benevolat = new Benevolat();
		this.fillBenevolat(model, benevolat);
		final EntityManager entityManager = this.getEntityManager();
		entityManager.getTransaction().begin();
		final Personne managedPersonne = entityManager.merge(personne);
		managedPersonne.getBenevolatList().add(benevolat);
		benevolat.setPersonne(managedPersonne);
		entityManager.persist(benevolat);
		entityManager.merge(personne);
		entityManager.getTransaction().commit();
		entityManager.close();
		return benevolat;
	}

	private void fillBenevolat(final BenevolatModel model, final Benevolat benevolat) {
		benevolat.setAmount(new BigDecimal(model.getMontant()));
		benevolat.setDateBenevolat(DateUtils.toDate(model.getDate()));
		benevolat.setTypeBenevolat(model.getDescription());
	}

	public void deleteBenevolat(final BenevolatModel selectedBenevolat) {
		final Long id = selectedBenevolat.getId();
		final EntityManager entityManager = this.getEntityManager();
		entityManager.getTransaction().begin();
		final Benevolat benevolat = entityManager.find(Benevolat.class, id);
		entityManager.remove(benevolat);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	public void saveBenevolat(final BenevolatModel selectedBenevolat) {
		final Long id = selectedBenevolat.getId();
		final EntityManager entityManager = this.getEntityManager();
		entityManager.getTransaction().begin();
		final Benevolat benevolat = entityManager.find(Benevolat.class, id);
		this.fillBenevolat(selectedBenevolat, benevolat);
		entityManager.merge(benevolat);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	private EntityManager getEntityManager() {
		return EntityManagerFactoryHelper.getFactory().createEntityManager();
	}

}
