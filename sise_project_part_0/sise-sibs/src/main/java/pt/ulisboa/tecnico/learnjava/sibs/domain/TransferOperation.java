package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class TransferOperation extends Operation {
	private final String sourceIban;
	private final String targetIban;
	private State currentState;
	private int operationId;
	private int retries;

	public TransferOperation(String sourceIban, String targetIban, int value) throws OperationException {
		super(Operation.OPERATION_TRANSFER, value);

		if (invalidString(sourceIban) || invalidString(targetIban)) {
			throw new OperationException();
		}

		this.sourceIban = sourceIban;
		this.targetIban = targetIban;
		this.currentState = new RegisteredState(sourceIban, targetIban, value);
		this.operationId = 0;
		this.retries = 4;
		// retries is 4 because we decrement every time the process method fails
		// so we have 1 attempt + 3 retries in total
	}

	private boolean invalidString(String name) {
		return name == null || name.length() == 0;
	}

	@Override
	public int commission() {
		if (sourceIban.substring(0, 3).equals(targetIban.substring(0, 3))) {
			return 0;
		} else {
			return (int) Math.round(super.commission() + getValue() * 0.05);
		}
	}

	public String getSourceIban() {
		return this.sourceIban;
	}

	public String getTargetIban() {
		return this.targetIban;
	}

	public State getOperationState() {
		return this.currentState;
	}

	public void setOperationState(State newState) {
		this.currentState = newState;
	}

	public void setOperationId(int id) {
		if (id > 0) {
			this.operationId = id;
		}
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

}
