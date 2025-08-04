package ch05.task3;

import org.openqa.selenium.*;

public class LoginPage {
	private final WebDriver driver;

	private final By usernameField = By.id("username");
	private final By passwordField = By.id("password");
	private final By loginButton = By.cssSelector("button.radius");
	private final By flashMessage = By.id("flash");

	public LoginPage(WebDriver driver) {
		this.driver = driver;
		driver.get("https://the-internet.herokuapp.com/login");
	}

	public void setUsername(String username) {
		driver.findElement(usernameField).sendKeys(username);
	}

	public void setPassword(String password) {
		driver.findElement(passwordField).sendKeys(password);
	}

	public void clickLogin() {
		driver.findElement(loginButton).click();
	}

	public String getFlashMessage() {
		return driver.findElement(flashMessage).getText();
	}
}