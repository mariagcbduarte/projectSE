package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.CheckingAccount;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.CancelledState;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class CancelOperationMethodTest {
	@Test
	public void checkIfCancelCallsCancelMethod() throws OperationException, SibsException, AccountException {
		Services mockServices = mock(Services.class);
		Sibs sibs = new Sibs(5, mockServices);
		String sourceIban = "STDCK1";
		String targetIban = "BPICK1";
		int amount = 100;

		when(mockServices.getAccountByIban(sourceIban)).thenReturn(mock(CheckingAccount.class));
		when(mockServices.getAccountByIban(targetIban)).thenReturn(mock(CheckingAccount.class));

		sibs.transfer(sourceIban, targetIban, amount);

		sibs.cancelOperation(0);
		TransferOperation currentOperation = (TransferOperation) sibs.getOperation(0);

		assertTrue(currentOperation.getOperationState() instanceof CancelledState);
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}
}
