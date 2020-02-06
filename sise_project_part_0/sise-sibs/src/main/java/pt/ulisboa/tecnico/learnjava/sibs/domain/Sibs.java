package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class Sibs {
	final Operation[] operations;
	Services services;

	public Sibs(int maxNumberOfOperations, Services services) {
		this.operations = new Operation[maxNumberOfOperations];
		this.services = services;
	}

	public Services getServices() {
		return this.services;
	}

	public void transfer(String sourceIban, String targetIban, int amount) throws SibsException {

		if (services.getAccountByIban(sourceIban) != null && services.getAccountByIban(targetIban) != null) {
			try {
				addOperation(Operation.OPERATION_TRANSFER, sourceIban, targetIban, amount);
			} catch (OperationException e) {
				throw new SibsException();
			}
		} else {
			throw new SibsException();
		}
	}

	public void processOperations() throws SibsException {
		for (int i = 0; i < operations.length; i++) {
			if (operations[i] instanceof TransferOperation) {
				TransferOperation currentOperation = (TransferOperation) getOperation(i);
				while (!(currentOperation.getOperationState() instanceof CompletedState)
						&& !(currentOperation.getOperationState() instanceof ErrorState)
						&& !(currentOperation.getOperationState() instanceof CancelledState)) {
					currentOperation.getOperationState().process(currentOperation, services);
				}
			}
		}
	}

	public void cancelOperation(int id) throws SibsException, AccountException {
		TransferOperation currentOperation = (TransferOperation) getOperation(id); // throws sibs exception
		currentOperation.getOperationState().cancel(currentOperation, services); // throws account exception
	}

	public int addOperation(String type, String sourceIban, String targetIban, int value)
			throws OperationException, SibsException {
		int position = -1;
		for (int i = 0; i < this.operations.length; i++) {
			if (this.operations[i] == null) {
				position = i;
				break;
			}
		}

		if (position == -1) {
			throw new SibsException();
		}

		Operation operation;
		if (type.equals(Operation.OPERATION_TRANSFER)) {
			operation = new TransferOperation(sourceIban, targetIban, value);
			TransferOperation currentTransferOperation = (TransferOperation) operation;
			currentTransferOperation.setOperationId(position); // sets the operation id as the position number
		} else {
			operation = new PaymentOperation(targetIban, value);
		}

		this.operations[position] = operation;
		return position;
	}

	public void removeOperation(int position) throws SibsException {
		if (position < 0 || position > this.operations.length) {
			throw new SibsException();
		}
		this.operations[position] = null;
	}

	public Operation getOperation(int position) throws SibsException {
		if (position < 0 || position > this.operations.length) {
			throw new SibsException();
		}
		return this.operations[position];
	}

	public int getNumberOfOperations() {
		int result = 0;
		for (int i = 0; i < this.operations.length; i++) {
			if (this.operations[i] != null) {
				result++;
			}
		}
		return result;
	}

	public int getTotalValueOfOperations() {
		int result = 0;
		for (int i = 0; i < this.operations.length; i++) {
			if (this.operations[i] != null) {
				result = result + this.operations[i].getValue();
			}
		}
		return result;
	}

	public int getTotalValueOfOperationsForType(String type) {
		int result = 0;
		for (int i = 0; i < this.operations.length; i++) {
			if (this.operations[i] != null && this.operations[i].getType().equals(type)) {
				result = result + this.operations[i].getValue();
			}
		}
		return result;
	}
}
