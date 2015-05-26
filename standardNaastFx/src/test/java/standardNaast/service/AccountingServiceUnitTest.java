package standardNaast.service;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

import standardNaast.model.AccountingModel;
import standardNaast.types.AccountingType;

public class AccountingServiceUnitTest {

	@Test
	public void testAddUpdateAndRemoveAccounting() {
		final AccountingModel model = new AccountingModel();
		final LocalDate now = LocalDate.now();
		model.setAccountingDate(now);
		model.setDescription("testDescription");
		model.setType(AccountingType.ENTRY);
		final AccountingService service = new AccountingService();
		final AccountingModel addAccounting = service.addAccounting(model);
		Assert.assertNotNull(addAccounting.getId());
		Assert.assertEquals("testDescription", addAccounting.getDescription());
		Assert.assertEquals(AccountingType.ENTRY, addAccounting.getType());
		addAccounting.setType(AccountingType.EXIT);
		final AccountingModel updateAccounting = service.updateAccounting(addAccounting);
		Assert.assertNotNull(updateAccounting.getId());
		Assert.assertEquals("testDescription", updateAccounting.getDescription());
		Assert.assertEquals(AccountingType.EXIT, updateAccounting.getType());
		// service.deleteAccounting(updateAccounting);
		// final List<AccountingModel> accountingsWithinMonth =
		// service.getAccountingsWithinMonth(now.getMonthValue(),
		// now.getYear());
		// Assert.assertEquals(0, accountingsWithinMonth.size());
		// final List<AccountingModel> accountingsWithinYear =
		// service.getAccountingsWithinYear(now.getYear());
		// Assert.assertEquals(0, accountingsWithinYear.size());
	}

}
