package mbWayController;

import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.TreeMap;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class Controller {
	static boolean splitBillMode = false;
	private static TreeMap<String, String> mbWayAssociations;
	private static SplitBillController currentSplitBill;

	public static void mbWayController(String command, Sibs sibs) throws SibsException {

		String[] line = command.split(" ", 0);
		String currentCommand = line[0];

		if (currentCommand.equals("end")) {
			splitBillMode = false;
		}

		if (splitBillMode) {
			currentCommand = "friend";
		}

		switch (currentCommand) {
		case "exit":
			System.exit(0);
			break;

		case "associate-mbway":
			// line[1] = iban, line[2] = phonenumber (o que queremos como chave para
			// procurar values que sao ibans)

			new AssociateMbWayController(line[1], line[2], sibs.getServices());
			mbWayAssociations.put(line[2], line[1]);
			System.out.println(mbWayAssociations.toString());
			break;

		case "confirm-mbway":
			// line[1] = phone, line[2] = confirmation code

			new ConfirmMbWayController(mbWayAssociations.get(line[1]), line[2], sibs.getServices());
			break;

		case "mbway-transfer":
			new MbWayTransferController(mbWayAssociations.get(line[1]), mbWayAssociations.get(line[2]),
					Integer.parseInt(line[3]), sibs);
			break;

		case "mbway-split-bill":
			currentSplitBill = new SplitBillController(Integer.parseInt(line[1]), Integer.parseInt(line[2]));
			System.out.println("You may start adding your friends now!");
			break;

		case "friend":
			new FriendController(currentSplitBill, mbWayAssociations.get(line[1]), Integer.parseInt(line[2]));
			System.out.println("Friend added successfully!");
			break;

		case "end":
			new EndController(currentSplitBill, sibs);
			break;

		default:
			System.out.println("Invalid command, try again!");
			break;
		}
	}

	public static void main(String[] args)
			throws FileNotFoundException, SibsException, BankException, ClientException, AccountException {

		Services services = new Services();
		Sibs sibs = new Sibs(10, services);
		mbWayAssociations = new TreeMap<String, String>();

		// PARA EFEITOS DE TESTE
//		Bank bank = new Bank("CGD");
//		bank.addClient(new Client(bank, "Maria", "Leal", "123456789", "937765498", "Entroncamento Sem Fim", 68));
//		bank.addClient(new Client(bank, "Quim", "Barreiros", "123456123", "937896498", "Peitos da Cabritinha", 45));
//		bank.addClient(new Client(bank, "Rosinha", "Ameijoa", "123123789", "915987456", "Manha Fria de Inverno", 38));
//		bank.createAccount(AccountType.CHECKING, bank.getClientByNif("123456789"), 100, 0);
//		bank.createAccount(AccountType.CHECKING, bank.getClientByNif("123456123"), 100, 0);
//		bank.createAccount(AccountType.CHECKING, bank.getClientByNif("123123789"), 100, 0);

		while (true) {
			Scanner sc = new Scanner(System.in);
			String command = sc.nextLine();
			mbWayController(command, sibs);
		}
	}

}
