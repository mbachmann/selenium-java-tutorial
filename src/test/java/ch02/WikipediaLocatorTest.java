package ch02;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import utils.*;

public class WikipediaLocatorTest extends AbstractTest implements HasLogger {

	@BeforeEach
	void setup() {
		super.setup("https://www.wikipedia.org");
	}

	@Test
	void locateElementsUsingVariousStrategies() {
		WebElement byId = driver.findElement(By.id("searchInput"));
		WebElement byName = driver.findElement(By.name("search"));
		WebElement byCss = driver.findElement(By.cssSelector("#searchInput"));
		WebElement byXpath = driver.findElement(By.xpath("//input[@id='searchInput']"));

		Assertions.assertNotNull(byId);
		Assertions.assertNotNull(byName);
		Assertions.assertNotNull(byCss);
		Assertions.assertNotNull(byXpath);
	}


}
