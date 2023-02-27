package module;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import controller.AppDatabase;
import controller.UserInputAndValidation;

/**
* Java Course 4 Module 3
*
* @author Justine Kaye Mojica
* @Description: A Java class that process policy quotation, policy purchase, 
* 		policy enrollment, cancellation of policy and policy look up and display.
* Created Date: 6/27/2022
* Modified Date: 6/28/2022
* @Modified By: Justine Kaye Mojica
*
*/

public class Policy extends CustomerAccount{

	//Working Variables
	protected Integer policyNumber;
	protected LocalDate policyCoverageEffDt;
	protected LocalDate policyCoverageExpDt;
	protected String polHoldr;
	protected Integer polHoldrNum;
	protected Integer polHoldrRows;
	protected BigDecimal polPremium;
	protected Integer polStatFlg;
	protected String statusDesc;
	protected LocalDate drvLicFirstIssdDt;
	protected Boolean isPolicyExists;
	protected Boolean isPolHldrExists;
	protected Boolean isValidPolHldrNum;
	protected Boolean useExistingPolHldr;
	protected Boolean isValidVhcleForPol;
	protected Boolean isNewAccountCreatedInPol;
	protected AppDatabase polDb;
	protected CustomerAccount customerAccount;
	protected Vehicle vehicle;
	private UserInputAndValidation polInputVal;
	protected Object[][] vehicleObj;
	protected List<String> vehicleList = new ArrayList<>();

	/*
	 * This class constructor initialize the AppDatabase polDb, customerAccount, policyHolder and vehicle object
	 */
	public Policy(AppDatabase polDb) {
		super(polDb);
		polInputVal = polDb.dbInputAndVal;
		this.polDb = polDb;
		customerAccount = new CustomerAccount(polDb);
		vehicle = new Vehicle(polDb);
	}

	/*
	 * This method displays the Policy Header 
	 */
	public Policy displayHeader() {
		System.out.println();
		System.out.println("-------------------------------------------- POLICY ---------------------------------------------");
		return this;
	}


	/*
	 * This method displays the Policy Main Page which prompts user for two option in policy , Policy Quotation and Policy Purchase.
	 */
	public void policyMainPage() {
		action = (Integer) polInputVal.inputAndValidate(
				"\n(1) Get a Policy Qoute \t(2) Purchase a Policy\n" + "Enter your Action (1,2): ", "Integer",
				"input action.");

		switch (action) {
		case 1:
			System.out.println("\t\t\t\t     >>>Policy Quotation<<<		");
			System.out.println();
			policyQuotationOpt();
			if (action.equals(2)) return;
			break;
		case 2:
			System.out.println("\t\t\t\t     >>>Policy Purchase<<<		");
			System.out.println();
			policyPurchaseOpt();
			if (action.equals(2)) return;
			break;
		}		
	}

