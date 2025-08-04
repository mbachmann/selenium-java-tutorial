package ch05.task5;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;

public class LoginPage {
	private WebDriver driver;

	@FindBy(id = "username")
	private WebElement usernameInput;

	@FindBy(id = "password")
	private WebElement passwordInput;

	@FindBy(css = "button.radius")
	private WebElement loginButton;

	@FindBy(css = ".flash.success")
	private WebElement successMessage;

	public LoginPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void login(String user, String pass) {
		usernameInput.sendKeys(user);
		passwordInput.sendKeys(pass);
		loginButton.click();
	}

	public String getSuccessMessage() {
		return successMessage.getText();
	}
}