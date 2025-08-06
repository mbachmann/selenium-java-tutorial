package ch03.task9;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;

import utils.*;

public class IFrameTest extends TestBase implements HasLogger {

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/nested_frames");
	}

	@Test
	void iframeTest() {
		driver.switchTo().frame("frame-top");

		// Jetzt in frame-middle
		driver.switchTo().frame("frame-middle");

		WebElement middle = driver.findElement(By.id("content"));
		System.out.println("Middle Frame Text: " + middle.getText());
		Assertions.assertEquals("MIDDLE", middle.getText());

		// Zurück zum übergeordneten Frame (frame-top)
		driver.switchTo().parentFrame();

		// Jetzt direkt in den unteren Frame (frame-bottom) wechseln
		driver.switchTo().defaultContent(); // Zurück zur Hauptseite
		driver.switchTo().frame("frame-bottom");

		WebElement bottomBody = driver.findElement(By.tagName("body"));
		System.out.println("Bottom Frame Text: " + bottomBody.getText());
		Assertions.assertTrue(bottomBody.getText().contains("BOTTOM"));
	}
}
