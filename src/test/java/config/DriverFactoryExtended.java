package config;

import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.edge.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.logging.*;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.remote.service.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.HasLogger;
import utils.OsCheck;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

public class DriverFactoryExtended implements HasLogger {

	private static final Logger logger = LoggerFactory.getLogger(DriverFactoryExtended.class);

	private static final String CHROME_DRIVER = "src/test/resources/drivers/%s/chromedriver-138";
	private static final String FIREFOX_DRIVER = "src/test/resources/drivers/%s/geckodriver";
	private static final String EDGE_DRIVER = "src/test/resources/drivers/%s/msedgedriver-139";

	private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static final ThreadLocal<DriverService> driverService = new ThreadLocal<>();

	public static WebDriver getDriver() {
		return driver.get();
	}

	private static void setDriver(WebDriver driverInstance) {
		driver.set(driverInstance);
	}


	public static WebDriver getLocalChromeDriver() {
		setDriverProperty("chrome");
		ChromeOptions options = getChromeOptions();

		// create and start ChromeDriverService
		ChromeDriverService service = new ChromeDriverService.Builder()
				.usingAnyFreePort()
				.withSilent(true)
				.build();
		try {
			service.start();
			driverService.set(service);
		} catch (IOException e) {
			logger.warn("Could not start ChromeDriverService, falling back to default ChromeDriver ctor", e);
		}

		// if service start successful ... use it, else use it without
		if (driverService.get() != null && driverService.get() instanceof ChromeDriverService && driverService.get().isRunning()) {
			setDriver(new ChromeDriver((ChromeDriverService) driverService.get(), options));
		} else {
			setDriver(new ChromeDriver(options));
		}
		return configureDriver(getDriver());
	}

	public static WebDriver getLocalFirefoxDriver() {
		setDriverProperty("firefox");
		FirefoxOptions options = getFirefoxOptions();

		// GeckoDriverService erstellen und starten
		GeckoDriverService service = new GeckoDriverService.Builder()
				.usingAnyFreePort()
				.build();
		try {
			service.start();
			driverService.set(service);
		} catch (IOException e) {
			logger.warn("Could not start GeckoDriverService, falling back to default FirefoxDriver ctor", e);
		}

		if (driverService.get() != null && driverService.get() instanceof GeckoDriverService && driverService.get().isRunning()) {
			setDriver(new FirefoxDriver((GeckoDriverService) driverService.get(), options));
		} else {
			setDriver(new FirefoxDriver(options));
		}
		return configureDriver(getDriver());
	}

	public static WebDriver getLocalEdgeDriver() {
		setDriverProperty("edge");
		EdgeOptions options = getEdgeOptions();

		// EdgeDriverService erstellen und starten
		EdgeDriverService service = new EdgeDriverService.Builder()
				.usingAnyFreePort()
				.build();
		try {
			service.start();
			driverService.set(service);
		} catch (IOException e) {
			logger.warn("Could not start EdgeDriverService, falling back to default EdgeDriver ctor", e);
		}

		if (driverService.get() != null && driverService.get() instanceof EdgeDriverService && driverService.get().isRunning()) {
			setDriver(new EdgeDriver((EdgeDriverService) driverService.get(), options));
		} else {
			setDriver(new EdgeDriver(options));
		}
		return configureDriver(getDriver());
	}

	public static WebDriver getRemoteDriver(BrowserType browser, URL gridUrl) {
		MutableCapabilities options = switch (browser) {
			case CHROME -> getChromeOptions();
			case FIREFOX -> getFirefoxOptions();
			case EDGE -> getEdgeOptions();
			default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
		};
		try {
			setDriver(new RemoteWebDriver(gridUrl, options));
		} catch (Exception e) {
			throw new RuntimeException("Could not connect to Selenium Grid", e);
		}
		return configureDriver(getDriver());
	}

