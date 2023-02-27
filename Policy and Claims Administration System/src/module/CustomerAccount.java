package module;

import controller.AppDatabase;
import controller.UserInputAndValidation;

/**
* Java Course 4 Module 3
*
* @author Justine Kaye Mojica
* @Description: A Java class that process the customer account creation and customer account look up and display.
* Created Date: 6/27/2022
* Modified Date: 6/28/2022
* @Modified By: Justine Kaye Mojica
*/

public class CustomerAccount extends PolicyHolder{

	//Working Variables
	protected Integer accountNumber;
	protected String acctName;
	protected Boolean isCusAccExists;
	protected Boolean isCustomerAccountPage = true;
	protected AppDatabase cusAccDb;
	private UserInputAndValidation cusAccInputVal;
	
	
	/*
	 * This class constructor initialize the AppDatabase cusAccDb object
	 */
	public CustomerAccount(AppDatabase cusAccDb) {
		super(cusAccDb);
		this.cusAccDb = cusAccDb;
		cusAccInputVal = cusAccDb.dbInputAndVal;
	}

	/*
	 * This method will display the Customer Account Header 
	 */
	public CustomerAccount displayHeader(){
		System.out.println();
		System.out.println("--------------------------------------- CUSTOMER ACCOUNT ----------------------------------------");
		return this;
	}
	
	/*
	 * This method will prompt user for creation of new customer account
	 */
	public CustomerAccount createAnAccount() {		
		exitProcess = false;
		do {
			System.out.println("\t\t\t      >>>Customer Account Creation<<<");
			System.out.println("Please enter details below.");
			firstName = (String)cusAccInputVal.inputAndValidate("First Name: ","String","First Name");
			lastName = (String)cusAccInputVal.inputAndValidate("Last Name: ","String","Last Name");
			address = (String)cusAccInputVal.inputAndValidate("Address: ","String","Address");
			accountPolicyHolderEnrollment(firstName,lastName,address);
			action = (Integer) cusAccInputVal.inputAndValidate("\n(1) Submit \t(2) "
					+ "Reenter Details \t(3) Back to Menu\n"
					+ "Enter your Action (1,2,3): ","Integer","input action.");

			if(action.equals(1)) {
				isCusAccExists = cusAccDb.isCusAccNameExists(firstName, lastName);
				if(isCusAccExists) {
					if(isCustomerAccountPage) {
						System.out.println("Account already exists for " + firstName + " " + lastName);					
						action = (Integer) cusAccInputVal.inputAndValidate("\n(2) Reenter Details \t(3) Back to Menu\n"
								+ "Enter your Action (2,3): ","Integer","input action.");
						if (action.equals(2)) exitProcess = false;
						else if (action.equals(3)) exitProcess = true;
					} else {
						exitProcess = false;
					}					
				} else {
					//Method called from AppDatabase class to store the account in database
					cusAccDb.addNewCustomerAccount(firstName, lastName, address);
					//Method called from AppDatabase class to get the generated keys which is the account number
					accountNumber = cusAccDb.getGeneratedKeys();
					//Method called from Policy Holder class to store the policy holder details in array and then save in database
					cusAccDb.addPolicyHolder(firstName, lastName, address, birthDt, drivLicNum ,drvLicFirstIssdDt, accountNumber);
					System.out.println("\nApplication successfully submitted ... \n");
					dispAcctTrnsctRcpt();
					if(isCustomerAccountPage) {
						action = (Integer) cusAccInputVal.inputAndValidate("\n(1) Add new customer account again \t(2) Back to Menu\n"
								+ "Enter your Action (1,2): ","Integer","input action.");
						if (action.equals(1)) exitProcess = false;
						else if (action.equals(2)) exitProcess = true;
					} else {
						return this;
					}
				}
			} 
			else if (action.equals(2)) exitProcess = false;
			else if (action.equals(3)) exitProcess = true;
		} while(exitProcess == false);
		System.out.println("\nReturning to Menu ... ");
		return this;
	}
	
	/*
	 * This method will display the transaction receipt of created new customer account
	 */
	public void dispAcctTrnsctRcpt() {
		System.out.println("\n*************************************************************************************************");
		System.out.println("\t\t\t\tCreated Customer Account Summary");
		System.out.println("Account Number: " + cusAccInputVal.numberFormatter(4, accountNumber));
		System.out.println("Account Name: " + firstName + " " + lastName);
		System.out.println("Address: " + address);
		if(cusAccDb.isCusAccPolHldrExists(accountNumber)) {
			cusAccDb.cusAccInfoPolHldrDisplay();
		}		
		System.out.println("\n*************************************************************************************************");
	}
	
	/*
	 * This method will search and display an account information if exists in database
	 */
	public CustomerAccount acctLookUpAndDisplay(){		
		do {
			System.out.println("\t\t\t\t      >>>Account Search<<<");
			System.out.println("Please enter account's name.");
			firstName = (String)cusAccInputVal.inputAndValidate("First Name: ","String","First Name");
			lastName = (String)cusAccInputVal.inputAndValidate("Last Name: ","String","Last Name");
			isCusAccExists = cusAccDb.isCusAccNameExists(firstName, lastName);
			if(isCusAccExists) {
				System.out.println("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("\t\t\t\t      Account Information");
				cusAccDb.cusAccInfoDisplay();
				if(cusAccDb.isCusAccPolHldrExists(firstName, lastName)) cusAccDb.cusAccInfoPolHldrDisplay();
				if(cusAccDb.isCusAccPolExists(firstName, lastName)) cusAccDb.cusAccInfoPolDisplay();
				System.out.println("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
			} else {
				System.out.println("Customer account does not exists...");
			}
			action = (Integer) cusAccInputVal.inputAndValidate("\n(1) Search again \t(2) Back to Menu\n"
					+ "Enter your Action (1,2): ","Integer","input action.");
			if(action.equals(1)) exitProcess= false;
			else if (action.equals(2)) exitProcess = true ;
		} while(exitProcess == false);	
		System.out.println("\nReturning to Menu ... ");
		return this;
	}
	
	/*
	 * This method retrieves account name given the account number.
	 */
	public CustomerAccount getAcctName(Integer accountNumber) {
		isCusAccExists = cusAccDb.isCusAccNumExists(accountNumber);
		if(isCusAccExists){
			String[] acctName = cusAccDb.getAcctName();
			firstName = acctName[0];
			lastName = acctName[1];
			this.acctName = acctName[0] + " " + acctName[1];			
		}
		return this;
	}
	
}
