package com.myapp.example.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

	Logger log;
	String testName;
	String testMethodName;
	/**
 	* Actions to be performed at the start of each test method execution.
 	* This method is part of a TestNG listener and is executed each time a test method starts.
 	*
 	* @param result The TestNG ITestResult instance for the current test method execution.
 	*               It contains information about the test method that is about to be executed.
 	*
 	* Description:
 	* - The method updates the class fields `testName` and `testMethodName` with the full class name and method name of the test, respectively.
 	* - It initializes and logs the start of the test method execution.
 	*/
	@Override
	public void onTestStart(ITestResult result) {
		this.testName = result.getMethod().getTestClass().getName();

		this.testMethodName = result.getMethod().getMethodName();
		log.info("[Starting " + testMethodName + "]");
	}

	/**
 	* Callback method that is invoked by TestNG upon the successful completion of a test method.
 	* This method is part of the TestNG listener and is used to perform actions when a test method passes.
 	*
 	* @param result The ITestResult instance provided by TestNG, containing information about the test method that has executed.
 	*
 	* Description:
 	* - The method logs a message indicating that the test method, identified by 'testMethodName', has passed successfully.
 	*/
	@Override
	public void onTestSuccess(ITestResult result) {
		log.info("[Test " + testMethodName + " passed]");
	}

	/**
	 * Callback method that is invoked by TestNG upon the failure of a test method.
	 * This method is part of the TestNG listener and is used to perform actions when a test method fails.
	 *
	 * @param result The ITestResult instance provided by TestNG, containing information about the test method that failed.
	 *
	 * Description:
	 * - The method logs a message indicating that the test method, identified by 'testMethodName', has failed.
	 */
	@Override
	public void onTestFailure(ITestResult result) {
		log.info("[Test " + testMethodName + " failed]");
	}

	/**
 	* Callback method that is invoked by TestNG when a test method is skipped.
 	* This method is part of the TestNG listener and is used to perform actions specific to skipped test methods.
 	*
 	* @param result The ITestResult instance provided by TestNG, containing information about the test method that was skipped.
 	*
 	* Description:
 	* - The method logs a message indicating that a particular test method was skipped.
 	* - It also checks if there is a Throwable associated with the skipped test (indicating the reason for skipping) and logs its message.
 	*/
	@Override
	public void onTestSkipped(ITestResult result) {
		log.info("Test Skipped: " + result.getMethod().getMethodName());
		Throwable skipCause = result.getThrowable();
		if (skipCause != null) {
			log.info("Reason for Skipping: " + skipCause.getMessage());
		}
	}
	/**
 	* Callback method that is invoked by TestNG at the start of a test context (e.g., a <test> tag in XML suite).
 	* This method is part of the TestNG listener and is used to perform actions at the beginning of a test context.
 	*
 	* @param context The ITestContext instance provided by TestNG, containing information about the current test context.
 	*
 	* Description:
 	* - The method sets the class field 'testName' with the name of the current test context.
 	* - It initializes a logger for the test context, using the test name for logging.
 	* - The method logs the start of the test context, indicating that a group of tests (as defined in a <test> tag) has started execution.
 	*/
	@Override
	public void onStart(ITestContext context) {
		this.testName = context.getCurrentXmlTest().getName();
		this.log = LogManager.getLogger(testName);
		log.info("[TEST " + testName + " STARTED]");
	}
	/**
 	* Callback method that is invoked by TestNG at the end of a test context (e.g., a <test> tag in XML suite).
 	* This method is part of the TestNG listener and is used to perform actions at the conclusion of a test context.
 	*
 	* @param context The ITestContext instance provided by TestNG, containing information about the current test context that has just finished.
 	*
 	* Description:
 	* - The method logs a message indicating the completion of all tests within the current test context, identified by 'testName'.
 	*/
	@Override
	public void onFinish(ITestContext context) {
		log.info("[ALL " + testName + " FINISHED]");
	}

}
