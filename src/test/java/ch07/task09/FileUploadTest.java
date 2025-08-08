package ch07.task09;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Paths;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import utils.*;

public class FileUploadTest extends TestBase implements HasLogger {

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/upload");
	}

	@Test
	void uploadTest() {
		WebElement upload = driver.findElement(By.id("file-upload"));

		String path = Paths.get("src/test/resources/testdata/edu-test-upload.txt").toAbsolutePath().toString();
		upload.sendKeys(path);

		driver.findElement(By.id("file-submit")).click();
		wait(1000);
		WebElement uploaded = driver.findElement(By.tagName("h3"));
		assertEquals("File Uploaded!", uploaded.getText());
	}

}
