package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;

public class RegisteredState extends State {

	public RegisteredState(String sourceIban, String targetIban, int value) {
		super(sourceIban, targetIban, value);
	}

	@Override
	public void process(TransferOperation currentOperation, Services services) {
		try {
			services.withdraw(sourceIban, value);
			currentOperation.setOperationState(new WithdrawnState(sourceIban, targetIban, value));
		} catch (AccountException e) {
			currentOperation.setOperationState(new RetryState(sourceIban, targetIban, value, this));
			currentOperation.setRetries(currentOperation.getRetries() - 1);
			currentOperation.getOperationState().process(currentOperation, services);
			// currentOperation.setOperationState(new ErrorState(sourceIban, targetIban,
			// value));

		}
	}

	@Override
	public void cancel(TransferOperation currentOperation, Services services) {
		currentOperation.setOperationState(new CancelledState(sourceIban, targetIban, value));
	}

}
