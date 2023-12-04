package com.myapp.example.api.utils;

import com.myapp.example.base.BaseTest;
import org.testng.Assert;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UtilsForAPI extends BaseTest {
    public UtilsForAPI() {
        log = LogManager.getLogger(UtilsForAPI.class);
    }
    protected Logger log;

public void takeAllOldestProductsOnAction(Response response) {
    try {
        List<String> dateStrings = response.path("items.date");
        //Find earliestDate and convert it to string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate earliestDate = dateStrings.stream()
                .map(dateStr -> LocalDate.parse(dateStr, formatter))
                .min(Comparator.naturalOrder())
                .orElse(null); // orElse part is to handle the case where the list might be empty
        String formattedEarliestDate = earliestDate != null ? earliestDate.format(formatter) : "No Date Found";

        //Take all offers from response with selected oldest date
        List<String> titles = response.path("items.findAll{it.date == '%s'}.link", formattedEarliestDate);

        log.info("Oldest offers are posted on date: " + formattedEarliestDate +
                " with #" + titles.toArray().length + " items!");
        Assert.assertNotNull(titles.toArray().length);
        int index = 1;
        for (String element : titles) {
            log.info("#" + index + " link: " + element);
            index++;
        }
    } catch (DateTimeParseException e) {
        log.error("Error parsing date: " + e.getMessage());
    } catch (Exception e) {
        log.error("An error occurred: " + e.getMessage());
    }
}
}