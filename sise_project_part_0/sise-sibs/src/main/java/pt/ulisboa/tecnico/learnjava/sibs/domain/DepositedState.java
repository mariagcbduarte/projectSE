package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;

public class DepositedState extends State {

	public DepositedState(String sourceIban, String targetIban, int value) {
		super(sourceIban, targetIban, value);
	}

	@Override
	public void process(TransferOperation currentOperation, Services services) {
		try {
			services.withdraw(sourceIban, currentOperation.commission());
			currentOperation.setOperationState(new CompletedState(sourceIban, targetIban, value));
		} catch (AccountException e) {
			currentOperation.setOperationState(new RetryState(sourceIban, targetIban, value, this));
			currentOperation.setRetries(currentOperation.getRetries() - 1);
			currentOperation.getOperationState().process(currentOperation, services);
			// currentOperation.setOperationState(new ErrorState(sourceIban, targetIban,
			// value));
		}
	}

	@Override
	public void cancel(TransferOperation currentOperation, Services services) throws AccountException {
		services.withdraw(targetIban, value);
		services.deposit(sourceIban, value);
		currentOperation.setOperationState(new CancelledState(sourceIban, targetIban, value));
	}

}
