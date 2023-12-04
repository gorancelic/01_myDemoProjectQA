package com.myapp.example.pages;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import java.util.Map;
import java.util.Map.Entry;

import java.util.*;
import java.util.NoSuchElementException;

public class SearchPage extends BasePageObject {

	public By searchedItem = By.cssSelector("#grid-products > *");
	public By itemName = By.cssSelector(".item__name > *");
	public By itemPrice = By.cssSelector(".item__bottom__prices__price");

	public static String selectedItemName;

	public SearchPage(WebDriver driver, Logger log) {
		super(driver, log);
	}

	/**
 	* Validates the number of displayed items on a page and ensures that at least one item is present.
 	* This method counts the number of elements matching a specific criterion (defined by 'searchedItem') and verifies that the count is greater than zero.
 	*
 	* @return The count of elements found on the page.
 	*
 	* Description:
 	* - The method uses 'findAll' to retrieve a list of WebElements matching the criteria specified by 'searchedItem'.
 	* - It calculates the size of this list, which represents the count of displayed items.
 	* - An assertion checks if the count is greater than zero, ensuring that at least one item is present.
 	* - If the assertion fails, an AssertionError is caught, logged, and then re-thrown for test case failure.
 	*/
	public int validateDisplayedItemsCount() {
		log.info("Finding all items");
		List<WebElement> elements = findAll(searchedItem);

		int count = elements.size();
		log.info("All items" + count);
		try {
			Assert.assertTrue(count > 0, "Displayed count is not greater than 0");
		} catch (AssertionError e) {
			// Handle the assertion error
			log.error("Assertion failed: " + e.getMessage());
			throw e;
		}

		return count;
	}

