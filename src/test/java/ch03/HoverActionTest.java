package ch03;

import java.util.List;

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
		List<WebElement> figures = driver.findElements(By.cssSelector(".figure"));

		if (!figures.isEmpty()) {
			WebElement avatar = figures.getFirst();
			actions.moveToElement(avatar).perform();// Erstes Element

			WebElement caption = avatar.findElement(By.className("figcaption"));
			Assertions.assertTrue(caption.isDisplayed());
			Assertions.assertTrue(caption.getText().contains("name: user1"));
		}
	}
}
