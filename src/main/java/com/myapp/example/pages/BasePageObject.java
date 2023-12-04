package com.myapp.example.pages;

import java.util.List;
import java.time.Duration;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class BasePageObject {

	protected WebDriver driver;
	protected Logger log;


	public BasePageObject(WebDriver driver, Logger log) {
		this.driver = driver;
		this.log = log;
	}

	/** Open page with given URL */
	protected void openUrl(String url) {
		driver.get(url);
	}

	/** Find element using given locator */
	protected WebElement find(By locator) {
		return driver.findElement(locator);
	}

	/** Find all elements using given locator */
	protected List<WebElement> findAll(By locator) {
		waitForVisibilityOf(locator, Duration.ofSeconds(5));
		return driver.findElements(locator);
	}

	/** Click on element with given locator when its visible */
	protected void click(By locator) {
		try {
			waitForVisibilityOf(locator, Duration.ofSeconds(5));
			find(locator).click();
			log.info("Clicked on element: " + locator.toString());
		} catch (Exception e) {
			log.error("Error clicking on element: " + locator.toString(), e);
			throw e;
		}
	}

	/** Type given text into element with given locator */
	protected void type(String text, By locator) {
		waitForVisibilityOf(locator, Duration.ofSeconds(5));
		find(locator).sendKeys(text);
	}

	/** Get URL of current page from browser */
	public String getCurrentUrl() {
		return driver.getCurrentUrl();
	}

	/** Get title of current page */
	public String getCurrentPageTitle() {
		return driver.getTitle();
	}

	/** Get source of current page */
	public String getCurrentPageSource() {
		return driver.getPageSource();
	}

	/**
	 * Wait for specific ExpectedCondition for the given amount of time in seconds
	 */
	private void waitFor(ExpectedCondition<WebElement> condition, Duration timeOutInSeconds) {
		timeOutInSeconds = timeOutInSeconds != null ? timeOutInSeconds : Duration.ofSeconds(30);
		WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
		wait.until(condition);
	}

	/**
	 * Wait for given number of seconds for element with given locator to be visible
	 * on the page
	 */
	protected void waitForVisibilityOf(By locator, Duration... timeOutInSeconds) {
		int attempts = 0;
		while (attempts < 2) {
			try {
				waitFor(ExpectedConditions.visibilityOfElementLocated(locator),
						(timeOutInSeconds.length > 0 ? timeOutInSeconds[0] : null));
				break;
			} catch (StaleElementReferenceException e) {
			}
			attempts++;
		}
	}

	/** Switch to iFrame using it's locator */
	protected void switchToFrame(By frameLocator) {
		driver.switchTo().frame(find(frameLocator));
	}

	/** Press Key on locator */
	protected void pressKey(By locator, Keys key) {
		find(locator).sendKeys(key);
	}

	/** Press Key using Actions class */
	public void pressKeyWithActions(Keys key) {
		log.info("Pressing " + key.name() + " using Actions class");
		Actions action = new Actions(driver);
		action.sendKeys(key).build().perform();
	}

	/** Perform scroll to the bottom */
	public void scrollToBottom() {
		log.info("Scrolling to the bottom of the page");
		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight)");
	}

	/** Perform scroll to the bottom with dynamic load while scrolling. Static wait of 400ms is needed for scroll iteration */
	public void scrollToBottomForDynamicLoad() {
		log.info("Scrolling to the bottom of the page");

		JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
		long lastHeight = (long) jsExecutor.executeScript("return document.body.scrollHeight");

		while (true) {
			// Scroll down to the bottom of the page
			jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");

			// Wait for the page to load
			try {
				Thread.sleep(400); // Delay can be adjusted if needed
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// Calculate the new scroll height and compare it with the last scroll height
			long newHeight = (long) jsExecutor.executeScript("return document.body.scrollHeight");
			if (newHeight == lastHeight) {
				break; // Exit the loop if the bottom of the page is reached
			}
			lastHeight = newHeight;
		}
	}
	/**
	 * Asserts a boolean condition and logs the result.
	 * This method can be used to perform assertions across different pages by inheriting from BasePageObject.
	 *
	 * @param condition The boolean condition to be asserted.
	 * @param successMessage The message to log if the assertion passes.
	 * @param failureMessage The message to log and include in the AssertionError if the assertion fails.
	 */
	public void assertCondition(boolean condition, String successMessage, String failureMessage) {
		try {
			Assert.assertTrue(condition);
			log.info(successMessage);
		} catch (AssertionError e) {
			log.error(failureMessage + " Error: " + e.getMessage());
			throw e;  // Rethrow to make sure the test fails and the error is reported in test results
		}
	}
}
