package mbWayController;

import java.util.HashMap;

public class SplitBillController {
	public HashMap<String, Integer> friendArray = new HashMap<String, Integer>();
	public int numberOfFriends;
	public int totalAmount;

	public SplitBillController(int numberOfFriends, int totalAmount) {
		this.numberOfFriends = numberOfFriends;
		this.totalAmount = totalAmount;
		Controller.splitBillMode = true;
	}

	public HashMap<String, Integer> getFriendArray() {
		return this.friendArray;
	}

	public int getNumberOfFriends() {
		return this.numberOfFriends;
	}

	public int getTotalAmount() {
		return this.totalAmount;
	}
}
