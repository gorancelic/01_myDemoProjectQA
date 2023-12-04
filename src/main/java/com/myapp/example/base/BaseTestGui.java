package com.myapp.example.base;

import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.lang.reflect.Method;

public class BaseTestGui extends BaseTest {

    /**
     * Initializes the test environment before each test method execution.
     * This method is responsible for setting up the web driver, browser, and test context information.
     *
     * @param method The test method that will be executed next. Provided by TestNG to retrieve method-related information.
     * @param browser The name of the browser in which the test will run. This is optional and defaults to "chrome" if not provided.
     * @param ctx The test context provided by TestNG, containing information about the ongoing test execution.
     *
     * Notes:
     * - The method assumes the existence of a `BrowserDriverFactory` class responsible for creating WebDriver instances.
     * - It's important that the web driver and browser are correctly initialized and configured, as they are crucial for the execution of web-based tests.
     * - The information extracted from the TestNG context (suite name, test name, method name) can be used for detailed logging or custom reporting.
     */
    @Parameters({"browser"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(Method method, @Optional("chrome") String browser, ITestContext ctx) {
            String testName = ctx.getCurrentXmlTest().getName();
            BrowserDriverFactory factory = new BrowserDriverFactory(browser, log);

            driver = factory.createDriver();

            driver.manage().window().maximize();

            this.testSuiteName = ctx.getSuite().getName();
            this.testName = testName;
            this.testMethodName = method.getName();
     }
    /**
     * Cleans up the test environment after each test method execution.
     * This method is responsible for closing and cleaning up the web driver instance used for the test.
     *
     * Description:
     * - The method logs the action of closing the driver.
     * - It then calls the `quit` method on the WebDriver instance to close the browser and end the session.
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        log.info("Close driver");
        // Close browser
        driver.quit();
    }
}
