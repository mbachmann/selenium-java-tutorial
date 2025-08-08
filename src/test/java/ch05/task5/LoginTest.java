package ch05.task5;

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
		loginPage.login("tomsmith", "SuperSecretPassword!");

		assertTrue(loginPage.getSuccessMessage().contains("You logged into a secure area!"));
	}
}