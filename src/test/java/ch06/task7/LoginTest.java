package ch06.task7;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import utils.*;

public class LoginTest extends TestBase implements HasLogger {

	LoginPage loginPage;

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/login");
		loginPage = new LoginPage(driver);
	}

	@ParameterizedTest
	@CsvSource({
			// username,password,expectedMessage
			"tomsmith,SuperSecretPassword!,You logged into a secure area!",
			"admin,wrongpass,Your username is invalid!",
			"user,,Your username is invalid!"
	})
	void successfulLogin(String username, String password, String expectedMessage) {
		loginPage.login(username, password);

		Assertions.assertTrue(
				loginPage.getMessage().contains(expectedMessage),
				"Expected Message: " + expectedMessage + "\nfound: " + loginPage.getMessage()
		);

		getLogger().info("username: " + username + ", password: " + password + ", message: " + loginPage.getMessage());
	}
}