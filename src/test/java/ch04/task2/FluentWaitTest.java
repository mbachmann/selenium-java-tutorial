package ch04.task2;

import java.time.Duration;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import utils.*;

public class FluentWaitTest extends TestBase implements HasLogger {
	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/dynamic_loading/1");
	}

	@Test
	void fluentWaitTest() {
		driver.findElement(By.cssSelector("#start button")).click();

		Wait<WebDriver> wait = new FluentWait<>(driver)
				.withTimeout(Duration.ofSeconds(10))
				.pollingEvery(Duration.ofMillis(500))
				.ignoring(NoSuchElementException.class);

		WebElement result = wait.until(d ->
				d.findElement(By.cssSelector("#finish h4")));

		getLogger().info(result.getText()); // → "Hello World!"

	}

}
