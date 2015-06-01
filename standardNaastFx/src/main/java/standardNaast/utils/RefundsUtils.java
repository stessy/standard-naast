package standardNaast.utils;

import java.util.HashMap;
import java.util.Map;

public class RefundsUtils {

	private static final Map<Integer, Integer> REFUNDS = new HashMap<>();

	static {
		RefundsUtils.REFUNDS.put(12, 5);
		RefundsUtils.REFUNDS.put(13, 7);
		RefundsUtils.REFUNDS.put(14, 9);
		RefundsUtils.REFUNDS.put(15, 11);
		RefundsUtils.REFUNDS.put(16, 13);
		RefundsUtils.REFUNDS.put(17, 15);
		RefundsUtils.REFUNDS.put(18, 17);
		RefundsUtils.REFUNDS.put(19, 19);
		RefundsUtils.REFUNDS.put(20, 20);
	}

	public static int getRefund(final int travels) {
		int refund = 0;
		if (RefundsUtils.REFUNDS.containsKey(travels)) {
			refund = RefundsUtils.REFUNDS.get(travels);
		}
		return refund;
	}

}
