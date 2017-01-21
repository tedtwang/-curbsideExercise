package com.curbside.curbSideJobs;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class curbsideJobs {
	private static WebDriver webDriver;
	private static String curbsideJobsURL = "https://www.shopcurbside.com/jobs/ ";
	private static String curbsideSmartrecruitersURL = "https://careers.smartrecruiters.com/Curbside1/";
	private HashMap<String, Integer> locationNumJobs = new HashMap<>();
	private HashMap<String, String> cityLocRelation = new HashMap<>();

	
	@Before
	public void setUp() throws Exception {
		webDriver = new FirefoxDriver();
		webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		webDriver.get(curbsideJobsURL);
		By locations = By.xpath(".//*[@class='location']");
		List<WebElement> locationsFound = webDriver.findElements(locations);
		//get count of jobs
		for (WebElement location : locationsFound) {
			String loc = location.getText();
			String[] cityState = loc.split(", ");
			String city = cityState[0], state = cityState[1];

			//city-state relation
			if (!cityLocRelation.containsKey(city)) {
				cityLocRelation.put(city, state);
			}
			//city-# of jobs relation
			if (locationNumJobs.containsKey(city)) {
				locationNumJobs.put(city, locationNumJobs.get(city)+1);
			}
			else 
				locationNumJobs.put(city, 1);
		}
		//print it out
		for (String key : locationNumJobs.keySet()) {
			System.out.println(key+", "+cityLocRelation.get(key)+": "+locationNumJobs.get(key));
		}
	}

	@After
	public void tearDown() throws Exception {
		webDriver.quit();
	}

	@Test
	public void test() {
		webDriver.get(curbsideSmartrecruitersURL);
		By cityLocations = By.xpath(".//h2[@class='opening-title title display--inline-block']");
		
		List<WebElement> locationsFound = webDriver.findElements(cityLocations);
		for (WebElement location : locationsFound) {
			String loc = location.getText();
			String[] cityState = loc.split(", ");
			String city = cityState[0], state = cityState[1];

			By numJobs = By.xpath(".//h2[@class='opening-title title display--inline-block' and text()='"+loc+"']/../following-sibling::*[1]/*");
			int numJobsAtLocSR = Integer.valueOf((webDriver.findElement(numJobs).getText().split(" "))[0]);
			int numJobsAtLocCurbside = 0;
			
			//verifications
			try {
				numJobsAtLocCurbside = locationNumJobs.get(city);
			} catch (NullPointerException e) {
				fail(loc+" was not found on the shopcurbside.com/jobs site");
			}
			
			assertTrue("Number of jobs at "+city+" don't match!", numJobsAtLocSR==numJobsAtLocCurbside);
			System.out.println("Number of jobs at "+city+" match!");
			assertTrue("The states for "+city+" don't match! Found "+state+" and "+cityLocRelation.get(city), state.equals(cityLocRelation.get(city)));
			System.out.println("The states for "+city+" match! Found: "+state);

		}
	}

}
