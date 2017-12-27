package kt.appmonitor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import kt.appmonitor.data.AppAliveEntry;
import kt.appmonitor.dto.AlertDto;
import kt.appmonitor.dto.AlertType;
import kt.appmonitor.persistence.AppAliveEntryRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Service responsible for generating alerts about state of monitored applications.
 */
@Service
public class AlertingService {
	private static final Logger LOG = LoggerFactory.getLogger(AlertingService.class);

	@Autowired
	private AppAliveEntryRepository appAliveEntryRepo;

	@Autowired
	private ReadableDuration maxDurationBetweenHeartbeats;

	
	public List<AlertDto> getAlertsFor(String appName, Locale locale) {
		DateTimeZone timeZone = getTimeZoneForLocale(locale);
		return generateAlerts(appName, appAliveEntryRepo.findAppAliveEntries(appName),
				maxDurationBetweenHeartbeats, DateTime.now(), timeZone, locale);
	}

	static List<AlertDto> generateAlerts(String appName, List<AppAliveEntry> appAliveEntries,
			ReadableDuration maxDurationBetweenHeartbeats, DateTime now, DateTimeZone timeZone, Locale locale) {

		Validate.notNull(now, "The current time must be given");

		if (appAliveEntries == null || appAliveEntries.isEmpty()) {
			LOG.info("No app-alive entries for application '{}'", appName);
			return Collections.emptyList();
		}
		final List<AlertDto> alerts = new LinkedList<>();

		DateTime refTime = now;
		AppAliveEntry prevAppAliveEntry = null;
		DateTimeFormatter formatter = DateTimeFormat.fullDateTime().withLocale(locale).withZone(timeZone);

		for (AppAliveEntry appAliveEntry : appAliveEntries) {
			Duration fromLastAliveTimeToRefTime = new Duration(appAliveEntry.getAliveToTime(), refTime);
			if (fromLastAliveTimeToRefTime.isLongerThan(maxDurationBetweenHeartbeats)) {
				if (prevAppAliveEntry != null) {
					addHeartbeatRecovered(appName, prevAppAliveEntry, alerts, formatter);
				}				
				addHeartbeatLost(appName, appAliveEntry.getAliveToTime().plus(maxDurationBetweenHeartbeats), appAliveEntry, alerts, formatter);
			}
			refTime = appAliveEntry.getAliveFromTime();
			prevAppAliveEntry = appAliveEntry;
		}

		if (prevAppAliveEntry != null) {
			addHeartbeatRecovered(appName, prevAppAliveEntry, alerts, formatter);
		}

		return alerts;
	}

	static void addHeartbeatLost(String appName, DateTime eventTime, AppAliveEntry appAliveEntry, List<AlertDto> alerts, DateTimeFormatter formatter) {
		alerts.add(new AlertDto(appName, eventTime, AlertType.HEARTBEAT_LOST,
				String.format("The last heartbeat was recorded at %s.", formatter.print(appAliveEntry.getAliveToTime())),
				makeId(appAliveEntry.getId(), AlertType.HEARTBEAT_LOST))
		);
	}

	static void addHeartbeatRecovered(String appName, AppAliveEntry appAliveEntry, List<AlertDto> alerts, DateTimeFormatter formatter) {
		alerts.add(new AlertDto(appName, appAliveEntry.getAliveFromTime(), AlertType.HEARTBEAT_RECOVERED,
				String.format("The last heartbeat was recorded at %s. The first heartbeat: %s.",
						formatter.print(appAliveEntry.getAliveToTime()), formatter.print(appAliveEntry.getAliveFromTime())),
				makeId(appAliveEntry.getId(), AlertType.HEARTBEAT_RECOVERED))
		);
	}
	
	static String makeId(Integer appAliveEntryId, AlertType type) {
		return appAliveEntryId + "/" + type;
	}

	// TODO better implementation
	static DateTimeZone getTimeZoneForLocale(Locale locale) {
		final String countryNameEng = locale.getDisplayCountry(Locale.ROOT);
		if (StringUtils.isNotBlank(countryNameEng)) {
			try {
				return DateTimeZone.forID(countryNameEng);
			} catch (IllegalArgumentException e) {
				LOG.warn("Cannot select time zone by id. " + e.getMessage());
			}
		}
		return DateTimeZone.UTC;
	}	
}
