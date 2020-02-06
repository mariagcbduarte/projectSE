package mbWayController;

import java.util.Random;

import pt.ulisboa.tecnico.learnjava.bank.services.Services;

public class AssociateMbWayController {

	public AssociateMbWayController(String iban, String phoneNumber, Services services) {
		if (services.getAccountByIban(iban).getClient().getPhoneNumber().equals(phoneNumber)) {
			Integer rndCode = new Random().nextInt(999999);
			System.out.println("Code: " + String.format("%06d", rndCode) + " (don't share it with anyone)");
			services.getAccountByIban(iban).getClient().setCode(String.format("%06d", rndCode));
		}

	}
}
