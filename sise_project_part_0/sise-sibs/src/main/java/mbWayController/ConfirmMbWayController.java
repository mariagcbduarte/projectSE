package mbWayController;

import pt.ulisboa.tecnico.learnjava.bank.services.Services;

public class ConfirmMbWayController {

	public ConfirmMbWayController(String currentIban, String confirmationCode, Services services) {

		if (currentIban != null
				&& services.getAccountByIban(currentIban).getClient().getCode().equals(confirmationCode)) {
			System.out.println("MBWay Association Confirmed Successfully!");
		} else {
			System.out.println("Wrong confirmation code!");
		}
	}
}
