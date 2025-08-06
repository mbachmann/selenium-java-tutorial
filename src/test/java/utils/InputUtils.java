package utils;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class InputUtils {

    public static void pasteText(WebDriver driver, WebElement element, String text) {
        // Text in die Zwischenablage legen
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);

        // Element fokussieren und einfügen
        element.click();
        element.sendKeys(Keys.COMMAND, "v"); // CMD + V auf macOS
    }
}
