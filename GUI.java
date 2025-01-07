package com.sunshine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import g12.Branch;
import g18.Loan;

/* 
 * The GUI class provides methods for handling user input and interacting with the console.
 *
 */
public class GUI {

	// Scanner object for user input (System.in)
	protected static Scanner scanner = new Scanner(System.in);

	/**
	 * Prompts the user to enter a choice and returns the integer value entered.
	 *
	 * @return The integer value representing the user's choice.
	 */
	protected int getChoice() {
		if (!scanner.hasNextInt()) {
			System.out.println("Invalid input. Please enter a number.");
			scanner.next();
			return getChoice();
		}
		int choice = scanner.nextInt();
		return choice;
	}

	/**
	 * Prompts the user to enter a BigDecimal value and returns it.
	 *
	 * @return The BigDecimal value entered by the user.
	 */
	protected double getDouble() {
		if (!scanner.hasNextDouble()) {
			System.out.println("Invalid input. Please try again.");
			scanner.next();
			return getDouble();
		}
		double dbl = scanner.nextDouble();
		return dbl;
	}

	/**
	 * Clears the console screen.
	 * <p>
	 * Note: This method works differently based on the operating system.
	 * It uses "cls" command for Windows and "clear" command for Unix-like systems.
	 */
	public static void clrScr() {
		String os = System.getProperty("os.name").toLowerCase();
		try {
			if (os.contains("win")) {
				// For Windows
				new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
			} else {
				// For Unix-like systems (Linux, macOS)
				new ProcessBuilder("clear").inheritIO().start().waitFor();
			}

		} catch (Exception e) {
			// Handle exceptions
			e.printStackTrace();
		}
	}

	/**
	 * After displaying the message, waits for the user to press 'Enter' to return
	 * back to previous menu.
	 */
	protected void returnToMenu() {
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Press Enter to return to the main menu.");
		try {
			System.in.read();
		} catch (IOException e) {
			System.err.println("Error reading input: " + e.getMessage());
		}
	}
}

/**
 * The LoginGUI class extends GUI and provides methods for displaying login and
 * registration menus.
 */
class LoginGUI extends GUI {
	private String[] loginDetails;

	/**
	 * Displays the initial menu for the Sunshine Bank application.
	 *
	 * @return The user's choice as an integer.
	 */
	public int initMenu() {
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Welcome to the Sunshine Bank");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("1. Login");
		System.out.println("2. Register");
		System.out.println("0. Exit");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Please enter your choice: ");
		return getChoice();
	}

	/**
	 * Displays the login menu and prompts the user to enter their username and
	 * password.
	 *
	 * @return An array containing the entered username and password.
	 */
	public String[] loginMenu() {
		System.out.print("Please enter your username: ");
		String username = GUI.scanner.next();
		System.out.print("Please enter your password: ");
		String password = GUI.scanner.next();
		this.loginDetails = new String[] { username, password };
		return loginDetails;
	}

	/**
	 * Displays the branch menu and allows the user to select a branch.
	 * <p>
	 * Each branch provides different services, such as some branches offering
	 * insurance services.
	 *
	 * @return The selected Branch object by the user.
	 */
	public static Branch branchMenu() {
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Branch Menu");
		System.out.println("---------------------------------------------------------------------------------");
		Branch selectedBranch = Branch.selectBranch(GUI.scanner);
		return selectedBranch;
	}

	/**
	 * Displays the registration menu and guides the user through the registration
	 * process.
	 *
	 * @return The newly registered Customer object.
	 * @throws NoSuchAlgorithmException     if a requested cryptographic algorithm
	 *                                      is not available in the environment.
	 * @throws UnsupportedEncodingException if the named charset is not supported.
	 * @throws ParseException               if the beginning of the specified string
	 *                                      cannot be parsed.
	 */
	public Customer registerMenu() throws NoSuchAlgorithmException, UnsupportedEncodingException, ParseException {
		System.out.println("Please enter the last 4 digits of your NRIC: ");
		String customerid = GUI.scanner.next();
		if (Customer.getCustomerByID(customerid) != null) {
			System.out.println("NRIC already exists, cannot create a new customer account.");
			return new Customer("", "", "", null, "", 0); // Use return instead of break to exit the method
		}

		System.out.println("Please enter your date of birth (YYYY-MM-DD): ");
		String dateOfBirthString = GUI.scanner.next();
		Date dateOfBirth = Customer.validateDate(dateOfBirthString);
		if (dateOfBirth == null) {
			System.out.println("Invalid date format. Please try again.");
			return new Customer("", "", "", null, "", 0); // Use return instead of break to exit the method
		} else if (!Customer.validateAge(dateOfBirth)) {
			System.out.println("You must be 16 years old and above to register for an account.");
			return new Customer("", "", "", null, "", 0); // Use return instead of break to exit the method
		}

		System.out.println("Please enter your username: ");
		String username = GUI.scanner.next();
		if (Customer.getCustomerByUsername(username) != null) {
			System.out.println("Username already exists, cannot create a new customer account.");
			return new Customer("", "", "", null, "", 0); // Use return instead of break to exit the method
		}

		System.out.println("Please enter your password: ");
		String password = GUI.scanner.next();

		System.out.println("Please enter your password again: ");
		String cfmPassword = GUI.scanner.next();

		if (password.equals(cfmPassword)) {
			// Generate salted value
			byte[] ByteSaltedValue = HashingSecurity.generateSalt();
			String hashedPassword = HashingSecurity.hashString(password, ByteSaltedValue);
			String hexSaltedValue = HashingSecurity.bytesToHex(ByteSaltedValue);
			System.out.println("Registration successful!");

			Customer customer = new Customer(customerid, username, hashedPassword, dateOfBirth, hexSaltedValue, 0);
			// Security security = new Security("Sunshine Bank", username, null);
			Customer.allCustomers.add(customer);
			// Security.allSecurity.add(security);
			return customer;

		} else {
			System.out.println("Passwords do not match. Please try again.");
			return new Customer("", "", "", null, "", 0); // Use return instead of break to exit the method
		}
	}

	/**
	 * Display the registration menu for 2FA after successful login
	 * 
	 * @param customer
	 * @throws IOException
	 */
	public boolean TwoFactorRegistrationMenu(Customer customer) {
		String username = customer.getUsername();
		Security security = Security.getSecurityByUsername(username);
		TwoFA twoFA = new TwoFA("Sunshine Bank", username, security.getSecretKey());
		boolean hasLogin = false;
		if (twoFA.getSecretKey() == null) {
			System.out.println("Two-Factor Authentication is not enabled for your account.");
			// 2FA is not enabled, ask the user if they want to enable it
			System.out.println("Would you like to enable Two-Factor Authentication (2FA)? (Y/N)");
			String response = scanner.next().trim().toLowerCase(); // Normalize input for easier comparison
			if (response.equals("yes") || response.equals("y")) {
				try {
					// Call to enable 2FA
					twoFA.enableTwoFactorForAccount(); // This should also set the secret key
					System.out.println("2FA has been enabled for your account.");
					hasLogin = TwoFactorAuth(customer);
				} catch (IOException e) {
					System.out.println("An error occurred while enabling 2FA: " + e.getMessage());
				}
			}
			if (response.equals("no") || response.equals("n")) {
				System.out.println("2FA will not be enabled for your account.");
				hasLogin = true;
			}
		} else {
			// 2FA is already enabled, perhaps prompt for a 2FA code
			System.out.println("2FA is already enabled for your account.");
			hasLogin = TwoFactorAuth(customer);
		}
		return hasLogin;
	}

	/**
	 * 2FA Login Menu for the customer based on TOTP
	 * 
	 * @param customer
	 * @return
	 */
	private boolean TwoFactorAuth(Customer customer) {
		String username = customer.getUsername();
		Security security = Security.getSecurityByUsername(username);
		TwoFA twoFA = new TwoFA("Sunshine Bank", username, security.getSecretKey());
		System.out.println("Please enter the 6-digit code from your authenticator app: ");
		String codeString = scanner.next();
		// System.out.println("Codestring: " + codeString );
		String secretKey = security.getSecretKey();
		// System.out.println("SecretKey: " + secretKey);
		if (twoFA.verifyCode(codeString)) {
			System.out.println("2FA code is correct. You are now logged in.");
			return true;
		} else {
			System.out.println("2FA code is incorrect. Please try again.");
			return false;
		}
	}
}

/**
 * The CustomerGUI class extends GUI and provides methods for displaying
 * customer-specific menus.
 */
class CustomerGUI extends GUI {
	/**
	 * Prints the table of accounts belonging to the given customer.
	 *
	 * @param customer The customer whose accounts are to be printed.
	 */
	private void printAccountsTable(Customer customer) {
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println(customer.getUsername() + "'s Accounts");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("|    | Account ID                           | Type        |");
		int index = 1;
		for (Account account : Account.getAccountsByCustomer(customer, "Active")) {
			System.out.println("| " + index + ". | " + account.getAccountId() + " | "
					+ String.format("%-11s", account.getType()) + " |");
			index++;
		}
		System.out.println("---------------------------------------------------------------------------------");
	}

	/**
	 * Displays the customer menu and prompts for user input.
	 *
	 * @param customer The customer accessing the menu.
	 * @return The user's choice as an integer.
	 */
	public int customerMenu(Customer customer) {
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Welcome, " + customer.getUsername());
		printAccountsTable(customer);
		System.out.println("1. Create New Account");
		System.out.println("2. Access Account");
		System.out.println("3. Close Account");
		System.out.println("0. Logout");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Please enter your choice: ");
		return getChoice();
	}

