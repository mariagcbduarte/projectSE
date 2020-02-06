package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;

public abstract class State {
	protected String sourceIban;
	protected String targetIban;
	protected int value;

	public State(String sourceIban, String targetIban, int value) {
		this.sourceIban = sourceIban;
		this.targetIban = targetIban;
		this.value = value;
	}

	public abstract void process(TransferOperation currentOperation, Services services);

	public abstract void cancel(TransferOperation currentOperation, Services services) throws AccountException;
}
