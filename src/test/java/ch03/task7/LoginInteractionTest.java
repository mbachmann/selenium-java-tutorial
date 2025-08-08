package ch03.task7;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import utils.*;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class LoginInteractionTest extends TestBase implements HasLogger {

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/login");
	}

	@Test
	void loginTest() {
		driver.findElement(By.id("username")).sendKeys("tomsmith");

		// sendkeys of ! on macos not working due to bug in selenium
		WebElement pwd = driver.findElement(By.id("password"));
		TestBase.setValue(driver, pwd,"SuperSecretPassword!");

		getLogger().info(driver.findElement(By.id("username")).getAttribute("value")); 		// is correct
		getLogger().info(driver.findElement(By.id("username")).getDomAttribute("value"));		// is not correct (null)
		getLogger().info(driver.findElement(By.id("username")).getDomProperty("value"));		// is correct
		getLogger().info(driver.findElement(By.id("username")).getText());							// is not correct

		getLogger().info(driver.findElement(By.id("password")).getAttribute("value")); 		// is correct

		driver.findElement(By.cssSelector("button.radius")).click();

		WebElement flashMessage = driver.findElement(By.id("flash"));
		Assertions.assertTrue(flashMessage.getText().contains("You logged into a secure area!"));
		getLogger().info("Login successful: " + flashMessage.getText());
	}

}