	/**
	 * Displays the menu for creating a new account such as Savings, FX, Insurance.
	 *
	 * @param customer The customer who is creating the account.
	 * @param branch   The branch selected by the user in the Branch Menu. Insurance
	 *                 account only available for branches with insurance services.
	 * @return The newly created Account object.
	 * @throws IOException If an I/O error occurs when reading / writing to CSV
	 *                     files.
	 */
	public Account createAccountMenu(Customer customer, Branch branch) throws IOException, Exception {
		System.out.println("Please enter the type of account you would like to create:");
		System.out.println("1. Savings");
		System.out.println("2. Foreign Exchange (FX)");
		System.out.println("3. Loan");
		System.out.println("4. Credit Card");
		// Only display Insurance option if Branch has Insurance services
		if (branch.hasInsuranceOption()) {
			System.out.println("5. Insurance");
		}
		int choice = getChoice();
		String newAccountID = UUID.randomUUID().toString();
		String customerID = customer.getCustomerID();
		String type = "";
		Balance newWithdrawLimit = new Balance();
		Balance newTransferLimit = new Balance();
		switch (choice) {
			case 1:
				// Create a new savings account
				type = "Savings";
				Savings newSavings = new Savings(newAccountID, new Balance());
				Savings.appendToCSV(Savings.getPath(), newSavings);
				// Set initial limits for the new account
				newWithdrawLimit.setSGD(new BigDecimal(1000));
				newTransferLimit.setSGD(new BigDecimal(1000));
				break;
			case 2:
				// Create a new FX account
				FX newFX = new FX(newAccountID, new Balance());
				FX.appendToCSV(FX.getPath(), newFX);
				FX.allFX.add(newFX);
				type = "Fx";
				newWithdrawLimit = Limits.setInitialLimits();
				newTransferLimit = Limits.setInitialLimits();
				break;
			case 3:
				// Create a new loan account
				type = "Loan";
				break;
			case 4:
				// Create a new credit card account
				type = "Credit Card";
				SunshineCreditCard newSunshineCreditCard = new SunshineCreditCard("newAccountID");
				break;
			case 5:
				// Create a new insurance account
				if (branch.hasInsuranceOption()) {
					type = "Insurance";
				} else {
					System.out.println("Invalid choice. Please try again.");
					createAccountMenu(customer, branch);
				}
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
				createAccountMenu(customer, branch);
				break;
		}
		Account newAccount = new Account(newAccountID, customerID, type, "Active");
		Account.addAccount(newAccount);
		if (!newAccount.getType().equals("Insurance")) {
			Limits newLimit = new Limits(newAccount, newWithdrawLimit, newTransferLimit);
			Limits.appendToLimitsCSV(Limits.getLimitsPath(), newLimit);
		}
		return newAccount;
	}

	/**
	 * Displays the menu for closing an account.
	 *
	 * @param customer The customer who is closing the account.
	 * @param branch   The branch selected by the user in the Branch Menu. Closing
	 *                 of Insurance account only available for branches with
	 *                 insurance services.
	 * @throws IOException if an I/O error occurs.
	 */
	public void closeAccountMenu(Customer customer, Branch branch) throws IOException {
		printAccountsTable(customer);
		System.out.println("0. Back to Home Menu");
		System.out.println("Please enter the index of the account you would like to close:");
		ArrayList<Account> customerAccounts = Account.getAccountsByCustomer(customer, "Active");
		int choice = getChoice();
		if (choice == 0) {
			return;
		}
		Account account = customerAccounts.get(choice - 1);
		if (account == null) {
			System.out.println("Account not found. Please try again.");
			returnToMenu();
			closeAccountMenu(customer, branch);
		} else if (!branch.hasInsuranceOption() && account.getType().equals("Insurance")) {
			System.out.println("Insurance related services are not offered at this branch.");
			returnToMenu();
			closeAccountMenu(customer, branch);
		}
		String accountID = account.getAccountId();
		System.out.println("Close Account: " + accountID);
		System.out.println("Are you sure you want to close this account? (Y/N)");
		String confirm = scanner.next();
		if (confirm.equalsIgnoreCase("N")) {
			System.out.println("Account not closed.");
			closeAccountMenu(customer, branch);
		} else if (!confirm.equalsIgnoreCase("Y")) {
			System.out.println("Invalid choice. Please try again.");
			closeAccountMenu(customer, branch);
		}
		if (accountID != null) {
			Account.closeAccount(accountID);
			System.out.println("Account closed successfully.");
		} else {
			System.out.println("Account not found. Please try again.");
			closeAccountMenu(customer, branch);
		}
	}

	/**
	 * Display the change password menu
	 * <p>
	 * If the current password is correct, prompt the user to enter
	 * a new password, update the password, and return the updated customer object.
	 * <p>
	 * If the confirmed new password doesn't match the first input for new password,
	 * prompt the user to enter the new password again.
	 *
	 * @param customer The Customer object of the customer who is changing the
	 *                 password.
	 * @return The updated Customer object.
	 * @throws NoSuchAlgorithmException if a requested cryptographic algorithm is
	 *                                  not available in the environment.
	 * @throws IOException              if an I/O error occurs.
	 */

	public Customer changePasswordMenu(Customer customer) throws NoSuchAlgorithmException, IOException {
		System.out.println("Please enter your current password:");
		String unhashedCurrentPassword = this.scanner.next();
		if (!customer.getPassword().equals(customer.getHashedPassword(unhashedCurrentPassword))) {
			System.out.println("Password is incorrect. Please try again.");
			return customer;
		}

		System.out.println("Please enter your new password:");
		String newPassword = this.scanner.next();
		System.out.println("Please enter your new password again:");
		String cfmNewPassword = this.scanner.next();
		if (newPassword.equals(cfmNewPassword)) {
			// retrieve salted value
			customer.setPassword(HashingSecurity.hashString(newPassword, customer.getSalt()));
			Customer.saveArrayListToCSV();
			System.out.println("Password changed successfully!");
		} else {
			System.out.println("Passwords do not match. Please try again.");
		}
		return customer;
	}

	/**
	 * Displays the menu for accessing an account.
	 *
	 * @param customer The customer who is accessing the account.
	 * @param branch   The branch selected by the user in the Branch Menu. Access of
	 *                 Insurance account only available for branches with insurance
	 *                 services.
	 * @return The accessed Account object.
	 */
	public Account accessAccountMenu(Customer customer, Branch branch) {
		printAccountsTable(customer);
		System.out.println("Please enter the index of the account you would like to access:");
		ArrayList<Account> customerAccounts = Account.getAccountsByCustomer(customer, "Active");
		int choice = getChoice();
		Account account = customerAccounts.get(choice - 1);
		String accountID = account.getAccountId();
		if (account == null) {
			System.out.println("Account not found. Please try again.");
			returnToMenu();
			accessAccountMenu(customer, branch);
		} else if (account.getType().equals("Insurance") && !branch.hasInsuranceOption()) {
			System.out.println("This branch does not offer Insurance services.");
			System.out.println("Please return to Branch Menu or select another account to access.");
			returnToMenu();
			accessAccountMenu(customer, branch);
		}
		System.out.println("Accessing Account (" + accountID + ")...");
		return account;
	}
}

/**
 * The AccountGUI class extends GUI and provides methods for displaying menus
 * related to specific types of accounts.
 */
class AccountGUI extends GUI {
	// All Account GUIs
	SavingsGUI savingsGUI = new SavingsGUI();
	InsuranceGUI insuranceGUI = new InsuranceGUI();
	FxGUI fxGUI = new FxGUI();
	CreditCardGUI creditCardGUI = new CreditCardGUI();
	SettingsGUI settingsGUI = new SettingsGUI();
	LoanGUI loanGUI = new LoanGUI();

	/**
	 * Displays the menu for a specific account type.
	 *
	 * @param account The account for which the menu is displayed.
	 * @return The user's choice as an integer.
	 * @throws Exception
	 * @throws IOException
	 */
	public int accountMenu(Account account) throws IOException, Exception {
		String type = account.getType();
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Account ID: " + account.getAccountId());
		System.out.println("Type: " + type);
		System.out.println("---------------------------------------------------------------------------------");
		int choice = 0;
		switch (type) {
			case "Savings":
				Savings savings = Savings.getSavingsByAccountID(account.getAccountId());
				if (savings == null) {
					System.out.println("Account not found. Please try again.");
					return accountMenu(account);
				}
				System.out.println("Balance: ");
				System.out.println("--------------------");
				System.out.println("SGD: " + savings.getBalance().getSGD()); // Savings only have SGD
				System.out.println("--------------------");
				System.out.println("1. Deposit");
				System.out.println("2. Withdraw");
				System.out.println("3. Transfer");
				System.out.println("4. View Transactions");
				System.out.println("5. Settings");
				break;
			case "Insurance":
				insuranceGUI.printTravelPolicyTable(account.getAccountId(), "All");
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println("1. Claim Insurance");
				System.out.println("2. Purchase Insurance");
				System.out.println("3. Cancel Insurance");
				break;
			case "Fx":
				FX fx = FX.getFXByAccountID(account.getAccountId());
				if (fx == null) {
					System.out.println("Account not found. Please try again.");
					// return accountMenu(account);
					break;
				}
				System.out.println("Balance: ");
				System.out.println("-------------");
				System.out.println("SGD: " + fx.getAmount().getSGD());
				System.out.println("MYR: " + fx.getAmount().getMYR());
				System.out.println("AUD: " + fx.getAmount().getAUD());
				System.out.println("USD: " + fx.getAmount().getUSD());
				System.out.println("GBP: " + fx.getAmount().getGBP());
				System.out.println("-------------");
				System.out.println("1. Convert Currency");
				System.out.println("2. View Transactions");
				System.out.println("3. View Exchange Rates");
				System.out.println("4. Top up funds from savings account");
				System.out.println("5. Settings");
				break;
			case "Credit Card":
				String accountId = account.getAccountId();
				CreditCardGUI.printCreditCardKeyAndName(accountId);
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println("Credit Card Information");
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println("1. Apply for Credit Card");
				System.out.println("2. View Credit Card Details");
				System.out.println("0. Exit Credit Card Menu");
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println("Please enter your choice: ");
				break;
			case "Loan":
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println("Loan Information");
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println("1. Apply for Loan");
				System.out.println("2. View Loan Details");
				System.out.println("3. Make Loan Payment");
				System.out.println("4. View Loan Transactions");
				break;
			default:
				System.out.println("Invalid account type. Please try again.");
				accountMenu(account);
				return choice;
		}
		System.out.println("0. Back to Home Menu");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Please enter your choice: ");
		return getChoice();
	}

	/**
	 * Prints the table of savings accounts belonging to the given customer
	 * consisting of Account ID, Account Type and Balance.
	 *
	 * @param customer The customer whose savings accounts are to be printed.
	 * @return An ArrayList containing the customer's savings accounts.
	 */
	protected static ArrayList<Account> printSavingsAccountsTable(Customer customer) {
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println(customer.getUsername() + "'s Savings Accounts");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("|    | Account ID                           | Type    | Balance");
		int index = 1;
		ArrayList<Account> customerAccounts = new ArrayList<Account>();
		for (Account account : Account.getAccountsByCustomer(customer, "Active")) {
			if (account.getType().equals("Savings")) {
				customerAccounts.add(account);
				System.out.println("| " + index + ". | " + account.getAccountId() + " | " + account.getType() + " | "
						+ Savings.getSavingsByAccountID(account.getAccountId()).getBalance().getSGD() + " SGD");
				index++;
			}
		}
		System.out.println("---------------------------------------------------------------------------------");
		return customerAccounts;
	}

