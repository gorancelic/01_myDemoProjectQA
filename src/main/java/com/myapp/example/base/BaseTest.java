package com.myapp.example.base;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import static com.myapp.example.base.TestRailAPI.createTestRunAndGetId;
import static com.myapp.example.base.TestRailAPI.getTestCaseIdInTestRun;
import static io.restassured.RestAssured.given;

@Listeners({ TestListener.class })

public class BaseTest {

	protected WebDriver driver;
	protected Logger log;

	protected String testSuiteName;
	protected String testName;
	protected String testMethodName;

	protected static String pageUrl; // Store the page URL

	/**
	 * Initializes the base URL for the test suite based on the specified environment.
	 * This method is designed to run before the execution of the test suite and configures the URL
	 * to be used for testing. It uses the 'environment' parameter to determine the appropriate base URL.
	 *
	 * @param environment The name of the environment for which the tests are to be run.
	 *                    This is used to determine the base URL for the tests.
	 *                    The parameter is optional and defaults to "production" if not provided.
	 *
	 * Description:
	 * - The method retrieves the base URL for the specified environment using the EnvironmentConfig class.
	 * - It initializes the logger for the test suite and logs the base URL that will be used for testing.
	 * - The base URL is stored in the 'pageUrl' field, presumably used by subsequent tests.
	 *
	 * Output:
	 * - The method sets the 'pageUrl' field with the base URL for the specified environment.
	 * - It also initializes the logger and logs the URL under testing.
	 *
	 * Note:
	 * - This method is annotated with @BeforeSuite(alwaysRun = true), ensuring it always runs before the test suite,
	 *   regardless of other method dependencies.
	 * - The method assumes the existence of a static method EnvironmentConfig.getBaseUrl, which retrieves the base URL.
	 */
	@BeforeSuite(alwaysRun = true)
	@Parameters("environment")
	public void setUpUrl(@Optional("production") String environment) {
		pageUrl = EnvironmentConfig.getBaseUrl(environment);
		log = LogManager.getLogger(testName);
		log.info("URL under testing: " + pageUrl);
	}

	private boolean shouldUpdateTestRail;
	protected static int currentTestRunId; // Store the page URL

	/**
	 * Sets up TestRail integration before executing the test suite.
	 * This method checks if TestRail results should be updated based on the configuration.
	 * If so, it creates a new Test Run in TestRail and logs the creation.
	 *
	 * @param context The test context provided by TestNG, containing information about the test run.
	 *                It's used to obtain information about the suite, tests, etc., during the test execution.
	 *
	 * Description:
	 * - The method first reads the configuration to determine whether to update TestRail results.
	 * - If updates are required, it generates a unique identifier for the new Test Run based on the current date and time.
	 * - It then creates a new Test Run in TestRail with this identifier and logs the creation.
	 *
	 * Note:
	 * - The method assumes the existence of a `createTestRunAndGetId` method that communicates with TestRail's API.
	 * - The method uses a logger to log important information.
	 * - It modifies the class field `currentTestRunId`, presumably used later in the test execution process.
	 */
	@BeforeSuite
	public void setUpTestRail(ITestContext context) {
		//next code block is reading if we should update Test  Rail results
		String updateTestRail = TestRailAPI.getProperty("updateTestRail", "false");
		updateTestRail = System.getProperty("updateTestRail", updateTestRail);
		shouldUpdateTestRail = "true".equalsIgnoreCase(updateTestRail);
		// above code block is reading if we should update Test  Rail results
		if (shouldUpdateTestRail != false) {
			String getTodaysDate = new SimpleDateFormat("yyMMdd_HHmm").format(new Date());
			int testRunId = createTestRunAndGetId(1, "TestRun_" + getTodaysDate, "This is a description of the new test run.");
			log = LogManager.getLogger("TestRail");
			log.info("Created Test Run with id: " + testRunId);
			currentTestRunId = testRunId;
		}
	}