	/*
	 * Method for Policy Qoutation option which user is getting their policy quotation first before deciding on buying the policy or not.
	 */
	public void policyQuotationOpt() {		
		do {
			isNewAccountCreatedInPol = false;
			getPolicyQuote();
			action = (Integer) polInputVal.inputAndValidate(
					"\n(1) Get a Quotation again \t(2) Buy Policy \t(3) Back to Menu\n" + "Enter your Action (1,2,3): ",
					"Integer", "input action.");
			if (action.equals(1)) {
				exitProcess = false;
			} else if (action.equals(2)) {
				accPrompt:
					do {
						if(isNewAccountCreatedInPol.equals(false)) {
							System.out.println("\nPlease enter Account Number to Proceed");
							accountNumber = (Integer) polInputVal.inputAndValidate("Account No: ", "String", "Account Number");
						}
						isCusAccExists = polDb.isCusAccNumExists(accountNumber);	
						if(isCusAccExists) {
							System.out.println("\nAccount Number Validated...");
							useExistingPolHolder();
							validatePolicyVehicle();
							if(action.equals(2)) return;
							if(useExistingPolHldr) {
								System.out.println("\nDue to changes, please see below new policy quotation.\n");
								System.out.println("\n*************************************************************************************************");
								System.out.println("\t\t\t\t   Quotation Summary");
								vehicle.recalculateVehicleArr(getDrvLicFirstIssdDt()).displayVehiclesPremium().displayTotalPremium();
								System.out.println("\n*************************************************************************************************");
								getAPolicy().addPolicy();
								vehicle.addVehicle(policyNumber);
								dispPolTrnsctRcpt();
								clearPolicyHolderArr();
								vehicle.clearVehicleArr();
								action = (Integer) polInputVal.inputAndValidate("\n(1) Get a Quotation again \t(2) Back to Menu\n" 
										+ "Enter your Action (1,2): ","Integer", "input action.");
								if (action.equals(1)) {
									System.out.println("\t\t\t\t     >>>Policy Quotation<<<");
									exitProcess = false;
									break accPrompt;
								} else if (action.equals(2)) {
									exitProcess = true;
								}
							} else {
								addPolicyHolder(accountNumber);
								getAPolicy().addPolicy();
								vehicle.addVehicle(policyNumber);
								dispPolTrnsctRcpt();
								clearPolicyHolderArr();
								vehicle.clearVehicleArr();
								action = (Integer) polInputVal.inputAndValidate("\n(1) Get a Quotation again \t(2) Back to Menu \n" 
										+ "Enter your Action (1,2): ","Integer", "input action.");
								if (action.equals(1)) {
									System.out.println("\t\t\t\t     >>>Policy Quotation<<<");
									exitProcess = false;
									break accPrompt;
								} else if (action.equals(2)) {
									exitProcess = true;
								}
							}
						} else {
							System.out.println("Account does not exists.");
							action = (Integer) polInputVal.inputAndValidate("\n(1) Reenter account number \t(2) Create a Customer Account "
									+ "(3) Back to Menu \nEnter your Action (1,2,3): ",	"Integer", "input action.");
							if (action.equals(1)) exitProcess = false;
							else if (action.equals(2)) {
								isCustomerAccountPage = false; 
								isNewAccountCreatedInPol = true;
								createAnAccount();
								exitProcess = false;
							}
							else if (action.equals(3))exitProcess = true;
						}
					} while(exitProcess == false);		
			} else if (action.equals(3)) {
				clearPolicyHolderArr();
				vehicle.clearVehicleArr();
				return;
			}
		}while(exitProcess == false);		
	}


	/*
	 * Method for Policy Purchase option which user is already buying a policy.
	 */
	public void policyPurchaseOpt() {
		isNewAccountCreatedInPol = false;
		do {
			if(isNewAccountCreatedInPol.equals(false)) {
				System.out.println("\nPlease enter Account Number to Proceed");
				accountNumber = (Integer) polInputVal.inputAndValidate("Account No: ", "String", "Account Number");
			}
			isCusAccExists = polDb.isCusAccNumExists(accountNumber);
			if(isCusAccExists) {
				System.out.println("\nAccount Validated...");
				useExistingPolHolder();
				if(useExistingPolHldr) {
					vehicle.vehicleEnrollment(drvLicFirstIssdDt,true);
					if(vehicle.action.equals(2)) {
						action = vehicle.action;
						exitProcess = true;
					} else {
						vehicle.displayVehiclesPremium().displayTotalPremium();
						getAPolicy().addPolicy();
						vehicle.addVehicle(policyNumber);
						dispPolTrnsctRcpt();
						clearPolicyHolderArr();
						vehicle.clearVehicleArr();
						action = (Integer) polInputVal.inputAndValidate(
								"\n(1) Purchase a policy again \t(2) Back to Menu\n" + "Enter your Action (1,2): ",
								"Integer", "input action.");
						if (action.equals(1)) {
							System.out.println("\t\t\t\t     >>>Policy Purchase<<<		");
							exitProcess = false;
						}
						else if (action.equals(2))exitProcess = true;
					}					
				} else {
					policyHolderEnrollment().addPolicyHolder(accountNumber);
					polHoldr = getPolHoldr();
					vehicle.vehicleEnrollment(getDrvLicFirstIssdDt() ,true);
					if(vehicle.action.equals(2)) {
						action = vehicle.action;
						exitProcess = true;
					} else {
						vehicle.displayVehiclesPremium().displayTotalPremium();
						getAPolicy().addPolicy();
						vehicle.addVehicle(policyNumber);
						dispPolTrnsctRcpt();
						clearPolicyHolderArr();
						vehicle.clearVehicleArr();
						action = (Integer) polInputVal.inputAndValidate(
								"\n(1) Purchase a policy again \t(2) Back to Menu\n" + "Enter your Action (1,2): ",
								"Integer", "input action.");
						if (action.equals(1)) {
							System.out.println("\t\t\t\t     >>>Policy Purchase<<<		");
							exitProcess = false;
						}
						else if (action.equals(2))exitProcess = true;
					}					
				}
			} else {
				System.out.println("Account does not exists.");
				action = (Integer) polInputVal.inputAndValidate(
						"\n(1) Reenter account number \t(2) Create a Customer Account (3) Back to Menu\n" + "Enter your Action (1,2,3): ",
						"Integer", "input action.");
				if (action.equals(1)) exitProcess = false;
				else if (action.equals(2)) {
					isCustomerAccountPage = false; 
					isNewAccountCreatedInPol = true;
					customerAccount.displayHeader();
					createAnAccount();
					exitProcess = false;
				}
				else if (action.equals(3))exitProcess = true;
			}
		} while(exitProcess == false);	

	}

