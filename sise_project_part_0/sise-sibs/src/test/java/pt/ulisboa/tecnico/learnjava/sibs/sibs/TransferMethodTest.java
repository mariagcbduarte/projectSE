package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.CheckingAccount;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.CompletedState;
import pt.ulisboa.tecnico.learnjava.sibs.domain.DepositedState;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Operation;
import pt.ulisboa.tecnico.learnjava.sibs.domain.RegisteredState;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;
import pt.ulisboa.tecnico.learnjava.sibs.domain.WithdrawnState;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class TransferMethodTest {

	@Test
	public void successTransferUsingMock()
			throws BankException, AccountException, ClientException, SibsException, OperationException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);

		String sourceIban = "BPICK1";
		String targetIban = "BPICK2";

		int amount = 100;
		when(mockServices.getAccountByIban(sourceIban)).thenReturn(mock(CheckingAccount.class));
		when(mockServices.getAccountByIban(targetIban)).thenReturn(mock(CheckingAccount.class));
		sibs.transfer(sourceIban, targetIban, amount);
		sibs.processOperations();

		verify(mockServices).deposit(targetIban, amount);
		verify(mockServices).withdraw(sourceIban, amount);

		assertEquals(1, sibs.getNumberOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperations());
		assertEquals(100, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
		assertEquals(0, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_PAYMENT));
	}

	@Test
	public void oneAccountsDoesNotExist() throws AccountException, OperationException, SibsException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);

		String sourceIban = "BPICK1";
		String targetIban = "BPICK2";

		int amount = 100;
		when(mockServices.getAccountByIban(sourceIban)).thenReturn(mock(CheckingAccount.class));

		try {
			sibs.transfer(sourceIban, targetIban, amount);
			fail();
		} catch (SibsException e) {
			assertEquals(0, sibs.getNumberOfOperations());
		}

	}

	@Test
	public void feeCollectedFromSourceAccountWhenBanksAreDifferent()
			throws SibsException, AccountException, OperationException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);

		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";

		int amount = 100;
		int knownCommission = (int) Math.round(1 + amount * 0.05);
		when(mockServices.getAccountByIban(sourceIban)).thenReturn(mock(CheckingAccount.class));
		when(mockServices.getAccountByIban(targetIban)).thenReturn(mock(CheckingAccount.class));
		sibs.transfer(sourceIban, targetIban, amount);
		sibs.processOperations();
		verify(mockServices).withdraw(sourceIban, amount);
		verify(mockServices).deposit(targetIban, amount);
		verify(mockServices).withdraw(sourceIban, knownCommission);

		assertEquals(1, sibs.getNumberOfOperations());
		assertEquals(amount, sibs.getTotalValueOfOperations());
		assertEquals(amount, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_TRANSFER));
		assertEquals(0, sibs.getTotalValueOfOperationsForType(Operation.OPERATION_PAYMENT));
	}

	@Test
	public void depositFailsBalanceStaysTheSame() throws SibsException, AccountException, OperationException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);

		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		when(mockServices.getAccountByIban(sourceIban)).thenReturn(mock(CheckingAccount.class));
		when(mockServices.getAccountByIban(targetIban)).thenReturn(mock(CheckingAccount.class));
		doThrow(new AccountException()).when(mockServices).deposit(targetIban, amount);

		try {
			sibs.transfer(sourceIban, targetIban, amount);
		} catch (SibsException e) {
			assertEquals(0, sibs.getNumberOfOperations());
			verify(mockServices, never()).withdraw(sourceIban, amount);
		}
	}

	@Test
	public void moneyTransferedOnlyDuringProcess() throws SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);

		String sourceIban = "STDCK1";
		String targetIban = "STDCK1";
		int amount = 100;

		when(mockServices.getAccountByIban(sourceIban)).thenReturn(mock(CheckingAccount.class));
		when(mockServices.getAccountByIban(targetIban)).thenReturn(mock(CheckingAccount.class));
		sibs.transfer(sourceIban, targetIban, amount);
		verify(mockServices, never()).withdraw(sourceIban, amount);
		verify(mockServices, never()).deposit(targetIban, amount);
		TransferOperation currentOperation = (TransferOperation) sibs.getOperation(0);
		assertTrue(currentOperation.getOperationState() instanceof RegisteredState);

		sibs.processOperations();
		assertTrue(currentOperation.getOperationState() instanceof CompletedState);
		verify(mockServices).withdraw(sourceIban, amount);
		verify(mockServices).deposit(targetIban, amount);
	}

	@Test
	public void operationsCompletedAfterProcessed() throws SibsException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);

		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		when(mockServices.getAccountByIban(sourceIban)).thenReturn(mock(CheckingAccount.class));
		when(mockServices.getAccountByIban(targetIban)).thenReturn(mock(CheckingAccount.class));
		sibs.transfer(sourceIban, targetIban, amount);
		sibs.transfer(sourceIban, targetIban, amount);
		sibs.transfer(sourceIban, targetIban, amount);

		TransferOperation op1 = (TransferOperation) sibs.getOperation(1);
		op1.setOperationState(new WithdrawnState(sourceIban, targetIban, amount));
		TransferOperation op2 = (TransferOperation) sibs.getOperation(2);
		op2.setOperationState(new DepositedState(sourceIban, targetIban, amount));

		sibs.processOperations();

		for (int i = 0; i <= 2; i++) {
			TransferOperation currentOperation = (TransferOperation) sibs.getOperation(i);
			assertTrue(currentOperation.getOperationState() instanceof CompletedState);
		}
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
