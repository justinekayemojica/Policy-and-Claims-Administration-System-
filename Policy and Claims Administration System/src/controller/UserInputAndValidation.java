package controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
* Java Course 4 Module 3
*
* @author Justine Kaye Mojica
* @Description: A Java class that handles the user input in the application and the validation of the inputted values.
* Created Date: 6/27/2022
* Modified Date: 6/29/2022
* @Modified By: Justine Kaye Mojica
*
*/

public class UserInputAndValidation {

	// Working Variables
	private Boolean isValidValue;
	private final String INVALID_INPUT_MSG = "Invalid input. Please enter a valid value for ";
	private final DateTimeFormatter DATE_FORMATTER_MMDDYYYY = DateTimeFormatter.ofPattern("MM-dd-yyyy");

	public Scanner input = new Scanner(System.in);

	/*
	 * This inputAndValidate method prompts the user for an input and then validate
	 * the given value. The method arguments define as display text , data type of
	 * the input value to be validated and the variable name to be appended to the
	 * catch message. If the given value is valid the method will return the value
	 * as an Object else the program will prompt the user again for an input until a
	 * valid value is given
	 */
	public Object inputAndValidate(String displayText, String dataType, String excVarName) {

		Object inputValue = null;

		/*
		 * Input and Validation for String Input
		 */
		if (dataType.equals("String")) {
			do {
				try {
					System.out.print(displayText);
					inputValue = input.nextLine();
					isValidValue = true;
					//Validation for Vehicle Type to check if valid
					if (excVarName.equals(("Type"))) {
						if (isValidType(inputValue)) {
							inputValue = type(inputValue);
						} else {
							throw new IllegalArgumentException();
						}
					}
					//Validation for Vehicle Fuel Type to check if valid
					if (excVarName.equals("Fuel Type")) {
						if (isValidFuelType(inputValue)) {
							inputValue = fuelType(inputValue);
						} else {
							throw new IllegalArgumentException();
						}
					}
					//Validation for Account Number to check if the input value is 4 digits.
					if (excVarName.equals("Account Number")) {
						if (inputValue.toString().length() != 4 || !isDigit(inputValue)){
							throw new IllegalArgumentException();
						} else {
							inputValue = Integer.parseInt(inputValue.toString());
						}
					}
					//Validation for Policy Number to check if the input value is 6 digits.
					if (excVarName.equals("Policy Number")) {
						if (inputValue.toString().length() != 6 || !isDigit(inputValue)){
							throw new IllegalArgumentException();
						} else {
							inputValue = Integer.parseInt(inputValue.toString());
						}
					}
					//Validation for Claim Number to check if the input value first character is C and the rest is digits.
					if (excVarName.equals("Claim Number")) {
						if (inputValue.toString().length() != 6 || !isValidClaimNum(inputValue)){
							throw new IllegalArgumentException();
						}	
					}
					//Validation for Driver's License Number to check if the input value length is 13
					if (excVarName.equals("Driver's License Number")) {
						if (inputValue.toString().length() > 13){
							throw new IllegalArgumentException();
						}	
					}
					//Validation to check if input value is empty or blank
					if(String.valueOf(inputValue).isEmpty() || String.valueOf(inputValue).isBlank()) {
						throw new IllegalArgumentException();
					}
				} catch (Exception e) {	
					input.reset();				
					isValidValue = false;
					System.out.println(INVALID_INPUT_MSG + excVarName);
				}				
			} while (isValidValue == false);
		}

		/*
		 * Input and Validation for Integer input.
		 */
		if (dataType.equals("Integer")) {
			do {
				try {
					System.out.print(displayText);
					inputValue = input.nextInt();
					input.nextLine();
					isValidValue = true;
					//Validation for action if the input value is valid given choices 1, 2 and 3
					if (displayText.contains("(1,2,3): ")) {
						if (!inputValue.equals(1) && !inputValue.equals(2) && !inputValue.equals(3)) {
							throw new IllegalArgumentException();
						}
					}
					//Validation for action if the input value is valid given choices 1 and 2
					if (displayText.contains("(1,2): ")) {
						if (!inputValue.equals(1) && !inputValue.equals(2)) {
							throw new IllegalArgumentException();
						}
					}
					//Validation for action if the input value is valid given choices 2 and 3
					if (displayText.contains("(2,3): ")) {
						if (!inputValue.equals(3) && !inputValue.equals(2)) {
							throw new IllegalArgumentException();
						}
					}
					//Validation for menu options if the input value is valid
					if (displayText.contains("Please select a menu option (1-8): ")) {
						if (!isMenuOpt((Integer) inputValue))
							throw new IllegalArgumentException();
					}
					//Validation for number of vehicles if the input value is valid and not a negative integer
					if (displayText.contains("Please enter number of vehicle: ")) {
						if (isNegativeValue((Integer) inputValue))
							throw new IllegalArgumentException();
					}
					//Validation for Year if the input value is valid year and not a negative integer
					if (excVarName.contains("Year")) {
						if (!isValidYear((Integer)inputValue) || isNegativeValue((Integer) inputValue))
							throw new IllegalArgumentException();
					}					
				}catch (InputMismatchException e) {		
					input.nextLine();
					isValidValue = false;
					System.out.println(INVALID_INPUT_MSG + excVarName);						
				} 
				catch (Exception e) {		
					input.reset();
					isValidValue = false;
					System.out.println(INVALID_INPUT_MSG + excVarName);						
				}
			} while (isValidValue == false);
		}

		/*
		 * Input and Validation for BigDecimal input
		 */
		if (dataType.equals("BigDecimal")) {
			do {
				try {
					System.out.print(displayText);
					inputValue = input.nextBigDecimal();
					input.nextLine();
					isValidValue = true;
					//Validation for BigDecimal if input value is negative or zero
					if(isNegativeOrZero((BigDecimal) inputValue)) {
						throw new IllegalArgumentException();
					}
				} catch (InputMismatchException e) {		
					input.nextLine();
					isValidValue = false;
					System.out.println(INVALID_INPUT_MSG + excVarName);						
				}catch (Exception e) {
					input.reset();
					isValidValue = false;
					System.out.println(INVALID_INPUT_MSG + excVarName);
				}
			} while (isValidValue == false);
		}

		/*
		 * Input and Validation for Date variables
		 */
		if (dataType.equals("Date")) {
			do {
				try {
					LocalDate date = null;
					System.out.print(displayText);
					inputValue = input.nextLine();
					//Validation for Birth Date, Issued Date and Accident date to check if the input date is a valid date
					if (excVarName.contains("Birth Date") || excVarName.contains("Issued Date") || excVarName.contains("Accident Date")) {
						date = LocalDate.parse((String) inputValue, DATE_FORMATTER_MMDDYYYY);
						if(!isValidDateForMonths((String) inputValue))
							throw new DateTimeParseException("Date of Month", excVarName, 0);						
						if (isFutureDated(date))
							throw new DateTimeParseException("Future Dated", excVarName, 0);
					}
					//Validation for Effective date to check if the input date is a valid date
					if (excVarName.contains("Effective Date")) {
						date = LocalDate.parse((String) inputValue, DATE_FORMATTER_MMDDYYYY);
						if(!isValidDateForMonths((String) inputValue))
							throw new DateTimeParseException("Date of Month", excVarName, 0);
						if (isPastDated(date))
							throw new DateTimeParseException("Past Dated", excVarName, 0);
					}
					//Validation to check if the input value is empty or blank
					if(String.valueOf(inputValue).isEmpty() || String.valueOf(inputValue).isBlank()) {
						throw new IllegalArgumentException();
					}
					inputValue = date;
					isValidValue = true;
				} catch (DateTimeParseException ex) {
					input.reset();
					isValidValue = false;
					System.out.println(INVALID_INPUT_MSG + ex.getParsedString());
				} catch (Exception e) {
					input.reset();
					isValidValue = false;
					System.out.println(INVALID_INPUT_MSG + excVarName);
				}
			} while (isValidValue == false);
		}

		return inputValue;
	}

