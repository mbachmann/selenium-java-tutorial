package ch05.task4;

import org.openqa.selenium.*;

public class LoginPage extends BasePage {

	private final By usernameField = By.id("username");
	private final By passwordField = By.id("password");
	private final By loginButton = By.cssSelector("button[type='submit']");
	private final By successMessage = By.cssSelector(".flash.success");

	public LoginPage(WebDriver driver) {
		super(driver);
	}

	public void login(String username, String password) {
		type(usernameField, username);
		type(passwordField, password);
		click(loginButton);
	}

	public String getSuccessMessage() {
		return getText(successMessage);
	}
}