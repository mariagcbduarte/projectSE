package controller;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.stream.Collectors;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank.AccountType;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class Controller {
	static boolean splitBillMode = false;
	private static TreeMap<String, String> mbWayAssociations;
	private static String phoneNumber;
	private static String iban;
	private static String confirmationCode;
	private static HashMap<String, Integer> friendArray;
	private static Integer numberOfFriends;
	private static Integer totalAmount;

	public static void mbWayController(String command, Sibs sibs) throws SibsException {
		Services services = sibs.getServices();

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
			iban = line[1];
			phoneNumber = line[2];

			if (services.getAccountByIban(iban).getClient().getPhoneNumber().equals(phoneNumber)) {
				Integer rndCode = new Random().nextInt(999999);
				System.out.println("Code: " + String.format("%06d", rndCode) + " (don't share it with anyone)");
				services.getAccountByIban(iban).getClient().setCode(String.format("%06d", rndCode));
			}

			mbWayAssociations.put(phoneNumber, iban);

			break;

		case "comfirm-mbway":
			phoneNumber = line[1];
			confirmationCode = line[2];

			String currentIban = mbWayAssociations.get(phoneNumber);

			if (currentIban != null
					&& services.getAccountByIban(currentIban).getClient().getCode().equals(confirmationCode)) {
				System.out.println("MBWay Association Confirmed Successfully!");
			} else {
				System.out.println("Wrong confirmation code!");
			}
			break;

		case "mbway-transfer":
			String sourcePhoneNumber = line[1];
			String targetPhoneNumber = line[2];
			int amount = Integer.parseInt(line[3]);
			String sourceIban = mbWayAssociations.get(sourcePhoneNumber);
			String targetIban = mbWayAssociations.get(targetPhoneNumber);

			try {
				sibs.transfer(sourceIban, targetIban, amount);
				System.out.println("Transfer successful!");
			} catch (SibsException e) {
				System.out.println("Could not complete transfer!");
			}
			break;

		case "mbway-split-bill":
			numberOfFriends = Integer.parseInt(line[1]);
			totalAmount = Integer.parseInt(line[2]);
			friendArray = new HashMap<String, Integer>();
			splitBillMode = true;

			System.out.println("You may start adding your friends now!");
			break;

		case "friend":
			phoneNumber = line[1];
			amount = Integer.parseInt(line[2]);
			friendArray.put(phoneNumber, amount);
			System.out.println("Friend added successfully!");
			break;

		case "end":
			int currentTotal = friendArray.values().stream().mapToInt(i -> i).sum();
			int currentTotalFriends = (int) friendArray.values().stream().count();

			if (totalAmount != currentTotal) {
				System.out.println("Something is wrong, did you set the bill amount right?");
			} else if (numberOfFriends > currentTotalFriends) {
				System.out.println("Oh no! One friend is missing.");
			} else if (numberOfFriends < currentTotalFriends) {
				System.out.println("Oh no! Too many friends.");
			} else {
				List<Integer> balances = friendArray.keySet().stream().map(i -> mbWayAssociations.get(i))
						.map(i -> services.getAccountByIban(i).getBalance()).collect(Collectors.toList());
				List<Integer> amounts = friendArray.values().stream().collect(Collectors.toList());
				boolean error = false;
				for (int i = 0; i < balances.size(); i++) {
					if (balances.get(i) < amounts.get(i)) {
						error = true;
					}
				}
				if (error) {
					System.out.println("Oh no! At least one of your friends does not have enough money to pay...");
				} else {
					List<String> ibanList = friendArray.keySet().stream().map(i -> mbWayAssociations.get(i))
							.collect(Collectors.toList());
					for (int i = 1; i < ibanList.size(); i++) {
						sibs.transfer(ibanList.get(i), ibanList.get(0), amounts.get(i));
					}
					System.out.println("Bill payed successfully!");
					friendArray = null;
				}
			}
			break;
		default:
			System.out.println("Invalid command, try again!");
			break;
		}
	}

	public static void main(String[] args)
			throws FileNotFoundException, SibsException, BankException, ClientException, AccountException {

		mbWayAssociations = new TreeMap<String, String>();
		phoneNumber = null;
		iban = null;
		confirmationCode = null;
		numberOfFriends = null;
		totalAmount = null;

		Services services = new Services();
		Sibs sibs = new Sibs(10, services);

		// PARA EFEITOS DE TESTE
		Bank bank = new Bank("CGD");
		bank.addClient(new Client(bank, "Maria", "Leal", "123456789", "937765498", "Entroncamento Sem Fim", 68));
		bank.addClient(new Client(bank, "Quim", "Barreiros", "123456123", "937896498", "Peitos da Cabritinha", 45));
		bank.addClient(new Client(bank, "Rosinha", "Ameijoa", "123123789", "915987456", "Manha Fria de Inverno", 38));
		bank.createAccount(AccountType.CHECKING, bank.getClientByNif("123456789"), 100, 0);
		bank.createAccount(AccountType.CHECKING, bank.getClientByNif("123456123"), 100, 0);
		bank.createAccount(AccountType.CHECKING, bank.getClientByNif("123123789"), 100, 0);

		while (true) {
			Scanner sc = new Scanner(System.in);
			String command = sc.nextLine();

			mbWayController(command, sibs);
		}
	}

}
