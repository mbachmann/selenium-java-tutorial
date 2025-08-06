package utils;

import java.text.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import ch.qos.logback.classic.spi.ILoggingEvent;

import config.DriverFactory;
import io.qameta.allure.Allure;

@ExtendWith(TestBase.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestBase implements AfterTestExecutionCallback, HasLogger {

	protected WebDriver driver;
	protected WebDriverWait wait;

	protected void setup(String url) {
		driver = DriverFactory.getLocalChromeDriver();
		driver.get(url);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	protected void wait(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@AfterEach
	protected void teardown() {
		printBrowserLogs();
		if (driver != null) {
			driver.quit();
		}
	}

	@Override
	public void afterTestExecution(ExtensionContext context) throws Exception {
		if (context.getExecutionException().isPresent()) {
			String filename = getScreenshotFilename(context);
			DriverFactory.saveScreenshot(filename);
		}
	}


	protected void saveScreenshot(TestInfo info)  {
		if (info.getTestClass().isPresent() || info.getTestMethod().isPresent()) {
			String filename = getScreenshotFilename(info.getTestClass().get().getSimpleName(), info.getTestMethod().get().getName());
			DriverFactory.saveScreenshot(filename);
		}
	}

	private String getScreenshotFilename(ExtensionContext context) {
		return context.getRequiredTestClass().getSimpleName() + "-" +
						  context.getRequiredTestMethod().getName() +
						  LocalDateTime.now().format(DateTimeFormatter.ofPattern("-yyyyMMdd-HHmmss"));
	}

	private String getScreenshotFilename(String className, String methodName) {
		return className + "-" +
			   methodName +
			   LocalDateTime.now().format(DateTimeFormatter.ofPattern("-yyyyMMdd-HHmmss"));
	}

	protected void printStep(String stepName) {
		Allure.step(stepName);
		getLogger().info("Step: " + stepName);
	}


	public void printBrowserLogs() {

		try {
			Logs browserLogs = driver.manage().logs();
			LogEntries logEntries = browserLogs.get(LogType.BROWSER);
			StringBuilder logs = new StringBuilder();

			// Log Levels LoggerFactory: error, warn, info, debug, trace,
			// Log Levels from Browser: OFF, SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST, ALL
			for (LogEntry entry : logEntries) {
				logs.append(formatDate(entry.getTimestamp()))
					.append(" ")
					.append("[browser]")
					.append(" ")
					.append(entry.getLevel())
					.append(" ")
					.append(entry.getMessage());
				logs.append(System.lineSeparator());
			}

			if (!logs.isEmpty()) {
				getLogger().info("{}{}", System.lineSeparator(), logs);
			}

			addLogEntriesToAllureFromMapAppender();

		} catch (Exception e) {
			getLogger().error("Failed to get browser logs {}", String.valueOf(e));
		}
	}

	private static String formatDate(long timestamp) {
		Date date = new Date(timestamp);
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return format.format(date);
	}

	private void addLogEntriesToAllureFromMapAppender() {
		List<String> logEntries = new ArrayList<>();
		ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
		MapAppender mapAppender = (MapAppender)root.getAppender("map");

		Map<String, ILoggingEvent> eventMap = mapAppender.getEventMap();
		eventMap.forEach((k, event) -> {
			logEntries.add(mapAppender.createLogEntry(event));
		});

		List<String> logs = new ArrayList<>();
		logEntries.forEach(entry -> {
			if (entry.contains("[browser]")) {
				if (entry.contains("\n") && entry.contains("\r")) {
					entry = entry.replace("\r", "");
				}
				logs.addAll(Arrays.asList(entry.split("\n")));
			} else {
				logs.add(entry); // + System.lineSeparator());
			}
		});
		logs.sort(String::compareTo);
		Allure.addAttachment("log", String.join("\n", logs));
		eventMap.clear();
	}
}