	/*
	 * This method shows the user the quotation of their policy
	 */
	public void getPolicyQuote() {
		polHoldr = policyHolderEnrollment().getPolHoldr();	
		vehicle.vehicleEnrollment(getDrvLicFirstIssdDt(), false);
		System.out.println("\n*************************************************************************************************");
		System.out.println("\t\t\t\t   Quotation Summary");
		displayPolHldrName();
		vehicle.displayVehiclesPremium().displayTotalPremium();
		System.out.println("\n*************************************************************************************************");
	}

	/*
	 * This method will prompts user for policy enrollment which requires input value for policy effective date.
	 */
	public Policy getAPolicy() {
		do {
			System.out.println("\t\t\t\t      >>Policy Enrollment<<");
			System.out.println("Please enter the details below.");
			policyCoverageEffDt = (LocalDate) polInputVal
					.inputAndValidate("Policy Coverage Effective Date (MM-DD-YYYY): ", "Date", "Effective Date");
			policyCoverageExpDt = policyCoverageEffDt.plusMonths(6);
			action = (Integer) polInputVal.inputAndValidate(
					"\n(1) Submit policy (2) Enter details again\n" + "Enter your Action (1,2): ", "Integer",
					"input action.");
			if (action.equals(1)) exitProcess = true;
			else if (action.equals(2)) exitProcess = false;
		} while (exitProcess == false);	
		return this;
	}

	/*
	 * This method prompts user to use a existing policy holder based on their preference.
	 */
	public void useExistingPolHolder() {
		useExistingPolHldr = true;
		isPolHldrExists = polDb.isCusAccPolHldrExists(accountNumber);
		if(isPolHldrExists){					
			System.out.println("The account number has existing policy holder. \nWould you like to use an existing policy holder?");
			action = (Integer) polInputVal.inputAndValidate(
					"\n(1) Yes \t(2) No \nEnter your Action (1,2): ", "Integer", "input action.");
			if(action.equals(1)) {
				polDb.cusAccPolHldrDisplay();
				polHoldrRows = polDb.getNumOfRows();
				do {
					polHoldrNum = (Integer) polInputVal.inputAndValidate(
							"\nPlease select a policy holder to use : ", "Integer", "policy holder");
					isValidPolHldrNum = polInputVal.isValidPolHldrNum(polHoldrNum, polHoldrRows);
					if(isValidPolHldrNum == false) System.out.println("Invalid policy holder number.");
				} while(isValidPolHldrNum == false);
				useExistingPolHldr = true;
				polHoldr = polDb.getExistingPolHldrName(polHoldrNum);
				drvLicFirstIssdDt = polDb.getPolHldrLicFrstIssdDt(polHoldrNum);
			} else if(action.equals(2)) {
				useExistingPolHldr = false;
				action = 0;
			}
		} else {
			useExistingPolHldr = false;
		}
	}

