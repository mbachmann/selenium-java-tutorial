package ch07.task10;

import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.File;
import java.time.Duration;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import config.DriverFactory;
import utils.*;

public class FileDownloadTest extends TestBase implements HasLogger {

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/download");
	}

	@Test
	void downloadTest() {
		driver.findElement(By.linkText("edu-test-upload.txt")).click();

		File downloaded = new File(DriverFactory.getDownloadDir() + "/edu-test-upload.txt");

		await()
				.atMost(10, SECONDS)
				.pollInterval(Duration.ofMillis(500))
				.until(downloaded::exists);

		assertTrue(downloaded.exists());

		if (downloaded.exists()) {
			getLogger().info("Downloaded file exists: " + downloaded.getAbsoluteFile());
			if (downloaded.delete()) getLogger().info("Successfully deleted " + downloaded.getName());
		}
	}
}
