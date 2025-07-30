package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import pages.*;

public class DuckDuckGoTest extends AbstractTest{

	private DuckDuckGoSearchPage page;


	@BeforeEach
	void setup() {
		super.setup("https://duckduckgo.com");
		page = new DuckDuckGoSearchPage(driver);
	}

	@Test
	void testDuckDuckGoSearch() {

		WebElement input = wait.until(ExpectedConditions.elementToBeClickable(By.id("searchbox_input")));
		input.sendKeys("Selenium WebDriver");
		input.sendKeys(Keys.ENTER);

		wait.until(ExpectedConditions.titleContains("Selenium WebDriver"));
		Assertions.assertTrue(driver.getTitle().contains("Selenium WebDriver"));
	}

	@Test
	void testDuckDuckGoSearchWithPage() {

		page.search("Selenium WebDriver");
		wait.until(ExpectedConditions.titleContains("Selenium WebDriver"));
		Assertions.assertTrue(driver.getTitle().contains("Selenium WebDriver"));
	}


}
