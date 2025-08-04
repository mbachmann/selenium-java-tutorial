package ch05.task4;

import java.time.Duration;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

public abstract class BasePage {
	protected WebDriver driver;
	protected WebDriverWait wait;

	public BasePage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	protected WebElement waitForVisibility(By locator) {
		return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
	}

	protected void click(By locator) {
		waitForVisibility(locator).click();
	}

	protected void type(By locator, String text) {
		WebElement element = waitForVisibility(locator);
		element.clear();
		element.sendKeys(text);
	}

	protected String getText(By locator) {
		return waitForVisibility(locator).getText();
	}
}