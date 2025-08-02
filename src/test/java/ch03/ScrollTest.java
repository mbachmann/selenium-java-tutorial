package ch03;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import utils.*;

public class ScrollTest extends TestBase implements HasLogger {

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/large");
	}

	@Test
	void javascriptScrollTest() {
		driver.get("https://the-internet.herokuapp.com/large");
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

		WebElement footer = driver.findElement(By.id("page-footer"));
		Assertions.assertTrue(footer.isDisplayed());
	}
}
