package config;

import java.io.*;
import java.nio.file.Files;
import java.text.*;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.logging.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.*;

import io.qameta.allure.Allure;
import utils.*;

public class DriverFactory implements HasLogger {

	private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
	public static WebDriver driver;

	public static WebDriver getLocalChromeDriver() {

		// System.setProperty("webdriver.chrome.driver", "C:\\work\\workspace\\edu\\selenium\\selenium-edu\\src\\test\\resources\\drivers\\win\\chromedriver-138.exe");
		System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/win/chromedriver-138.exe");

		ChromeOptions options = getChromeOptions();
		driver = new ChromeDriver(options);
		logger.info("Chrome driver created");
		configureDriver();
		logger.info("Webdriver started with browser: [{}] [{}] ", options.getBrowserName(),  options.getBrowserVersion());
		Dimension windowSize = driver.manage().window().getSize();
		// driver.manage().window().setSize(new Dimension(1800,1100));
		logger.info("Tests with Window size: {}x{}", windowSize.width,  windowSize.height);

		return driver;
	}

	private static LoggingPreferences getLoggingPreferences() {
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		logPrefs.enable(LogType.PERFORMANCE, Level.INFO);
		logPrefs.enable(LogType.PROFILER, Level.INFO);
		logPrefs.enable(LogType.CLIENT, Level.INFO);
		logPrefs.enable(LogType.DRIVER, Level.INFO);
		logPrefs.enable(LogType.SERVER, Level.INFO);
		return logPrefs;
	}

	private static ChromeOptions getChromeOptions() {
		LoggingPreferences logPrefs = getLoggingPreferences();

		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.setCapability("goog:loggingPrefs", logPrefs);

		if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Linux) {
			chromeOptions.addArguments("--headless");
		}

		chromeOptions.addArguments("--disable-gpu");
		chromeOptions.addArguments("--safebrowsing-disable-download-protection");
		chromeOptions.addArguments("--safebrowsing-disable-extension-blacklist");
		setDownloadOptions(chromeOptions);
		return chromeOptions;
	}

	private static void configureDriver() {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
		driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));

	}

	private static void setDownloadOptions(ChromeOptions chromeOptions) {
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("download.default_directory", getDownloadDir());
		prefs.put("download.prompt_for_download", false);
		prefs.put("safebrowsing.enabled", true);
		chromeOptions.setExperimentalOption("prefs", prefs);
	}

	public static String getDownloadDir() {
		return System.getProperty("user.home") + File.separator + "downloads";
	}

	public static void saveScreenshot(String name) {
		attachPageScreenshotToAllure(name);
		File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			if (screenshot.exists()) {
				File target = new File("target/screenshots/" + name + ".png");
				Files.createDirectories(target.toPath().getParent());
				Files.copy(screenshot.toPath(), target.toPath());
			}
		} catch (
			IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void attachPageScreenshotToAllure(String name) {
		byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		Allure.attachment(name, new ByteArrayInputStream(screenshotBytes));
	}

	public static void attachLogResponseToAllure(String responseName, String responseContent) {
		Allure.addAttachment(responseName, "text/plain", responseContent);
	}

	public String getBrowserUserLanguage() {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		return (String) executor.executeScript("return window.navigator.language");
	}

	public String getUserAgent() {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		return (String) executor.executeScript("return window.navigator.userAgent");
	}

	public static void sleep(int milliSeconds) {

		try {
			TimeUnit.MILLISECONDS.sleep(milliSeconds);
		} catch (InterruptedException ignored) {
		}

	}



}
