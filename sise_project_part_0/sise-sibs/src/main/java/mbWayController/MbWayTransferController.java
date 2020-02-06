package mbWayController;

import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class MbWayTransferController {

	public MbWayTransferController(String sourceIban, String targetIban, int amount, Sibs sibs) {
		try {
			sibs.transfer(sourceIban, targetIban, amount);
			System.out.println("Transfer successful!");
		} catch (SibsException e) {
			System.out.println("Could not complete transfer!");
		}
	}

}
