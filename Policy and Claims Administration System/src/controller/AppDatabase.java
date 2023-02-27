package controller;

import java.math.BigDecimal;
import module.Policy;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
* Java Course 4 Module 3
*
* @author Justine Kaye Mojica
* @Description: A Java class that process the connection of the MySQL server, 
* 		the setup of application, the initialization of database such creation of tables, events, procedure and triggers of tables,
* 		the insertion , update and selection of records in tables and as well as the display of records from database tables.
* Created Date: 6/27/2022
* Modified Date: 6/28/2022
* @Modified By: Justine Kaye Mojica
*
*/

public class AppDatabase {

	// Working Variables
	public Connection connection;
	private Statement statement;
	private ResultSet results;
	private PreparedStatement prepStatement;
	private String databaseName;
	private String userName;
	private String password;
	private Integer action;
	private Boolean exitProcess;
	private Boolean connectToDefault;
	private String dbUrl;

	// Instantiation of UserInputAndValidation object..
	public UserInputAndValidation dbInputAndVal = new UserInputAndValidation();

	/*
	 * This method prompts user of application setup of database which consists of using a existing database or creating a new one.
	 */
	public AppDatabase appSetup() {
		do {
			System.out.println();
			System.out.println("--------------------------------------- APPLICATION SETUP ---------------------------------------");

			connectToDefault = false;
			action = (Integer) dbInputAndVal.inputAndValidate(
					"\n(1) Use Existing Database \t\t(2) " + "Create New Database\n" + "Enter your Action (1,2): ",
					"Integer", "input action.");

			if (action.equals(2)) {
				newDbSetup();
				if (action.equals(3)) exitProcess = false;
			} else if (action.equals(1)) {
				existingDbSetup();
				if (action.equals(3)) exitProcess = false ;
				if (action.equals(3) && connectToDefault.equals(true)) exitProcess = true;
			}
		} while (exitProcess == false);
		initDbSetup();
		return this;
	}

	/*
	 * This method prompts user for creation of new database.
	 */
	public AppDatabase newDbSetup() {
		do {
			String url = "jdbc:mysql://localhost/";
			System.out.println("\n\t\t\t\t\tDatabase Creation");
			System.out.println("\nPlease enter your desired database name.");
			databaseName = (String) dbInputAndVal.inputAndValidate("Database Name: ", "String", "Database Name");
			System.out.println("\n\t\t\t\t     Connect to MySQL Server");
			userName = (String) dbInputAndVal.inputAndValidate("User Name: ", "String", "User Name");
			password = (String) dbInputAndVal.inputAndValidate("Password: ", "String", "Password");

			System.out.println("\nPlease validate your database \nDatabase Name: " + databaseName + "\nUsername: " + userName);
			action = (Integer) dbInputAndVal.inputAndValidate("\n(1) Submit \t(2) Reenter Details \t(3) Back \n" 
					+ "Enter your Action (1,2,3): ", "Integer","input action.");
			System.out.println();

			if (action.equals(1)) {
				try (Connection conn = DriverManager.getConnection(url, userName, password);
						Statement stmt = conn.createStatement();) {
					String createDbQuery = "CREATE DATABASE IF NOT EXISTS " + databaseName;
					stmt.executeUpdate(createDbQuery);
					System.out.println("Database created successfully...");
				} catch (SQLException e) {
					System.out.println("Database connection unsuccessful...");
					action = (Integer) dbInputAndVal.inputAndValidate(
							"\n(2) Reenter Details \t(3) Back \n" + "Enter your Action (2,3): ", "Integer",
							"input action.");
					if (action.equals(2)) exitProcess = false;
					else if (action.equals(3)) return this;
				}
				exitProcess = true;
			} else if (action.equals(2)) {
				exitProcess = false;
			}
			else if (action.equals(3)){
				return this;
			}
		} while (exitProcess.equals(false));
		dbUrl = "jdbc:mysql://localhost/" + databaseName;
		return this;
	}

