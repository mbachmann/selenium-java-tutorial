package ch04;

import java.time.Duration;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import utils.*;

public class WaitTestBase extends TestBase implements HasLogger {

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/dynamic_loading/1");
	}

	@Test
	void waitTest() {
		driver.findElement(By.cssSelector("#start button")).click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
		WebElement helloWorld = wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#finish h4"))
		);

		Assertions.assertEquals("Hello World!", helloWorld.getText());
		getLogger().info("Hello World visible: " + helloWorld.getText());
	}

}
