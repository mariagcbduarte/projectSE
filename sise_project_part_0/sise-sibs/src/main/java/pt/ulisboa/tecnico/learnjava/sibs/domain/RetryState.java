package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;

public class RetryState extends State {
	public State previousState;

	public RetryState(String sourceIban, String targetIban, int value, State previousState) {
		super(sourceIban, targetIban, value);
		this.previousState = previousState;
	}

	@Override
	public void process(TransferOperation currentOperation, Services services) {
		if (currentOperation.getRetries() == 0) {
			currentOperation.setOperationState(new ErrorState(sourceIban, targetIban, value));
		} else {
			previousState.process(currentOperation, services);
		}
	}

	@Override
	public void cancel(TransferOperation currentOperation, Services services) throws AccountException {
	}

}
