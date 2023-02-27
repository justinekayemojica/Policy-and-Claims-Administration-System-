package controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

/**
* Java Course 4 Module 3
*
* @author Justine Kaye Mojica
* @Description: A Java class that process the policy rating such as the premium calculation. 
* Created Date: 6/27/2022
* Modified Date: 07/04/2022
* @Modified By: Justine Kaye Mojica
*/

public class RatingEngine {

	// WorkinG Variables
	private BigDecimal p;
	private BigDecimal vp;
	private BigDecimal vpf;
	private BigDecimal dlx;
	private LocalDate drvLicFirstIssdDt;
	private Integer year;
	private Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
	
	/*
	 * A class constructor that initialize the value of Vehicle Purchase Price(vp) , Driver
	 * License First Issued Date and Year.
	 */
	public RatingEngine(BigDecimal vp, LocalDate drvLicFirstIssdDt, Integer year) {
		super();
		this.vp = vp;
		this.drvLicFirstIssdDt = drvLicFirstIssdDt;
		this.year = year;
	}

	/*
	 * Method that calculates the premium give formula of P (premium) = (vp x vpf) +
	 * ((vp/100)/dlx). Where P = calculated premium , vp = vehicle purchase price
	 * vpf = vehicle price factor , and dlx = num of years since driver license was
	 * first issued
	 */
	public BigDecimal premium() {
		dlx = dlx();
		vpf = vpf();
		BigDecimal vpVpf = vp.multiply(vpf);
		BigDecimal vpDlx = (vp.divide(new BigDecimal(100))).divide(dlx, 2, RoundingMode.HALF_UP);
		p = vpVpf.add(vpDlx);
		return p.setScale(2, RoundingMode.HALF_UP);
	}

	/*
	 * Method that caculates the Vehicle Price factor
	 */
	public BigDecimal vpf() {
		Integer vehicleYearsOld = currentYear - year;
		if (vehicleYearsOld < 1)
			vpf = new BigDecimal(0.01);
		else if (vehicleYearsOld < 3)
			vpf = new BigDecimal(0.008);
		else if (vehicleYearsOld < 5)
			vpf = new BigDecimal(0.007);
		else if (vehicleYearsOld < 10)
			vpf = new BigDecimal(0.006);
		else if (vehicleYearsOld < 15)
			vpf = new BigDecimal(0.004);
		else if (vehicleYearsOld < 20)
			vpf = new BigDecimal(0.002);
		else if (vehicleYearsOld < 40)
			vpf = new BigDecimal(0.001);
		return vpf;
	}

	/*
	 * This method caculates the num of years since driver license was first issued. 
	 * Calculated by getting the age of the license in Days  and then converted
	 * to years to get the exact number of years in a decimal format with two decimal places
	 * if the given issued date is in present year.
	 */
	public BigDecimal dlx() {
		long ageInDays = ChronoUnit.DAYS.between(drvLicFirstIssdDt, LocalDate.now());
		BigDecimal yearDivisor = new BigDecimal(365);
		if(drvLicFirstIssdDt.getYear() == LocalDate.now().getYear()) {
			dlx = new BigDecimal(ageInDays).divide(yearDivisor, 2 , RoundingMode.HALF_UP);
		} else {
			dlx = new BigDecimal(ageInDays).divide(yearDivisor, 0 , RoundingMode.FLOOR);
		}
		return dlx;
	}

}