	/**
	 * Handles user input for different savings account operations such as Deposit,
	 * Withdraw, Transfer, Viewing of Transactions, and Settings.
	 *
	 * @param choice  The user's choice in integer.
	 * @param account The savings account on which the operation is performed.
	 * @return The next menu to be displayed.
	 * @throws Exception
	 */
	public int savingsAccountChoice(int choice, Account account) throws Exception {
		Savings savings = Savings.getSavingsByAccountID(account.getAccountId());
		Limits limitAcc = Limits.getLimitAccByID(account.getAccountId());
		switch (choice) {
			case 1:
				// Deposit
				savingsGUI.depositMenu(savings);
				return accountMenu(account);
			case 2:
				// Withdraw
				if (limitAcc.overWithdrawLimit(account.getAccountId(), "SGD")) {
					System.out.println("You have used up your daily withdrawal limit. Please try again tomorrow.");
				} else {
					savingsGUI.withdrawMenu(savings, limitAcc);
				}
				return accountMenu(account);
			case 3:
				// Transfer
				if (limitAcc.overTransferLimit(account.getAccountId(), "SGD")) {
					System.out.println("You have used up your daily transfer limit. Please try again tomorrow.");
				} else {
					savingsGUI.transferMenu(savings, limitAcc);
				}
				return accountMenu(account);
			case 4:
				// View Transactions
				savingsGUI.transactionHistoryMenu(savings);
				return accountMenu(account);
			case 5:
				// Settings
				settingsChoice(account);
				return accountMenu(account);
			default:
				System.out.println("Invalid choice. Please try again.");
				return accountMenu(account);
		}
	}

	/**
	 * Handles user input for insurance account operations such as Claim, Purchase,
	 * Cancel.
	 *
	 * @param choice  The user's choice.
	 * @param account The insurance account on which the operation is performed.
	 * @return The next menu to be displayed.
	 * @throws Exception
	 */
	public int insuranceAccountChoice(int choice, Account account) throws Exception {
		switch (choice) {
			case 1:
				// Claim Insurance
				int typeChoice = insuranceGUI.travelInsuranceMenu();
				if (typeChoice == 1) {
					insuranceGUI.claimTravelInsuranceMenu(account);
					return accountMenu(account);
				} else if (typeChoice == 0) {
					return accountMenu(account);
				}
			case 2:
				// Purchase Insurance
				typeChoice = insuranceGUI.travelInsuranceMenu();
				if (typeChoice == 1) {
					boolean purchaseCompleted = insuranceGUI.purchaseTravelInsuranceMenu(account);
					if (purchaseCompleted) {
						returnToMenu();
						return accountMenu(account);
					} else {
						return accountMenu(account);
					}
				} else if (typeChoice == 0) {
					return accountMenu(account);
				}
			case 3:
				// Cancel Insurance
				typeChoice = insuranceGUI.travelInsuranceMenu();
				if (typeChoice == 1) {
					insuranceGUI.cancelInsuranceMenu(account);
				} else if (typeChoice == 0) {
					return accountMenu(account);
				}
			default:
				System.out.println("Invalid choice. Please try again.");
				return accountMenu(account);
		}
	}

	/**
	 * Handles user input for foreign exchange account operations.
	 *
	 * @param choice  The user's choice.
	 * @param account The foreign exchange account on which the operation is
	 *                performed.
	 * @return The next menu to be displayed.
	 * @throws Exception if an error occurs.
	 */
	public int fxAccountChoice(int choice, Account account) throws Exception {
		FX fx = FX.getFXByAccountID(account.getAccountId());
		Customer customer = Customer.getCustomerByID(account.getCustomerID());
		switch (choice) {
			case 1:
				// Convert Currency
				fxGUI.convertCurrencyMenu(fx);
				return accountMenu(account);
			case 2:
				// View Transactions
				fxGUI.fxTransactionTable(fx);
				return accountMenu(account);
			case 3:
				// View Exchange Rates
				try {
					fxGUI.printExchangeRates(fx);
				} catch (Exception e) {
					System.out.println("An error occurred while printing exchange rates: " + e.getMessage());
				}
				return accountMenu(account);
			case 4:
				// Top up funds from savings account
				fxGUI.topUpFundsMenu(fx, customer);
				return accountMenu(account);
			case 5:
				// Settings
				settingsChoice(account);
				return accountMenu(account);
			default:
				System.out.println("Invalid choice. Please try again.");
				return accountMenu(account);
		}
	}

	public int creditCardAccountChoice(int choice, Account account) throws Exception {
		switch (choice) {
			case 1:
				// Apply for Credit Card
				creditCardGUI.creditCardTypeChoice(account);
				return accountMenu(account);
			case 2:
				// View Credit Card Details
				creditCardGUI.viewCreditCardDetails(account);
				return accountMenu(account);
			case 3:
				// Make Credit Card Payment
				// creditCardGUI.makeCreditCardPayment(account);
				return accountMenu(account);
			case 4:
				// View Credit Card Transactions
				// creditCardGUI.viewCreditCardTransactions(account);
				return accountMenu(account);
			case 0:
				return 0;
			default:
				System.out.println("Invalid choice. Please try again.");
				return accountMenu(account);
		}
	}

	/**
	 * Handles user input for different operations in the account settings such as
	 * Updating Daily Transfer / Withdraw Limits or Viewing of Daily Limits and
	 * remaining amount that can be transferred / withdrawn today.
	 *
	 * @param account The account for which settings are being changed.
	 * @throws IOException    if an I/O error occurs.
	 * @throws ParseException if an error occurs while parsing.
	 */
	public void settingsChoice(Account account) throws IOException, ParseException {
		Limits limitAcc = Limits.getLimitAccByID(account.getAccountId());
		if (limitAcc == null) {
			System.out.println("No limits found for this account. Please try again.");
			return;
		}
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Settings Menu");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("1. Change Daily Transfer Limit");
		System.out.println("2. Change Daily Withdrawal Limit");
		System.out.println("3. View Daily Limits & Remaining for Today");
		System.out.println("0. Back to Home Menu");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Please enter your choice: ");
		int choice = getChoice();
		switch (choice) {
			case 1:
				// Transfer Limit
				settingsGUI.transferLimitMenu(limitAcc);
				break;
			case 2:
				// Withdraw Limit
				settingsGUI.withdrawLimitMenu(limitAcc);
				break;
			case 3:
				// Display limits and remaining for today
				settingsGUI.displayLimits(limitAcc);
				break;
			case 0:
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
				settingsChoice(account);
		}
	}
	/**
	 * Handles loan account actions based on user choice.
	 * Choices include applying for loans, viewing details, making payments, or seeing transaction history.
	 *
	 * @param choice The user's selected action.
	 * @param account The account to which the action applies.
	 * @return The next menu to be displayed.
	 * @throws Exception If an operation fails or is invalid.
	 */
	public int loanAccountChoice(int choice, Account account) throws Exception {
		SunshineLoan loan = SunshineLoan.getLoanByAccountID(account.getAccountId());
		switch (choice) {
			case 1:
				// Apply for loan
				loanGUI.applyForLoanTypeChoice(account, loan);
				return accountMenu(account);
			case 2:
				// View Loan Details
				loanGUI.viewLoanDetailsChoice(account, loan);
				return accountMenu(account);
			case 3:
				// Make Loan Payment
				loanGUI.makeLoanPayment(account);
				return accountMenu(account);
			case 4:
				// View Loan Transactions
				loanGUI.loanTransactions(account);
				return accountMenu(account);
			case 0:
				// Exit Loan Menu
				return 0;
			default:
				System.out.println("Invalid choice. Please try again.");
				return accountMenu(account);
		}
	}

}

/**
 * The SavingsGUI class extends GUI and provides methods for displaying menus
 * related to savings accounts.
 */
class SavingsGUI extends GUI {
	/**
	 * Displays the menu for depositing funds into a savings account.
	 *
	 * @param savings The savings account for which funds are being deposited.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void depositMenu(Savings savings) throws IOException {
		double deposit = 0;
		System.out.println("Please enter the amount you would like to deposit:");
		deposit = this.getDouble();
		if (deposit <= 0) {
			System.out.println("Invalid amount. Please try again.");
			depositMenu(savings);
		}
		savings.deposit(BigDecimal.valueOf(deposit), "Deposit");
	}

	/**
	 * Displays the menu for withdrawing funds from a savings account.
	 *
	 * @param savings  The savings account from which funds are being withdrawn. The
	 *                 balance of the account will be checked against the withdrawal
	 *                 amount.
	 * @param limitAcc The daily limits associated with the account. Amount to be
	 *                 withdrawn will be checked against the withdrawal limits and
	 *                 the remaining amount that can be withdrawn today.
	 * @throws IOException    if an I/O error occurs.
	 * @throws ParseException if an error occurs while parsing.
	 */
	protected void withdrawMenu(Savings savings, Limits limitAcc) throws IOException, ParseException {
		BigDecimal withdraw = new BigDecimal(0);
		System.out.println("Please enter the amount you would like to withdraw:");
		withdraw = BigDecimal.valueOf(this.getDouble());
		BigDecimal remainWithdrawSGD = limitAcc.getRemainWithdrawAmt(limitAcc).getSGD();
		if (withdraw.compareTo(BigDecimal.ZERO) <= 0) {
			System.out.println("Invalid amount. Please try again.");
			withdrawMenu(savings, limitAcc);
		} else if (withdraw.compareTo(savings.getBalance().getSGD()) > 0) {
			System.out.println("Insufficient funds. Please try again.");
			withdrawMenu(savings, limitAcc);
		} else if (withdraw.compareTo(remainWithdrawSGD) > 0) {
			System.out.println("Withdrawal amount exceeds remaining daily limit. Please try again.");
			withdrawMenu(savings, limitAcc);
		} else {
			savings.withdraw(withdraw, "Withdraw");
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("Withdrawal amount of $" + withdraw + " successful!");
			returnToMenu();
		}
	}

