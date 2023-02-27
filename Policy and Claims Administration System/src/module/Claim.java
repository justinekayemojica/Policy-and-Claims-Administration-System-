package module;

import java.math.BigDecimal;
import java.time.LocalDate;

import controller.AppDatabase;
import controller.UserInputAndValidation;

/**
* Java Course 4 Module 3
*
* @author Justine Kaye Mojica
* @Description: A Java class that process the filling and look up and display of Claims
* Created Date: 6/27/2022
*
*/

public class Claim extends Policy{

	//Working Variables
	private String claimNum;
	private LocalDate accidentDate;
	private String accidentAdd;
	private String accidentDesc;
	private String vehicleDmgDesc;
	private BigDecimal estimatedRepairCost;
	private Boolean isClaimExists;
	private AppDatabase claimDb;
	private UserInputAndValidation claimInputVal;

	/*
	 * This class constructor initialize the AppDatabase claimDb object
	 */
	public Claim(AppDatabase claimDb) {
		super(claimDb);
		claimInputVal = claimDb.dbInputAndVal;
		this.claimDb = claimDb;
	}

	/*
	 * This method will display the Claim Header 
	 */
	public Claim displayHeader() {
		System.out.println();
		System.out.println("-------------------------------------------- CLAIM ----------------------------------------------");		
		return this;
	}

	/*
	 * This method process the filing of Claim
	 */
	public Claim fileClaim(){
		do {
			System.out.println("\t\t\t\t     File an Accident Claim");
			policyNumber = (Integer) claimInputVal.inputAndValidate("Please enter policy number : ", "String",
					"Policy Number");
			isPolicyExists = claimDb.isPolExists(policyNumber);
			if(isPolicyExists) {
				polStatFlg = claimDb.getPolStatFlg();
				statusDesc = statusDesc(polStatFlg);
				if(polStatFlg.equals(20) || polStatFlg.equals(30)) {
					System.out.println("\nPolicy Number Validated ... Redirecting to Accident Claim Form ...\n");
					addClaim();
					if(action.equals(1) || action.equals(3)) exitProcess = false;
					else if (action.equals(2)) exitProcess = true;
				} else {
					System.out.println("\nPolicy is " + statusDesc + ". Filing of Claim is prohibited.");
					action = (Integer) claimInputVal.inputAndValidate("\n(1) File a Claim for another policy  (2) Back to Menu\n" + 
							"Enter your Action (1,2): ", "Integer", "input action.");
					if(action.equals(1)) exitProcess = false;
					else if (action.equals(2)) exitProcess = true;
				}				
			} else {
				System.out.println("\nPolicy Number does not exists ...\n");
				action = (Integer) claimInputVal.inputAndValidate("\n(1) Reenter again \t(2) Back to Menu\n" + 
						"Enter your Action (1,2): ", "Integer", "input action.");
				if(action.equals(1)) exitProcess = false;
				else if (action.equals(2)) exitProcess = true;
			}
		} while(exitProcess == false);
		System.out.println("\nReturning to Menu ... ");
		return this;
	}


