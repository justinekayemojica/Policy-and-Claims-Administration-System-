package View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import controller.UserInputAndValidation;

/**
* Java Course 4 Module 3
*
* @author Justine Kaye Mojica
* @Description: A Java class that process the display and selection of menu options.
* Created Date: 6/27/2022
*
*/

public class Menu {	
	
	//Working Variables
	private Integer selectedOpt;
	private List<String> menuDesc = new ArrayList<>(Arrays.asList(
			"Create New Customer Account",
			"Get Policy Quote and Buy Policy",
			"Cancel Policy",
			"File an Accident Claim",
			"Customer Account Search",
			"Policy Search",
			"Claim Search",
			"Exit"			
			));

	//Initialize UserInputAndValidation object..
	UserInputAndValidation menuAccInputVal = new UserInputAndValidation();
	
	/*
	 * Method to display class header
	 */
	public Menu displayHeader(){
		System.out.println();
		System.out.println("--------------------------------------------- MENU ----------------------------------------------");
		return this;
	}
	
	/*
	 * Method to display Menu details
	 */
	public Menu displayMenu() {
		for(int descCount = 0 ; descCount< menuDesc.size(); descCount++) {
			System.out.printf("%-1d %-50s \n" ,descCount + 1 , menuDesc.get(descCount));
		}
		System.out.println();
		
		return this;
	}
	
	/*
	 * Method that prompts user for selected option and returns an integer value of it.
	 */
	public Integer selectOption() {		
		selectedOpt = (Integer) menuAccInputVal.inputAndValidate("Please select a menu option (1-8): ", 
				"Integer", "selected option.")	;	 
		return selectedOpt;
	}


}