	/**
	 * Displays the menu for transferring funds from a savings account to another
	 * savings account.
	 *
	 * @param savings  The savings account from which funds are being transferred.
	 *                 The balance of the account will be checked against the
	 *                 transfer amount.
	 * @param limitAcc The daily limits associated with the account. Amount to be
	 *                 transferred will be checked against the transfer limits and
	 *                 the remaining amount that can be transferred today.
	 * @throws IOException    if an I/O error occurs.
	 * @throws ParseException if an error occurs while parsing.
	 */
	protected void transferMenu(Savings savings, Limits limitAcc) throws IOException, ParseException {
		System.out.println("Please enter the account ID you would like to transfer to:");
		String accountID = this.scanner.next();
		Savings transferTo = Savings.getSavingsByAccountID(accountID);
		BigDecimal remainTransferSGD = limitAcc.getRemainTransferAmt(limitAcc).getSGD();
		if (transferTo == null || transferTo == savings) {
			System.out.println("Account not found. Please try again.");
			transferMenu(savings, limitAcc);
		}
		System.out.println("Please enter the amount you would like to transfer:");
		BigDecimal amount = new BigDecimal(this.getDouble());
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			System.out.println("Invalid amount. Please try again.");
			transferMenu(savings, limitAcc);
		} else if (amount.compareTo(savings.getBalance().getSGD()) > 0) {
			System.out.println("Insufficient funds. Please try again.");
			transferMenu(savings, limitAcc);
		} else if (amount.compareTo(remainTransferSGD) > 0) {
			System.out.println("Transfer amount exceeds remaining daily limit. Please try again.");
			transferMenu(savings, limitAcc);
		} else {
			savings.transferTo(transferTo, amount);
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("Transfer amount of $" + amount + " to " + transferTo.getAccountID() + " successful!");
			returnToMenu();
		}
	}

	/**
	 * Handles the display of transaction history of a savings account and returning
	 * back to previous menu.
	 *
	 * @param savings The savings account for which the transaction history is
	 *                displayed.
	 */
	protected void transactionHistoryMenu(Savings savings) {
		savingsTransactionTable(savings);
		System.out.println("0. Return to Main Menu");
		int choice = getChoice();
		while (choice != 0) {
			System.out.println("Please enter 0 to return to main menu");
			getChoice();
		}
	}

	/**
	 * Prints the transaction history table of a savings account including Timestamp
	 * of transaction, Transaction ID, and Amount of the transaction in SGD.
	 *
	 * @param savings The savings account for which the transaction history is
	 *                printed.
	 */
	protected void savingsTransactionTable(Savings savings) {
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Account ID: " + savings.getAccountID());
		System.out.println("Transaction History");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("||      || Timestamp               || Transaction ID                       || Amount (SGD)");
		int index = 1;
		// Put all transactions in arraylist to sort by timestamp
		ArrayList<Transactions> transactionHist = savings.getTransactionHistory();
		Collections.sort(transactionHist, Comparator.comparing(Transactions::getTimestamp).reversed());
		for (Transactions transactions : transactionHist) {
			System.out.println("|| " + String.format("%3s.", index) + " || " + String.format("%-23s", transactions.getTimestamp()) +
					" || " + transactions.getTransactionID() + " || " + transactions.getAmount().getSGD());
			index++;
		}
		System.out.println("---------------------------------------------------------------------------------");
	}
}

/**
 * The InsuranceGUI class extends GUI and provides methods for displaying menus
 * related to insurance policies.
 */
class InsuranceGUI extends GUI {
	/**
	 * Displays the menu for selecting the type of insurance such as Travel
	 * Insurance.
	 *
	 * @return The user's choice in integer.
	 */
	protected int travelInsuranceMenu() {
		System.out.println("Please enter the index of the type of insurance for this request:");
		System.out.println("1. Travel Insurance");
		System.out.println("0. Exit Current Menu");
		int choice = getChoice();
		return choice;
	}

	/**
	 * Prints the table of travel insurance policies associated with an account for
	 * different types of operations.
	 * <p>
	 * If user wants to cancel insurance, only the insurances that are available for
	 * cancellation will be displayed.
	 * <p>
	 * If user wants to claim insurance, only the insurances that are active and
	 * within the travel period will be displayed.
	 * <p>
	 *
	 * @param accountID   The ID of the account.
	 * @param displayType The type of policies to display such as Cancel, Claim, or
	 *                    All.
	 * @return An ArrayList of TravelInsurance objects.
	 */
	protected ArrayList<TravelInsurance> printTravelPolicyTable(String accountID, String displayType) {
		ArrayList<TravelInsurance> travelAcc = TravelInsurance.getInsuranceByAccountID(accountID);
		ArrayList<TravelInsurance> travelTemp = new ArrayList<TravelInsurance>();
		Date currentDate = new Date(); // Getting today's date
		System.out.println("---------------------------------------------------------------------------------");
		String travelTableHeader = "|     | Policy ID                            | Account ID                           | Status        | Type   | Coverage | Premium  | Start Date | End Date   | Continent";
		if (travelAcc.isEmpty()) {
			System.out.println("No travel insurance policies found for this account.");
			System.out.println("Please purchase an insurance policy first.");
		} else {
			int index = 1;
			for (TravelInsurance insurance : travelAcc) {
				// Display only active policies for cancellation
				if (displayType == "Cancel" && insurance.getStatus().equals(Insurance.statusList[0])) {
					travelTemp.add(insurance);
				} else if (displayType == "Claim" && insurance.getStatus().equals(Insurance.statusList[0])
						&& (currentDate.compareTo(insurance.getStartDate()) >= 0)
						&& (currentDate.compareTo(insurance.getEndDate()) <= 0)) {
					travelTemp.add(insurance);
				} else if (displayType == "All") {
					travelTemp.add(insurance);
				}
			}
			if (!travelTemp.isEmpty()) { // Checks if there are any insurances to display
				System.out.println(travelTableHeader);
				for (TravelInsurance insuranceTemp : travelTemp) {
					String formattedIndex = String.format("%2d.", index);
					String formattedStatus = String.format("%-13s", insuranceTemp.getStatus());
					String formattedCoverage = String.format("$%-7.2f", insuranceTemp.getCoverage());
					String formattedPremium = String.format("$%-7.2f", insuranceTemp.getPremium());
					System.out.println("| " + formattedIndex + " | " + insuranceTemp.getPolicyID() + " | " + accountID
							+ " | " + formattedStatus + " | " + insuranceTemp.getType() + " | " + formattedCoverage +
							" | " + formattedPremium + " | " + Insurance.sdf.format(insuranceTemp.getStartDate())
							+ " | " + Insurance.sdf.format(insuranceTemp.getEndDate()) + " | "
							+ insuranceTemp.getContinent());
					index++;
				}
			}
		}
		return travelTemp;
	}

	/**
	 * Prints the IDs of savings accounts associated with a customer, this is used
	 * when purchasing / cancelling a policy, where the amount will be deducted /
	 * refunded to the selected savings account.
	 *
	 * @param account The account for which savings IDs are printed.
	 * @return An ArrayList of savings account IDs.
	 */
	protected static ArrayList<String> printSavingsID(Account account) {
		String customerID = account.getCustomerID(); // Get the customerID based on the account
		ArrayList<String> customerSavingsID = new ArrayList<String>();
		for (Account acc : Account.allAccounts) {
			if (acc.getCustomerID().equals(customerID) && acc.getType().equals("Savings")) {
				customerSavingsID.add(acc.getAccountId()); // get all savings accountID for the customer
			}
		}
		if (customerSavingsID.isEmpty()) {
			System.out.println("No savings account found for this customer.");
		} else {
			int index = 1;
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("|    |          Savings Account ID          | Balance (SGD)");
			for (String id : customerSavingsID) {
				System.out.println("| " + index + ". | " + id + " | " + Savings.getSavingsByAccountID(id).getBalance().getSGD());
				index++;
			}
		}
		return customerSavingsID;
	}

	/**
	 * Displays the menu for claiming a travel insurance policy. Insurances that can
	 * be claimed will be displayed for the customer.
	 *
	 * @param account The account associated with the insurance policy.
	 * @throws ParseException if an error occurs while parsing String to Date
	 *                        format.
	 * @throws IOException    if an I/O error occurs.
	 */
	protected void claimTravelInsuranceMenu(Account account) throws ParseException, IOException {
		ArrayList<TravelInsurance> claimInsurances = printTravelPolicyTable(account.getAccountId(), "Claim"); // display only active policies for customer to claim
		if (claimInsurances.isEmpty()) { // Checks if the claimInsurances array is empty
			System.out.println("You do not have any active travel insurance policies to claim.");
			System.out.println("You may only claim insurance during the period of your travel.");
			returnToMenu();
		} else {
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("0. Exit Claim Insurance Menu");
			System.out.println("Please enter the index of the insurance you would like to claim: ");
			int choice = getChoice();
			if (choice == 0) {
				return;
			}
			TravelInsurance insuranceClaim = claimInsurances.get(choice - 1);
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("Please enter the number of days you would like to claim insurance for: ");
			int days = Integer.parseInt(this.scanner.next());
			BigDecimal claimAmt = TravelInsurance.processTravelClaim(insuranceClaim, days);
			if (claimAmt.equals(BigDecimal.ZERO)) {
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println("No. of days to be claimed exceeds travel duration. Please try again.");
				returnToMenu();
			} else {
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println("Your calculated amount to be claimed is: $" + claimAmt);
				System.out.println("Please wait for 3 - 5 business days for your claim to be processed.");
				returnToMenu();
			}
		}
	}

	/**
	 * Displays the menu for purchasing a travel insurance policy. Date validations
	 * take place such as ensuring the entered date is not before today's date
	 * and customers can purchase policies from 180 days before the travel start
	 * date.
	 * <p>
	 * If the customer does not have a savings account for the premium price to be
	 * deducted from, the customer will be prompted to create a savings account
	 * first.
	 *
	 * @param account The account for which the insurance policy is being purchased.
	 * @return true if the insurance has been purchased by customer, false
	 *         otherwise.
	 * @throws ParseException if an error occurs while parsing.
	 * @throws IOException    if an I/O error occurs.
	 */
	protected boolean purchaseTravelInsuranceMenu(Account account) throws ParseException, IOException {
		boolean hasPurchased = false;
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Travel Insurance Information:");
		System.out.println("Coverage Amount: Starting from $150SGD");
		System.out.println("Premium Amount: Starting from $10SGD");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Please choose the continent you will be traveling to:");
		System.out.println("1. Asia");
		System.out.println("2. Australia");
		System.out.println("3. Europe");
		System.out.println("4. USA");
		System.out.println("5. Africa");
		System.out.println("6. Other parts of the world");
		System.out.println("0. Exit Purchase Insurance Menu");
		int choice = getChoice();
		if (choice == 0) {
			return hasPurchased;
		}
		String continent = TravelInsurance.continents[choice - 1];

		System.out.println("Please enter the start date of your travel (YYYY-MM-DD):");
		String startDate = this.scanner.next();
		if (TravelInsurance.validateDate(startDate) == null) {
			System.out.println("You cannot enter a start date before today's date. Please try again.");
			returnToMenu();
			return hasPurchased;
		} else if (TravelInsurance.isStartDateWithRange(startDate) == false) {
			System.out.println("Start date of travel insurance must be within 180 days from today's date.");
			returnToMenu();
			return hasPurchased;
		}
		System.out.println("Please enter the end date of your travel (YYYY-MM-DD):");
		String endDate = this.scanner.next();
		if (TravelInsurance.validateDate(endDate) == null) {
			System.out.println("You cannot enter an end date before today's date. Please try again.");
			returnToMenu();
			return hasPurchased;
		}

		BigDecimal premium = TravelInsurance.travelPremium(startDate, endDate, continent);
		System.out.println("The calculated premium price for your travel insurance is: $" + premium);
		System.out.println("Would you like to purchase this travel insurance? (Y/N)");
		String purchaseResponse = this.scanner.next().trim().toLowerCase(); // Normalize input for easier comparison

		if (purchaseResponse.equals("yes") || purchaseResponse.equals("y")) {
			ArrayList<String> customerSavings = printSavingsID(account);
			if (customerSavings.isEmpty()) {
				System.out.println("Please make a savings account first.");
				returnToMenu();
			} else {
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println(
						"Please enter the index of the savings account you would like to deduct the premium from:");
				choice = getChoice();
				String savingsID = customerSavings.get(choice - 1);
				Savings savings = Savings.getSavingsByAccountID(savingsID);
				String policyID = TravelInsurance.generatePolicyID();
				BigDecimal coverage = TravelInsurance.calculateTravelCoverage(continent);
				TravelInsurance insuranceTravel = new TravelInsurance(policyID, account.getAccountId(), "Active",
						"Travel", coverage, premium, TravelInsurance.dateFormatter(startDate), TravelInsurance.dateFormatter(endDate), continent);
				TravelInsurance.purchaseTravelInsurance(insuranceTravel, savings);
				System.out.println("---------------------------------------------------------------------------------");
				if (savings.getBalance().getSGD().compareTo(insuranceTravel.getPremium()) >= 0) {
					System.out.println("You have purchased travel insurance at $" + premium + " from " + startDate
							+ " to " + endDate + " for " + continent);
					System.out.println("Remaining balance for " + savings.getAccountID() + ": $" + savings.getBalance().getSGD());
					hasPurchased = true;
				}
				return hasPurchased;
			}
		} else if (purchaseResponse.equals("no") || purchaseResponse.equals("n")) {
			System.out.println("Travel insurance will not be purchased.");
			returnToMenu();
		} else {
			System.out.println("Invalid choice. Please try again.");
		}
		return hasPurchased;
	}