	/*
	 * This method prompts user for credentials of existing database.
	 */
	public AppDatabase existingDbSetup() {
		do {
			System.out.println("\n\t\t\t\t     Connect to MySQL Server");
			databaseName = (String) dbInputAndVal.inputAndValidate("Database Name: ", "String", "Database Name");
			userName = (String) dbInputAndVal.inputAndValidate("User Name: ", "String", "User Name");
			password = (String) dbInputAndVal.inputAndValidate("Password: ", "String", "Password");
			dbUrl = "jdbc:mysql://localhost/" + databaseName;

			System.out.println("\nPlease validate your database url: " + dbUrl + "\nUsername: " + userName);
			action = (Integer) dbInputAndVal.inputAndValidate(
					"\n(1) Submit \t(2) Reenter Details \t(3) Back \n" + "Enter your Action (1,2,3): ", "Integer",
					"input action.");
			System.out.println();

			if (action.equals(1)) {
				exitProcess = true;
				try {
					dbConnect();
					System.out.println("Database Connected Successfully...");
				} catch (Exception e) {
					System.out.println("Failed to connect to database...");
					action = (Integer) dbInputAndVal.inputAndValidate(
							"\n(2) Reenter Details \t(3) Back \n" + "Enter your Action (2,3): ", "Integer",
							"input action.");
					if (action.equals(2)) exitProcess = false;
					else if (action.equals(3)) return this;
				}
			} else if (action.equals(2)) exitProcess = false;
			else if (action.equals(3))	return this;
		} while (exitProcess == false);
		return this;
	}

	/*
	 * Method for initializing database connection
	 */
	public AppDatabase dbConnect() {
		try {
			connection = DriverManager.getConnection(dbUrl, userName, password);
			statement = connection.createStatement();
		} catch (SQLException e) {
			try {
				System.out.println("\nFailed to connect to database...");
				action = (Integer) dbInputAndVal.inputAndValidate(
						"\n(1) Enter existing database again \t(2) Create database \t(3) Connect to default database\n" 
								+ "Enter your Action (1,2,3): ", "Integer",	"input action.");
				if (action.equals(1)) existingDbSetup();
				else if (action.equals(2)) newDbSetup();
				else if (action.equals(3)) {
					System.out.println("\nDefault DB URL : jdbc:mysql://localhost/CAPSTONEPROJ");
					System.out.println("Default DB Username and Password : root\n");
					dbUrl = "jdbc:mysql://localhost/CAPSTONEPROJ";
					userName = "root";
					password = "root";
					connection = DriverManager.getConnection(dbUrl, userName, password);
					statement = connection.createStatement();
					connectToDefault = true;
				}				
			} catch (SQLException ex) {
				System.out.println("Failed to connect to database... Redirecting to Application Setup");
				appSetup();
			}
		}
		return this;
	}

