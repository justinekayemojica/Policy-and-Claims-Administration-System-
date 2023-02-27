package module;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import controller.AppDatabase;
import controller.RatingEngine;
import controller.UserInputAndValidation;

/**
* Java Course 4 Module 3
*
* @author Justine Kaye Mojica
* @Description: A Java class that process the enrollment and display of 
* 		vehicles as well as the display of computed premiums.
* Created Date: 6/27/2022
* 
*/

public class Vehicle{

	// Working Variables
	private String plateNum;
	private String make;
	private String model;
	private Integer year;
	private String type;
	private String fuelType;
	private BigDecimal purchasePrice;
	private String color;
	private BigDecimal premiumCharged;
	private Integer numOfVehicle;
	private BigDecimal polPremium;
	public Integer action;	
	private Boolean exitProcess;
	private AppDatabase vhclDb;
	private Object[][] vehicle;
	public List<Integer> inValidVhcleForPol;
	private UserInputAndValidation vehicleInputVal;

	/*
	 * This class constructor initialize the AppDatabase vhclDb object
	 */
	public Vehicle(AppDatabase vhclDb) {
		this.vhclDb = vhclDb;
		vehicleInputVal = vhclDb.dbInputAndVal;
	}

	/*
	 * This method will display the Vehicle Header 
	 */
	public Vehicle displayHeader() {
		System.out.println();
		System.out.println(
				"------------------------------------------- VEHICLE ---------------------------------------------");
		return this;
	}

	/*
	 * This method process the enrollment of Vehicle/s
	 */
	public Vehicle vehicleEnrollment(LocalDate drvLicFirstIssdDt , Boolean buyPolicy) {
		System.out.println("\t\t\t\t      >>Vehicle Enrollment<<");
		System.out.println("Please input details below.");
		numOfVehicle = (Integer) vehicleInputVal.inputAndValidate("\nEnter number of vehicle/s: ", "Integer",
				"number of vehicle");
		vehicle = new Object[numOfVehicle][9];
		for (int vhclCount = 0; vhclCount < numOfVehicle; vhclCount++) {
			do {
				System.out.println("\nVehicle #" + String.valueOf(vhclCount + 1));
				vehicleEnrollmentForm(drvLicFirstIssdDt);
				action = (Integer) vehicleInputVal.inputAndValidate("\n(1) Submit \t(2) Reenter details for vehicle #"
						+ String.valueOf(vhclCount + 1) + "\n" + "Enter your Action (1,2): ", "Integer",
						"input action.");
				System.out.println();
				if (action.equals(1)) {
					if(vhclDb.isValidVhcleToUse(plateNum) && buyPolicy.equals(true)) {
						addInVehicleArr(vhclCount);
						exitProcess = true;
					} else if(!vhclDb.isValidVhcleToUse(plateNum) && buyPolicy.equals(true)) {
						System.out.println("Vehicle is already enrolled in an active Policy");
						action = (Integer) vehicleInputVal.inputAndValidate("\n(1) Reenter details for vehicle #"
								+ String.valueOf(vhclCount + 1) + " (2) Cancel Enrollment and Back to Menu \n" 
								+ "Enter your Action (1,2): ", "Integer","input action.");
						if (action.equals(1)) exitProcess = false;
						else if (action.equals(2)) return this;
					} else {
						addInVehicleArr(vhclCount);
						exitProcess = true;
					}
				} else if (action.equals(2)) {
					exitProcess = false;
				}
			} while (exitProcess == false);
		}
		return this;
	}
	
	/*
	 * This method prompt user to input the following details for vehicle enrollment
	 */
	public void vehicleEnrollmentForm(LocalDate drvLicFirstIssdDt) {
		Integer vehicleAge;
		do {
			plateNum = (String) vehicleInputVal.inputAndValidate("Plate Number: ", "String", "Plate Number");
			make = (String) vehicleInputVal.inputAndValidate("Make: ", "String", "Make");
			model = (String) vehicleInputVal.inputAndValidate("Model: ", "String", "Model");
			year = (Integer) vehicleInputVal.inputAndValidate("Year: ", "Integer", "Year");
			type = (String) vehicleInputVal.inputAndValidate("(1) 4-Door Sedan  (2) 2-Door Sports Car  (3) SUV  (4) Truck"
					+"\nEnter Type (1,2,3,4): ", "String", "Type");
			System.out.println("Type: " + type);
			fuelType = (String) vehicleInputVal.inputAndValidate("(1) Diesel  (2) Electric  (3) Petrol"
					+"\nEnter Fuel Type (1,2,3): ", "String", "Fuel Type");
			System.out.println("Fuel Type: " + fuelType);
			purchasePrice = (BigDecimal) vehicleInputVal.inputAndValidate("Purchase Price: ", "BigDecimal",
					"Purchase Price");
			color = (String) vehicleInputVal.inputAndValidate("Color: ", "String", "Color");
			vehicleAge = Calendar.getInstance().get(Calendar.YEAR) - year;
			if(vehicleAge >= 40) {
				System.out.println("\nVehicle age is greater than or equal to 40 \nPlease enroll other vehicle.");
				exitProcess = false;
			}else {
				exitProcess = true;
			}
		}while(exitProcess == false);	
		premiumCharged = new RatingEngine(purchasePrice, drvLicFirstIssdDt, year).premium();
		System.out.println("Premium: $" + premiumCharged);
	}	

