package ch06.task7;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import utils.TestBase;

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

	@FindBy(id = "flash")
	private WebElement message;

	public LoginPage(WebDriver driver) {
		this.driver = driver;
		PageFactory.initElements(driver, this);
	}

	public void login(String user, String pass) {
		usernameInput.sendKeys(user);
		if (pass != null)  {
			TestBase.setValue(driver, passwordInput,"SuperSecretPassword!");
		}
		loginButton.click();
	}

	public String getSuccessMessage() {
		return successMessage.getText();
	}

	public String getMessage() {
		return message.getText();
	}
}