	/**
	 * Performs a health check of the application before running any tests in the class.
	 * This method is executed before the first test method of the current test class is invoked.
	 */
	@BeforeClass
	public void healthCheck() {
		log = LogManager.getLogger(testName);
		getRequest(pageUrl, null, null, null, 200);
		log.info("Health check passed");
	}


	/**
	 * Updates the test result in TestRail for the executed test method.
	 * This method is called after each test method execution if TestRail integration is enabled.
	 *
	 * @param result The result of the test method execution, provided by TestNG. It contains
	 *               information about the test method, its execution status, and other details.
	 *
	 * Input:
	 * - The method takes an `ITestResult` object as an input, which contains information about the test's execution.
	 *
	 * Output:
	 * - The method communicates with TestRail's API to update the test case's result based on the test execution status.
	 * - Logs the start and completion of the update process, as well as any errors encountered.
	 *
	 * Notes:
	 * - The method assumes the existence of `TestRailAPI`, a custom class to interact with TestRail's API.
	 * - It uses a method `getTestCaseIdInTestRun` to retrieve the mapping of TestNG test methods to TestRail test cases.
	 * - The method `mapTestNGResultToTestRailStatus` is used to translate TestNG result status to TestRail status IDs.
	 * - The method handles any exceptions during the API call and logs the error message.
	 */
	@AfterMethod
	public void updateTestRailResult(ITestResult result) {
		if (shouldUpdateTestRail) {
			log.info("[TestRail] Updating starting");
			String testCase = String.valueOf(result.getMethod().getMethodName());
			if (testCase != null) {
				try {
					TestRailAPI testRailAPI = new TestRailAPI();
					Map<String, Integer> testArtifacts = getTestCaseIdInTestRun(currentTestRunId,testCase);
					Integer testCaseId = testArtifacts.get("testCaseId");
					//This is testCase RunID. Might be useful later for some operations
					Integer testRunId = testArtifacts.get("testRunId");
					int statusId = testRailAPI.mapTestNGResultToTestRailStatus(result.getStatus());
					log.info("[TestRail] The Test Run ID is: " +currentTestRunId+ ", CaseID is: "
							+ testCaseId + " and TestCaseRun ID is: " + testRunId+ " with statusID: " +statusId) ;
					String comment = "Automated test result comment";
					//TestRail needs RunID, CaseID, Status of testCase and comment for executed case. Comment area should be developed further
					testRailAPI.updateTestResult(currentTestRunId, testCaseId, statusId, comment);
					log.info("[TestRail] Updating finished");
				}catch (Exception e) {
					log.info("[TestRail] Error updating result: " + e.getMessage());
					}
				}
			}
		}

	/**
	 * Sends a POST request to a specified URL and returns the response.
	 * This method is designed to make an HTTP POST request with configurable headers, query parameters, and request body.
	 *
	 * @param url The base URL to which the POST request is to be sent.
	 * @param path The path relative to the base URL. Can be an empty string or null if not applicable.
	 * @param headers A map of request header key-value pairs. Can be null if no headers are needed.
	 * @param queryParams A map of query parameter key-value pairs to be appended to the URL. Can be null if no query parameters are needed.
	 * @param body The request body. This can be a string, a map, or any object that can be serialized into the request body. Can be null if no body is to be sent.
	 * @param expectedStatus The expected HTTP status code for the response. This is used for assertion or validation.
	 *
	 * Output:
	 * - Returns a `Response` object which includes information like response body, status code, headers, etc.
	 * - Logs information about the POST request and its response status.
	 *
	 */
	protected Response postRequest(String url, String path, Map<String, String> headers, Map<String, Object> queryParams, Object body,int expectedStatus) {
		RequestSpecification request = given().baseUri(url);

		if (headers != null) {
			request.headers(headers);
		}
		if (queryParams != null) {
			request.queryParams(queryParams);
		}
		if (body != null) {
			request.body(body); // Add body only if it's not null
		}
		path = (path != null) ? path : "";

		// Making the POST request
		Response response = request.post(path);
		handleFailedResponse(response,expectedStatus);
		log.info("POST request executed with status code "+ response.statusCode()+ ". Endpoint: "+ url+ ". Query params: " + queryParams);
		return response;
	}

