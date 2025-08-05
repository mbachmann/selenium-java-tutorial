package ch07.task11;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import utils.*;

public class ErrorScreenshotTest extends TestBase implements HasLogger {

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/login");
	}


	@Test
	void staleElementError(TestInfo info) {
		WebElement loginButton = driver.findElement(By.cssSelector("button.radius"));
		driver.navigate().refresh(); // causes StaleElementReferenceException
		Exception exception = assertThrows(StaleElementReferenceException.class, () -> {
			loginButton.click(); // access on "old" Element
		});
		saveScreenshot(info);
		getLogger().info("Tests with StaleElementReferenceException", exception);
	}

	@Test
	void assertionError(TestInfo info) {
		String heading = driver.findElement(By.tagName("h2")).getText();
		printStep("verify correct assertion");
		assertEquals("Login Page", heading); // correct
		if (!"Wrong Title".equals(heading) ) {  // not correct
			printStep("verify wrong assertion");
			saveScreenshot(info);
		};
	}

	@Test
	void noSuchElementError(TestInfo info) {
		Exception exception = assertThrows(NoSuchElementException.class, () -> {
			driver.findElement(By.id("doesNotExist")); // throws NoSuchElementException
		});
		saveScreenshot(info);
	}

	@Test
	void notInteractableError(TestInfo info) {
		driver.get("https://the-internet.herokuapp.com/dynamic_controls");
		WebElement input = driver.findElement(By.cssSelector("#input-example input"));
		Exception exception = assertThrows(ElementNotInteractableException.class, () -> {
			input.sendKeys("text"); // not activated → ElementNotInteractableException
		});
		saveScreenshot(info);
	}
}
