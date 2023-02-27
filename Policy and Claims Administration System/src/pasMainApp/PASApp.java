package pasMainApp;

import View.Menu;
import controller.AppDatabase;
import module.Claim;
import module.CustomerAccount;
import module.Policy;

/**
* Java Course 4 Module 3
*
* @author Justine Kaye Mojica
* @Description: Java program created to manage customer automobile insurance policies 
* 		and as well as accident claims for an insurance company. 
* 		This java program used an external library mysql-connector-java-8.0.29.jar
* Created Date: 6/27/2022
* Modified Date: 6/28/2022
* @Modified By: Justine Kaye Mojica
*
*/

public class PASApp{

	public static void main(String[] args) {

		//Working Variables
		Integer selectedMenuOpt;
		Boolean exitProcess = false;

		//Initialize menu.. 
		Menu menu = new Menu();
		
		//Initialize PASDatabase object...
		AppDatabase pasDatabase = new AppDatabase();
		
		//Display PAS app header
		displayAppHeader();
		 
		//Application Setup
		pasDatabase.appSetup();


		do {

			menu.displayHeader().displayMenu();
			selectedMenuOpt = menu.selectOption();

			switch(selectedMenuOpt) {

			case 1:
				CustomerAccount newCusAcc = new CustomerAccount(pasDatabase);
				newCusAcc.displayHeader().createAnAccount();
				System.out.println();
				break;

			case 2:
				Policy policy = new Policy(pasDatabase);
				policy.displayHeader().policyMainPage();
				break;

			case 3: 
				Policy cancelPolicy = new Policy(pasDatabase);
				cancelPolicy.displayHeader().cancelPolicy();
				break;

			case 4: 
				Claim addClaim = new Claim(pasDatabase);
				addClaim.displayHeader().fileClaim();
				break;

			case 5:
				CustomerAccount searchCusAcc = new CustomerAccount(pasDatabase);
				searchCusAcc.displayHeader().acctLookUpAndDisplay();
				break;

			case 6:
				Policy searchPolicy = new Policy(pasDatabase);
				searchPolicy.displayHeader().polLookUpAndDisplay();
				break;

			case 7:
				Claim searchClaim = new Claim(pasDatabase);
				searchClaim.displayHeader().claimLookUpAndDisplay();
				break;

			case 8:
				System.out.println("Exiting appication ...");
				pasDatabase.dbInputAndVal.input.close();
				pasDatabase.closeConnection();
				exitProcess = true;
				break;			

			}
		} while (exitProcess == false);

	}
	
	/*
	 * Method for displaying Application Header
	 */
	public static void displayAppHeader(){
		System.out.println();
		System.out.println("=================================================================================================");
		System.out.println("\t\t Automobile Insurance Policy and Claims Administration System (PAS)");
		System.out.println("=================================================================================================");
//		System.out.println("\t\t\t\t\tcreated by: JKMojica");
		System.out.println();
	}

}
