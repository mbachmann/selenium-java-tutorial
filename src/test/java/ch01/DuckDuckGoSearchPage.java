package ch01;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;

import utils.HasLogger;

public class DuckDuckGoSearchPage implements HasLogger {
	private WebDriver driver;

	@FindBy(id = "searchbox_input")
	private WebElement searchInput;

	public DuckDuckGoSearchPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void search(String query) {
		searchInput.sendKeys(query);
		searchInput.sendKeys(Keys.ENTER);
	}

}