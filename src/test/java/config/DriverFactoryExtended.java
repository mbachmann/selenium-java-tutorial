package config;

import io.qameta.allure.Allure;
import org.openqa.selenium.*;
import org.openqa.selenium.bidi.log.LogLevel;
import org.openqa.selenium.bidi.module.LogInspector;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.logging.Level;

public class DriverFactoryExtended implements HasLogger {

	private static final Logger logger = LoggerFactory.getLogger(DriverFactoryExtended.class);

	private static final String PROJECT_CHROME_DRIVER = "src/test/resources/drivers/%s/chromedriver-138";
	private static final String PROJECT_FIREFOX_DRIVER = "src/test/resources/drivers/%s/geckodriver";
	private static final String PROJECT_EDGE_DRIVER = "src/test/resources/drivers/%s/msedgedriver-139";

	private static final String LINUX_CHROME_DRIVER = "/usr/local/bin/chromedriver";
	private static final String LINUX_FIREFOX_DRIVER = "/usr/local/bin/geckodriver";
	private static final String LINUX_EDGE_DRIVER = "/usr/local/bin/msedgedriver";

	private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
	private static final ThreadLocal<DriverService> driverService = new ThreadLocal<>();

	public static WebDriver getDriver() {
		return driver.get();
	}

	private static void setDriver(WebDriver driverInstance) {
		driver.set(driverInstance);
	}

	/**
	 * Initializes the WebDriver based on system properties.
	 * If no properties "browser" are set, defaults to Chrome.
	 * If no remoteUrl is set, this method sets the WebDriver based on the browser type and initializes it locally with DriverService.
	 *
	 * @return WebDriver instance
	 */
	public static WebDriver initDriver() {
		String browser = System.getProperty("browser", "chrome").toLowerCase();
		String remoteUrl = System.getProperty("remoteUrl", "").trim();
		logger.info("Initializing WebDriver with browser: [{}] and remote URL: [{}]", browser, remoteUrl);

		URL url = getRemoteUrl(remoteUrl);
		try {
            return switch (browser) {
                case "firefox" ->
                        (remoteUrl.isEmpty()) ? getLocalFirefoxDriver() : getRemoteDriver(BrowserType.FIREFOX, url);
                case "edge" -> (remoteUrl.isEmpty()) ? getLocalEdgeDriver() : getRemoteDriver(BrowserType.EDGE, url);
                default -> (remoteUrl.isEmpty()) ? getLocalChromeDriver() : getRemoteDriver(BrowserType.CHROME, url);
            };
		} catch (Exception e) {
			throw new RuntimeException("Error at creation of the WebDrivers: " + e.getMessage(), e);
		}
	}

	/**
	 * Initializes the WebDriver with specified browser and remote URL.
	 *
	 * @param browser   the browser type (e.g., "chrome", "firefox", "edge")
	 * @param remoteUrl the URL of the Selenium Grid or remote WebDriver server
	 * @return WebDriver instance
	 */
	public static WebDriver initDriver(String browser, String remoteUrl) {
		System.setProperty("browser", browser);
		System.setProperty("remoteUrl", remoteUrl);
		return initDriver();
	}


	/**
	 * Initializes the WebDriver with Local ChromeDriver.
	 * This method sets the ChromeDriver system property and starts the ChromeDriverService.
	 * @return WebDriver instance
	 */
	public static WebDriver getLocalChromeDriver() {
		setDriverProperty("chrome");
		configureSeleniumLogging();
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

	/**
	 * Initializes the WebDriver with Local FirefoxDriver.
	 * This method sets the GeckoDriver system property and starts the GeckoDriverService.
	 * @return WebDriver instance
	 */
	public static WebDriver getLocalFirefoxDriver() {
		setDriverProperty("firefox");
		logGeckoDriverVersion();
		FirefoxOptions options = getFirefoxOptions();

		// create and start GeckoDriverService
		GeckoDriverService service = new GeckoDriverService.Builder()
				.usingDriverExecutable(new File(System.getProperty("webdriver.firefox.driver")))
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
			configureSeleniumLogging();
			try(LogInspector logInspector = new LogInspector(getDriver())) {
				// configureSeleniumLogging();
				logInspector.onJavaScriptLog(logEntry -> {
					log(logEntry.getLevel(), "[browser] " + normalizeLog(logEntry.getText()));
				});

			} catch (Exception e) {
				logger.error("Error setting up Firefox log inspector", e);
			}
		} else {
			if (getDriver() == null) {
				setDriver(new FirefoxDriver(options));
			}
		}
		return configureDriver(getDriver());
	}

