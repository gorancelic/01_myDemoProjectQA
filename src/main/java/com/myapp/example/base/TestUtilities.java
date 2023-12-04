package com.myapp.example.base;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.DataProvider;

public class TestUtilities extends BaseTestGui {

	private Duration defaultTimeout = Duration.ofSeconds(30);
	// STATIC SLEEP 
	protected void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public WebElement waitForElementToBeVisible(WebElement element) {
		WebDriverWait wait = new WebDriverWait(driver, defaultTimeout);
		return wait.until(ExpectedConditions.visibilityOf(element));
	}
	/**
	 * Retrieves the URL of the current page.
	 * This method is used to obtain the URL that the test is currently interacting with or intends to interact with.
	 *
	 * @return The URL of the current page as a String. Returns null if the URL has not been initialized.
	 *
	 * Description:
	 * - The method checks if 'pageUrl' is initialized and returns it.
	 * - If 'pageUrl' is not initialized, it logs an informational message indicating that the URL is not set.
	 */
	public  String getUrl() {
		if (pageUrl != null) {
			// Safe to use 'pageUrl'
		} else {
			log = LogManager.getLogger(testName);
			log.info("Page URL is not initialized.");
		}
		return pageUrl;
	}

	/**
	 * Captures and saves a screenshot of the current state of the web page.
	 * This method is used for taking screenshots during test execution, typically for documentation or debugging purposes.
	 *
	 * @param fileName The name to be assigned to the saved screenshot file.
	 *
	 * Description:
	 * - The method captures a screenshot using the WebDriver's 'getScreenshotAs' method and saves it to a specified location.
	 * - The file path for the screenshot includes the current date, test suite name, test name, test method name, and a timestamp, ensuring uniqueness and easy identification.
	 * - Screenshots are saved in the 'screenshots' directory under the 'test-output' folder.
	 */
	protected void takeScreenshot(String fileName) {
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String path = System.getProperty("user.dir")
				+ File.separator + "target"
				+ File.separator + "test-output" 
				+ File.separator + "screenshots"
				+ File.separator + getTodaysDate() 
				+ File.separator + testSuiteName 
				+ File.separator + testName
				+ File.separator + testMethodName 
				+ File.separator + getSystemTime() 
				+ " " + fileName + ".png";
		try {
			FileUtils.copyFile(scrFile, new File(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/** Today date in yyyyMMdd format */
	private static String getTodaysDate() {
		return (new SimpleDateFormat("yyyyMMdd").format(new Date()));
	}

	/** Current time in HHmmssSSS */
	private String getSystemTime() {
		return (new SimpleDateFormat("HHmmssSSS").format(new Date()));
	}

	/** Get logs from browser console */
	protected List<LogEntry> getBrowserLogs() {
		LogEntries log = driver.manage().logs().get("browser");
		List<LogEntry> logList = log.getAll();
		return logList;
	}
}
