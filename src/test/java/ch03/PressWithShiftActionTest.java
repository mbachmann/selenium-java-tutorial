package ch03;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import utils.*;

public class PressWithShiftActionTest extends AbstractTest implements HasLogger {

	Actions actions;

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/key_presses");
		actions = new Actions(driver);
	}

	@Test
	void keyPressWithShift() {
		WebElement body = driver.findElement(By.tagName("body"));

		actions.keyDown(Keys.SHIFT).sendKeys("a").keyUp(Keys.SHIFT).perform();

		WebElement result = driver.findElement(By.id("result"));
		Assertions.assertTrue(result.getText().contains("A"));
	}
}
