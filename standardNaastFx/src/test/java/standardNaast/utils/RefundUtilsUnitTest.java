package standardNaast.utils;

import org.junit.Assert;
import org.junit.Test;

public class RefundUtilsUnitTest {

	@Test
	public void getRefundTest() {
		int travels = 0;
		Assert.assertEquals(0, RefundsUtils.getRefund(travels));
		travels = 11;
		Assert.assertEquals(0, RefundsUtils.getRefund(travels));
		travels = 12;
		Assert.assertEquals(5, RefundsUtils.getRefund(travels));
		travels = 13;
		Assert.assertEquals(7, RefundsUtils.getRefund(travels));
		travels = 14;
		Assert.assertEquals(9, RefundsUtils.getRefund(travels));
		travels = 15;
		Assert.assertEquals(11, RefundsUtils.getRefund(travels));
		travels = 16;
		Assert.assertEquals(13, RefundsUtils.getRefund(travels));
		travels = 17;
		Assert.assertEquals(15, RefundsUtils.getRefund(travels));
		travels = 18;
		Assert.assertEquals(17, RefundsUtils.getRefund(travels));
		travels = 19;
		Assert.assertEquals(19, RefundsUtils.getRefund(travels));
		travels = 20;
		Assert.assertEquals(20, RefundsUtils.getRefund(travels));
	}

}
