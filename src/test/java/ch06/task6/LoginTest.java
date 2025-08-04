package ch06.task6;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import utils.*;

public class LoginTest extends TestBase implements HasLogger {

	LoginPage loginPage;

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/login");
		loginPage = new LoginPage(driver);
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/testdata/logindata.csv", numLinesToSkip = 0)
	void successfulLogin(String username, String password, String expectedMessage) {
		loginPage.login(username, password);

		Assertions.assertTrue(
				loginPage.getMessage().contains(expectedMessage),
				"Expected Message: " + expectedMessage + "\nfound: " + loginPage.getMessage()
		);

		getLogger().info("username: " + username + ", password: " + password + ", message: " + loginPage.getMessage());
	}
}