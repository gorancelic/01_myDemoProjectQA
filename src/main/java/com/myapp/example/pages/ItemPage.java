package com.myapp.example.pages;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.testng.Assert;


public class ItemPage extends BasePageObject {

    public ItemPage(WebDriver driver, Logger log) {super(driver, log);}

    public By itemName = By.cssSelector("h1[itemprop='name']");

    /**
    * Verifies if the expected product name is present on the item page.
    * This method checks for the presence and correctness of a product name on a web page.
    *
    * @param expectedName The expected name of the product to be verified on the page.
    * @return true if the product name matches the expected name, false otherwise.
    *
    * Description:
    * - The method first waits for the visibility of the element identified by 'itemName'.
    * - It then tries to find the WebElement for the product name and retrieves its text.
    * - An assertion is used to compare the retrieved product name with the expected name.
    * - If the product name matches the expected name, the method logs the success and returns true.
    * - If a NoSuchElementException is caught (indicating the element could not be found), an error is logged, the test is explicitly failed using Assert.fail(), and false is returned.
    */
    public boolean findOnItemPage(String expectedName) {
        waitForVisibilityOf(itemName);
        try {
            WebElement productNameElement = driver.findElement(itemName);
            String productName = productNameElement.getText();
            // Assert that the product name matches the expected name
            Assert.assertEquals(productName, expectedName, "Product name does not match the expected name.");
            log.info("Element found on Item Page with product name: " + productName);
            return true;
        } catch (NoSuchElementException e) {
            log.error("Element not found: " + e.getMessage());
            Assert.fail();
            return false;
        }
    }
}
