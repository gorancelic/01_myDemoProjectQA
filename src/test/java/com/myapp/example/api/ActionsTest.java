package com.myapp.example.api;


import com.myapp.example.base.BaseTest;
import com.myapp.example.base.CsvDataProviders;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;
import com.myapp.example.api.utils.UtilsForAPI;

public class ActionsTest extends BaseTest {

    @Test( dataProvider = "csvReader", dataProviderClass = CsvDataProviders.class)
    public void gigatron_006_actionCheck(Map<String, String> testData){

        // Test Data extraction from CSV file
        String no = testData.get("no");
        String url  = testData.get("url");
        String path = testData.get("path");
        String uid = testData.get("uid");
        String description = testData.get("description");

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("uid", uid); // Ensure 'uid' is defined

        Object body = null; // Define the request body as needed

        Response response = this.postRequest(url, path, headers, queryParams, body,200);

        UtilsForAPI apiUtils = new UtilsForAPI();
        apiUtils.takeAllOldestProductsOnAction(response);
    }
}