	/**
	 * Initializes the WebDriver with Local EdgeDriver.
	 * This method sets the EdgeDriver system property and starts the EdgeDriverService.
	 * @return WebDriver instance
	 */
	public static WebDriver getLocalEdgeDriver() {
		setDriverProperty("edge");
		configureSeleniumLogging();
		EdgeOptions options = getEdgeOptions();

		// create and start EdgeDriverService
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

	/**
	 * Initializes the WebDriver with Remote WebDriver using Selenium Grid.
	 * This method sets the browser options based on the specified browser type and connects to the Selenium Grid URL.
	 *
	 * @param browser  the browser type (e.g., BrowserType.CHROME, BrowserType.FIREFOX, BrowserType.EDGE)
	 * @param gridUrl  the URL of the Selenium Grid
	 * @return WebDriver instance
	 */
	public static WebDriver getRemoteDriver(BrowserType browser, URL gridUrl) {
		configureSeleniumLogging();
		MutableCapabilities options = switch (browser) {
			case CHROME -> getChromeOptions();
			case FIREFOX -> getFirefoxOptions();
			case EDGE -> getEdgeOptions();
			default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
		};
		try {
			setDriver(new RemoteWebDriver(gridUrl, options));
			((RemoteWebDriver) getDriver()).setFileDetector(new LocalFileDetector());
		} catch (Exception e) {
			throw new RuntimeException("Could not connect to Selenium Grid", e);
		}
		return configureDriver(getDriver());
	}

	private static WebDriver configureDriver(WebDriver driver) {

		getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
		getDriver().manage().timeouts().scriptTimeout(Duration.ofMinutes(2));
		getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));

		getDriver().manage().window().setSize(new Dimension(1920,1080));

		try {
			Dimension windowSize = getDriver().manage().window().getSize();
			logger.info("Window size: {}x{}", windowSize.width, windowSize.height);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return driver;
	}

	private static void configureSeleniumLogging() {
		java.util.logging.Logger.getLogger(org.openqa.selenium.bidi.Connection.class.getName()).setLevel(Level.WARNING);
	}

