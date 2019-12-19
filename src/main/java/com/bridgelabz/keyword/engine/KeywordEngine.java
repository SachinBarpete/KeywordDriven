package com.bridgelabz.keyword.engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.bridgelabz.keyword.base.Base;

public class KeywordEngine {

	public WebDriver driver;
	public WebElement element;
	public Properties properties;
	public Base base;

	public static Workbook book;
	public static Sheet sheet;
	public final String SCENARIO_SHEET_PATH = "/home/bridglabz/XLSX/HubSpot.xlsx";

	public void startExecution(String sheetName) {

		String locatorName = null;
		String locatorValue = null;
		FileInputStream file = null;
		try {
			file = new FileInputStream(SCENARIO_SHEET_PATH);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			book = WorkbookFactory.create(file);
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sheet = book.getSheet(sheetName);

		int k = 0;
		for (int i = 0; i < sheet.getLastRowNum(); i++) {
			String locatorColumnValue = sheet.getRow(i + 1).getCell(k + 1).toString().trim();
			if (!locatorColumnValue.equalsIgnoreCase("NA")) {
				locatorName = locatorColumnValue.split("=")[0].trim();
				locatorValue = locatorColumnValue.split("=")[1].trim();
			}

			String action = sheet.getRow(i + 1).getCell(k + 2).toString().trim();
			String value = sheet.getRow(i + 1).getCell(k + 3).toString().trim();
			switch (action) {
			case "open browser":
				base = new Base();
				properties = base.intializeProperties();
				if (value.equalsIgnoreCase("NA") || value.isEmpty())
					driver = base.initializeDriver(properties.getProperty("browser"));
				else
					driver = base.initializeDriver(value);
				break;

			case "enter url":
				if (value.equalsIgnoreCase("NA") || value.isEmpty())
					driver.get(properties.getProperty("url"));
				else
					driver.get(value);
				break;

			case "quit":
				driver.quit();
				break;

			default:
				break;
			}

			switch (action) {
			case "id":
				element = driver.findElement(By.id(locatorValue));
				if (action.equalsIgnoreCase("sendkeys")) {
					element.clear();
					element.sendKeys(value);
				} else if (action.equalsIgnoreCase("click"))
					element.click();
				break;
				
			case "linkText":
				element = driver.findElement(By.id(locatorValue));
				element.click();
				break;	
				
			default:
				break;

			}
		}
	}
}
