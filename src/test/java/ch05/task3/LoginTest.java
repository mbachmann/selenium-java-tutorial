package ch05.task3;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.*;

import utils.*;

public class LoginTest extends TestBase implements HasLogger {

	LoginPage loginPage;

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/login");
		loginPage = new LoginPage(driver);
	}

	@Test
	void successfulLogin() {
		loginPage.setUsername("tomsmith");
		loginPage.setPassword("SuperSecretPassword!");
		loginPage.clickLogin();

		Assertions.assertTrue(loginPage.getFlashMessage().contains("You logged into a secure area!"));

	}
}