	private static WebDriver configureDriver(WebDriver driver) {
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
		driver.manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
		driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
		try {
			Dimension windowSize = driver.manage().window().getSize();
			logger.info("Window size: {}x{}", windowSize.width, windowSize.height);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return driver;
	}

	private static ChromeOptions getChromeOptions() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-gpu", "--no-sandbox", "--remote-allow-origins=*");
		if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Linux) options.addArguments("--headless=new");
		options.setCapability("goog:loggingPrefs", getLoggingPreferences());
		options.addArguments("--safebrowsing-disable-download-protection");
		options.addArguments("--safebrowsing-disable-extension-blacklist");
		String chromeUserDataDir = System.getProperty("SelChromeUserDataDir");
		if (chromeUserDataDir != null) {
			options.addArguments("--user-data-dir=" + chromeUserDataDir);
		}
		setChromeDownloadOptions(options);
		return options;
	}

	private static void setChromeDownloadOptions(ChromeOptions chromeOptions) {
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("download.default_directory", getDownloadDir());
		prefs.put("download.prompt_for_download", false);
		prefs.put("safebrowsing.enabled", true);
		chromeOptions.setExperimentalOption("prefs", prefs);
	}

	public static String getDownloadDir() {
		return System.getProperty("user.home") + File.separator + "downloads";
	}


	private static FirefoxOptions getFirefoxOptions() {
		FirefoxOptions options = new FirefoxOptions();
		if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Linux) options.addArguments("-headless");
		options.setCapability("moz:firefoxOptions", Map.of("args", Collections.emptyList()));
		// Download prefs
		options.addPreference("browser.download.folderList", 2);
		options.addPreference("browser.download.dir", getDownloadDir());
		options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream,text/plain,application/pdf");
		options.addPreference("pdfjs.disabled", true);
		options.setAcceptInsecureCerts(true);
		return options;
	}

	private static EdgeOptions getEdgeOptions() {
		EdgeOptions options = new EdgeOptions();
		options.addArguments("--disable-gpu", "--no-sandbox", "--remote-allow-origins=*");
		options.setCapability(EdgeOptions.LOGGING_PREFS, getLoggingPreferences());
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("download.default_directory", getDownloadDir());
		prefs.put("download.prompt_for_download", false);
		prefs.put("safebrowsing.enabled", true);
		options.setExperimentalOption("prefs", prefs);
		return options;
	}

	private static LoggingPreferences getLoggingPreferences() {
		LoggingPreferences logPrefs = new LoggingPreferences();
		logPrefs.enable(LogType.BROWSER, Level.ALL);
		logPrefs.enable(LogType.DRIVER, Level.INFO);
		return logPrefs;
	}

	private static void setDriverProperty(String browser) {
		OsCheck.OSType os = OsCheck.getOperatingSystemType();
		String osFolder = OsCheck.getDriverFolder();
		String path = switch (browser) {
			case "chrome" -> String.format(CHROME_DRIVER, osFolder) + (os == OsCheck.OSType.Windows ? ".exe" : "");
			case "firefox" -> String.format(FIREFOX_DRIVER, osFolder) + (os == OsCheck.OSType.Windows ? ".exe" : "");
			case "edge" -> String.format(EDGE_DRIVER, osFolder) + (os == OsCheck.OSType.Windows ? ".exe" : "");
			default -> throw new IllegalArgumentException("Unknown browser: " + browser);
		};
		System.setProperty("webdriver." + browser + ".driver", path);
		logger.info("Set {} driver: {}", browser, path);
	}

	public static void saveScreenshot(String name) {
		attachPageScreenshotToAllure(name);
		File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			File target = new File("target/screenshots/" + name + ".png");
			Files.createDirectories(target.toPath().getParent());
			Files.copy(screenshot.toPath(), target.toPath());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void attachPageScreenshotToAllure(String name) {
		byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
		Allure.attachment(name, new ByteArrayInputStream(screenshotBytes));
	}

	public enum BrowserType {
		CHROME, FIREFOX, EDGE
	}

	public static void quitDriver() {
		WebDriver drv = driver.get();
		if (drv != null) {
			try {
				drv.quit();
			} catch (Exception e) {
				logger.warn("Error quitting WebDriver", e);
			}
			driver.remove();
		}

		DriverService service = driverService.get();
		if (service != null) {
			try {
				if (service.isRunning()) {
					service.stop();
				}
			} catch (Exception e) {
				logger.warn("Error stopping driver service", e);
			}
			driverService.remove();
		}
	}
}
