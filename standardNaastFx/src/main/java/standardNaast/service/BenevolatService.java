/**
 * 
 */
package standardNaast.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import standardNaast.dao.PersonDAO;
import standardNaast.dao.PersonDAOImpl;
import standardNaast.dao.SeasonDAO;
import standardNaast.dao.SeasonDAOImpl;
import standardNaast.entities.Benevolat;
import standardNaast.entities.Personne;
import standardNaast.model.BenevolatModel;
import standardNaast.model.PersonModel;
import standardNaast.model.SeasonModel;
import standardNaast.utils.DateUtils;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

/**
 * @author stessy
 * 
 */
public class BenevolatService implements Serializable {

	private final PersonDAO personDAO = new PersonDAOImpl();

	private final SeasonDAO seasonDAO = new SeasonDAOImpl();

	public Benevolat addBenevolat(final BenevolatModel model, final PersonModel personModel) {
		final Benevolat benevolat = new Benevolat();
		this.fillBenevolat(model, benevolat);
		final EntityManager entityManager = this.getEntityManager();
		entityManager.getTransaction().begin();
		final Personne person = this.personDAO.getPerson(personModel.getPersonneId());
		final Personne managedPersonne = entityManager.merge(person);
		managedPersonne.getBenevolatList().add(benevolat);
		benevolat.setPersonne(managedPersonne);
		entityManager.persist(benevolat);
		entityManager.merge(managedPersonne);
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

	public List<BenevolatModel> getBenevolats(final PersonModel model) {
		final Personne person = this.personDAO.getPerson(model.getPersonneId());
		final List<Benevolat> benevolats = person.getBenevolatList();
		final List<BenevolatModel> benevolatsModel = benevolats.stream().map(b -> BenevolatModel.toModel(b))
				.collect(Collectors.toList());
		return benevolatsModel;
	}

	public List<BenevolatModel> getBenevolatsForASeason(final PersonModel model, final SeasonModel seasonModel) {
		final List<BenevolatModel> benevolats = this.getBenevolats(model);
		final LocalDate seasonStartDate = seasonModel.getStartDate();
		final LocalDate seasonEndDate = seasonModel.getEndDate();
		return benevolats.stream().filter(new Predicate<BenevolatModel>() {
			@Override
			public boolean test(final BenevolatModel t) {
				return t.getDate().isAfter(seasonStartDate) && t.getDate().isBefore(seasonEndDate);
			}
		}).collect(Collectors.toList());
	}

	private EntityManager getEntityManager() {
		return EntityManagerFactoryHelper.getFactory().createEntityManager();
	}

}
