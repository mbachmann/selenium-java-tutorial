package ch03;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import utils.*;

public class InteractionTest extends AbstractTest implements HasLogger {

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/login");
	}

	@Test
	void loginTest() {
		driver.findElement(By.id("username")).sendKeys("tomsmith");
		driver.findElement(By.id("password")).sendKeys("SuperSecretPassword!");
		driver.findElement(By.cssSelector("button.radius")).click();

		WebElement flashMessage = driver.findElement(By.id("flash"));
		Assertions.assertTrue(flashMessage.getText().contains("You logged into a secure area!"));
		getLogger().info("Login successful: " + flashMessage.getText());
	}

}