	/*
	 * This method validates the enrolled vehicle is eligible for policy considering the vehicle currently not enrolled 
	 * to any pending active or active policy
	 */
	public void validatePolicyVehicle() {
		isValidVhcleForPol = vehicle.isValidVhcleForPol(false);
		while(isValidVhcleForPol.equals(false)) {
			action = (Integer) polInputVal.inputAndValidate("\n(1) Reenter details for vehicle/s"
					+ "\t(2) Cancel Enrollment and Back to Menu \n" + "Enter your Action (1,2): ", "Integer",
						"input action.");
			if (action.equals(1)) {
				for(Integer inValidVhclCount = 0 ; inValidVhclCount < vehicle.inValidVhcleForPol.size() ; inValidVhclCount++) {
					System.out.println("\t\t\t\t    >>Vehicle Enrollment<<");
					System.out.println("Please input details below.");
					exitProcess = false;
					while(exitProcess.equals(Boolean.FALSE)) {
						vehicle.vehicleEnrollmentForm(getDrvLicFirstIssdDt());
						action = (Integer) polInputVal.inputAndValidate("\n(1) Submit \t(2) Reenter details for vehicle #"
								 + "\nEnter your Action (1,2): ", "Integer","input action.");
						if (action.equals(1)) {
							vehicle.addInVehicleArr(vehicle.inValidVhcleForPol.get(inValidVhclCount));
							exitProcess = true;
						} else {
							exitProcess = false;
						}
					}
				}
				isValidVhcleForPol = vehicle.isValidVhcleForPol(true);
				if(isValidVhcleForPol.equals(true)) {
					System.out.println("\nVehicles validated ...");
				}
			}
			else if (action.equals(2)) return;			
		}
	}

	/*
	 * This method adds the new policy to the database and sets the value of policyNumber variable.
	 */
	public void addPolicy() {
		polPremium = vehicle.getPolPremium();
		polDb.addNewPolicy(policyCoverageEffDt, policyCoverageExpDt, polHoldr, polPremium, accountNumber);
		policyNumber = polDb.getGeneratedKeys();
	}

	/*
	 * This method displays the transaction receipt of purchased policy.
	 */
	public void dispPolTrnsctRcpt() {	
		System.out.println("\n*************************************************************************************************");
		System.out.println("\t\t\t\tPurchased Policy Summary");
		System.out.println("Policy Number: " + polInputVal.numberFormatter(6, policyNumber));
		System.out.printf("%-55s %-30s\n", "Effective Date: " + policyCoverageEffDt,
				"Expiration Date: " + policyCoverageExpDt);
		System.out.printf("%-55s %-30s\n", "Account Number: " + polInputVal.numberFormatter(4, accountNumber),
				"Account Name: " + getAcctName(accountNumber).firstName + " " + getAcctName(accountNumber).lastName);
		if(useExistingPolHldr) System.out.println("Policy Holder: " + polHoldr);
		else displayPolHldrName();
		System.out.println();
		vehicle.displayVehiclesPremium().displayTotalPremium();
		System.out.print("\n*************************************************************************************************");
		System.out.println();
	}


	/*
	 * This method prompts user for search and display of policy information considered existing in database
	 */
	public Policy polLookUpAndDisplay() {
		do {
			System.out.println("\t\t\t\t     >>>Policy Search<<<");
			policyNumber = (Integer) polInputVal.inputAndValidate("Please enter policy number : ", "String",
					"Policy Number");
			System.out.println();
			isPolicyExists = polDb.isPolExists(policyNumber);
			if (isPolicyExists) {
				System.out.println("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");;
				System.out.println("\t\t\t\t      Policy Information");
				System.out.println("Status: " + statusDesc(polDb.getPolStatFlg()));
				polDb.polInfoDisplay();
				isPolicyExists = polDb.isPolVhclExists(policyNumber);
				if(isPolicyExists) polDb.polVhcleDisplay();	
				System.out.println("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				action = (Integer) polInputVal.inputAndValidate(
						"\n(1) Search again \t(2) Back to Menu\n" + "Enter your Action (1,2): ", "Integer", "input action.");
				if (action.equals(1)) exitProcess = false;
				else if (action.equals(2)) exitProcess = true;
			} else {
				System.out.println("Policy Number does not exists...");
				action = (Integer) polInputVal.inputAndValidate(
						"\n(1) Search again \t(2) Back to Menu\n" + "Enter your Action (1,2): ", "Integer", "input action.");
				if (action.equals(1)) exitProcess = false;
				else if (action.equals(2)) exitProcess = true;
			}			
		} while (exitProcess == false);
		System.out.println("\nReturning to Menu ... ");
		return this;
	}