	/*
	 * Method that returns a string after the input is formatted based on the given
	 * number of digits.
	 */
	public String numberFormatter(Integer numOfDigits, Integer number) {
		String formattedNumber = null;

		if (numOfDigits == 4) {
			if (number < 10)
				formattedNumber = "000" + number;
			else if (number < 100)
				formattedNumber = "00" + number;
			else if (number < 1000)
				formattedNumber = "0" + number;
		}

		if (numOfDigits == 5) {
			if (number < 10)
				formattedNumber = "0000" + number;
			else if (number < 100)
				formattedNumber = "000" + number;
			else if (number < 1000)
				formattedNumber = "00" + number;
			else if (number < 10000)
				formattedNumber = "0" + number;
		}

		if (numOfDigits == 6) {
			if (number < 10)
				formattedNumber = "00000" + number;
			else if (number < 100)
				formattedNumber = "0000" + number;
			else if (number < 1000)
				formattedNumber = "000" + number;
			else if (number < 10000)
				formattedNumber = "00" + number;
			else if (number < 100000)
				formattedNumber = "0" + number;
		}

		return formattedNumber;
	}

	/*
	 *  Method that returns true if an input value is valid for Menu Options else will return false.
	 */
	private Boolean isMenuOpt(Integer inputValue) {
		List<String> monthList = new ArrayList<>(Arrays.asList("1,2,3,4,5,6,7,8".split(",")));
		if (monthList.contains(inputValue.toString())) {
			return true;
		}
		return false;
	}

