package mbWayController;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class EndController {
	public HashMap<String, Integer> friendArray;

	public EndController(SplitBillController currentSplitBill, Sibs sibs) throws SibsException {
		friendArray = currentSplitBill.getFriendArray();
		int currentTotal = friendArray.values().stream().mapToInt(i -> i).sum();
		int currentTotalFriends = (int) friendArray.values().stream().count();

		int totalAmount = currentSplitBill.getTotalAmount();
		int numberOfFriends = currentSplitBill.getNumberOfFriends();
		Services services = sibs.getServices();

		if (totalAmount != currentTotal) {
			System.out.println("Something is wrong, did you set the bill amount right?");
		} else if (numberOfFriends > currentTotalFriends) {
			System.out.println("Oh no! One friend is missing.");
		} else if (numberOfFriends < currentTotalFriends) {
			System.out.println("Oh no! Too many friends.");
		} else {
			List<Integer> balances = friendArray.keySet().stream().map(i -> services.getAccountByIban(i).getBalance())
					.collect(Collectors.toList());
			List<Integer> amounts = friendArray.values().stream().collect(Collectors.toList());
			boolean error = false;
			for (int i = 0; i < balances.size(); i++) {
				if (balances.get(i) < amounts.get(i)) {
					error = true;
				}
			}
			if (error) {
				System.out.println("Oh no! At least one of your friends does not have enough money to pay...");
			} else {
				List<String> ibanList = friendArray.keySet().stream().collect(Collectors.toList());
				for (int i = 1; i < ibanList.size(); i++) {
					sibs.transfer(ibanList.get(i), ibanList.get(0), amounts.get(i));
				}
				System.out.println("Bill payed successfully!");
				friendArray = null;
			}
		}
	}
}
