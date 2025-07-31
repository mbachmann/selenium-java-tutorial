package ch03;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import utils.*;

public class DragAndDropActionTest extends AbstractTest implements HasLogger {

	Actions actions;

	@BeforeEach
	void setup() {
		super.setup("https://the-internet.herokuapp.com/drag_and_drop");
		actions = new Actions(driver);
	}

	@Test
	void dragAndDropTest() {
		WebElement columnA = driver.findElement(By.id("column-a"));
		WebElement columnB = driver.findElement(By.id("column-b"));

		actions.dragAndDrop(columnA, columnB).perform();

		WebElement headerA = driver.findElement(By.cssSelector("#column-a header"));
		Assertions.assertEquals("B", headerA.getText());
	}
}
