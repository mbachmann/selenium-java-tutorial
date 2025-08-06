package ch05.task5;

import org.openqa.selenium.*;
import org.openqa.selenium.support.*;
import utils.TestBase;

public class LoginPage  {
	private final WebDriver driver;

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
		TestBase.setValue(driver, passwordInput, pass);
		loginButton.click();
	}

	public String getSuccessMessage() {
		return successMessage.getText();
	}
}
