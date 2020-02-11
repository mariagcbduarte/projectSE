package pt.ulisboa.tecnico.learnjava.sibs.mbway;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.TreeMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import mbWayController.Controller;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank.AccountType;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class ControllerMethodTest {
	private Services mockServices;
	private Bank bank;
	private Sibs sibs;
	private static final String PHONE_NUMBER = "937765498";
	private static final String NIF = "123456789";
	private static final String IBAN = "CGDCK1";
	private String command;

	@Before
	public void setUp() throws ClientException, BankException, AccountException {
		mockServices = mock(Services.class);
		sibs = new Sibs(10, mockServices);
		Controller.mbWayAssociations = new TreeMap<String, String>();

		bank = new Bank("CGD");
		bank.addClient(new Client(bank, "Maria", "Leal", NIF, PHONE_NUMBER, "Entroncamento Sem Fim", 68));
		bank.createAccount(AccountType.CHECKING, bank.getClientByNif(NIF), 100, 0);

		command = "associate-mbway " + IBAN + " " + PHONE_NUMBER;

	}

	@Test
	public void associateMbWayControllerTest() throws SibsException {
		when(mockServices.getAccountByIban(IBAN)).thenReturn(bank.getAccountByAccountId("CK1"));
		Controller.mbWayController(command, sibs);
		assertEquals(1, Controller.mbWayAssociations.size());
		assertTrue(Controller.mbWayAssociations.containsKey(PHONE_NUMBER));
		assertTrue(Controller.mbWayAssociations.containsValue(IBAN));
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
		Controller.mbWayAssociations = null;
	}
}
