package standardNaast.service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import standardNaast.entities.Accounting;
import standardNaast.model.AccountingModel;
import standardNaast.utils.DateUtils;

import com.standardnaast.persistence.EntityManagerFactoryHelper;

public class AccountingService {

	private final EntityManager entityManager = EntityManagerFactoryHelper.getFactory().createEntityManager();

	public List<AccountingModel> getAccountingsWithinMonth(final int month, final int year) {
		final LocalDate date = LocalDate.of(year, month, 1);
		final LocalDate start = date.withDayOfMonth(1);
		final LocalDate end = date.withDayOfMonth(date.lengthOfMonth());
		final TypedQuery<Accounting> query = this.entityManager.createNamedQuery("withinMonth", Accounting.class);
		query.setParameter("startDate", DateUtils.toDate(start));
		query.setParameter("endDate", DateUtils.toDate(end));
		final List<Accounting> resultList = query.getResultList();
		resultList.stream().forEach(a -> this.entityManager.refresh(a));
		return resultList.stream().map(a -> AccountingModel.toModel(a)).collect(Collectors.toList());
	}

	public List<AccountingModel> getAccountingsWithinYear(final int year) {
		final LocalDate date = LocalDate.of(year, 1, 1);
		final LocalDate start = date.withDayOfMonth(1);
		final LocalDate end = date.withDayOfYear(date.lengthOfYear());
		final TypedQuery<Accounting> query = this.entityManager.createNamedQuery("withinMonth", Accounting.class);
		query.setParameter("startDate", DateUtils.toDate(start));
		query.setParameter("endDate", DateUtils.toDate(end));
		final List<Accounting> resultList = query.getResultList();
		return resultList.stream().map(a -> AccountingModel.toModel(a)).collect(Collectors.toList());
	}

	public AccountingModel addAccounting(final AccountingModel model) {
		final Accounting entity = AccountingModel.toEntity(model);
		this.entityManager.getTransaction().begin();
		this.entityManager.persist(entity);
		this.entityManager.getTransaction().commit();
		return AccountingModel.toModel(entity);
	}

	public void deleteAccounting(final AccountingModel model) {
		this.entityManager.getTransaction().begin();
		final Accounting foundAccounting = this.entityManager.find(Accounting.class, model.getId());
		this.entityManager.remove(foundAccounting);
		this.entityManager.getTransaction().commit();
	}

	public AccountingModel updateAccounting(final AccountingModel model) {
		this.entityManager.getTransaction().begin();
		Accounting foundAccounting = this.entityManager.find(Accounting.class, model.getId());
		foundAccounting = AccountingModel.toEntity(model, foundAccounting);
		this.entityManager.merge(foundAccounting);
		this.entityManager.getTransaction().commit();
		return AccountingModel.toModel(foundAccounting);
	}

	public int getMaxAccountingYear() {
		int year;
		final TypedQuery<Date> query = this.entityManager.createNamedQuery("maxYear", Date.class);
		final List<Date> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			year = LocalDate.now().getYear();
		}
		else {
			year = DateUtils.toLocalDate(resultList.get(0)).getYear();
		}
		return year;
	}

	public int getMinAccountingYear() {
		int year;
		final TypedQuery<Date> query = this.entityManager.createNamedQuery("minYear", Date.class);
		final List<Date> resultList = query.getResultList();
		if (resultList.isEmpty()) {
			year = LocalDate.now().getYear();
		}
		else {
			year = DateUtils.toLocalDate(resultList.get(0)).getYear();
		}
		return year;
	}
}
