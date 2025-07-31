package ch03;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import utils.*;

public class HoverActionTest extends AbstractTest implements HasLogger {

	Actions actions;

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/hovers");
		actions = new Actions(driver);
	}

	@Test
	void hoverTest() {
		WebElement avatar = driver.findElement(By.cssSelector(".figure:first-child"));
		actions.moveToElement(avatar).perform();

		WebElement caption = driver.findElement(By.cssSelector(".figure:first-child .figcaption h5"));
		Assertions.assertTrue(caption.isDisplayed());
	}
}
