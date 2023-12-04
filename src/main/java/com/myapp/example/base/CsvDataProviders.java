package com.myapp.example.base;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;

import com.opencsv.CSVReader;

import static org.apache.commons.lang3.StringUtils.split;

public class CsvDataProviders {
	/**
	 * Provides test data by reading CSV files. This method is a data provider for TestNG tests.
	 * It dynamically reads a CSV file corresponding to a test method and returns the test data.
	 *
	 * @param method The test method for which the data provider is being called. This is used to determine
	 *               the specific CSV file to be read.
	 *
	 * Output:
	 * - Returns an iterator over a collection of Object arrays, where each Object array contains test data
	 *   for one iteration of the test method.
	 *
	 * Description:
	 * - The method constructs the path to the CSV file based on the test method's details.
	 * - It reads the CSV file and converts each row into a map where the keys are the column headers,
	 *   and values are the corresponding entries in the row.
	 * - Each map is then added to a list, which is converted into an iterator and returned.
	 *
	 * Notes:
	 * - The CSV file's path is constructed using the test method's class name and the method name, assuming a specific directory structure.
	 * - The method handles FileNotFoundException and IOException, throwing a RuntimeException if any file reading issues occur.
	 * - The method assumes CSV files are located in 'src/test/resources/dataproviders'.
	 * - It is important that the CSV file's format matches the expected structure (first row as headers, subsequent rows as data).
	 */
	@DataProvider(name = "csvReader")
	public static Iterator<Object[]> csvReader(Method method) {
		List<Object[]> list = new ArrayList<Object[]>();
		String fullTestingTypePath = method.getDeclaringClass().getPackage().getName();
		System.out.println(fullTestingTypePath);
		String testingType = getLastSegmentOfPackageName(fullTestingTypePath);
		System.out.println(testingType);
		String pathname = "src" + File.separator + "test" + File.separator + "resources" + File.separator
				+ "dataproviders" + File.separator + testingType + File.separator
				+ method.getDeclaringClass().getSimpleName() + File.separator
				+ method.getName() + ".csv";
		File file = new File(pathname);
		try {
			CSVReader reader = new CSVReader(new FileReader(file));
			String[] keys = reader.readNext();
			if (keys != null) {
				String[] dataParts;
				while ((dataParts = reader.readNext()) != null) {
					Map<String, String> testData = new HashMap<String, String>();
					for (int i = 0; i < keys.length; i++) {
						testData.put(keys[i], dataParts[i]);
					}
					list.add(new Object[] { testData });
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File " + pathname + " was not found.\n" + e.getStackTrace().toString());
		} catch (IOException e) {
			throw new RuntimeException("Could not read " + pathname + " file.\n" + e.getStackTrace().toString());
		}

		return list.iterator();
	}
	/**
	 * Extracts the last segment from a fully qualified package name.
	 * This utility method is used to parse a package name and return its last segment.
	 *
	 * @param fullTestingTypePath The full package name from which the last segment is to be extracted.
	 *
	 * Output:
	 * - Returns a String representing the last segment of the provided package name.
	 *
	 * Notes:
	 * - This method is particularly useful in scenarios where a specific part of the package name is needed,
	 *   for example, to construct file paths or for logging purposes.
	 * - It assumes that the package name is in standard Java format, using dots as separators.
	 * - If the input string is empty or does not contain any dots, the method will return the entire string.
	 */
	public static String getLastSegmentOfPackageName(String fullTestingTypePath) {
		String[] parts = fullTestingTypePath.split("\\.");
		return parts[parts.length - 1]; // Returns the last segment
	}

}
