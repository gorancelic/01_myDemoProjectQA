package com.myapp.example.ui;

import com.myapp.example.base.CsvDataProviders;
import com.myapp.example.base.TestUtilities;
import com.myapp.example.pages.ItemPage;
import com.myapp.example.pages.SearchPage;
import com.myapp.example.pages.WelcomePage;
import org.testng.annotations.Test;
import java.util.Map;
import java.util.Map.Entry;

public class SearchTests extends TestUtilities {

	protected static String itemForSearching;
	private static double averagePrice;
	private static int countedItems;
	private static Map<String, Integer> products;

	@Test( dataProvider = "csvReader", dataProviderClass = CsvDataProviders.class)
	public void gigatron_002_insertItem(Map<String, String> testData){
		// Test Data extraction from CSV file
		itemForSearching = testData.get("itemForSearching");

		WelcomePage welcomePage = new WelcomePage(driver, log);
		//Open main page
		welcomePage.openPage();

		//Insert text in searchBox
		welcomePage.insertInSearchBox(itemForSearching);

		welcomePage.validateDropDownItem();
	}

	@Test(dependsOnMethods = "gigatron_002_insertItem")
	public void gigatron_003_searchItemsAverageValue(){
		WelcomePage welcomePage = new WelcomePage(driver, log);
		SearchPage searchPage = new SearchPage(driver, log);
		// open main page
		welcomePage.openPage();

		welcomePage.search(itemForSearching);

		//Scroll all the way to the bottom with dynamic load of items while scrolling
		searchPage.scrollToBottomForDynamicLoad();

		//validateDisplayed items are more than 0
		 countedItems = searchPage.validateDisplayedItemsCount();

		//extract all Products from page. Create Map with all products and prices as key-value pair
		products = searchPage.extractProductPrices(driver);
		//display in log all extracted products. Could be skipped. Left only for presentational purposes
		//searchPage.displayProductPrices(products);

		//Calculate average price from collected Map. Pass that map in next function
		averagePrice = searchPage.calculateAveragePrice(products);
	}

	@Test(dependsOnMethods = {"gigatron_003_searchItemsAverageValue","gigatron_002_insertItem"})
	public void gigatron_004_searchItemsReturnBasedOnCondition() {
		SearchPage searchPage = new SearchPage(driver, log);

		//Find item with second-lowest price if total number of items is even
		if (countedItems % 2 == 0) {
			Map.Entry<String, Integer> secondLowest = searchPage.findSecondLowestPriceItem(products);
			log.info("Second lowest price item: " + secondLowest.getKey() + " with price: " + secondLowest.getValue());
		//Find item which price is closest to average price if total number of items is odd
		} else {
			Entry<String, Integer> closestItem = searchPage.findItemClosestToAveragePrice(products, averagePrice);
			log.info("Item closest to average price: " + closestItem.getKey() + " with price: " + closestItem.getValue());
		}
	}
	@Test(dependsOnMethods = "gigatron_002_insertItem")
	public void gigatron_005_searchItemsRandomSelectItem(){
		WelcomePage welcomePage = new WelcomePage(driver, log);
		SearchPage searchPage = new SearchPage(driver, log);
		ItemPage itemPage = new ItemPage(driver, log);

		//Open main page
		welcomePage.openPage();
		takeScreenshot("WelcomePage opened");
		welcomePage.acceptCookies();
		welcomePage.search(itemForSearching);

		//Select Random Item
		String randomItemName = searchPage.selectRandomItem();
		takeScreenshot("SearchPage opened");

		//Click on chosen item
		searchPage.clickOnItemWithText(randomItemName);

		//Validate on new page that proper item is listed
		searchPage.validateItem(randomItemName);
		takeScreenshot("ItemPage opened");
	}
}
