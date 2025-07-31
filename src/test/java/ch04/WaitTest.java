package ch04;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import utils.*;

public class WaitTest extends AbstractTest implements HasLogger {
	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/dynamic_loading/1");
	}

	@Test
	void loginTest() {
		driver.findElement(By.cssSelector("#start button")).click();

		WebElement helloWorld = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#finish h4"))
		);

		Assertions.assertEquals("Hello World!", helloWorld.getText());
		getLogger().info("Hello World visible: " + helloWorld.getText());
	}

}