	/**
	 * Displays the menu for canceling an insurance policy. If there are no active
	 * policies to cancel, the customer will be displayed a message.
	 * <p>
	 * If the customer does not have a savings account to refund the premium to, the
	 * customer will be prompted to create a savings account first.
	 *
	 * @param account The account associated with the insurance policy.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void cancelInsuranceMenu(Account account) throws IOException {
		ArrayList<TravelInsurance> cancelTravelInsurances = printTravelPolicyTable(account.getAccountId(), "Cancel"); // display only active policies for customer to cancel
		if (cancelTravelInsurances.isEmpty()) {
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("You do not have any active insurance policies to cancel.");
			returnToMenu();
		} else {
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("0. Exit Cancel Insurance Menu");
			System.out.println("Please enter the index of the insurance you would like to cancel:");
			int choice = getChoice();
			if (choice == 0) {
				return;
			}
			TravelInsurance insuranceCancel = cancelTravelInsurances.get(choice - 1);
			ArrayList<String> customerSavings = printSavingsID(account);
			if (customerSavings.isEmpty()) {
				System.out.println("Please make a savings account first.");
				returnToMenu();
			} else {
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println("Please enter the index of the savings account you would like to refund the premium to:");
				choice = getChoice();

				System.out.println("Are you sure you want to cancel this insurance? (Y/N)");
				String cancelResponse = this.scanner.next().trim().toLowerCase(); // Normalize input for easier
																					// comparison
				if (cancelResponse.equals("yes") || cancelResponse.equals("y")) {
					String savingsID = customerSavings.get(choice - 1);
					Savings savings = Savings.getSavingsByAccountID(savingsID);
					TravelInsurance.cancelTravelInsurance(insuranceCancel, savings);
					System.out.println("---------------------------------------------------------------------------------");
					System.out.println("You have cancelled travel insurance " + insuranceCancel.getPolicyID() + " at $" + insuranceCancel.getPremium());
					System.out.println("Status set to: Terminated");
					System.out.println("Updated balance for " + savings.getAccountID() + ": $" + savings.getBalance().getSGD());
					returnToMenu();
				} else if (cancelResponse.equals("no") || cancelResponse.equals("n")) {
					System.out.println("Insurance will not be cancelled.");
					returnToMenu();
				} else {
					System.out.println("Invalid choice. Please try again.");
				}
			}
		}
	}
}

/**
 * The FxGUI class extends GUI and provides methods for displaying menus related
 * to foreign exchange transactions.
 */
class FxGUI extends GUI {

	/**
	 * Displays the menu for converting currencies.
	 *
	 * @param fx The FX object representing the foreign exchange functionality.
	 * @throws Exception if an error occurs.
	 */
	protected void convertCurrencyMenu(FX fx) throws Exception {
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Please enter the currency you would like to convert from:");
		System.out.println("1. SGD");
		System.out.println("2. MYR");
		System.out.println("3. AUD");
		System.out.println("4. USD");
		System.out.println("5. GBP");
		System.out.println("---------------------------------------------------------------------------------");

		String fromCurrency = fx.convertInput(getChoice());
		// Check for valid user input
		if (fromCurrency.equals("Invalid input")) {
			System.out.println("Invalid choice. Please try again.");
			convertCurrencyMenu(fx);
			return;
		}

		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Please enter the currency you would like to convert to:");
		System.out.println("1. SGD");
		System.out.println("2. MYR");
		System.out.println("3. AUD");
		System.out.println("4. USD");
		System.out.println("5. GBP");
		System.out.println("---------------------------------------------------------------------------------");
		String toCurrency = fx.convertInput(getChoice());
		// Check for valid user input
		if (toCurrency.equals("Invalid input")) {
			System.out.println("Invalid choice. Please try again.");
			convertCurrencyMenu(fx);
			return;
		} else if (fromCurrency.equals(toCurrency)) {
			System.out.println("Unable to convert the same currency. Please try again.");
			convertCurrencyMenu(fx);
			return;
		}

		// Get user input for amount to convert
		System.out.println("Please enter the amount you would like to convert:");
		BigDecimal amount = new BigDecimal(GUI.scanner.nextDouble());

		// Check if the user has sufficient funds to convert
		boolean sufficientAmount = false;
		switch (fromCurrency) {
			case "SGD":
				if (fx.getAmount().getSGD().compareTo(amount) >= 0) {
					sufficientAmount = true;
				} else {
					System.out.println("Insufficient funds. Please try again.");
					convertCurrencyMenu(fx);
					return;
				}
				break;
			case "MYR":
				if (fx.getAmount().getMYR().compareTo(amount) >= 0) {
					sufficientAmount = true;
				} else {
					System.out.println("Insufficient funds. Please try again.");
					convertCurrencyMenu(fx);
					return;
				}
				break;
			case "AUD":
				if (fx.getAmount().getAUD().compareTo(amount) >= 0) {
					sufficientAmount = true;
				} else {
					System.out.println("Insufficient funds. Please try again.");
					convertCurrencyMenu(fx);
					return;
				}
				break;
			case "USD":
				if (fx.getAmount().getUSD().compareTo(amount) >= 0) {
					sufficientAmount = true;
				} else {
					System.out.println("Insufficient funds. Please try again.");
					convertCurrencyMenu(fx);
					return;
				}
				break;
			case "GBP":
				if (fx.getAmount().getGBP().compareTo(amount) >= 0) {
					sufficientAmount = true;
				} else {
					System.out.println("Insufficient funds. Please try again.");
					convertCurrencyMenu(fx);
					return;
				}
				break;
		}

		// If user has sufficient funds, proceed with the conversion
		if (sufficientAmount && amount.compareTo(BigDecimal.ZERO) > 0) {

			BigDecimal exchangeRate = fx.getExchangeRate(fromCurrency, toCurrency);
			BigDecimal commissionRate = fx.calculateCommissionRate(amount, toCurrency);
			BigDecimal convertedAmount = fx.calculateConvertedAmount(amount, exchangeRate, commissionRate);
			BigDecimal roundConvertedAmount = convertedAmount.setScale(2, RoundingMode.DOWN);
			BigDecimal commission = fx.calculateExchangeFee(amount, commissionRate);

			// Show the user the current exchange rates and the conversion details
			System.out.println("Current exchange rates: 1 " + fromCurrency + " = " + exchangeRate + " " + toCurrency);
			System.out.println("Converting " + amount.subtract(commission) + " " + fromCurrency + " to " + roundConvertedAmount
							+ " " + toCurrency + " with a fee of " + commission + " " + fromCurrency);
			// Prompt user to confirm the conversion
			System.out.println("Would you like to proceed with the conversion? (Y/N)");
			String confirm = scanner.next();
			if (confirm.equalsIgnoreCase("N")) {
				System.out.println("Conversion cancelled. Returning to FX menu.");
				TimeUnit.SECONDS.sleep(3); // Delay for 3 seconds before returning to FX menu
				return;
			} else if (!confirm.equalsIgnoreCase("Y")) {
				System.out.println("Invalid choice. Please try again.");
				convertCurrencyMenu(fx);
				return;
			} else {
				fx.convertCurrency(amount, fromCurrency, toCurrency, commission, roundConvertedAmount); // User has confirmed the conversion
				System.out.println("Please press Enter to go back to FX menu.");
				System.in.read();
				scanner.nextLine();
				return;
			}
		} else if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			System.out.println("Invalid amount. Please try again.");
			convertCurrencyMenu(fx);
			return;
		}

	}

	/**
	 * Prints the current exchange rates for different currencies.
	 *
	 * @param fx The FX object representing the foreign exchange functionality.
	 * @throws Exception if an error occurs.
	 */
	protected void printExchangeRates(FX fx) throws Exception {
		System.out.println("Exchange Rates");
		System.out.println("-----------------------");
		// Get exchange rate of SGD to MYR and print
		BigDecimal myrRate = fx.getExchangeRate("SGD", "MYR");
		System.out.println("SGD 1.00 = MYR " + myrRate);

		// Get exchange rate of SGD to AUD and print
		BigDecimal audRate = fx.getExchangeRate("SGD", "AUD");
		System.out.println("SGD 1.00 = AUD " + audRate);

		// Get exchange rate of SGD to USD and print
		BigDecimal usdRate = fx.getExchangeRate("SGD", "USD");
		System.out.println("SGD 1.00 = USD " + usdRate);

		// Get exchange rate of SGD to GBP and print
		BigDecimal gbpRate = fx.getExchangeRate("SGD", "GBP");
		System.out.println("SGD 1.00 = GBP " + gbpRate);
		System.out.println("-----------------------");

		System.out.println("Press Enter to continue to FX menu.");
		System.in.read();
		scanner.nextLine();
		return;
	}

