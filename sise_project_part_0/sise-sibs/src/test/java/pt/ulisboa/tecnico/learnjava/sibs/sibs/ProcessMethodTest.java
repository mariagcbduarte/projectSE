package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.After;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.CancelledState;
import pt.ulisboa.tecnico.learnjava.sibs.domain.CompletedState;
import pt.ulisboa.tecnico.learnjava.sibs.domain.DepositedState;
import pt.ulisboa.tecnico.learnjava.sibs.domain.ErrorState;
import pt.ulisboa.tecnico.learnjava.sibs.domain.RegisteredState;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;
import pt.ulisboa.tecnico.learnjava.sibs.domain.WithdrawnState;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class ProcessMethodTest {
	@Test
	public void checkIfOperationRegistered() throws OperationException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		assertTrue(testOp.getOperationState() instanceof RegisteredState);
	}

	@Test
	public void checkIfOperationRegisteredToWithdrawn() throws OperationException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.getOperationState().process(testOp, mockServices);
		assertTrue(testOp.getOperationState() instanceof WithdrawnState);
		verify(mockServices).withdraw(sourceIban, amount);
	}

	@Test
	public void checkIfOperationWithdrawnProcessedToDeposited()
			throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.setOperationState(new WithdrawnState(sourceIban, targetIban, amount));
		testOp.getOperationState().process(testOp, mockServices);
		assertTrue(testOp.getOperationState() instanceof DepositedState);
		verify(mockServices).deposit(targetIban, amount);
	}

	@Test
	public void checkIfOperationDepositedProcessedToCompleted()
			throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.setOperationState(new DepositedState(sourceIban, targetIban, amount));
		testOp.getOperationState().process(testOp, mockServices);
		assertTrue(testOp.getOperationState() instanceof CompletedState);
		verify(mockServices).withdraw(sourceIban, testOp.commission());
	}

	@Test
	public void checkIfOperationWithdrawnProcessedToCompleted()
			throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "BPICK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.setOperationState(new WithdrawnState(sourceIban, targetIban, amount));
		testOp.getOperationState().process(testOp, mockServices);
		assertTrue(testOp.getOperationState() instanceof CompletedState);
		verify(mockServices, never()).withdraw(sourceIban, testOp.commission());
	}

	@Test
	public void checkIfOperationRegisteredProcessedToError()
			throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		doThrow(new AccountException()).when(mockServices).withdraw(anyString(), anyInt());
		testOp.getOperationState().process(testOp, mockServices);
		verify(mockServices, times(4)).withdraw(anyString(), anyInt());
		assertTrue(testOp.getOperationState() instanceof ErrorState);
	}

	@Test
	public void checkIfOperationWithdrawnProcessedToError() throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.setOperationState(new WithdrawnState(sourceIban, targetIban, amount));
		doThrow(new AccountException()).when(mockServices).deposit(anyString(), anyInt());
		testOp.getOperationState().process(testOp, mockServices);
		verify(mockServices, times(4)).deposit(anyString(), anyInt());
		assertTrue(testOp.getOperationState() instanceof ErrorState);
	}

	@Test
	public void checkIfOperationDepositedProcessedToError() throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.setOperationState(new DepositedState(sourceIban, targetIban, amount));
		doThrow(new AccountException()).when(mockServices).withdraw(anyString(), anyInt());
		testOp.getOperationState().process(testOp, mockServices);
		verify(mockServices, times(4)).withdraw(anyString(), anyInt());
		assertTrue(testOp.getOperationState() instanceof ErrorState);
	}

	@Test
	public void checkIfOperationErrorProcessedToError() throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.setOperationState(new ErrorState(sourceIban, targetIban, amount));
		testOp.getOperationState().process(testOp, mockServices);
		assertTrue(testOp.getOperationState() instanceof ErrorState);
	}

	@Test
	public void checkIfRegisteredEntersRetryStateEndsAsWithdrawn() throws OperationException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		doThrow(new AccountException()).doNothing().when(mockServices).withdraw(anyString(), anyInt());
		testOp.getOperationState().process(testOp, mockServices);
		verify(mockServices, times(2)).withdraw(anyString(), anyInt());
		assertEquals(3, testOp.getRetries());
		assertTrue(testOp.getOperationState() instanceof WithdrawnState);
	}

	@Test
	public void checkIfOperationCompletedProcessedToCompleted()
			throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.setOperationState(new CompletedState(sourceIban, targetIban, amount));
		testOp.getOperationState().process(testOp, mockServices);
		assertTrue(testOp.getOperationState() instanceof CompletedState);
	}

	@Test
	public void checkIfOperationCancelledProcessedToCancelled()
			throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		TransferOperation testOp = new TransferOperation(sourceIban, targetIban, amount);
		testOp.setOperationState(new CancelledState(sourceIban, targetIban, amount));
		testOp.getOperationState().process(testOp, mockServices);
		assertTrue(testOp.getOperationState() instanceof CancelledState);
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}
}
