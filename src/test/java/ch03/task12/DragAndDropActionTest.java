package ch03.task12;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import utils.*;

import java.time.Duration;

public class DragAndDropActionTest extends TestBase implements HasLogger {

	SafeActions safe;

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/drag_and_drop");
		safe = new SafeActions(driver);
	}

	@Test
	void dragAndDropTest() {
		WebElement columnA = driver.findElement(By.id("column-a"));
		WebElement columnB = driver.findElement(By.id("column-b"));

		safe.dragAndDrop(columnA, columnB,
				() -> driver.findElement(By.cssSelector("#column-a header")).getText().equals("B"),
				Duration.ofSeconds(3));

		Assertions.assertEquals("B", driver.findElement(By.cssSelector("#column-a header")).getText());

	}
}