	/**
	 * Displays the menu for topping up funds to the FX account from a savings
	 * account.
	 * <p>
	 * If the amount to be topped up exceeds the daily transfer limit of the savings
	 * account, the user will be prompted to try again the next day.
	 * <p>
	 *
	 * @param fx       The FX object representing the foreign exchange
	 *                 functionality.
	 * @param customer The customer associated with the FX account.
	 * @throws IOException if an I/O error occurs.
	 * @throws Exception   if an error occurs.
	 */
	protected void topUpFundsMenu(FX fx, Customer customer) throws IOException, Exception {

		// Print the customer's savings accounts
		ArrayList<Account> customerAccounts = AccountGUI.printSavingsAccountsTable(customer);

		// Prompt the user to select a savings account to top up from
		System.out.println("Please enter the index of the savings account you would like to top up from:");
		int choice = getChoice();
		Account selectedAccount = customerAccounts.get(choice - 1);
		String accountID = selectedAccount.getAccountId();

		System.out.println("You have chosen to top up funds from " + accountID + " to your FX account.");
		Savings savings = Savings.getSavingsByAccountID(accountID);

		System.out.println("Please enter the amount you would like to top up:");
		BigDecimal topUpAmount = new BigDecimal(GUI.scanner.nextDouble());
		Limits limitAcc = Limits.getLimitAccByID(accountID);

		if (topUpAmount.compareTo(BigDecimal.ZERO) <= 0 || topUpAmount.compareTo(savings.getBalance().getSGD()) > 0) {
			System.out.println("Invalid amount. Please try again.");
			topUpFundsMenu(fx, customer);
			return;
		} else if (topUpAmount.compareTo(limitAcc.getRemainTransferAmt(limitAcc).getSGD()) > 0) {
			System.out.println("Amount exceeds remaining daily limit of the savings account. Please try again.");
			topUpFundsMenu(fx, customer);
			return;
		}
		savings.transferFromSavingsToFX(fx, topUpAmount, savings);
		System.out.println(
				topUpAmount + " SGD has been successfully transferred from " + accountID + " to your FX account.");
		System.out.println("Your FX account balance is now: " + fx.getAmount().getSGD() + " SGD");
		System.out.println("Returning to FX menu...");
		TimeUnit.SECONDS.sleep(7); // Delay for 7 seconds before returning to FX menu
		return;
	}

	/**
	 * Displays the transaction history for the FX account including Timestamp of
	 * transaction, Transaction ID, and the Amount of the transaction in the
	 * currencies that are related to the transaction.
	 * <p>
	 * Transaction history is displayed in order of most recent Timestamp to the
	 * oldest Timestamp.
	 *
	 * @param fx The FX object representing the foreign exchange functionality.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void fxTransactionTable(FX fx) throws IOException {
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Account ID: " + fx.getAccountID());
		System.out.println("Transaction History");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("||      || Timestamp               || Transaction ID                       || Amount");
		int index = 1;
		// Put all transactions in arraylist to sort by timestamp
		ArrayList<Transactions> transactionHist = fx.getTransactionHistory();
		Collections.sort(transactionHist, Comparator.comparing(Transactions::getTimestamp).reversed());
		for (Transactions transactions : transactionHist) {
			String amt = "";
			// Display only the amounts that are non-zero
			if (transactions.getAmount().getSGD().compareTo(BigDecimal.ZERO) != 0) {
				amt += "SGD: " + transactions.getAmount().getSGD() + " ";
			}
			if (transactions.getAmount().getMYR().compareTo(BigDecimal.ZERO) != 0) {
				amt += "MYR: " + transactions.getAmount().getMYR() + " ";
			}
			if (transactions.getAmount().getAUD().compareTo(BigDecimal.ZERO) != 0) {
				amt += "AUD: " + transactions.getAmount().getAUD() + " ";
			}
			if (transactions.getAmount().getUSD().compareTo(BigDecimal.ZERO) != 0) {
				amt += "USD: " + transactions.getAmount().getUSD() + " ";
			}
			if (transactions.getAmount().getGBP().compareTo(BigDecimal.ZERO) != 0) {
				amt += "GBP: " + transactions.getAmount().getGBP() + " ";
			}

			System.out.println("|| " + String.format("%3s.", index) + " || "
					+ String.format("%-23s", transactions.getTimestamp()) +
					" || " + transactions.getTransactionID() + " || " + amt.trim());
			index++;
		}
		System.out.println("---------------------------------------------------------------------------------");
		returnToMenu();
	}
}

/**
 * The SettingsGUI class extends GUI and provides methods for displaying menus
 * related to settings and daily limits.
 */
class SettingsGUI extends GUI {
	private static String[] currencyList = { "SGD", "MYR", "AUD", "USD", "GBP" };

	/**
	 * Displays the menu for choosing a currency to change the withdrawal / transfer
	 * limit for.
	 * <p>
	 * If the account type is Fx, the user will be able to choose from a list of
	 * currencies.
	 * <p>
	 * If the account type is Savings, the user can only change limits for SGD
	 * currency.
	 *
	 * @param accountType The type of account.
	 * @return The choice of currency in integer.
	 */
	private int chooseCurrency(String accountType) {
		System.out.println("Please choose the currency you would like to change the limit for:");
		System.out.println("1. SGD");
		if (accountType.equals("Fx")) {
			System.out.println("2. MYR");
			System.out.println("3. AUD");
			System.out.println("4. USD");
			System.out.println("5. GBP");
		}
		System.out.println("0. Back to Home Menu");
		int currencyChoice = getChoice();
		return currencyChoice;
	}

	/**
	 * Displays the menu for changing the daily transfer limit.
	 * <p>
	 * Current daily transfer limits will be shown to the user before the user can
	 * input the new limit.
	 * <p>
	 * If the account type is FX, the user can choose from a list of currencies to
	 * change the limit for.
	 * <p>
	 * If the account type is Savings, the user can only change the transfer limit
	 * for SGD currency.
	 *
	 * @param limitAcc The Limits object representing the account limits.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void transferLimitMenu(Limits limitAcc) throws IOException {
		System.out.println("------------------------Change Daily Transfer Limit------------------------------");
		System.out.println("Your current transfer limits:");
		Balance transferLimit = Limits.getTransferLimitByID(limitAcc.getAccountId());
		System.out.println("SGD: " + transferLimit.getSGD());
		if (limitAcc.getType().equals("Fx")) {
			System.out.println("MYR: " + transferLimit.getMYR());
			System.out.println("AUD: " + transferLimit.getAUD());
			System.out.println("USD: " + transferLimit.getUSD());
			System.out.println("GBP: " + transferLimit.getGBP());
		}
		System.out.println("---------------------------------------------------------------------------------");
		int currencyChoice = chooseCurrency(limitAcc.getType());
		if (currencyChoice == 0) {
			return;
		}
		String currency = currencyList[currencyChoice - 1];
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Please enter the new daily transfer limit:");
		BigDecimal newLimit = new BigDecimal(this.getDouble());
		if (newLimit.compareTo(BigDecimal.ZERO) <= 0) {
			System.out.println("Invalid amount. Please try again.");
			transferLimitMenu(limitAcc);
		} else {
			limitAcc.updateTransferLimit(limitAcc, currency, newLimit);
			limitAcc.updateCSV(Limits.getLimitsPath());
			System.out.println("Daily transfer limit changed successfully!");
			returnToMenu();
		}
	}

	/**
	 * Displays the menu for changing the daily withdrawal limit.
	 * Current daily withdrawal limits will be shown to the user before the user can
	 * input the new limit.
	 * <p>
	 * If the account type is FX, the user can choose from a list of currencies to
	 * change the limit for.
	 * <p>
	 * If the account type is Savings, the user can only change the withdrawal limit
	 * for SGD currency.
	 *
	 * @param limitAcc The Limits object representing the account limits.
	 * @throws IOException if an I/O error occurs.
	 */
	protected void withdrawLimitMenu(Limits limitAcc) throws IOException {
		System.out.println("-----------------------Change Daily Withdrawal Limit-----------------------------");
		System.out.println("Your current transfer limits:");
		Balance withdrawLimit = Limits.getWithdrawLimitByID(limitAcc.getAccountId());
		System.out.println("SGD: " + withdrawLimit.getSGD());
		if (limitAcc.getType().equals("Fx")) {
			System.out.println("MYR: " + withdrawLimit.getMYR());
			System.out.println("AUD: " + withdrawLimit.getAUD());
			System.out.println("USD: " + withdrawLimit.getUSD());
			System.out.println("GBP: " + withdrawLimit.getGBP());
		}
		System.out.println("---------------------------------------------------------------------------------");
		int currencyChoice = chooseCurrency(limitAcc.getType());
		if (currencyChoice == 0) {
			return;
		}
		String currency = currencyList[currencyChoice - 1];
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Please enter the new daily withdrawal limit:");
		BigDecimal newLimit = new BigDecimal(this.getDouble());
		if (newLimit.compareTo(BigDecimal.ZERO) <= 0) {
			System.out.println("Invalid amount. Please try again.");
			withdrawLimitMenu(limitAcc);
		} else {
			limitAcc.updateWithdrawLimit(limitAcc, currency, newLimit);
			limitAcc.updateCSV(Limits.getLimitsPath());
			System.out.println("Daily withdrawal limit changed successfully!");
			returnToMenu();
		}
	}

	/**
	 * Displays the current transfer and withdrawal limits and the remaining amount
	 * that can be withdrawn / transferred today.
	 *
	 * @param limitAcc The Limits object representing the account limits.
	 * @throws IOException    if an I/O error occurs.
	 * @throws ParseException if an error occurs while parsing.
	 */
	protected void displayLimits(Limits limitAcc) throws IOException, ParseException {
		System.out.println("----------------------View Daily Limits & Remaining for Today--------------------");
		System.out.println("------------Transfer Limits-------------");
		System.out.println("Currency | Daily Limit | Remaining Limit");
		Balance transferLimit = Limits.getTransferLimitByID(limitAcc.getAccountId());
		Balance remainTransferLimit = limitAcc.getRemainTransferAmt(limitAcc);
		System.out.println("   SGD   | " + String.format("%-11.2f", transferLimit.getSGD()) + " | "
				+ remainTransferLimit.getSGD());
		if (limitAcc.getType().equals("Fx")) {
			System.out.println("   MYR   | " + String.format("%-11.2f", transferLimit.getMYR()) + " | "
					+ remainTransferLimit.getMYR());
			System.out.println("   AUD   | " + String.format("%-11.2f", transferLimit.getAUD()) + " | "
					+ remainTransferLimit.getAUD());
			System.out.println("   USD   | " + String.format("%-11.2f", transferLimit.getUSD()) + " | "
					+ remainTransferLimit.getUSD());
			System.out.println("   GBP   | " + String.format("%-11.2f", transferLimit.getGBP()) + " | "
					+ remainTransferLimit.getGBP());
		}
		System.out.println("----------------------------------------");
		System.out.println("------------Withdraw Limits-------------");
		System.out.println("Currency | Daily Limit | Remaining Limit");
		Balance withdrawLimit = Limits.getWithdrawLimitByID(limitAcc.getAccountId());
		Balance remainWithdrawLimit = limitAcc.getRemainWithdrawAmt(limitAcc);
		System.out.println("   SGD   | " + String.format("%-11.2f", withdrawLimit.getSGD()) + " | "
				+ remainWithdrawLimit.getSGD());
		if (limitAcc.getType().equals("Fx")) {
			System.out.println("   MYR   | " + String.format("%-11.2f", withdrawLimit.getMYR()) + " | "
					+ remainWithdrawLimit.getMYR());
			System.out.println("   AUD   | " + String.format("%-11.2f", withdrawLimit.getAUD()) + " | "
					+ remainWithdrawLimit.getAUD());
			System.out.println("   USD   | " + String.format("%-11.2f", withdrawLimit.getUSD()) + " | "
					+ remainWithdrawLimit.getUSD());
			System.out.println("   GBP   | " + String.format("%-11.2f", withdrawLimit.getGBP()) + " | "
					+ remainWithdrawLimit.getGBP());
		}
		returnToMenu();
	}
}

/**
 * This class represents the GUI for credit card operations.
 * It extends the base GUI class.
 */
class CreditCardGUI extends GUI {