	/*
	 * Method that will cancel an existing policy
	 */
	public Policy cancelPolicy() {
		do {
			System.out.println("\t\t\t\t     >>>Policy Cancellation<<<");
			policyNumber = (Integer) polInputVal.inputAndValidate("Please enter policy number : ", "String",
					"Policy Number");
			System.out.println();
			isPolicyExists = polDb.isPolExists(policyNumber);
			polStatFlg = polDb.getPolStatFlg();
			if (isPolicyExists && !polStatFlg.equals(40) && !polStatFlg.equals(30)) {
				System.out.println("Please validate below policy details.");
				System.out.println("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("\t\t\t\t      Policy Information");
				polDb.polInfoDisplay();
				if(polDb.isPolVhclExists(policyNumber)) polDb.polVhcleDisplay();
				System.out.println("\n- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
				System.out.println("\nAfter policy has been revoked, Claims will no longer be processed. "
						+ "\nAre you certain you want to cancel this policy?");
				action = (Integer) polInputVal.inputAndValidate(
						"\n(1) Cancel Policy \t(2) Back to Menu\n" + "Enter your Action (1,2): ", "Integer", "input action.");
				if (action.equals(1)) {
					polDb.cancelPolicy(policyNumber);
					System.out.println("\nPolicy " + polInputVal.numberFormatter(6, policyNumber) + " has been cancelled...");
					action = (Integer) polInputVal.inputAndValidate(
							"\n(1) Cancel another policy \t(2) Back to Menu\n" + "Enter your Action (1,2): ", "Integer", "input action.");
					if (action.equals(1)) exitProcess = false;
					else if (action.equals(2)) exitProcess = true;
				} else if (action.equals(2)) {
					exitProcess = true;
				}
			} else if(isPolicyExists && polStatFlg.equals(30)) {
				System.out.println("Policy Number is expired ...");
				action = (Integer) polInputVal.inputAndValidate(
						"\n(1) Reenter a policy number \t(2) Back to Menu\n" + "Enter your Action (1,2): ", "Integer", "input action.");
				if (action.equals(1)) exitProcess = false;
				else if (action.equals(2)) exitProcess = true;

			} else if(isPolicyExists && polStatFlg.equals(40)) {
				System.out.println("Policy Number is already cancelled ...");
				action = (Integer) polInputVal.inputAndValidate(
						"\n(1) Reenter a policy number \t(2) Back to Menu\n" + "Enter your Action (1,2): ", "Integer", "input action.");
				if (action.equals(1)) exitProcess = false;
				else if (action.equals(2)) exitProcess = true;
			} else {
				System.out.println("Policy Number does not exists...");
				action = (Integer) polInputVal.inputAndValidate(
						"\n(1) Reenter a policy number \t(2) Back to Menu\n" + "Enter your Action (1,2): ", "Integer", "input action.");
				if (action.equals(1)) exitProcess = false;
				else if (action.equals(2)) exitProcess = true;
			}
		} while (exitProcess == false);
		System.out.println("\nReturning to Menu ... ");
		return this;
	}

	/*
	 * This method will return String value of policy status description.
	 */
	public String statusDesc(Integer polStatFlg) {
		if(polStatFlg.equals(10)) {
			statusDesc = "Pending Active";
		} else if(polStatFlg.equals(20)) {
			statusDesc = "Active";
		} else if(polStatFlg.equals(30)) {
			statusDesc = "Expired";
		} else if(polStatFlg.equals(40)) {
			statusDesc = "Cancelled";
		}
		return statusDesc;
	}

	/*
	 * This method returns a LocalDate value of policy effective date
	 */
	public LocalDate getPolEffectiveDate() {
		policyCoverageEffDt = polDb.getPolEffectiveDate();
		return policyCoverageEffDt;
	}

	/*
	 * This method returns a LocalDate value of policy expiration date
	 */
	public LocalDate getPolExpirationDate() {
		policyCoverageExpDt = polDb.getPolExpirationDate();
		return policyCoverageExpDt;
	}
}