	/*
	 * Method for setting up database tables , procedure , events and triggers.
	 */
	public AppDatabase initDbSetup() {
		dbConnect();
		try {
			// Execute query statement for creation of table CI_VHCLE
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS CI_VHCLE(" 
					+ "	VHCL_PLT_NUM varchar(10),"
					+ "	MAKE varchar(30), " 
					+ "	MODEL varchar(30), " 
					+ "	YEAR int(4), " 
					+ "	TYPE varchar(30),"
					+ "	FUEL_TYPE varchar(30), " 
					+ "	PURCHASE_PRC decimal, " 
					+ "	COLOR varchar(30), "
					+ "	PREM_CHRG varchar(30), "
					+ " POL_NUM int(6) zerofill," 
					+ "	KEY(VHCL_PLT_NUM))");

			// Execute query statement for creation of table CI_POL_HLDR
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS  CI_POL_HLDR("
					+ "	POL_HLDR varchar(60) DEFAULT (CONCAT(NEW.PLHLD_FIRST_NAME, ' ', NEW.PLHLD_LAST_NAME)),"
					+ "	PLHLD_FIRST_NAME varchar(30)," 
					+ "	PLHLD_LAST_NAME varchar(30), "
					+ "	PLHLD_ADDRESS varchar(60)," 
					+ "	BIRTH_DT date," 
					+ "	DRV_LIC_NUM varchar(15),"
					+ "	DRV_LIC_FIRST_ISSDT date," 
					+ " ACCT_NUM int(4) zerofill," 
					+ "	KEY(POL_HLDR))");

			// Execute query statement for creation of table CI_CUS_ACCT
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS  CI_CUS_ACCT(" 
					+ "	ACCT_NUM int(4) zerofill auto_increment,"
					+ "	FIRST_NAME varchar(30), " 
					+ "	LAST_NAME varchar(30), " 
					+ "	ADDRESS varchar(50), " 
					+ "	KEY(ACCT_NUM))");

			// Execute query statement for creation of table CI_POL
			statement.execute("CREATE TABLE IF NOT EXISTS CI_POL(" 
					+ "	POL_NUM int(6) zerofill auto_increment,"
					+ "	EFF_DT date, " 
					+ "	EXP_DT date, " 
					+ "	POL_STAT_FLG int(2), " 
					+ "	POL_HLDR varchar(60), "
					+ "	POL_PREMIUM decimal(18,2),"
					+ "	ACCT_NUM int(4) zerofill ,"
					+ "	KEY(POL_NUM),"
					+ " FOREIGN KEY(ACCT_NUM) REFERENCES CI_CUS_ACCT(ACCT_NUM), "
					+ " FOREIGN KEY(POL_HLDR) REFERENCES CI_POL_HLDR(POL_HLDR))");

			// Execute query statement for creation of table CI_POL
			statement.executeUpdate("CREATE TABLE IF NOT EXISTS CI_CLAIM(	" 
					+ " CLAIM_ID int(5) zerofill auto_increment,"
					+ "	CLAIM_NUM varchar(6),"
					+ "	ACCDNT_DT date," 
					+ "	ACCDNT_ADD varchar(60)," 
					+ "	ACCDNT_DESC varchar(60), "
					+ "	VHCLE_DMG_DESC varchar(60)," 
					+ "	ESTM_RPR_COST decimal," 
					+ "	ACCT_NUM int(4) zerofill,"
					+ " POL_NUM int(6) zerofill, 	" 
					+ "	KEY(CLAIM_NUM),"
					+ " PRIMARY KEY(CLAIM_ID), "
					+ "	FOREIGN KEY(ACCT_NUM) REFERENCES CI_CUS_ACCT(ACCT_NUM),"
					+ "	FOREIGN KEY(POL_NUM) REFERENCES CI_POL(POL_NUM))");

			// Execute query statement for dropping of after_policy_insert trigger if trigger is existing
			statement.executeUpdate("DROP TRIGGER IF EXISTS before_policy_insert");

			/*
			 * Execute query statement for creation of after_policy_insert trigger to update
			 * the POL_STAT_FLG column values to pending active(10) , active(20) ,
			 * expired(30) and cancelled(40) in CI_POL table
			 */
			statement.executeUpdate("CREATE TRIGGER before_policy_insert	" 
					+ " BEFORE INSERT ON CI_POL FOR EACH ROW "
					+ "	SET NEW.POL_STAT_FLG = " 
					+ " CASE "
					+ "	  WHEN (NEW.EFF_DT > CURRENT_DATE AND NEW.EXP_DT > CURRENT_DATE) THEN '10' "
					+ "   WHEN NEW.EFF_DT = CURRENT_DATE THEN '20' " 
					+ " END ");

			// Execute query statement for dropping of update_policy_status procedure if procedure is existing
			statement.executeUpdate("DROP PROCEDURE IF EXISTS update_policy_status"); 

			// Execute query statement for creation of update_policy_status procedure
			statement.executeUpdate("CREATE PROCEDURE update_policy_status()"
					+ " UPDATE CI_POL SET POL_STAT_FLG =  "
					+ " CASE "
					+ " WHEN EFF_DT > CURRENT_DATE AND EXP_DT > CURRENT_DATE THEN '10' "
					+ " WHEN EFF_DT <= CURRENT_DATE() AND EXP_DT > CURRENT_DATE THEN '20' "
					+ " WHEN EXP_DT <= CURRENT_DATE() THEN '30' "
					+ " END "
					+ " WHERE POL_STAT_FLG != 40 OR POL_STAT_FLG IS NULL"); 

			/*Execute query statement for creation of call_update_pol_status_procd event which calls 
			the update_policy_status procedure scheduled every day*/
			statement.executeUpdate("CREATE EVENT IF NOT EXISTS call_update_pol_status_procd "
					+ " ON SCHEDULE "
					+ "    EVERY 1 DAY" 
					+ "    STARTS (TIMESTAMP(CURRENT_DATE)) "
					+ "  DO "
					+ "   CALL update_policy_status()"); 

			// Execute query statement for setting the event_scheduler to ON
			statement.executeUpdate("SET GLOBAL event_scheduler = ON"); 

		} catch (SQLException e) {
			System.out.println("Initialize database setup is not successful...");
			System.out.println(e.getMessage());
		}
		return this;
	}
	