	/*
	 * This method will prompt user for creation of new Claim
	 */
	public Claim addClaim() {
		do {
			System.out.println("\t\t\t\t\tAccident Claim Form");
			System.out.println("Please enter details below.");
			accidentDate = (LocalDate) claimInputVal.inputAndValidate("Accident Date (MM-DD-YYYY): ", "Date", "Accident Date"); 
			accidentAdd = (String) claimInputVal.inputAndValidate("Accident Address: ", "String", "Accident Address");
			accidentDesc = (String) claimInputVal.inputAndValidate("Accident Description: ", "String",
					"Accident Description");
			vehicleDmgDesc = (String) claimInputVal.inputAndValidate("Vehicle Damage Description: ", "String",
					"Vehicle Damage Description");
			estimatedRepairCost = (BigDecimal) claimInputVal.inputAndValidate("Estimated Repair Cost: ", "BigDecimal",
					"Estimated Repair Cost");

			action = (Integer) claimInputVal.inputAndValidate(
					"\n(1) Submit (2) Reenter details again \t(3) Back\n" + "Enter your Action (1,2,3): ", "Integer", "input action.");

			if(action.equals(1)) {
				if(claimInputVal.isValidAccidentDate(accidentDate, getPolEffectiveDate(), getPolExpirationDate())) {
					accountNumber = claimDb.getPolAcctNum();
					claimDb.addClaim(accidentDate, accidentAdd, accidentDesc, vehicleDmgDesc, estimatedRepairCost, accountNumber, policyNumber);
					claimNum = "C" + claimInputVal.numberFormatter(5,claimDb.getGeneratedKeys());
					claimDb.addClaimNum(Integer.parseInt(claimNum.substring(1)));
					System.out.println("\nClaim successfully submitted...");
					dispClaimTrnsctRcpt();
					action = (Integer) claimInputVal.inputAndValidate(
							"\n(1) File claim again \t(2) Back to Menu\n" + "Enter your Action (1,2): ", "Integer", "input action.");
					if (action.equals(1)) exitProcess = true;
					else if (action.equals(2)) return this;					
				} else {
					System.out.println("\nClaim accident date is not covered by the policy.");
					action = (Integer) claimInputVal.inputAndValidate(
							"\n(2) Reenter Details \t(3) Back\n" + "Enter your Action (2,3): ", "Integer", "input action.");
					if (action.equals(2)) exitProcess = false;
					else if (action.equals(3)) exitProcess = true;	
				}				
			} else if(action.equals(2)) {
				exitProcess = false;
			} else if(action.equals(3)) {
				exitProcess = true;
			}
		} while(exitProcess == false);
		System.out.println("\nReturning to File an Accident Claim ... ");
		return this;
	}

	/*
	 * This method will display the transaction receipt of created claim
	 */
	public Claim dispClaimTrnsctRcpt() {
		System.out.println("\n*************************************************************************************************");
		System.out.println("\t\t\t\t\tFiled Claim Summary");
		System.out.printf("%-55s %-30s\n", "Claim Number: " + claimNum ,"Account Number: " 
				+ claimInputVal.numberFormatter(4, accountNumber));
		System.out.printf("%-55s %-30s\n", "Policy Number: " + claimInputVal.numberFormatter(6, policyNumber),
				"Account Name: " + getAcctName(accountNumber).acctName);
		System.out.println("Accident Date: " + accidentDate);
		System.out.println("Accident Address: " + accidentAdd);
		System.out.println("Accident Description: " + accidentDesc);
		System.out.println("Vehicle Damage Description: " + vehicleDmgDesc);
		System.out.println("Estimated Report: $" + estimatedRepairCost);
		System.out.println("\n*************************************************************************************************");
		return this;
	}

	/*
	 * This method wil search and display a claim information if exists in database
	 */
	public Claim claimLookUpAndDisplay() {
		do {
			System.out.println("\t\t\t\t\t  Claim Search ");
			claimNum = (String) claimInputVal.inputAndValidate("Please enter claim number : ", "String",
					"Claim Number");
			System.out.println();
			isClaimExists = claimDb.isClaimExists(claimNum);
			if(isClaimExists) {
				System.out.println("\t\t\t\t        Claim Information");
				claimDb.claimInfoDisplay();
				action = (Integer) claimInputVal.inputAndValidate("\n(1) Search again \t(2) Back to Menu\n" + 
						"Enter your Action (1,2): ", "Integer", "input action.");
				if(action.equals(1)) exitProcess = false;
				else if (action.equals(2)) exitProcess = true;
			} else {
				System.out.println("Claim does not exists ...");
				action = (Integer) claimInputVal.inputAndValidate("\n(1) Search again \t(2) Back to Menu\n" + 
						"Enter your Action (1,2): ", "Integer", "input action.");
				if(action.equals(1)) exitProcess = false;
				else if (action.equals(2)) exitProcess = true;
			}
		} while (exitProcess == false);
		System.out.println("\nReturning to Menu ... ");
		return this;
	}
}