	/*
	 * This method adds the vehicle stored in array in database once submitted.
	 */
	public Vehicle addVehicle(Integer policyNumber) {
		for (int vhclCount = 0; vhclCount < numOfVehicle; vhclCount++) {
			vhclDb.addVehicle((String) vehicle[vhclCount][0], (String) vehicle[vhclCount][1],
					(String) vehicle[vhclCount][2], (Integer) vehicle[vhclCount][3], (String) vehicle[vhclCount][4],
					(String) vehicle[vhclCount][5], (BigDecimal) vehicle[vhclCount][6], (String) vehicle[vhclCount][7],
					(BigDecimal) vehicle[vhclCount][8], policyNumber);
		}
		return this;
	}

	/*
	 * This method store the vehicles in vehicle 2d array
	 */
	public Vehicle addInVehicleArr(Integer vhclCount) {
		vehicle[vhclCount][0] = plateNum;
		vehicle[vhclCount][1] = make;
		vehicle[vhclCount][2] = model;
		vehicle[vhclCount][3] = year;
		vehicle[vhclCount][4] = type;
		vehicle[vhclCount][5] = fuelType;
		vehicle[vhclCount][6] = purchasePrice;
		vehicle[vhclCount][7] = color;
		vehicle[vhclCount][8] = premiumCharged;
		return this;
	}
	
	/*
	 * This method returns true if the vehicle to be enroll in policy is valid by checking if the vehicle plate number 
	 * is currently not enrolled to a pending active or an active exisitng policy else will return false; 
	 */
	public Boolean isValidVhcleForPol(Boolean isInVhclValidation) {
		Boolean isValidVhcleForPol = true;
		Boolean isValidVhcleToUse = true;
		inValidVhcleForPol = new ArrayList<>();
		for (int vhclCount = 0; vhclCount < numOfVehicle; vhclCount++) {
			isValidVhcleToUse = vhclDb.isValidVhcleToUse((String)vehicle[vhclCount][0]);
			if(isValidVhcleToUse.equals(false)) {
				if(vhclCount == 0 && isInVhclValidation.equals(Boolean.FALSE)) {
					System.out.println("\nThe vehicle plate number/s below is already enrolled in an active or pending active Policy.");
					System.out.println("Please enroll other vehicle ...");
					System.out.println("\nVehicle Plate Number/s");
				}
				if(isInVhclValidation.equals(Boolean.TRUE)) {
					System.out.println("\nBelow vehicle is still not valid for Policy. Please enroll other vehicle ...");
					System.out.println("\nVehicle Plate Number/s");
				}
				System.out.println((String)vehicle[vhclCount][0]);
				inValidVhcleForPol.add(vhclCount);
				isValidVhcleForPol = false;
			}
		}
		return isValidVhcleForPol;
	}
	
	/*
	 * This method recalculates the premium if the user decides to use an existing policy holder in Policy Quotation 
	 * Option of Policy.
	 */
	public Vehicle recalculateVehicleArr(LocalDate drvLicFirstIssdDt) {
		for (int vhclCount = 0; vhclCount < numOfVehicle; vhclCount++) {
			BigDecimal newPremiumCharged;
			newPremiumCharged = new RatingEngine((BigDecimal)vehicle[vhclCount][6], drvLicFirstIssdDt, (Integer)vehicle[vhclCount][3]).premium();
			vehicle[vhclCount][8] = newPremiumCharged;			
		}
		return this;
	}

	/*
	 * This method displays the vehicle plate number and calculated premium.
	 */
	public Vehicle displayVehiclesPremium() {
		System.out.printf("%-25s %-30s %-15s\n" , "Vehicle Plate Number" ,"Vehicle Description", "Premium");
		for (int vhclCount = 0; vhclCount < numOfVehicle; vhclCount++) {
			System.out.printf("%-25s %-30s %-15s\n", vehicle[vhclCount][0], vehicle[vhclCount][1] + " " + vehicle[vhclCount][2] + " "
					+ vehicle[vhclCount][3], "$" + vehicle[vhclCount][8]);
		}
		System.out.println();
		return this;
	}

	/*
	 * This method displays the calculated premium of the policy.
	 */
	public Vehicle displayTotalPremium() {
		polPremium = getPolPremium();
		System.out.println("Total Policy Premium : " + "$" + polPremium.setScale(2));
		System.out.println();
		return this;
	}

	/*
	 * This method clears the vehicle 2d array setting thee value into null.
	 */
	public Vehicle clearVehicleArr() {
		for (int vhclCount2d = 0; vhclCount2d < numOfVehicle; vhclCount2d++) {
			for (int vhclArrCount = 0; vhclArrCount < vehicle[vhclCount2d].length; vhclArrCount++) {
				vehicle[vhclCount2d][vhclArrCount] = null;
			}
		}
		return this;
	}
	
	/*
	 * This method returns a BigDecimal value of policy premium.
	 */
	public BigDecimal getPolPremium() {
		polPremium = BigDecimal.ZERO;
		for (int vhclCount = 0; vhclCount < numOfVehicle; vhclCount++) {
			polPremium = polPremium.add((BigDecimal) vehicle[vhclCount][8]);
		}
		return polPremium;
	}

	/*
	 * This method returns the 2d array object of vehicle.
	 */
	public Object[][] getVehicle() {
		return vehicle;
	}
	

}
