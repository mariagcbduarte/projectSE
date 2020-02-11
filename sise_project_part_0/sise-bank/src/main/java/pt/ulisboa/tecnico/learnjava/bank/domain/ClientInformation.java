package pt.ulisboa.tecnico.learnjava.bank.domain;

public class ClientInformation {
	private final String firstName;
	private final String lastName;
	private final String phoneNumber;
	private final String address;

	public ClientInformation(String firstName, String lastName, String phoneNumber, String address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.address = address;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public String getAddress() {
		return address;
	}

}
