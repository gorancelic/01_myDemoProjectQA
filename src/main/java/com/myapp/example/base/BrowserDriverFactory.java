package com.myapp.example.base;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class BrowserDriverFactory {

	private ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();
	private String browser;
	private Logger log;

	public BrowserDriverFactory(String browser, Logger log) {
		this.browser = browser.toLowerCase();
		this.log = log;
	}
	/**
	 * Creates and initializes a WebDriver instance based on the specified browser type.
	 * This method configures and returns a WebDriver instance for different browsers and modes (normal or headless).
	 *
	 * Description:
	 * - The method logs the browser type being used for creating the WebDriver.
	 * - It uses a switch statement to handle different browser types: Chrome, Firefox, and their headless versions.
	 * - For each browser type, the method sets the system property for the corresponding WebDriver executable and initializes it.
	 * - For headless modes, it configures the browser options to run without a UI.
	 * - If an unknown browser type is specified, it defaults to using Chrome.
	 *
	 * Output:
	 * - Returns an instance of `WebDriver` configured for the specified browser.
	 */
	public WebDriver createDriver() {
		// Create driver
		log.info("Create driver: " + browser);

		switch (browser) {
		case "chrome":
			System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
			driver.set(new ChromeDriver());
			break;
		case "firefox":
			String geckoDriverPath = System.getenv("GECKO_DRIVER_PATH"); // Get path from environment variable
			if (geckoDriverPath == null || geckoDriverPath.isEmpty()) {
				geckoDriverPath = "src/main/resources/geckodriver.exe"; // Fallback to a default path if not set
			}
			System.setProperty("webdriver.gecko.driver", geckoDriverPath);
			FirefoxOptions options = new FirefoxOptions();

			driver.set(new FirefoxDriver(options));
			break;

			
		case "chromeheadless":
			System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--headless");
			driver.set(new ChromeDriver(chromeOptions));
			break;

		case "firefoxheadless":
			System.setProperty("webdriver.gecko.driver", "src/main/resources/geckodriver.exe");
			FirefoxBinary firefoxBinary = new FirefoxBinary();
			firefoxBinary.addCommandLineOptions("--headless");
			FirefoxOptions firefoxOptions = new FirefoxOptions();
			firefoxOptions.setBinary(firefoxBinary);
			driver.set(new FirefoxDriver(firefoxOptions));
			break;

		default:
			System.out.println("Do not know how to start: " + browser + ", starting chrome.");
			System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
			driver.set(new ChromeDriver());
			break;
		}

		return driver.get();
	}




}
