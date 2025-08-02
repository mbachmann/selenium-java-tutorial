package ch02;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import utils.TestBase;

public class LoginPageTestBase extends TestBase {

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/login");
	}

	@Test
	void findElements() {
		WebElement username = driver.findElement(By.id("username"));
		WebElement password = driver.findElement(By.id("password"));
		WebElement loginButton = driver.findElement(By.cssSelector("button.radius"));

		Assertions.assertTrue(username.isDisplayed());
		Assertions.assertTrue(password.isDisplayed());
		Assertions.assertTrue(loginButton.isDisplayed());
	}

}