	/**
	 * Displays the credit card menu options.
	 */
	protected void creditCardMenu() {
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Credit Card Information");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("1. Apply for Credit Card");
		System.out.println("2. View Credit Card Details");
	}

	/**
	 * Displays the menu options for applying for a credit card.
	 */
	protected void applyForCreditCardMenu() {
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Which type of credit card would you like to apply for?");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("1. Standard Visa (Minimum Salary: $30000, Minimum Age: 21)");
		System.out.println("2. Standard Mastercard (Minimum Salary: $30000, Minimum Age: 21)");
		System.out.println("3. Platnium Sunshine (Minimum Salary: $30000, Minimum Age: 21)");
		System.out.println("0. Exit Credit Card Menu");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Please enter your choice: ");
	}

	/**
	 * Prompts the user to choose a credit card type and handles the input.
	 * 
	 * @param account The account object associated with the credit card.
	 * @throws Exception If an error occurs during credit card processing.
	 */
	protected void creditCardTypeChoice(Account account) throws Exception {
		// Display the credit card menu
		applyForCreditCardMenu();
		// Get the user's ID
		String accountId = account.getAccountId();
		// Get the user's choice
		int choice = getChoice();
		// Create a SunshineCreditCard object
		SunshineCreditCard existingSunshineCreditCard = new SunshineCreditCard(accountId);

		// Default values for salary and age
		double Salary = 300000;
		int Age = 21;
		// Prompt to get the customer's salary and age
		System.out.println("Please enter your annual salary: ");
		try {
			Salary = scanner.nextDouble();
		} catch (Exception e) {
			System.out.println("Invalid input. Please enter a valid number.");
		}

		try {
			System.out.println("Please enter your age: ");
			Age = scanner.nextInt();
		} catch (Exception e) {
			System.out.println("Invalid input. Please enter a valid number.");
		}

		switch (choice) {
			case 1:
				// Standard Visa Card
				try {
					existingSunshineCreditCard.addCreditCard("1", Salary, Age);
				} catch (Exception e) {
					System.out.println(e.getMessage());

				}
				break;
			case 2:
				// Standard MasterCard
				try {
					existingSunshineCreditCard.addCreditCard("2", Salary, Age);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			case 3:
				// Sunshine Platnium
				try {
					existingSunshineCreditCard.addCreditCard("3", Salary, Age);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				break;
			default:
				System.out.println("Invalid choice. Please try again.");
				// return creditCardTypeChoice(account);
				break;
		}

	}

	/**
	 * Displays the details of all credit cards associated with the account.
	 * 
	 * @param account The account object associated with the credit cards.
	 * @throws IOException If an I/O error occurs.
	 * @throws Exception   If an error occurs during credit card processing.
	 */
	protected void viewCreditCardDetails(Account account) throws IOException, Exception {
		// Get the account ID
		String accountId = account.getAccountId();
		// Create a SunshineCreditCard object
		SunshineCreditCard existingSunshineCreditCard = new SunshineCreditCard(accountId);
		// Get all the credit card information
		HashMap<String, String[]> allCustCCInfo = existingSunshineCreditCard.getAllCustCCAccInfo();
		// Iterate over the entry set of the map to print each credit card information
		// array in a readable format
		for (Map.Entry<String, String[]> entry : allCustCCInfo.entrySet()) {
			String key = entry.getKey();
			String[] details = entry.getValue();
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("Credit Card: " + key + " Information.");
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("Card Name: " + details[1]);
			System.out.println("CVV: " + details[2]);
			System.out.println("Expiry Date: " + details[3]);
			System.out.println("Credit Limit: " + details[4]);
			System.out.println("Remaining Credit: " + details[5]);
			System.out.println("---------------------------------------------------------------------------------");

		}
	}

	/**
	 * Prints the credit card key and card name for all credit cards associated with
	 * the account.
	 * 
	 * @param accountId The ID of the account.
	 * @throws IOException If an I/O error occurs.
	 * @throws Exception   If an error occurs during credit card processing.
	 */
	public static void printCreditCardKeyAndName(String accountId) throws IOException, Exception {
		// Create a SunshineCreditCard object
		SunshineCreditCard existingSunshineCreditCard = new SunshineCreditCard(accountId);
		// Get all the credit card information
		HashMap<String, String[]> allCustCCInfo = existingSunshineCreditCard.getAllCustCCAccInfo();

		int count = 1;
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("|    | Credit Card      |      Card Name     ");
		for (Map.Entry<String, String[]> entry : allCustCCInfo.entrySet()) {
			String key = entry.getKey();
			String[] details = entry.getValue();

			System.out.println("| " + count + "  | " + key + " | " + details[1]);
			count = count + 1;
		}
	}
}

/**
 * This class represents the GUI for Loan operations.
 * It extends the base GUI class.
 */
class LoanGUI extends GUI {
	private static String[] loanTypeList = { "Personal", "Car", "Study", "Home" };
	private static String[] loanStatusList = { "Ongoing", "Completed" };

	/**
     * Displays the menu options for applying for various types of loans.
     * Allows the user to apply for Personal, Car, Study, or Home Loans.
     */
	public void applyForLoanMenu() {
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Which type of loan would you like to apply for?");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("1. Personal Loan");
		System.out.println("2. Car Loan");
		System.out.println("3. Study Loan");
		System.out.println("4. Home Loan");
		System.out.println("0. Exit Loan Menu");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Please enter your choice: ");
	}

    /**
     * Checks if the account has an ongoing loan of a specific type.
     *
     * @param accountID The ID of the account to check for ongoing loans.
     * @param loan      The loan object to check against.
     * @param loanType  The type of loan to check for.
     * @return True if there is an ongoing loan of the specified type, false otherwise.
     */
	protected Boolean showOngoingLoan(String accountID, SunshineLoan loan, String loanType) {
		ArrayList<SunshineLoan> accountLoans = SunshineLoan.getLoanByAccount(accountID);
		if (accountLoans != null) {
			for (SunshineLoan loanAcc : accountLoans) {
				if (loanAcc.getStatus().equals("Ongoing") && loanAcc.getType().equals(loanType)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
     * Handles the user's choice of loan application type.
     * It checks if the user has an ongoing loan of the same type before allowing them to proceed.
     *
     * @param account The account object associated with the user.
     * @param loan    The loan object to apply for.
     */
	protected void applyForLoanTypeChoice(Account account, SunshineLoan loan) {

		applyForLoanMenu();
		String accountId = account.getAccountId();
		int choice = getChoice();
		if (choice == 0) {
			return;
		} else if (choice < 0 || choice > 4) {
			System.out.println("Invalid choice. Please try again.");
			applyForLoanTypeChoice(account, loan);
			return;
		}

		Boolean isOngoing = showOngoingLoan(accountId, loan, loanTypeList[choice - 1]);
		if (!isOngoing) {
			applyForLoan(accountId, loanTypeList[choice - 1]);
		} else {
			System.out.println("You already have an ongoing " + loanTypeList[choice - 1] + " Loan.");
			System.out.println("You are unable to apply for other loans while you have an ongoing loan.");
			returnToMenu();
		}
	}

	/**
     * Displays the loan details based on the loan status (Ongoing/Completed).
     * The user can choose to view all ongoing loans or all past loans.
     *
     * @param account The account object associated with the user.
     * @param loan    The loan object to view details for.
     */
	protected void viewLoanDetailsChoice(Account account, SunshineLoan loan) {
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("You have chosen to view Loan Details");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("1. View All Ongoing Loans");
		System.out.println("2. View All Past Loans");
		System.out.println("0. Exit View Loan Details Menu");
		System.out.println("---------------------------------------------------------------------------------");
		System.out.println("Please enter your choice: ");

		String accountId = account.getAccountId();
		int choice = getChoice();
		if (choice == 0) {
			return;
		} else if (choice < 0 || choice > 2) {
			System.out.println("Invalid choice. Please try again.");
			applyForLoanTypeChoice(account, loan);
			return;
		}

		viewLoanDetails(accountId, loanStatusList[choice - 1]);
	}

    /**
     * Displays the details of loans based on their status for a given account.
     *
     * @param accountId  The ID of the account for which loan details are requested.
     * @param loanStatus The status of loans to display (Ongoing/Completed).
     */
	protected void viewLoanDetails(String accountId, String loanStatus) {
		// Gets all the loans associated with the account and loan type into an arraylist
		ArrayList<SunshineLoan> loansByStatus = new ArrayList<SunshineLoan>();
		if (!SunshineLoan.getLoanByAccount(accountId).isEmpty()) {
			for (SunshineLoan loan : SunshineLoan.getLoanByAccount(accountId)) {
				if (loan.getStatus().equals(loanStatus)) {
					loansByStatus.add(loan);
				}
			}
		} else {
			System.out.println("No loans found for this account.");
		}

		// Check if the arraylist is empty or not
		if (!loansByStatus.isEmpty()) {
			// Display the loan details
			int index = 1;
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("Loan Details for " + loanStatus + " Loan");
			System.out.println(
					"|    |              Loan ID                 | Type     | Status   | Loan Amount | Years of Loan | Interest Rate | Monthly Payment | Total Payment | Remaining Amount");
			for (SunshineLoan loan : loansByStatus) {
				System.out.println(
						"| " + index + ". | " + loan.getLoanID() + " | " + String.format("%-8s", loan.getType()) + " | "
								+ String.format("%-8s", loan.getStatus()) + " | " +
								String.format("$%-10.2f", loan.getLoanAmount()) + " | "
								+ String.format("%-13d", loan.getYearsOfLoan()) + " | " +
								String.format("%-12.2f%%", loan.getInterestRate()) + " | "
								+ String.format("$%-14.2f", loan.getMonthlyPayment()) + " | $"
								+ String.format("%-12.2f", loan.getTotalPayment()) + " | $"
								+ String.format("%.2f", loan.getRemainingAmount()));
			}
		} else {
			// If the loan doesn't exist, display an error message
			System.out.println(loanStatus + " loans not found for account ID: " + accountId);
		}
		returnToMenu();
	}

    /**
     * Handles the process for a user to apply for a loan.
     * Prompts the user for the loan amount and term, and calculates the monthly payment and total payment.
     *
     * @param accountID The ID of the account applying for the loan.
     * @param loanType  The type of loan to apply for (Personal/Car/Study/Home).
     */
	protected void applyForLoan(String accountID, String loanType) {

		BigDecimal totalPayment;
		BigDecimal monthlyPayment;
		BigDecimal interestRate;

		System.out.println("Applying for a " + loanType + " Loan");
		System.out.println("Please enter the loan amount you would like to request:");
		BigDecimal loanAmount = scanner.nextBigDecimal();
		// Check if the loan amount is more than $500
		if (loanAmount.compareTo(new BigDecimal("500")) < 0) {
			System.out.println("Loan amount must be at least $500. Please try again.");
			returnToMenu();
			return;
		}

		// Allow user to choose the number of years to repay the loan
		// Loan amounts less than $4000 can be repaid over 1 - 2 years while loan amounts greater than or equal to $4000 can be repaid over 1 - 5 years
		System.out.println("Loan amounts less than $4000 can be repaid over: 1 - 2 years");
		System.out.println("Loan amounts greater than or equal to $4000 can be repaid over: 1 - 5 years");
		System.out.println("Please enter the number of years you would like to take the loan out for:");
		int yearsOfLoan = scanner.nextInt();

		if (loanAmount.compareTo(new BigDecimal("4000")) < 0 && (yearsOfLoan < 1 || yearsOfLoan > 2)) {
			System.out.println("Invalid number of years. Please try again.");
			returnToMenu();
			return;
		} else if (loanAmount.compareTo(new BigDecimal("4000")) >= 0 && (yearsOfLoan < 1 || yearsOfLoan > 5)) {
			System.out.println("Invalid number of years. Please try again.");
			returnToMenu();
			return;
		}

		// Initialise the loan object based on the loan type
		switch (loanType) {
			case "Personal":
				SunshinePersonalLoan personalLoan = new SunshinePersonalLoan(loanAmount, yearsOfLoan);
				totalPayment = personalLoan.calculateInterest(loanAmount, yearsOfLoan).add(loanAmount);
				interestRate = BigDecimal.valueOf(personalLoan.getInterestRate());
				break;
			case "Car":
				SunshineCarLoan carLoan = new SunshineCarLoan(loanAmount, yearsOfLoan);
				totalPayment = carLoan.calculateInterest(loanAmount, yearsOfLoan).add(loanAmount);
				interestRate = BigDecimal.valueOf(carLoan.getInterestRate());
				break;
			case "Study":
				SunshineStudyLoan studyLoan = new SunshineStudyLoan(loanAmount, yearsOfLoan);
				totalPayment = studyLoan.calculateInterest(loanAmount, yearsOfLoan).add(loanAmount);
				interestRate = BigDecimal.valueOf(studyLoan.getInterestRate());
				break;
			case "Home":
				SunshineHomeLoan homeLoan = new SunshineHomeLoan(loanAmount, yearsOfLoan);
				totalPayment = homeLoan.calculateInterest(loanAmount, yearsOfLoan).add(loanAmount);
				interestRate = BigDecimal.valueOf(homeLoan.getInterestRate());
				break;
			default:
				System.out.println("Invalid loan type.");
				returnToMenu();
				return;
		}

		monthlyPayment = totalPayment.divide(BigDecimal.valueOf(yearsOfLoan).multiply(BigDecimal.valueOf(12)),
				RoundingMode.HALF_UP);
		String status = "Ongoing"; // Default status for a new loan
		String loanID = UUID.randomUUID().toString();
		BigDecimal remainingAmount = totalPayment;
		// Create the loan object using the calculated values
		SunshineLoan newLoan = new SunshineLoan(loanID, accountID, loanType, status, loanAmount, yearsOfLoan,
				interestRate, monthlyPayment, totalPayment, remainingAmount);
		// Append the new loan to the CSV file
		try {
			SunshineLoan.appendToCSV(SunshineLoan.getPath(), newLoan);
			System.out.println("Your " + loanType + " Loan application has been submitted successfully.");
		} catch (IOException e) {
			System.err.println("An error occurred while saving your loan application: " + e.getMessage());
		}
	}

    /**
     * Handles the process for making a loan payment.
     * Allows the user to select a loan and make a payment either as a monthly payment or a custom amount.
     *
     * @param account The account object associated with the user.
     * @throws IOException If there is an I/O error during the process.
     */
	protected void makeLoanPayment(Account account) throws IOException {
		// Get all the ongoing loans associated with the account
		ArrayList<SunshineLoan> accountLoans = SunshineLoan.getLoanByAccount(account.getAccountId());

		BigDecimal paymentAmount;
		// Check if the account has any ongoing loans, if it does, display the loan details
		if (!accountLoans.isEmpty()) {
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println(
					"|    |              Loan ID                 | Type     | Loan Amount | Years of Loan | Interest Rate | Monthly Payment | Total Payment | Remaining Amount");
			int index = 1;
			for (SunshineLoan loanAcc : accountLoans) {
				if (loanAcc.getStatus().equals("Ongoing")) {
					System.out.println("| " + index + ". | " + loanAcc.getLoanID() + " | "
							+ String.format("%-8s", loanAcc.getType()) + " | "
							+ String.format("$%-10.2f", loanAcc.getLoanAmount()) + " | "
							+ String.format("%-13d", loanAcc.getYearsOfLoan()) + " | "
							+ String.format("%-12.2f%%", loanAcc.getInterestRate()) + " | "
							+ String.format("$%-14.2f", loanAcc.getMonthlyPayment())
							+ " | $" + String.format("%-12.2f", loanAcc.getTotalPayment()) + " | $"
							+ String.format("%.2f", loanAcc.getRemainingAmount()));
					index++;
				}
			}

			// Prompt the user to choose a loan to make a payment for
			System.out.println("Please choose the loan you would like to make a payment for:");
			int choice = getChoice();
			SunshineLoan loan = accountLoans.get(choice - 1);
			// Prompt the user to choose the type of payment, whether monthly or custom amount
			System.out.println("Would you like to pay the monthly payment or a custom amount?");
			System.out.println("1. Monthly Payment");
			System.out.println("2. Custom Amount");
			int paymentChoice = getChoice();
			if (paymentChoice == 1) {
				paymentAmount = loan.getMonthlyPayment();
			} else if (paymentChoice == 2) {
				System.out.println("Please enter the payment amount:");
				paymentAmount = new BigDecimal(scanner.nextDouble());
				BigDecimal totalPayment = loan.getTotalPayment();
				if (paymentAmount.compareTo(totalPayment) > 0) {
					// Check if the payment amount exceeds the total loan amount
					System.out.println("Payment amount cannot exceed the total loan amount.");
					makeLoanPayment(account);
					return;
				}
			} else {
				System.out.println("Invalid choice. Please try again.");
				makeLoanPayment(account);
				return;
			}

			// Allow user to choose the savings account to deduct the payment from
			// Reuses the printSavingsID method from InsuranceGUI to display the savings accounts
			ArrayList<String> customerSavings = InsuranceGUI.printSavingsID(account);
			if (customerSavings.isEmpty()) {
				System.out.println("Please make a savings account first.");
				returnToMenu();
			} else {
				System.out.println("---------------------------------------------------------------------------------");
				System.out.println("Please enter the index of the savings account you would like to deduct the payment from:");
				choice = getChoice();
				String savingsID = customerSavings.get(choice - 1);
				Savings savings = Savings.getSavingsByAccountID(savingsID);
				System.out.println("---------------------------------------------------------------------------------");
				boolean paid = loan.repayLoan(paymentAmount, savings);
				if (paid) {
					System.out.println(
							"Remaining balance for " + savings.getAccountID() + ": $" + savings.getBalance().getSGD());
					returnToMenu();
				} else {
					System.out.println("Payment unsuccessful. Please try again.");
					makeLoanPayment(account);
				}
			}
		}
		else {
			System.out.println("No ongoing loans found for this account.");
			returnToMenu();
		}
	}
	/**
	 * Displays loan repayment transactions for a specific account.
	 * Retrieves transactions from savings accounts linked to the customer of the specified account 
	 * that are marked as 'Loan Repayment' and displays them in reverse chronological order.
	 *
	 * @param account The account whose loan transactions are to be displayed.
	 */
	protected void loanTransactions(Account account) {
		ArrayList<Transactions> loanAccTransactions = new ArrayList<Transactions>();

		Customer customer = Customer.getCustomerByID(account.getCustomerID());
		for (Account savingsAcc : Account.getAccountsByType(customer, "Savings")) {
			ArrayList<Transactions> savingsAccTransactions = Transactions.getTransactionsByAccountID(savingsAcc.getAccountId());
			if (!savingsAccTransactions.isEmpty()) {
				for (Transactions transaction : savingsAccTransactions) {
					if (transaction.getType().equals("Loan Repayment")) {
						loanAccTransactions.add(transaction);
					}
				}
			}
		}

		if (loanAccTransactions.isEmpty()) {
			System.out.println("No loan transactions found for this account.");
		} else {
			Collections.sort(loanAccTransactions, Comparator.comparing(Transactions::getTimestamp).reversed());
			System.out.println("---------------------------------------------------------------------------------");
			System.out.println("Account ID: " + account.getAccountId());
			System.out.println("Transaction History for Loan Repayments");
			System.out.println("---------------------------------------------------------------------------------");
			int index = 1;
			System.out.println("||      || Timestamp               || Transaction ID                       || Savings Account ID                   || Amount (SGD)");
			for (Transactions transaction : loanAccTransactions) {
				System.out.println("|| " + String.format("%3s.", index) + " || " + String.format("%-23s", transaction.getTimestamp()) +
						" || " + transaction.getTransactionID() +  " || "  + transaction.getAccountID() + " || " + transaction.getAmount().getSGD());
				index++;
			}
			System.out.println("---------------------------------------------------------------------------------");
		}
		returnToMenu();
	}
}
