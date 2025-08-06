package utils;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class InputUtils {

    public static void pasteText(WebDriver driver, WebElement element, String text) {
        // copy Text in Clipboard
        Toolkit.getDefaultToolkit().getSystemClipboard()
                .setContents(new StringSelection(text), null);

        // Focus to Element and paste
        element.click();
        if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Windows) {
            element.sendKeys(Keys.CONTROL, "v"); // CTRL + V auf Windows
        } else if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Linux) {
            element.sendKeys(Keys.CONTROL, "v"); // CTRL + V auf Linux
        } else if (OsCheck.getOperatingSystemType() == OsCheck.OSType.MacOS) {
            element.sendKeys(Keys.COMMAND, "v"); // CMD + V auf macOS
        }
    }
}
