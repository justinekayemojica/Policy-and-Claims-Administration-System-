package module;

import java.time.LocalDate;

import controller.AppDatabase;
import controller.UserInputAndValidation;

/**
* Java Course 4 Module 3
*
* @author Justine Kaye Mojica
* @Description: A Java class that process the enrollment and display of policy holder. 
* Created Date: 6/27/2022
* Modified Date: 6/29/2022
* @Modified By: Justine Kaye Mojica
*/

public class PolicyHolder{

	//Working Variables
	protected String firstName;
	protected String lastName;
	protected String address;
	private String polHoldr;
	protected LocalDate birthDt;
	protected String drivLicNum;
	protected LocalDate drvLicFirstIssdDt;
	protected Integer action;	
	protected Boolean exitProcess;
	private AppDatabase polHldrDb;
	private Object policyHolder[] = new Object[7];
	private UserInputAndValidation polHolderInputVal;
	
	/*
	 * This class constructor initialize the AppDatabase polHldrDb object
	 */
	public PolicyHolder(AppDatabase polHldrDb) {
		this.polHldrDb = polHldrDb;
		polHolderInputVal = polHldrDb.dbInputAndVal;		
	}

	/*
	 * This method will display the Policy Holder Header 
	 */
	public PolicyHolder displayHeader(){
		System.out.println();
		System.out.println("---------------------------------------- POLICY HOLDER ------------------------------------------");
		return this;
	}

	/*
	 * This method process the enrollment of policy holder
	 */
	public PolicyHolder policyHolderEnrollment() {	
		polHldrEnrl:
			do {
				System.out.println("\t\t\t\t   >>Policy Holder Enrollment<<");
				System.out.println("Please enter details below.");
				firstName = (String) polHolderInputVal.inputAndValidate("Policy Holder First Name: ", "String", "FirstName");
				lastName = (String) polHolderInputVal.inputAndValidate("Policy Holder Last Name: ", "String", "FirstName");
				address = (String) polHolderInputVal.inputAndValidate("Policy Holder Address: ", "String", "Address");
				birthDt = (LocalDate) polHolderInputVal.inputAndValidate("Birthday (MM-DD-YYYY): ", 
						"Date", "Birth Date");
				drivLicNum = (String) polHolderInputVal.inputAndValidate("Driver's License Number: ", "String", 
						"Driver's License Number");
				drvLicFirstIssdDt = (LocalDate) polHolderInputVal.inputAndValidate("Driver's License first issued date (MM-DD-YYYY): ", 
						"Date", "Issued Date");
				if(!polHolderInputVal.isRequiredAge(birthDt)) {
					System.out.println("Policy Holder age is below 17. "
							+ "Please enter another policy holder or reenter details again.\n");
					exitProcess = false;
					continue polHldrEnrl;
				} else if(!polHolderInputVal.isValidIssuedDate(birthDt, drvLicFirstIssdDt)){
					System.out.println("Policy Holder driver's license first issued date is invalid.\n");
					exitProcess = false;
					continue polHldrEnrl;
				}
				action = (Integer) polHolderInputVal.inputAndValidate("\n(1) Submit \t(2) "
						+ "Reenter Details\n"
						+ "Enter your Action (1,2): ","Integer","input action.");

				if (action.equals(1)) {
					addInPolHolderArr();
					exitProcess = true;
				}			
				else if (action.equals(2)) exitProcess = false;
			} while(exitProcess == false);
		return this;
	}
	
	/*
	 * This method process the enrollment of policy holder upon creation of a customer account in menu option 1
	 */
	public PolicyHolder accountPolicyHolderEnrollment(String firstName, String lastName, String address) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;		
		do {
			birthDt = (LocalDate) polHolderInputVal.inputAndValidate("Birthday (MM-DD-YYYY): ", 
					"Date", "Birth Date");
			if(!polHolderInputVal.isRequiredAge(birthDt)) {
				System.out.println("Policy Holder age is below 17. "
						+ "Please enter another policy holder or reenter details again.\n");
				exitProcess = false;
			} else {
				exitProcess = true;
			}
		} while(exitProcess == false);				
		drivLicNum = (String) polHolderInputVal.inputAndValidate("Driver's License Number: ", "String", 
						"Driver's License Number");
		drvLicFirstIssdDt = (LocalDate) polHolderInputVal.inputAndValidate("Driver's License first issued date (MM-DD-YYYY): ", 
						"Date", "Issued Date");
		return this;
	}
	
	/*
	 * This method add the policy holder in database once submitted
	 */
	public PolicyHolder addPolicyHolder(Integer accountNumber) {
		polHldrDb.addPolicyHolder((String)policyHolder[0], (String)policyHolder[1], (String)policyHolder[2], (LocalDate)policyHolder[3], 
				(String)policyHolder[4], (LocalDate)policyHolder[5], accountNumber);
		return this;
	}
	
	/*
	 * This method add the policy holder in policy holder array
	 */
	public PolicyHolder addInPolHolderArr() {
		policyHolder[0] = firstName;
		policyHolder[1] = lastName;
		policyHolder[2] = address;
		policyHolder[3] = birthDt;
		policyHolder[4] = drivLicNum;
		policyHolder[5] = drvLicFirstIssdDt;
		return this;
	}
	
	/*
	 * This method displays the Policy Holder name.
	 */
	public PolicyHolder displayPolHldrName() {
		System.out.println("Policy Holder: " + policyHolder[0] + " " + policyHolder[1]);
		System.out.println();
		return this;
	}

	/*
	 * This method sets the value of policyHolder array into null
	 */
	public PolicyHolder clearPolicyHolderArr() {
		for(int polHldrArrCount = 0 ; polHldrArrCount < policyHolder.length ; polHldrArrCount++) {
			policyHolder[polHldrArrCount] = null;
		}
		return this;
	}

	/*
	 * This method returns a String value of Policy Holder name.
	 */
	public String getPolHoldr() {
		polHoldr = (String) policyHolder[0] + " " +  (String) policyHolder[1];
		return polHoldr;
	}

	/*
	 * This method returns a LocalDate value of drivers license's first issued date.
	 */
	public LocalDate getDrvLicFirstIssdDt() {
		return drvLicFirstIssdDt;
	}


}
