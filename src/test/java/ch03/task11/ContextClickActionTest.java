package ch03.task11;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.*;

import java.time.Duration;

public class ContextClickActionTest extends TestBase implements HasLogger {

	SafeActions safe;

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/context_menu");
		safe = new SafeActions(driver);
	}

	@Test
	void contextClickTest() {
		WebElement box = driver.findElement(By.id("hot-spot"));

		safe.contextClickExpectAlert(box, Duration.ofSeconds(5), "You selected a context menu");

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
		Alert alert = wait.until(ExpectedConditions.alertIsPresent());

		Assertions.assertTrue(alert.getText().contains("You selected a context menu"));
		alert.accept();
	}
}