	/*
	 * Method that returns true if the calculated age is not less than 17 else
	 * will return false.
	 */
	public Boolean isRequiredAge(LocalDate inputValue) {
		Period age = Period.between(inputValue, LocalDate.now());
		if (age.getYears() < 17) {
			return false;
		} else {
			return true;
		}
	}
	
	/*
	 * Method that returns true if the licensed first issued date is valid by checking if the date 
	 * is after the date where the policy holder turns 17.
	 */
	public Boolean isValidIssuedDate(LocalDate birthDate, LocalDate drvrLicIssuedDt) {
		LocalDate polHldrSeventeenthdBirthDate = birthDate.plusYears(17);
		if (drvrLicIssuedDt.isBefore(polHldrSeventeenthdBirthDate)) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * Method that returns true if an input value is a negative value else will
	 * return false.
	 */
	private Boolean isNegativeValue(Integer inputValue) {
		if (inputValue < 0) {
			return true;
		}
		return false;
	}

	/*
	 * Method that returns true if an input date value is future date else will
	 * return false.
	 */
	private Boolean isFutureDated(LocalDate inputValue) {
		if (inputValue.isAfter(LocalDate.now())) {
			return true;
		}
		return false;
	}

	/*
	 * Method that returns true if an input date value is past date else will return
	 * false.
	 */
	private Boolean isPastDated(LocalDate inputValue) {
		if (inputValue.isBefore(LocalDate.now())) {
			return true;
		}
		return false;
	}
	
	/*
	 * Method that returns true if an input date value is a valid date of Month 
	 *  else will return false.
	 */
	private Boolean isValidDateForMonths(String inputValue) {
		LocalDate date = LocalDate.parse(inputValue, DATE_FORMATTER_MMDDYYYY);
		List<String> monthLstDtIs31 =  new ArrayList<>(Arrays.asList("1,3,5,7,8,10,12".split(",")));
		List<String> monthLstDtIs30=  new ArrayList<>(Arrays.asList("4,6,9,11".split(",")));
		List<String> inputValueList = new ArrayList<>(Arrays.asList(inputValue.split("-")));
		if (monthLstDtIs31.contains(String.valueOf(date.getMonthValue()))) {
			if (Integer.parseInt(inputValueList.get(1)) > 31) {
				return false;
			}
		} else if (monthLstDtIs30.contains(String.valueOf(date.getMonthValue()))) {
			if (Integer.parseInt(inputValueList.get(1)) > 30) {
				return false;
			}
		} else if(date.getMonthValue() == 2 && date.isLeapYear()) {
			if (Integer.parseInt(inputValueList.get(1)) > 29) {
				return false;
			}
		} else if(date.getMonthValue() == 2 && !date.isLeapYear()) {
			if (Integer.parseInt(inputValueList.get(1)) > 28) {
				return false;
			}
			if (Integer.parseInt(inputValueList.get(1)) > 29) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * Method that returns true if the year is a 4 digits, if a year less than the after 10 years of the current year 
	 * and if a year is greater than 1672 where the vehicle is first invented else will return false.
	 */
	private Boolean isValidYear(Integer inputValue) {
		 if(inputValue < 1000 || inputValue > 9999) return false;
		 if(inputValue > (LocalDate.now().getYear() + 10)) return false;
		 if(inputValue <= 1672) return false;
		 
		return true;		
	}
	
	/*
	 * Method that returns true if the input value is a valid policy holder choice base on 
	 * the number of row return else will return false.
	 */
	public Boolean isValidPolHldrNum(Integer inputValue, Integer numberOfRows) {
		 if(inputValue > numberOfRows || inputValue <= 0) return false;
		return true;		
	}
	
	/*
	 * Method that returns true if vehicle year is not greater than 40 else will return false.
	 */
	public Boolean isValidVehicleYear(Integer year) {
		Integer currentYear = LocalDate.now().getYear();
		Integer vehicleAge = currentYear - year;
		if(vehicleAge >= 40) return false;
		return true;
	}
	
	/*
	 * Method that returns true if input value is a valid vehicle type else will return false.
	 */
	public Boolean isValidType(Object inputValue) {
		List<String> validType =  new ArrayList<>(Arrays.asList("1,2,3,4".split(",")));
		if(validType.contains(inputValue)) return true;
		return false;		
	}
	
	/*
	 * This method returns string value of vehicle type description depending on the 
	 * corresponding input value
	 */
	public Object type(Object inputValue) {
		Object type = null;
		String[] vehicleTypeArr  =  new String[]{"4-Door Sedan","2-Door Sports Car","SUV","Truck"};
		if(inputValue.equals("1")) type = vehicleTypeArr[0];
		else if(inputValue.equals("2")) type = vehicleTypeArr[1];
		else if(inputValue.equals("3")) type = vehicleTypeArr[2];
		else if(inputValue.equals("4")) type = vehicleTypeArr[3];
		return type;
	}
	
	/*
	 * This method returns true if input value is a valid vehicle fuel type else will return false.
	 */
	public Boolean isValidFuelType(Object inputValue) {
		List<String> validFuelType =  new ArrayList<>(Arrays.asList("1,2,3".split(",")));
		if(validFuelType.contains(inputValue)) return true;
		return false;		
	}
	
	/*
	 * This method returns string value of vehicle fuel type description depending 
	 * on the corresponding input value
	 */
	public Object fuelType(Object inputValue) {
		Object type = null;
		String[] vehicleTypeArr  =  new String[]{"Diesel","Electric","Petrol"};
		if(inputValue.equals("1")) type = vehicleTypeArr[0];
		else if(inputValue.equals("2")) type = vehicleTypeArr[1];
		else if(inputValue.equals("3")) type = vehicleTypeArr[2];
		return type;
	}
	
	/*
	 * This method returns true if the accident date is within policy coverage date.
	 */
	public Boolean isValidAccidentDate(LocalDate accidentDate,LocalDate effectiveDate , LocalDate expirationDate) {
		if(accidentDate.isBefore(effectiveDate)) return false;
		if(accidentDate.isEqual(expirationDate) || accidentDate.isAfter(expirationDate)) return false;
		return true;
	}
	
	/*
	 * This method returns true if an input value is digits.
	 */
	public Boolean isDigit(Object inputValue) {	
		char[] inputValChar = String.valueOf(inputValue).toCharArray();
		for(int count = 0 ; count < inputValChar.length ; count++) {
			if(!Character.isDigit(inputValChar[count])) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * This method returns true if an input value starts with C and the rest is digits.
	 */
	public Boolean isValidClaimNum(Object inputValue) {
		char[] inputValChar = String.valueOf(inputValue).toCharArray();
		for(int count = 0 ; count < inputValChar.length ; count++) {
			if(count == 0) {
				if(inputValChar[count] != 'C') return false;
			} else {
				if(!Character.isDigit(inputValChar[count])) return false;
			}
		}
		return true;
	}
	
	/*
	 * This method returns true if an input value of BigDecimal variable is negative or zero
	 */
    public Boolean isNegativeOrZero(BigDecimal inputValue){
    	if(inputValue.signum() == -1 || inputValue.signum() == 0) return true;
    	return false;
    }

}
