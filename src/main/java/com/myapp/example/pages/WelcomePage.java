package com.myapp.example.pages;

import com.myapp.example.base.TestUtilities;
import com.myapp.example.base.EnvironmentConfig;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import com.myapp.example.base.BaseTest;

public class WelcomePage extends BasePageObject {


	public By searchBox = By.cssSelector("#header__center__searchbox > div > div > input[type=text]");
	private By searchButton = By.cssSelector("#header__center__searchbox > div > div > div.search-icon");
	private By firstItemInDropdown = By.cssSelector("#search-dropdown > div.search-recommended.true > div > div:nth-child(1) > a");
	public By buttonForAcceptCookies = By.cssSelector(" #content div.gdpr-title button.btn.primary");

	public WelcomePage(WebDriver driver, Logger log) {
		super(driver, log);
	}

	TestUtilities url = new TestUtilities();
	String page = url.getUrl();

	/** Open WelcomePage with it's url */
	public void openPage() {
	try	{
		log.info("Opening page: " + page);
		openUrl(page);
		log.info("Page opened!");
	} catch (Exception e) {
		log.error("Failed to open page: " + page, e);
		}
	}
	/** Insert item for search in Search Box without clicking on search button */
	public void insertInSearchBox(String searchString) {
	try {
		type(searchString, searchBox);
		log.info("In search box inserted: " + searchString);
	} catch (Exception e) {
		log.error("Failed to insert in search box: " + searchString, e);
		Assert.fail();
		}
	}
	/** Insert item for search in Search Box with clicking on search button */
	public void search(String searchString) {
		try {
			type(searchString, searchBox);
			click(searchButton);
			log.info("Searching button clicked after inserted value: " + searchString);
		} catch (NoSuchElementException e) {
			log.error("Element not found: " + e.getMessage());
			Assert.fail();
		} catch (ElementNotInteractableException e) {
			log.error("Element not interactable: " + e.getMessage());
			Assert.fail();
		} catch (WebDriverException e) {
			log.error("WebDriver exception occurred: " + e.getMessage());
			Assert.fail();
		} catch (Exception e) {
			log.error("An unexpected error occurred: " + e.getMessage());
			Assert.fail();
		}
	}
	/** Validate first item in dropdown area */
	public void validateDropDownItem() {
		waitForVisibilityOf(firstItemInDropdown);
		log.info("Item from drop down menu is visible");
	}
	/**
 	* Accepts cookies on a web page by clicking the designated 'Accept' button.
 	* This method looks for a cookie acceptance button and clicks it if found, handling various potential exceptions.
 	*/
	public void acceptCookies() {
		try {
			waitForVisibilityOf(buttonForAcceptCookies);
			if (!driver.findElements(buttonForAcceptCookies).isEmpty()) {
				click(buttonForAcceptCookies);
			}
		} catch (TimeoutException e) {
			log.error("Timeout waiting for the cookies acceptance button: " + e.getMessage());
			Assert.fail();
		} catch (NoSuchElementException e) {
			log.error("Cookies acceptance button not found: " + e.getMessage());
			Assert.fail();
		} catch (WebDriverException e) {
			log.error("WebDriver exception occurred: " + e.getMessage());
			Assert.fail();
		} catch (Exception e) {
			log.error("An unexpected error occurred in acceptCookies: " + e.getMessage());
			Assert.fail();
		}
	}
}
