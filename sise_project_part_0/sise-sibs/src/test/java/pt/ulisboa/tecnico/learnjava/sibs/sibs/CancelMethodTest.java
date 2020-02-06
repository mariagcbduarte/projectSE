package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.CancelledState;
import pt.ulisboa.tecnico.learnjava.sibs.domain.CompletedState;
import pt.ulisboa.tecnico.learnjava.sibs.domain.ErrorState;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class CancelMethodTest {

	@Test
	public void cancelRegistered() throws OperationException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.getOperationState().cancel(testOp, mockServices);
		assertTrue(testOp.getOperationState() instanceof CancelledState);
	}

	@Test
	public void cancelWithdrawn() throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.getOperationState().process(testOp, mockServices); // passa a withdrawn
		testOp.getOperationState().cancel(testOp, mockServices);
		assertTrue(testOp.getOperationState() instanceof CancelledState);
		verify(mockServices).deposit(sourceIban, amount);
	}

	@Test
	public void cancelDeposited() throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.getOperationState().process(testOp, mockServices); // passa a withdrawn
		testOp.getOperationState().process(testOp, mockServices); // passa a deposited
		testOp.getOperationState().cancel(testOp, mockServices);
		assertTrue(testOp.getOperationState() instanceof CancelledState);
		verify(mockServices).deposit(sourceIban, amount);
		verify(mockServices).withdraw(targetIban, amount);
	}

	@Test
	public void cancelCompleted() throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.getOperationState().process(testOp, mockServices); // passa a withdrawn
		testOp.getOperationState().process(testOp, mockServices); // passa a deposited
		testOp.getOperationState().process(testOp, mockServices); // passa a completed
		testOp.getOperationState().cancel(testOp, mockServices);
		assertTrue(testOp.getOperationState() instanceof CompletedState);
	}

	@Test
	public void cancelError() throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.setOperationState(new ErrorState(sourceIban, targetIban, amount));
		testOp.getOperationState().cancel(testOp, mockServices);
		assertTrue(testOp.getOperationState() instanceof ErrorState);
	}

	@Test
	public void cancelCancelled() throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.setOperationState(new CancelledState(sourceIban, targetIban, amount));
		testOp.getOperationState().cancel(testOp, mockServices);
		assertTrue(testOp.getOperationState() instanceof CancelledState);
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}
}
