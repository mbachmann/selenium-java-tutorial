package config;

import java.time.Duration;
import java.util.logging.Level;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.logging.*;
import org.slf4j.*;


public class SeleniumConfiguration {

	private static final Logger logger = LoggerFactory.getLogger(SeleniumConfiguration.class);
	public static WebDriver driver;

	public static WebDriver getLocalChromeDriver() {

		// System.setProperty("webdriver.chrome.driver", "C:\\work\\workspace\\edu\\selenium\\selenium-edu\\src\\test\\resources\\drivers\\win\\chromedriver-138.exe");
		System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/win/chromedriver-138.exe");

		ChromeOptions options = getChromeOptions();
		driver = new ChromeDriver(options);
		configureDriver();
		logger.info("Webdriver started with browser: [{}] [{}] ", options.getBrowserName(),  options.getBrowserVersion());

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
		// chromeOptions.addArguments("--headless");
		chromeOptions.addArguments("--disable-gpu");
		return chromeOptions;
	}

	private static void configureDriver() {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
		driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
		Dimension windowSize = driver.manage().window().getSize();
		// driver.manage().window().setSize(new Dimension(1800,1100));
		logger.info("Tests with Window size: {}", windowSize);
	}
}
