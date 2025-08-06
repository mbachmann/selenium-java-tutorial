package config;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.logging.*;
import org.slf4j.*;

import io.qameta.allure.Allure;
import utils.*;

public class DriverFactory implements HasLogger {

	private static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);
	/**
	 * Download current driver from <a href="https://googlechromelabs.github.io/chrome-for-testing/">...</a>
	 */
	private static final String LINUX_WEBDRIVER = "src/test/resources/drivers/linux/chromedriver-139";
	private static final String MACOS_WEBDRIVER = "src/test/resources/drivers/macos/chromedriver-138";
	private static final String WINDOWS_WEBDRIVER = "src/test/resources/drivers/win/chromedriver-138.exe";
	public static WebDriver driver;

	public static WebDriver getLocalChromeDriver() {

		setChromeDriver();

		ChromeOptions options = getChromeOptions();
		driver = new ChromeDriver(options);
		logger.trace("Chrome driver created");
		configureDriverTimeouts();
		logger.info("Webdriver started with browser: [{}]", options.getBrowserName());
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

		chromeOptions.addArguments("--remote-allow-origins=*");
		chromeOptions.addArguments("--disable-gpu");
		chromeOptions.addArguments("--no-sandbox");
		chromeOptions.addArguments("--safebrowsing-disable-download-protection");
		chromeOptions.addArguments("--safebrowsing-disable-extension-blacklist");
		setDownloadOptions(chromeOptions);
		return chromeOptions;
	}

	private static void configureDriverTimeouts() {
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

	/**
	 * Download current driver from <a href="https://googlechromelabs.github.io/chrome-for-testing/">...</a>
	 */
	private static void setChromeDriver() {
		switch(OsCheck.getOperatingSystemType()) {
		case OsCheck.OSType.Linux:
			System.setProperty("webdriver.chrome.driver", LINUX_WEBDRIVER);
			break;
		case OsCheck.OSType.Windows:
			System.setProperty("webdriver.chrome.driver", WINDOWS_WEBDRIVER);
			break;
		case OsCheck.OSType.MacOS:
			System.setProperty("webdriver.chrome.driver", MACOS_WEBDRIVER);
			break;
		default: throw new RuntimeException("Unsupported operating system: " + OsCheck.getOperatingSystemType());
		}

		logger.info("Detected OS: {}, architecture: {}, version: {}", OsCheck.getName(), OsCheck.getArchitecture(), OsCheck.getVersion());
	}

}