	private static ChromeOptions getChromeOptions() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--disable-gpu", "--no-sandbox", "--remote-allow-origins=*", "--disable-dev-shm-usage");
		if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Linux) options.addArguments("--headless=new");
		options.setCapability("goog:loggingPrefs", getLoggingPreferences());
		options.addArguments("--safebrowsing-disable-download-protection");
		options.addArguments("--safebrowsing-disable-extension-blacklist");
		options.addArguments("--enable-logging=stderr", "--v=1");
		options.setAcceptInsecureCerts(true);
		options.setPageLoadStrategy(PageLoadStrategy.EAGER);
		options.addArguments("--user-data-dir=" + getUserDataDir());
		setChromeDownloadOptions(options);
		getProxyInformation().ifPresent(proxyInformation -> {options.setCapability("proxy", proxyInformation);});
		String optionsString = getOptionsAsString(options);
		logger.info(optionsString);
		return options;
	}

	private static void setChromeDownloadOptions(ChromeOptions chromeOptions) {

		Map<String, Object> prefs = new HashMap<>();
		prefs.put("download.default_directory", getBrowserDownloadDir());
		prefs.put("download.prompt_for_download", false);
		prefs.put("safebrowsing.enabled", true);
		chromeOptions.setExperimentalOption("prefs", prefs);
	}

	public static String getUserDataDir() {
		try {
			String userDataDir = System.getProperty("UserDataDir");
			// Fallback: create a temp dir if not provided
			if (userDataDir == null || userDataDir.isBlank()) {
				userDataDir = Files.createTempDirectory("chrome-profile-").toString();
			}
			// Make sure it exists
			Files.createDirectories(Path.of(userDataDir));
			return userDataDir;

		} catch (Exception e) {
			logger.error("Error creating Chrome user data directory: {}", e.getMessage());
			throw new RuntimeException("Could not create Chrome user data directory", e);
		}
	}

	public static String getDownloadDir() {
		String remoteUrl = System.getProperty("remoteUrl", "").trim();
		if (!remoteUrl.isEmpty()) {
			return "downloads"; // Default download directory for Selenium Grid in docker compose
		} else {
			return System.getProperty("user.home") + File.separator + "downloads";
		}
	}

	private static String getBrowserDownloadDir() {
		String remoteUrl = System.getProperty("remoteUrl", "").trim();
		if (!remoteUrl.isEmpty()) {
			return "/home/seluser/Downloads"; // Default download directory for Selenium Grid in docker compose
		} else {
			return System.getProperty("user.home") + File.separator + "downloads";
		}
	}

	private static FirefoxOptions getFirefoxOptions() {
		FirefoxOptions options = new FirefoxOptions();
		options.setLogLevel(FirefoxDriverLogLevel.WARN);
		options.addArguments("--disable-gpu", "--no-sandbox");
		options.addPreference("network.cors_preflight.allow", true);
		options.addPreference("network.cors_preflight.max_age", 3600);
		options.setCapability("moz:firefoxOptions", Map.of(
				"log", Map.of("level", "warn")
		));
		if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Linux) options.addArguments("-headless");
		if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Windows) {
			findFirefoxBinaryOnWindows().ifPresent(options::setBinary);
		}
			// Download prefs
		options.addPreference("browser.download.folderList", 2);
		options.setCapability("webSocketUrl", true);
		options.addPreference("browser.download.dir", getBrowserDownloadDir());
		options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream,text/plain,application/pdf");
		options.addPreference("pdfjs.disabled", true);
		getProxyInformation().ifPresent(proxyInformation -> {options.setCapability("proxy", proxyInformation);});
		options.setAcceptInsecureCerts(true);
		options.addArguments("--user-data-dir=" + getUserDataDir());
		options.setPageLoadStrategy(PageLoadStrategy.EAGER);
		return options;
	}

	private static EdgeOptions getEdgeOptions() {
		EdgeOptions options = new EdgeOptions();
		options.addArguments("--disable-gpu", "--no-sandbox", "--remote-allow-origins=*", "--disable-dev-shm-usage");
		if (OsCheck.getOperatingSystemType() == OsCheck.OSType.Linux) options.addArguments("--headless=new");
		options.setCapability(EdgeOptions.LOGGING_PREFS, getLoggingPreferences());
		Map<String, Object> prefs = new HashMap<>();
		prefs.put("download.default_directory", getBrowserDownloadDir());
		prefs.put("download.prompt_for_download", false);
		prefs.put("download.directory_upgrade", true);
		prefs.put("safebrowsing.enabled", true);
		options.setAcceptInsecureCerts(true);
		options.addArguments("--user-data-dir=" + getUserDataDir());
		options.setPageLoadStrategy(PageLoadStrategy.EAGER);
		getProxyInformation().ifPresent(proxyInformation -> {options.setCapability("proxy", proxyInformation);});
		options.setExperimentalOption("prefs", prefs);
		return options;
	}

	private void configureChromeRemoteDownloadOptions(ChromeOptions chromeOptions) {
		chromeOptions.setExperimentalOption("prefs", Map.of(
				"download.default_directory", getBrowserDownloadDir(),
				"download.prompt_for_download", false,
				"download.directory_upgrade", true,
				"safebrowsing.enabled", true
		));

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
		String path = null;
		if (os == OsCheck.OSType.Linux) {
			path = switch (browser) {
				case "chrome" -> LINUX_CHROME_DRIVER;
				case "firefox" -> LINUX_FIREFOX_DRIVER;
				case "edge" -> LINUX_EDGE_DRIVER;
				default -> throw new IllegalArgumentException("Unknown browser: " + browser);
			};
		} else {
			path = switch (browser) {
				case "chrome" -> String.format(PROJECT_CHROME_DRIVER, osFolder) + (os == OsCheck.OSType.Windows ? ".exe" : "");
				case "firefox" -> String.format(PROJECT_FIREFOX_DRIVER, osFolder) + (os == OsCheck.OSType.Windows ? ".exe" : "");
				case "edge" -> String.format(PROJECT_EDGE_DRIVER, osFolder) + (os == OsCheck.OSType.Windows ? ".exe" : "");
				default -> throw new IllegalArgumentException("Unknown browser: " + browser);
			};
		}
		File driverFile = new File(path);
		if (!driverFile.exists()) {
			throw new IllegalStateException(
					"WebDriver binary for " + browser + " not found at: " + driverFile.getAbsolutePath()
			);
		}

		if (!driverFile.canExecute()) {
			throw new IllegalStateException(
					"WebDriver binary for " + browser + "  found but not executable at: " + driverFile.getAbsolutePath()
			);
		}
		System.setProperty("webdriver." + browser + ".driver", path);
		logger.debug("Set {} driver: {}", browser, path);
	}

	public static void saveScreenshot(String name) {
		attachPageScreenshotToAllure(name);
		File screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
		try {
			File target = new File("target/screenshots/" + name + ".png");
			Files.createDirectories(target.toPath().getParent());
			Files.copy(screenshot.toPath(), target.toPath());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public static void attachPageScreenshotToAllure(String name) {
		byte[] screenshotBytes = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
		Allure.attachment(name, new ByteArrayInputStream(screenshotBytes));
	}

	public static void attachLogResponseToAllure(String responseName, String responseContent) {
		Allure.addAttachment(responseName, "text/plain", responseContent);
	}

	public String getBrowserUserLanguage() {
		JavascriptExecutor executor = (JavascriptExecutor) getDriver();
		return (String) executor.executeScript("return window.navigator.language");
	}

	public String getUserAgent() {
		JavascriptExecutor executor = (JavascriptExecutor) getDriver();
		return (String) executor.executeScript("return window.navigator.userAgent");
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
	}

	public static void quitService() {
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
			logger.info("Driver service stopped");
		}
	}

	public static void quitDriverAndService() {
		quitDriver();
		quitService();
	}

	private static URL getRemoteUrl(String remoteUrl) {
		if (remoteUrl == null || remoteUrl.isEmpty()) {
			return null;
		}
		try {;
			URI uri = new URI(remoteUrl);
            return uri.toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			throw new RuntimeException("Invalid remote URL: " + remoteUrl, e);
		}
    }

	public static void log(LogLevel level, String message) {
		switch (level) {
			case DEBUG: logger.debug(message); break;
			case INFO: logger.info(message); break;
			case WARNING: logger.warn(message); break;
			case ERROR: logger.error(message); break;
		}
	}

	/**
	 * Logs geckodriver version from the executable.
	 */
	private static void logGeckoDriverVersion() {
		String geckoPath = System.getProperty("webdriver.firefox.driver");
		if (geckoPath != null) {
			try {
				Process process = new ProcessBuilder(geckoPath, "--version")
						.redirectErrorStream(true)
						.start();
				try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
					String line;
					while ((line = reader.readLine()) != null) {
						logger.debug("[GeckoDriver] {}", line);
					}
				}
			} catch (IOException e) {
				logger.warn("Could not get geckodriver version: {}", e.getMessage());
			}
		} else {
			logger.warn("webdriver.gecko.driver not set");
		}
	}

	private static Optional<String> findFirefoxBinaryOnWindows() {
		// Common paths
		String[] candidates = new String[] {
				System.getenv("MOZ_FIREFOX_BINARY"),                                  // allow override via env
				"C:\\Program Files\\Mozilla Firefox\\firefox.exe",
				"C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe"
		};
		for (String p : candidates) {
			if (p != null && new java.io.File(p).isFile()) {
				logger.debug("Found firefox binary on windows {}", p);
				return Optional.of(p);
			}
		}
		// Try PATH
		try {
			Process proc = new ProcessBuilder("where", "firefox").redirectErrorStream(true).start();
			try (java.io.BufferedReader r = new java.io.BufferedReader(new java.io.InputStreamReader(proc.getInputStream()))) {
				String line;
				while ((line = r.readLine()) != null) {
					if (line.toLowerCase().endsWith("firefox.exe") && new java.io.File(line).isFile()) {
						logger.debug("Found firefox binary on windows {}", line);
						return Optional.of(line.trim());
					}
				}
			}
		} catch (Exception ignored) {}
		logger.warn("Could not find firefox binary on windows");
		return Optional.empty();
	}

	private static String normalizeLog(String input) {
		return input
				.replace("ΓÇÿ", "'")  // fix single quote mojibake
				.replace("ΓÇ£", "\"") // fix double quote mojibake
				.replace("ΓÇ¥", "\"") // fix double quote mojibake
				.replace("ΓÇô", "-")  // fix dash mojibake
				.replace("ΓÇó", "•")  // bullet point
				.replace("ΓÇ£", "\"")
				.replace("“", "\"")
				.replace("”", "\"")
				.replace("’", "'")
				.replace("–", "-")
				.replace("…", "...")
				.replace("\u00A0", " ") // non-breaking space to normal space
				.trim();
	}

	private static Optional<Proxy> getProxyInformation(){
		String proxyHost = System.getProperty("http.proxyHost", System.getenv("HTTP_PROXY_HOST"));
		String proxyPort = System.getProperty("http.proxyPort", System.getenv("HTTP_PROXY_PORT"));
		if (proxyHost != null && proxyPort != null) {
			org.openqa.selenium.Proxy px = new org.openqa.selenium.Proxy();
			String hp = proxyHost + ":" + proxyPort;
			px.setHttpProxy(hp).setSslProxy(hp).setFtpProxy(hp);
			return Optional.of(px);
		}
		return Optional.empty();
	}

	private static String getOptionsAsString(MutableCapabilities options) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Object> entry : options.asMap().entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
		}
		return sb.toString().replaceAll(", $", "");
	}

}
