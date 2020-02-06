package pt.ulisboa.tecnico.learnjava.sibs.domain;

import pt.ulisboa.tecnico.learnjava.bank.services.Services;

public class CompletedState extends State {

	public CompletedState(String sourceIban, String targetIban, int value) {
		super(sourceIban, targetIban, value);
	}

	@Override
	public void process(TransferOperation currentOperation, Services services) {
	}

	@Override
	public void cancel(TransferOperation currentOperation, Services services) {
	}
}