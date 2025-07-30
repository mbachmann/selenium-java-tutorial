package tests;

import java.time.Duration;

import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import config.SeleniumConfiguration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AbstractTest {

	protected WebDriver driver;
	protected WebDriverWait wait;



	void setup(String url) {
		driver = SeleniumConfiguration.getLocalChromeDriver();
		driver.get(url);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	@AfterEach
	void teardown() {
		if (driver != null) {
			driver.quit();
		}
	}
}
