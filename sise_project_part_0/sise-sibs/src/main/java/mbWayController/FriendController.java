package mbWayController;

import java.util.HashMap;

public class FriendController {
	public FriendController(SplitBillController currentSplitBill, String friendIban, int amount) {
		HashMap<String, Integer> friendArray = currentSplitBill.getFriendArray();
		friendArray.put(friendIban, amount);
	}
}
