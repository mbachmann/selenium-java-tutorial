package ch02.task5;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import utils.*;

public class LocatorOptionsTest extends TestBase implements HasLogger {

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/login");
	}

	@Test
	void testElementLocators() {
		// By.name
		WebElement username = driver.findElement(By.name("username"));
		username.sendKeys("tomsmith");

		// By.className (verwenden wir für den Login-Button)
		WebElement loginButton = driver.findElement(By.className("radius"));
		Assertions.assertEquals("Login", loginButton.getText());

		// By.tagName
		WebElement form = driver.findElement(By.tagName("form"));
		Assertions.assertTrue(form.isDisplayed());

		// By.linkText
		WebElement linkText = driver.findElement(By.linkText("Elemental Selenium"));
		Assertions.assertTrue(linkText.getAttribute("href").contains("elementalselenium.com"));

		// By.partialLinkText
		WebElement partialLink = driver.findElement(By.partialLinkText("Selenium"));
		Assertions.assertEquals(linkText, partialLink);

		// By.xpath
		WebElement password = driver.findElement(By.xpath("//input[@type='password']"));
		pasteText(driver, password, "SuperSecretPassword!");
	}

}
