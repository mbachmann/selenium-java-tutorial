package ch03.task10;

import java.util.List;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import org.openqa.selenium.remote.RemoteWebDriver;
import utils.*;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class HoverActionTest extends TestBase implements HasLogger {

	SafeActions safe;

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/hovers");
		safe = new SafeActions(driver);
	}

	@Test
	void hoverTest() {
		List<WebElement> figures = driver.findElements(By.cssSelector(".figure"));
		Assertions.assertFalse(figures.isEmpty(), "No figures found on page");

		WebElement avatar = figures.getFirst();
		WebElement caption = avatar.findElement(By.className("figcaption"));

		safe.hover(avatar, By.className("figcaption"), Duration.ofSeconds(3));

		Assertions.assertTrue(caption.isDisplayed(), "Caption should be visible after hover");
		Assertions.assertTrue(caption.getText().contains("name: user1"), "Caption text mismatch");
	}
}
