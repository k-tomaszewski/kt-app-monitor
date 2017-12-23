package kt.appmonitor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import kt.appmonitor.data.AppAliveEntry;
import kt.appmonitor.dto.AlertDto;
import kt.appmonitor.persistence.AppAliveEntryRepository;
import org.apache.commons.lang3.Validate;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.ReadableDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Service responsible for generating alerts about state of monitored applications.
 */
@Service
public class AlertingService {

	static final String HEARTBEAT_LOST_TITLE_PATTERN = "%s heartbeat lost!";
	static final String HEARTBEAT_RECOVERED_TITLE_PATTERN = "%s heartbeat appeared.";

	private static final Logger LOG = LoggerFactory.getLogger(AlertingService.class);

	@Autowired
	private AppAliveEntryRepository appAliveEntryRepo;

	@Autowired
	private ReadableDuration maxDurationBetweenHeartbeats;

	
	public List<AlertDto> getAlertsFor(String appName) {
		return generateAlerts(appName, appAliveEntryRepo.findAppAliveEntries(appName),
				maxDurationBetweenHeartbeats, DateTime.now());
	}

	static List<AlertDto> generateAlerts(String appName, List<AppAliveEntry> appAliveEntries,
			ReadableDuration maxDurationBetweenHeartbeats, DateTime now) {

		Validate.notNull(now, "The current time must be given");

		if (appAliveEntries == null || appAliveEntries.isEmpty()) {
			LOG.info("No app-alive entries for application '{}'", appName);
			return Collections.emptyList();
		}
		final List<AlertDto> alerts = new LinkedList<>();

		DateTime refTime = now;
		AppAliveEntry prevAppAliveEntry = null;

		for (AppAliveEntry appAliveEntry : appAliveEntries) {
			Duration fromLastAliveTimeToRefTime = new Duration(appAliveEntry.getAliveToTime(), refTime);
			if (fromLastAliveTimeToRefTime.isLongerThan(maxDurationBetweenHeartbeats)) {
				if (prevAppAliveEntry != null) {
					addHeartbeatRecovered(appName, prevAppAliveEntry, alerts);
				}				
				addHeartbeatLost(appName, appAliveEntry.getAliveToTime().plus(maxDurationBetweenHeartbeats), appAliveEntry, alerts);
			}
			refTime = appAliveEntry.getAliveFromTime();
			prevAppAliveEntry = appAliveEntry;
		}

		if (prevAppAliveEntry != null) {
			addHeartbeatRecovered(appName, prevAppAliveEntry, alerts);
		}

		return alerts;
	}

	static void addHeartbeatLost(String appName, DateTime eventTime, AppAliveEntry appAliveEntry, List<AlertDto> alerts) {
		alerts.add(new AlertDto(appName, eventTime, String.format(HEARTBEAT_LOST_TITLE_PATTERN, appName),
				String.format("The last heartbeat was recorded at %s.", appAliveEntry.getAliveToTime())
		));
	}

	static void addHeartbeatRecovered(String appName, AppAliveEntry appAliveEntry, List<AlertDto> alerts) {
		alerts.add(new AlertDto(appName, appAliveEntry.getAliveFromTime(), String.format(HEARTBEAT_RECOVERED_TITLE_PATTERN, appName),
				String.format("The first heartbeat was recorded at %s. The last heartbeat was recorded at %s",
						appAliveEntry.getAliveFromTime(), appAliveEntry.getAliveToTime())
		));
	}
}