	/**
	 * Extracts and maps product names to their corresponding prices from the web page.
	 * This method navigates through the elements on a web page to collect product names and their prices.
	 *
	 * @param driver The WebDriver instance used to interact with the web page.
	 * @return A Map<String, Integer> where each key is a product name and the corresponding value is the product price.
	 *
	 * Description:
	 * - The method first finds all elements representing products on the page using 'searchedItem'.
	 * - For each product element, it extracts the product name and price.
	 * - The method processes the price text to extract numerical values, handling scenarios where the price might be discounted or formatted with non-numeric characters.
	 * - It then maps each product name to its corresponding price in a HashMap.
	 */
	public Map<String, Integer> extractProductPrices(WebDriver driver) {
		Map<String, Integer> productPrices = new HashMap<>();

		// Find product elements
		List<WebElement> productElements = driver.findElements(searchedItem);

		// Loop through each product element
		for (WebElement productElement : productElements) {

			String productName = productElement.findElement(itemName).getText();
			String productPrice = productElement.findElement(itemPrice).getText();

			// Default to the first price if the discounted price is not available
			String priceText = productPrice.isEmpty() ? "" : productPrice;
			priceText = priceText.replaceAll("[^\\d]", ""); // Remove all non-numeric characters

			if (!priceText.isEmpty()) {
				int price = Integer.parseInt(priceText);
				productPrices.put(productName, price);
			}
		}
		return productPrices;
	}
	/**
 	* Displays the product names and their respective prices from a provided map.
 	* This method logs the name and price of each product contained within the 'productPrices' map.
 	*
 	* @param productPrices A Map containing product names as keys and their prices as values.
 	*
 	* Description:
 	* - Iterates over the 'productPrices' Map using a for-each loop on the entrySet.
 	* - For each entry in the map, logs the product name and its price.
 	*
 	* Notes:
 	* - This method is useful for logging purposes, especially in testing scenarios where verification of product pricing is essential.
 	*/
	public void displayProductPrices(Map<String, Integer> productPrices) {
		for (Map.Entry<String, Integer> entry : productPrices.entrySet()) {
			log.info("Product: " + entry.getKey() + ", Price: " + entry.getValue());
		}
	}
	/**
 	* Calculates the average price of products from a given map.
 	* This method computes the average price from a Map containing product names and their respective prices.
 	*
 	* @param productPrices A Map containing product names as keys and their prices as integer values.
 	* @return The average price calculated from the product prices, rounded to two decimal places.
 	*
 	* Description:
 	* - First, checks if the provided map is null or empty, throwing IllegalArgumentException if true.
 	* - Sums all the prices from the map.
 	* - Calculates the average price by dividing the total sum by the number of products.
 	* - Rounds the average price to two decimal places for precision.
 	* - Logs the calculated average price for reference.
 	*/
	public double calculateAveragePrice(Map<String, Integer> productPrices) {
		if (productPrices == null || productPrices.isEmpty()) {
			throw new IllegalArgumentException("Product price map cannot be null or empty");
		}
		// Summing all prices
		int totalSum = 0;
		for (Integer price : productPrices.values()) {
			totalSum += price;
		}

		// Calculating average
		double average = (double) totalSum / productPrices.size();
		// Rounding to 2 decimal places
		average = Math.round(average * 100.0) / 100.0;
		log.info("Calculated average price for all items is : " + average);
		return average;
	}
	/**
 	* Identifies and returns the map entry with the second-lowest price.
 	* This method sorts a given map of product prices and retrieves the entry with the second-lowest price.
 	*
 	* @param productPrices A Map containing product names as keys and their prices as integer values.
 	* @return A Map.Entry object representing the product with the second-lowest price.
 	*
 	* Description:
 	* - First, checks if the provided map is null or contains fewer than two items, throwing IllegalArgumentException if true.
 	* - Converts the map's entry set into a list and sorts it in ascending order based on the price values.
 	* - Retrieves and returns the second entry from the sorted list, representing the product with the second-lowest price.
 	*/
	public static Map.Entry<String, Integer> findSecondLowestPriceItem(Map<String, Integer> productPrices) {
		// Check if the map has at least two items
		if (productPrices == null || productPrices.size() < 2) {
			throw new IllegalArgumentException("Map must contain at least two items.");
		}

		// Convert the entry set to a list and sort it by values (prices)
		List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(productPrices.entrySet());
		sortedEntries.sort(Comparator.comparing(Map.Entry::getValue));

		// Retrieve the second item from the sorted list
		return sortedEntries.get(1); // 0 is the first, 1 is the second
	}
	/**
 	* Finds and returns the product whose price is closest to a given average price.
 	* This method identifies the product entry from a map whose price value is nearest to the specified average price.
 	*
 	* @param productPrices A Map containing product names as keys and their prices as integer values.
 	* @param averagePrice The average price to compare against the product prices.
 	* @return A Map.Entry representing the product with the price closest to the average price.
 	*
 	* Description:
 	* - Checks if the provided map is null or empty and throws IllegalArgumentException if so.
 	* - Iterates through each entry in the productPrices map.
 	* - Calculates the absolute difference between each product's price and the given average price.
 	* - Keeps track of the product with the minimum difference, updating it as it finds closer matches.
 	* - Returns the entry (product name and price) with the closest price to the average.
 	*/
	public static Entry<String, Integer> findItemClosestToAveragePrice(Map<String, Integer> productPrices, double averagePrice) {
		if (productPrices == null || productPrices.isEmpty()) {
			throw new IllegalArgumentException("Product price map cannot be null or empty");
		}

		// Finding the item with the price closest to the average
		Entry<String, Integer> closestItem = null;
		double minDifference = Double.MAX_VALUE;
		for (Entry<String, Integer> entry : productPrices.entrySet()) {
			double difference = Math.abs(entry.getValue() - averagePrice);
			if (difference < minDifference) {
				minDifference = difference;
				closestItem = entry;
			}
		}
		return closestItem;
	}
	/**
 	* Selects a random item from a list of items on a web page and returns its name.
 	* This method randomly chooses one item from all the available items on the page and retrieves its name.
 	*
 	* @return The name of the randomly selected item.
 	*
 	* Description:
 	* - Finds all elements that match the 'searchedItem' criteria.
 	* - Throws IllegalStateException if no items are found.
 	* - Randomly selects one item from the list of found items.
 	* - Retrieves the name or detail of the selected item using the 'itemName' locator.
 	* - Logs and returns the name of the randomly chosen item.
 	*/
	public String selectRandomItem() {
		// Find all items
		List<WebElement> items = findAll(searchedItem);
		if (items.isEmpty()) {
			throw new IllegalStateException("No items found");
		}

		// Randomly select one item
		Random random = new Random();
		WebElement randomItem = items.get(random.nextInt(items.size()));

		// Find the name or detail of the chosen item
		WebElement nameElement = randomItem.findElement(itemName);
		selectedItemName = nameElement.getText();
		log.info("Random item chosen: "+ selectedItemName);
		return selectedItemName;
	}
	/**
 	* Clicks on an item with a specific text on the web page.
 	* This method locates an item by its text and performs a click action on it.
 	*
 	* @param text The text of the item to be clicked.
 	*
 	* Description:
 	* - Retrieves a list of all elements that match the 'itemName' criteria.
 	* - Iterates through these elements to find the one whose text matches the specified 'text'.
 	* - Once the matching element is found, it moves the cursor over the element and performs a click action.
 	* - If the element is not found, an ElementClickInterceptedException or a WebDriverException is caught, logged, and results in test failure.
 	*/
	public void clickOnItemWithText(String text) {
		List<WebElement> items;
		try {
			items = driver.findElements(itemName);
			for (WebElement item : items) {
				if (item.getText().equals(text)) {
					Actions actions = new Actions(driver);
					actions.moveToElement(item).perform();
					item.click();
					break;
				}
			}
		} catch (NoSuchElementException e) {
			log.error("Element not found: " + e.getMessage());
			Assert.fail();
		} catch (ElementClickInterceptedException e) {
			log.error("Element click intercepted: " + e.getMessage());
			Assert.fail();
		} catch (WebDriverException e) {
			log.error("WebDriver exception occurred: " + e.getMessage());
			Assert.fail();
		}
	}

	public void validateItem(String expectedTitle) {
		assertCondition(selectedItemName.equals(expectedTitle),
				"Item is as expected: " + expectedTitle,
				"Item does not match. Expected: " + expectedTitle + ", but found: " + selectedItemName);
	}
}






