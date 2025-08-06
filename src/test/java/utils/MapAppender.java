package utils;

import java.text.*;
import java.util.*;
import java.util.concurrent.*;

import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.spi.*;
import ch.qos.logback.core.*;

public class MapAppender extends AppenderBase<ILoggingEvent> {

	private final ConcurrentMap<String, ILoggingEvent> eventMap = new ConcurrentHashMap<>();

	@Override
	protected void append(ILoggingEvent event) {
		eventMap.put(String.valueOf(System.currentTimeMillis()), event);
	}

	public Map<String, ILoggingEvent> getEventMap() {
		return eventMap;
	}

	public String createLogEntry(ILoggingEvent event) {
		String logEntry =  formatDate(event.getTimeStamp()) + " [" + event.getThreadName() + "] " + event.getLevel() + "  " + event.getLoggerName() + " " + event.getFormattedMessage();
		if (event.getThrowableProxy() != null) {
			logEntry = logEntry + System.lineSeparator() + stackTraceToString(event);
		}
		return logEntry;
	}

	public String stackTraceToString(ILoggingEvent event) {
		StringBuilder sbuf = new StringBuilder(128);

		IThrowableProxy proxy = event.getThrowableProxy();
		if (proxy != null) {
			ThrowableProxyConverter converter = new ThrowableProxyConverter();
			converter.start();
			sbuf.append(converter.convert(event));
			sbuf.append(CoreConstants.LINE_SEPARATOR);
		}
		return sbuf.toString();
	}

	private String formatDate(long timestamp) {
		Date date = new Date(timestamp);
		Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return format.format(date);
	}


}