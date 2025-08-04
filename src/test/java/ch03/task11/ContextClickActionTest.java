package ch03.task11;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import utils.*;

public class ContextClickActionTest extends TestBase implements HasLogger {

	Actions actions;

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/context_menu");
		actions = new Actions(driver);
	}

	@Test
	void contextClickTest() {
		WebElement box = driver.findElement(By.id("hot-spot"));
		actions.contextClick(box).perform();

		Alert alert = driver.switchTo().alert();
		Assertions.assertTrue(alert.getText().contains("You selected a context menu"));
		alert.accept();
	}
}