	/**
	 * Sends a GET request to a specified URL and returns the response.
	 * This method is designed to make an HTTP GET request with configurable headers and query parameters.
	 *
	 * @param url The base URL to which the GET request is to be sent.
	 * @param path The path relative to the base URL. Can be an empty string or null if not applicable.
	 * @param headers A map of request header key-value pairs. Can be null if no headers are needed.
	 * @param queryParams A map of query parameter key-value pairs to be appended to the URL. Can be null if no query parameters are needed.
	 * @param expectedStatus The expected HTTP status code for the response. This is used for assertion or validation.
	 *
	 * Output:
	 * - Returns a `Response` object which includes information like response body, status code, headers, etc.
	 * - Logs information about the GET request and its response status.
	 *
	 */
	protected Response getRequest(String url, String path, Map<String, String> headers, Map<String, Object> queryParams, int expectedStatus) {
		RequestSpecification request = given().baseUri(url);

		if (headers != null) {
			request.headers(headers);
		}
		if (queryParams != null) {
			request.queryParams(queryParams);
		}
		path = (path != null) ? path : "";

		Response response = request.get(path);
		handleFailedResponse(response, expectedStatus);
		log.info("GET request executed with status code " + response.statusCode() + ". Endpoint: " + url + ". Query params: " + queryParams);
		return response;
	}
	/**
	 * Sends a PUT request to a specified URL and returns the response.
	 * This method is designed to make an HTTP PUT request with configurable headers, query parameters, and request body.
	 *
	 * @param url The base URL to which the PUT request is to be sent.
	 * @param path The path relative to the base URL. Can be an empty string or null if not applicable.
	 * @param headers A map of request header key-value pairs. Can be null if no headers are needed.
	 * @param queryParams A map of query parameter key-value pairs to be appended to the URL. Can be null if no query parameters are needed.
	 * @param body The request body. This can be a string, a map, or any object that can be serialized into the request body. Can be null if no body is to be sent.
	 * @param expectedStatus The expected HTTP status code for the response. This is used for assertion or validation.
	 *
	 * Output:
	 * - Returns a `Response` object which includes information like response body, status code, headers, etc.
	 * - Logs information about the PUT request and its response status.
	 */
	protected Response putRequest(String url, String path, Map<String, String> headers, Map<String, Object> queryParams, Object body, int expectedStatus) {
		RequestSpecification request = given().baseUri(url);

		if (headers != null) {
			request.headers(headers);
		}
		if (queryParams != null) {
			request.queryParams(queryParams);
		}
		if (body != null) {
			request.body(body);
		}
		path = (path != null) ? path : "";

		Response response = request.put(path);
		handleFailedResponse(response, expectedStatus);
		log.info("PUT request executed with status code " + response.statusCode() + ". Endpoint: " + url + ". Query params: " + queryParams);
		return response;
	}
	/**
	 * Handles the response of an HTTP request if it fails to meet the expected status code.
	 * This method is designed to validate the response status code against an expected value and
	 * take appropriate action if they do not match.
	 *
	 * @param response The HTTP response object obtained from the request. It contains the status code,
	 *                 response body, and other details of the HTTP response.
	 * @param expectedStatus The HTTP status code that is expected from the request. This is used to validate
	 *                       if the request was successful or not.
	 *
	 */
	private void handleFailedResponse(Response response,int expectedStatus) {
		if (!(response.statusCode() == expectedStatus)) {
			String errorMessage = "Request failed with status code " + response.statusCode() + ",expected " + expectedStatus;
			log.error(errorMessage);
			// Additional error handling logic
			Assert.fail(errorMessage);
		}
	}
}
