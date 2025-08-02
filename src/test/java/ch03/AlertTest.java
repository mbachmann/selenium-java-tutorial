package ch03;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import utils.*;

public class AlertTest extends TestBase implements HasLogger {

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/javascript_alerts");
	}

	@Test
	void alertAcceptTest() {
		driver.findElement(By.xpath("//button[text()='Click for JS Alert']")).click();

		Alert alert = driver.switchTo().alert();
		alert.accept();

		WebElement result = driver.findElement(By.id("result"));
		Assertions.assertTrue(result.getText().contains("You successfully clicked an alert"));
	}
}