	/*
	 * This method close the connection.
	 */
	public AppDatabase closeConnection() {
		dbConnect();
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println(e);
		}
		return this;
	}

	/*
	 * Method that returns List of String of retrieved table's column names.
	 */
	public List<String> columnNamesList(String tableName) {
		dbConnect();
		List<String> columnName = new ArrayList<>();
		try {
			String columnNameQuery = "SELECT * FROM " + tableName;
			ResultSet colNameRs = statement.executeQuery(columnNameQuery);
			ResultSetMetaData metadata = colNameRs.getMetaData();
			int colCount = metadata.getColumnCount();
			for (int count = 1; count <= colCount; count++) {
				columnName.add(metadata.getColumnName(count));
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		return columnName;
	}
	
	/*
	 * Method for returning integer value of recent generated keys
	 */
	public Integer getGeneratedKeys() {
		Integer generatedKeys = null;
		dbConnect();
		try {
			results = prepStatement.getGeneratedKeys();
			if(results.next()) {
				generatedKeys =  results.getInt(1);
			}
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();	
		}
		return generatedKeys;
	}

	/*
	 * Method for adding new customer account in CI_CUS_ACCT table in database.
	 */
	public void addNewCustomerAccount(String firstName, String lastName, String address) {
		dbConnect();
		try {
			String addCusAccQuery = "INSERT INTO CI_CUS_ACCT (FIRST_NAME, LAST_NAME , ADDRESS) VALUES (?, ?, ?)";
			prepStatement = connection.prepareStatement(addCusAccQuery , Statement.RETURN_GENERATED_KEYS);
			prepStatement.setString(1, firstName);
			prepStatement.setString(2, lastName);
			prepStatement.setString(3, address);
			prepStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	/*
	 * Method that returns true if accountNum exists in CI_CUS_ACCT table in database.
	 */
	public Boolean isCusAccNumExists(Integer accountNumber) {
		dbConnect();
		Boolean isCusAccNumExists = true;
		try {
			String cusAccLookUpQuery = "SELECT * FROM CI_CUS_ACCT WHERE ACCT_NUM = ? ";
			prepStatement = connection.prepareStatement(cusAccLookUpQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			prepStatement.setInt(1, accountNumber);
			results = prepStatement.executeQuery();
			if (!results.next()) {
				isCusAccNumExists = false;
			}
		} catch (SQLException e) {
			System.out.println(e);
		}

		return isCusAccNumExists;
	}

	/*
	 * Method that returns true if account name exists in CI_CUS_ACCT table in database.
	 */
	public Boolean isCusAccNameExists(String firstName, String lastName) {
		dbConnect();
		Boolean isCusAccNameExists = false;
		try {
			String cusAccLookUpQuery = "SELECT * FROM CI_CUS_ACCT WHERE FIRST_NAME = ? AND LAST_NAME = ? ";
			prepStatement = connection.prepareStatement(cusAccLookUpQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			prepStatement.setString(1, firstName);
			prepStatement.setString(2, lastName);
			results = prepStatement.executeQuery();
			if(results.next()) {
				isCusAccNameExists = true;
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		return isCusAccNameExists;
	}
	
	/*
	 * Method that returns true if account name exists in CI_CUS_ACCT and in CI_POL_HLDR tables in database.
	 */
	public Boolean isCusAccPolHldrExists(String firstName, String lastName) {
		dbConnect();
		Boolean isCusAccPolHldrExists = false;
		try {
			String cusAccLookUpQuery = "SELECT * FROM CI_CUS_ACCT CA, CI_POL_HLDR PH WHERE FIRST_NAME = ? "
					+ "AND LAST_NAME = ? AND CA.ACCT_NUM = PH.ACCT_NUM";
			prepStatement = connection.prepareStatement(cusAccLookUpQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			prepStatement.setString(1, firstName);
			prepStatement.setString(2, lastName);
			results = prepStatement.executeQuery();
			if(results.next()) {
				isCusAccPolHldrExists = true;
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		return isCusAccPolHldrExists;
	}
	
	/*
	 * Method that returns true if account name exists in CI_CUS_ACCT and in CI_POL tables in database.
	 */
	public Boolean isCusAccPolExists(String firstName, String lastName) {
		dbConnect();
		Boolean isCusAccPolHldrExists = false;
		try {
			String cusAccLookUpQuery = "SELECT * FROM CI_CUS_ACCT CA, CI_POL PL WHERE FIRST_NAME = ? "
					+ "AND LAST_NAME = ? AND CA.ACCT_NUM = PL.ACCT_NUM";
			prepStatement = connection.prepareStatement(cusAccLookUpQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			prepStatement.setString(1, firstName);
			prepStatement.setString(2, lastName);
			results = prepStatement.executeQuery();
			if(results.next()) {
				isCusAccPolHldrExists = true;
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		return isCusAccPolHldrExists;
	}
	
	/*
	 * Method that displays customer account details
	 */
	public void cusAccInfoDisplay() {
		dbConnect();
		try {
			List<String> dbColNameList = columnNamesList("CI_CUS_ACCT");
			results.beforeFirst();
			System.out.println(">Customer Account Information");
			if(results.next()) {
				System.out.println("Account Number: "
						+ dbInputAndVal.numberFormatter(4, results.getInt(dbColNameList.get(0))));
				System.out.print("Account Name: " + results.getString(dbColNameList.get(1)) + " "
						+ results.getString(dbColNameList.get(2)) + "\n");
				System.out.println("Address: " + results.getString(dbColNameList.get(3)));				
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/*
	 * Method that displays customer account policy holders details
	 */
	public void cusAccInfoPolHldrDisplay() {
		dbConnect();
		try {
			List<String> dbColNameList = columnNamesList("CI_POL_HLDR");
			results.beforeFirst();
			System.out.println("\n>Policy Holder Information");
			System.out.printf("%-30s %-15s %-30s %-20s\n","Name","Birthday","Address", "Driver's License No.");		
			while(results.next()) {
				System.out.printf("%-30s %-15s %-30s %-20s\n",results.getString(dbColNameList.get(0)), results.getString(dbColNameList.get(4)),
						results.getString(dbColNameList.get(3)), results.getString(dbColNameList.get(5)));				
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/*
	 * Method that displays customer account policy details
	 */
	public void cusAccInfoPolDisplay() {
		dbConnect();
		try {
			List<String> dbColNameList = columnNamesList("CI_POL");
			System.out.println("\n>Policy Information");
			System.out.printf("%-18s  %-30s %-10s %-20s\n" , "Policy Number" , "Policy Holder" , "Premium" , "Status");		
			results.beforeFirst();
			while(results.next()) {
				System.out.printf("%-18s  %-30s %-10s %-20s\n" , dbInputAndVal.numberFormatter(6, results.getInt(dbColNameList.get(0))) , 
						results.getString(dbColNameList.get(4)), results.getString(dbColNameList.get(5)), 
						new Policy(this).statusDesc(results.getInt(dbColNameList.get(3))));
				System.out.println();
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Method for adding new Policy in CI_POL table in database.
	 */
	public void addNewPolicy(LocalDate policyCoverageEffDt, LocalDate policyCoverageExpDt, String polHoldr,
			BigDecimal premium, Integer accountNumber) {
		dbConnect();
		try {
			String addNewPolicyQuery = "INSERT INTO CI_POL (EFF_DT, EXP_DT, POL_HLDR, POL_PREMIUM , ACCT_NUM"
					+ ") VALUES (?, ?, ?, ?, ?)";
			prepStatement = connection.prepareStatement(addNewPolicyQuery, Statement.RETURN_GENERATED_KEYS);
			prepStatement.setDate(1, Date.valueOf(policyCoverageEffDt));
			prepStatement.setDate(2, Date.valueOf(policyCoverageExpDt));
			prepStatement.setString(3, polHoldr);
			prepStatement.setBigDecimal(4, premium);
			prepStatement.setInt(5, accountNumber);
			prepStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}
	
	/*
	 * Method to cancel a Policy by updating the POL_STAT_FLG column value to 40(cancelled) in CI_POL table in database.
	 */
	public void cancelPolicy(Integer policyNumber) {
		dbConnect();
		try {
			String cancelPolicyQuery = "UPDATE CI_POL SET POL_STAT_FLG = 40 ,EXP_DT = CURRENT_DATE() WHERE POL_NUM = ?";
			prepStatement = connection.prepareStatement(cancelPolicyQuery);
			prepStatement.setInt(1, policyNumber);
			prepStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	/*
	 * Method that returns true if account Num exists in CI_POL table in database.
	 */
	public Boolean isCusAccPolExists(Integer accountNumber) {
		dbConnect();
		Boolean isCusAccNumExists = false;
		try {
			String cusAccPolLookUpQuery = "SELECT * FROM CI_POL WHERE ACCT_NUM = ? ";
			prepStatement = connection.prepareStatement(cusAccPolLookUpQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			prepStatement.setInt(1, accountNumber);
			results = prepStatement.executeQuery();
			if (results.next()) {
				isCusAccNumExists = true;
			}
		} catch (SQLException e) {
			System.out.println(e);
		}

		return isCusAccNumExists;
	}
	
	/*
	 * Method that returns true if policy number exists in CI_POL table in database.
	 */
	public Boolean isPolExists(Integer policyNumber) {
		dbConnect();
		Boolean isPolExists = false;
		try {
			String polLookUpQuery = "SELECT * FROM CI_POL WHERE POL_NUM = ?";
			prepStatement = connection.prepareStatement(polLookUpQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			prepStatement.setInt(1, policyNumber);
			results = prepStatement.executeQuery();
			if (results.next())
				isPolExists = true;
		} catch (SQLException e) {
			System.out.println(e);
		}
		return isPolExists;
	}
	
	/*
	 * Method that displays policy details
	 */
	public void polInfoDisplay() {
		dbConnect();
		try {
			List<String> dbColNameList = columnNamesList("CI_POL");
			results.beforeFirst();
			if (results.next()) {
				System.out.printf("%-55s %-30s\n", "Policy Number: " + dbInputAndVal.numberFormatter(6, results.getInt(dbColNameList.get(0))),
						"Account Number: " + dbInputAndVal.numberFormatter(4, results.getInt(dbColNameList.get(6))));
				System.out.printf("%-55s %-30s\n", "Effective Date: " + results.getString(dbColNameList.get(1)),
						"Expiration Date: " + results.getString(dbColNameList.get(2)));
				System.out.printf("%-55s %-30s\n", "Policy Holder: " + results.getString(dbColNameList.get(4)),
						"Policy Premium: $" + results.getString(dbColNameList.get(5)));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Method for adding new Policy Holder in CI_POL_HLDR table in database.
	 */
	public void addPolicyHolder(String polHldrFirstName, String polHldrLastName, String polHldrAddress,
			LocalDate birthDate, String driversLicenseNum, LocalDate drvLicFirstIssdDt, Integer accountNumber) {
		dbConnect();
		try {
			String addPolHoldrQuery = "INSERT INTO CI_POL_HLDR ( PLHLD_FIRST_NAME , PLHLD_LAST_NAME , PLHLD_ADDRESS, "
					+ "BIRTH_DT, DRV_LIC_NUM, DRV_LIC_FIRST_ISSDT , ACCT_NUM) VALUES (?, ?, ?, ?, ?, ?, ?)";
			prepStatement = connection.prepareStatement(addPolHoldrQuery);
			prepStatement.setString(1, polHldrFirstName);
			prepStatement.setString(2, polHldrLastName);
			prepStatement.setString(3, polHldrAddress);
			prepStatement.setDate(4, Date.valueOf(birthDate));
			prepStatement.setString(5, driversLicenseNum);
			prepStatement.setDate(6, Date.valueOf(drvLicFirstIssdDt));
			prepStatement.setInt(7, accountNumber);
			prepStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}
	
	/*
	 * Method that returns true if account Num exists in CI_POL table in database.
	 */
	public Boolean isCusAccPolHldrExists(Integer accountNumber) {
		dbConnect();
		Boolean isCusAccPolHldrExists = false;
		try {
			String cusAccPolLookUpQuery = "SELECT * FROM CI_POL_HLDR WHERE ACCT_NUM = ? ";
			prepStatement = connection.prepareStatement(cusAccPolLookUpQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			prepStatement.setInt(1, accountNumber);
			results = prepStatement.executeQuery();
			if (results.next()) {
				isCusAccPolHldrExists = true;
			}
		} catch (SQLException e) {
			System.out.println(e);
		}

		return isCusAccPolHldrExists;
	}
	
	/*
	 * Method that displays customer account policy holder 
	 */
	public void cusAccPolHldrDisplay() {
		dbConnect();
		try {
			System.out.printf("\n%-5s \t\t%-30s\n" , "" , "Policy Holder");	
			results.beforeFirst();
			while(results.next()) {
				System.out.printf("%-5s \t\t%-30s\n" , "(" 
						+ results.getRow() + ")" , results.getString("POL_HLDR") );
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/*
	 * Method for adding new Vehicle in CI_VHCLE table in database.
	 */
	public void addVehicle(String plateNum, String make, String model, Integer year, String type, String fuelType,
			BigDecimal purchasePrice, String color, BigDecimal premiumCharged, Integer policyNumber) {
		dbConnect();
		try {
			// addVehicleDetails
			String addVehicleQuery = "INSERT INTO CI_VHCLE (VHCL_PLT_NUM , MAKE, MODEL, YEAR, TYPE, FUEL_TYPE, "
					+ "PURCHASE_PRC, COLOR , PREM_CHRG , POL_NUM) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			prepStatement = connection.prepareStatement(addVehicleQuery);
			prepStatement.setString(1, plateNum);
			prepStatement.setString(2, make);
			prepStatement.setString(3, model);
			prepStatement.setInt(4, year);
			prepStatement.setString(5, type);
			prepStatement.setString(6, fuelType);
			prepStatement.setBigDecimal(7, purchasePrice);
			prepStatement.setString(8, color);
			prepStatement.setBigDecimal(9, premiumCharged);
			prepStatement.setInt(10, policyNumber);
			prepStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}
	
	/*
	 * Method that returns true if policy number exists in CI_VHCLE table in database.
	 */
	public Boolean isPolVhclExists(Integer policyNumber) {
		dbConnect();
		Boolean isPolVhclExists = false;
		try {
			String polLookUpQuery = "SELECT * FROM CI_VHCLE WHERE POL_NUM = ?";
			prepStatement = connection.prepareStatement(polLookUpQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			prepStatement.setInt(1, policyNumber);
			results = prepStatement.executeQuery();
			if (results.next())
				isPolVhclExists = true;
		} catch (SQLException e) {
			System.out.println(e);
		}
		return isPolVhclExists;
	}

	/*
	 * Method that displays policy vehicle details
	 */
	public void polVhcleDisplay() {
		dbConnect();
		try {
			List<String> dbColNameList = columnNamesList("CI_VHCLE");
			System.out.printf("\n%-25s %-30s %-15s\n" , "Vehicle Plate Number" ,"Vehicle Description", "Premium");
			results.beforeFirst();
			while(results.next()) {
				System.out.printf("%-25s %-30s %-15s\n" , results.getString(dbColNameList.get(0)) ,
						results.getString(dbColNameList.get(1)) + " " + results.getString(dbColNameList.get(2)) + 
						" " + results.getString(dbColNameList.get(3)), "$" + results.getString(dbColNameList.get(8)));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Method for adding new Claim in CI_CLAIM table in database.
	 */
	public void addClaim(LocalDate accidentDate, String accidentAdd, String accidentDesc, String vehicleDmgDesc,
			BigDecimal estimatedRepairCost, Integer accountNumber, Integer policyNumber) {
		dbConnect();
		try {
			String addClaimQuery = "INSERT INTO CI_CLAIM (ACCDNT_DT , ACCDNT_ADD, ACCDNT_DESC, VHCLE_DMG_DESC, ESTM_RPR_COST, ACCT_NUM, "
					+ "POL_NUM) VALUES (?, ?, ?, ?, ?, ?, ?)";
			prepStatement = connection.prepareStatement(addClaimQuery, Statement.RETURN_GENERATED_KEYS);
			prepStatement.setDate(1, Date.valueOf(accidentDate));
			prepStatement.setString(2, accidentAdd);
			prepStatement.setString(3, accidentDesc);
			prepStatement.setString(4, vehicleDmgDesc);
			prepStatement.setBigDecimal(5, estimatedRepairCost);
			prepStatement.setInt(6, accountNumber);
			prepStatement.setInt(7, policyNumber);
			prepStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	/*
	 * Method for adding new Claim in CI_CLAIM table in database.
	 */
	public AppDatabase addClaimNum(Integer claimId) {
		dbConnect();
		try {
			String addClaimQuery = "UPDATE CI_CLAIM SET CLAIM_NUM =  CONCAT('C', ?) WHERE CLAIM_ID = ?";
			prepStatement = connection.prepareStatement(addClaimQuery);
			prepStatement.setString(1, dbInputAndVal.numberFormatter(5, claimId));
			prepStatement.setInt(2, claimId);
			prepStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}

		return this;
	}
	
	/*
	 * Method that returns true if claim number exists in CI_CLAIM table in database.
	 */
	public Boolean isClaimExists(String claimNumber) {
		dbConnect();
		Boolean isClaimExists = false;
		try {
			String claimLookUpQuery = "SELECT * FROM CI_CLAIM WHERE CLAIM_NUM = ?";
			prepStatement = connection.prepareStatement(claimLookUpQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			prepStatement.setString(1, claimNumber);
			results = prepStatement.executeQuery();
			if (results.next())
				isClaimExists = true;
		} catch (SQLException e) {
			System.out.println(e);
		}
		return isClaimExists;
	}

	/*
	 * Method that displays claim details
	 */
	public void claimInfoDisplay() {
		dbConnect();
		try {
			List<String> dbColNameList = columnNamesList("CI_CLAIM");
			results.beforeFirst();
			if (results.next()) {
				System.out.printf("%-55s %-30s\n", "Claim Number: " + results.getString(dbColNameList.get(1)),
						"Policy Number: " + dbInputAndVal.numberFormatter(6, results.getInt(dbColNameList.get(8))));
				System.out.printf("%-55s %-30s\n", "Accident Date: " + results.getString(dbColNameList.get(2)),
						"Account Number: " + dbInputAndVal.numberFormatter(6, results.getInt(dbColNameList.get(7))));
				System.out.println("Accident Address: " + results.getString(dbColNameList.get(3)));
				System.out.println("Accident Description: " + results.getString(dbColNameList.get(4)));
				System.out.println("Damaged Vehicle Description: " + results.getString(dbColNameList.get(5)));
				System.out.println("Estimated Repair Cost: $" + results.getString(dbColNameList.get(6)));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Method that returns array of string of Account Name
	 */
	public String[] getAcctName() {
		dbConnect();
		String[] acctName = new String[2];
		try {
			results.beforeFirst();
			if (results.next())
				acctName[0] = results.getString("FIRST_NAME");
			acctName[1] = results.getString("LAST_NAME");
		} catch (SQLException e) {
			System.out.println(e);
		}
		return acctName;
	}

	

	/*
	 * Method that returns integer value of POL_STAT_FLG of policy Number
	 */
	public LocalDate getPolEffectiveDate() {
		dbConnect();
		LocalDate polEffectiveDate = null;
		try {
			results.beforeFirst();
			if (results.next())
				polEffectiveDate = Instant.ofEpochMilli((results.getDate("EFF_DT")).getTime())
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
		} catch (SQLException e) {
			System.out.println(e);
		}
		return polEffectiveDate;
	}

	/*
	 * Method that returns integer value of POL_STAT_FLG of policy Number
	 */
	public LocalDate getPolExpirationDate() {
		dbConnect();
		LocalDate polExpirationDate = null;
		try {
			results.beforeFirst();
			if (results.next())
				polExpirationDate = Instant.ofEpochMilli((results.getDate("EXP_DT")).getTime())
				.atZone(ZoneId.systemDefault())
				.toLocalDate();
		} catch (SQLException e) {
			System.out.println(e);
		}
		return polExpirationDate;
	}

	/*
	 * Method that returns integer value of POL_STAT_FLG of policy Number
	 */
	public Integer getPolStatFlg() {
		dbConnect();
		Integer polStatus = null;
		try {
			results.beforeFirst();
			if (results.next())
				polStatus = results.getInt("POL_STAT_FLG");
		} catch (SQLException e) {
			System.out.println(e);
		}
		return polStatus;
	}

	/*
	 * Method that returns integer value of ACCT_NUM of policy Number
	 */
	public Integer getPolAcctNum() {
		dbConnect();
		Integer acctNum = null;
		try {
			results.beforeFirst();
			if (results.next())
				acctNum = results.getInt("ACCT_NUM");
		} catch (SQLException e) {
			System.out.println(e);
		}
		return acctNum;
	}

	/*
	 * Method that returns resultset number of row returned
	 */
	public Integer getNumOfRows() {
		dbConnect();
		Integer numOfRows = 0;
		try {
			results.beforeFirst();
			while(results.next()) {
				if(results.isLast()) {
					numOfRows = results.getRow();
				}
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		return numOfRows;
	}

	/*
	 * Method that returns resultset number of row returned
	 */
	public String getExistingPolHldrName(Integer polHldrNum) {
		dbConnect();
		String polHolderName = null;
		try {
			results.beforeFirst();
			if (results.absolute(polHldrNum)) {
				polHolderName = results.getString("POL_HLDR");
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		return polHolderName;
	}

	/*
	 * Method that returns resultset number of row returned
	 */
	public LocalDate getPolHldrLicFrstIssdDt(Integer polHldrNum) {
		dbConnect();
		LocalDate drvLicFirstIssdDt = null;
		try {
			results.beforeFirst();
			if (results.absolute(polHldrNum)) {
				drvLicFirstIssdDt = Instant.ofEpochMilli((results.getDate("DRV_LIC_FIRST_ISSDT")).getTime())
						.atZone(ZoneId.systemDefault())
						.toLocalDate();
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		return drvLicFirstIssdDt;
	}


	/*
	 * Method that returns true if vehicle plate number is currently not enrolled to a pending active or an active policy else returns false.
	 */
	public Boolean isValidVhcleToUse(String plateNum) {
		dbConnect();
		Boolean isValidVhcleToUse = true;
		try {
			String cusAccLookUpQuery = "SELECT * FROM CI_VHCLE VHL , CI_POL PL WHERE VHL.POL_NUM = PL.POL_NUM "
					+ "AND POL_STAT_FLG IN ('10','20') AND VHL.VHCL_PLT_NUM = ?";
			prepStatement = connection.prepareStatement(cusAccLookUpQuery, ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			prepStatement.setString(1, plateNum);
			results = prepStatement.executeQuery();
			if(results.next()) {
				isValidVhcleToUse = false;
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
		return isValidVhcleToUse;
	}
}
