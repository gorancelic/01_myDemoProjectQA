package com.myapp.example.base;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.ITestResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TestRailAPI {

    private static String username = "";
    private static String password = "";
    private static String testRailUrl = "";
    private CloseableHttpClient client;

    public void TestRailAPI(String username, String password, String testRailUrl) {
        this.username = username;
        this.password = password;
        this.testRailUrl = testRailUrl;
        this.client = HttpClients.createDefault();
    }
    /**
    * Retrieves the value of a specified property from a properties file, with a default value as a fallback.
    * This method is designed to access configuration settings from a properties file.
    *
    * @param key The key of the property to be retrieved.
    * @param defaultValue The default value to return if the property key is not found or in case of any issues.
    *
    * Output:
    * - Returns a String representing the value of the specified property or the default value if the property is not found.
    */
    public static String getProperty(String key, String defaultValue) {
        Properties prop = new Properties();
        String value = defaultValue;

        try (InputStream input = TestRailAPI.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return defaultValue;
            }
            prop.load(input);
            value = prop.getProperty(key, defaultValue);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return value;
    }

    /**
    * Creates a new test run in TestRail and retrieves its ID.
    * This method communicates with the TestRail API to create a new test run for a given project.
    *
    * @param projectId The ID of the project in TestRail where the test run will be created.
    * @param runName The name of the test run to be created.
    * @param description A description for the test run.
    *
    * Output:
    * - Returns an integer representing the ID of the newly created test run. Returns -1 if creation fails.
    */
    public static int createTestRunAndGetId( int projectId, String runName, String description) {
        String auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        String url = testRailUrl + "/index.php?/api/v2/add_run/" + projectId;
        int testRunId = -1;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth);
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            JSONObject json = new JSONObject();
            json.put("name", runName);
            json.put("description", description);
            // Add more fields as needed

            StringEntity entity = new StringEntity(json.toString());
            post.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(post)) {
                String result = EntityUtils.toString(response.getEntity());
                JSONObject resultJson = new JSONObject(result);
                if (resultJson.has("id")) {
                    testRunId = resultJson.getInt("id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testRunId;
    }
    /**
     * Retrieves the case ID and run ID of a specific test case in a given TestRail test run.
     * This method communicates with the TestRail API to fetch details of a test case within a specific test run.
     *
     * @param currentTestRunId The ID of the test run in TestRail from which the test case details are to be retrieved.
     * @param testCaseTitle The title of the test case to search for within the test run.
     *
     * Output:
     * - Returns a Map<String, Integer> containing the 'testCaseId' and 'testRunId' of the specified test case.
     *   Returns -1 for these IDs if the test case is not found or in case of any issues.
     *
     * Description:
     * - The method builds an HTTP GET request to the TestRail API's 'get_tests' endpoint for the specified test run ID.
     * - It sets up the necessary authentication headers for the request.
     * - The method processes the response from TestRail to find the test case with the given title and extracts its case ID and run ID.
     * - Exception handling is implemented to catch any issues during the HTTP request or response parsing.
     *
     * Notes:
     * - The method assumes valid authentication credentials (username and password) for TestRail access.
     * - It uses the Apache HttpClient library for HTTP communications.
     * - This method is useful in scenarios where test case details are required for updating results or fetching additional information from TestRail.
     * - Proper error handling ensures that default values (-1) are returned in case of failure, which can be used to detect and handle API communication issues.
     */
    public static Map<String, Integer> getTestCaseIdInTestRun( int currentTestRunId,  String testCaseTitle) {
        String auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        if (currentTestRunId == -1) {
            throw new IllegalStateException("Test run ID is not set. Please create a test run first.");
        }

        String url = testRailUrl + "/index.php?/api/v2/get_tests/" + currentTestRunId;
        int testCaseId = -1;
        int testRunId = -1;
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet get = new HttpGet(url);
            get.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth);
            get.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            try (CloseableHttpResponse response = client.execute(get)) {
                String result = EntityUtils.toString(response.getEntity());
                JSONObject responseObject = new JSONObject(result);
                JSONArray tests = responseObject.getJSONArray("tests");
                for (int i = 0; i < tests.length(); i++) {
                    JSONObject test = tests.getJSONObject(i);
                    if (test.getString("title").equals(testCaseTitle)) {
                        testCaseId = test.getInt("case_id");
                        testRunId = test.getInt("id");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("testCaseId", testCaseId);
        result.put("testRunId", testRunId);
        return result;
    }
    /**
     * Updates the test result for a specific test case in TestRail.
     * This method communicates with the TestRail API to update the result of a test case within a specific test run.
     *
     * @param testRunId The ID of the test run in TestRail to which the test case belongs.
     * @param testCaseId The ID of the test case in the test run whose result is to be updated.
     * @param statusId The status ID representing the test result according to TestRail's status definitions.
     * @param comment A comment or note to be attached to the test result in TestRail.
     *
     * Description:
     * - The method builds an HTTP POST request to the TestRail API's 'add_result_for_case' endpoint for the specified test run ID and test case ID.
     * - It sets up the necessary authentication headers and JSON payload, which includes the test result status and comment.
     * - The method sends the request to TestRail and checks the response status code to verify successful update.
     * - Exception handling is implemented to catch any issues during the HTTP request or response processing.
     *
     * Notes:
     * - The method assumes the presence of valid TestRail URL, username, and password, which are used for authentication.
     * - It uses the Apache HttpClient library for HTTP communications.
     * - This method is crucial in automated testing scenarios where test results are reported back to TestRail for centralized tracking and reporting.
     * - Proper error handling ensures that any failures during the update process are logged, and the program can handle such exceptions gracefully.
     */
    public void updateTestResult( int testRunId, int testCaseId, int statusId, String comment) {
        String auth = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        String url = testRailUrl + "/index.php?/api/v2/add_result_for_case/" + testRunId + "/" + testCaseId;
        try (CloseableHttpClient client = HttpClients.createDefault()){
            HttpPost post = new HttpPost(url);
            post.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + auth);
            post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            JSONObject body = new JSONObject();
            body.put("status_id", statusId); // Status ID as per TestRail documentation
            body.put("comment", comment);

            StringEntity entity = new StringEntity(body.toString());
            post.setEntity(entity);

            try (CloseableHttpResponse response = client.execute(post)) {
                HttpEntity responseEntity = response.getEntity();
                String result = EntityUtils.toString(responseEntity);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new IOException("Failed to update TestRail. Status code: " + statusCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * - The method uses a switch statement to map TestNG result status codes to TestRail status IDs.
     * - TestNG statuses like SUCCESS and FAILURE are translated to their respective TestRail counterparts.
     * - If the TestNG status does not match any predefined case, a default TestRail status is returned.
     *
     * @param testNGStatus The status code from TestNG, representing the outcome of a test (e.g., success, failure).
     * @return The corresponding status ID for TestRail.
     */
    int mapTestNGResultToTestRailStatus(int testNGStatus) {
        // Map TestNG status to TestRail status ID
        switch (testNGStatus) {
            case ITestResult.SUCCESS:
                return 1; // 1' is the status ID for 'Passed' in TestRail
            case ITestResult.FAILURE:
                return 5; // '5' is the status ID for 'Failed' in TestRail
            default:
                return 0; // Status ID for 'Untested' or another appropriate status
        }
    }


}