package kt.appmonitor;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import kt.appmonitor.data.AppAliveEntry;
import kt.appmonitor.dto.AlertDto;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;


public class AlertingServiceTest {
	
	@Test
	public void shouldGenerateAlertWhenLastHeartbeatTooOld() {
		// given
		final DateTime aliveStart = DateTime.parse("2017-08-08T10:00");
		final DateTime aliveEnd = aliveStart.plusHours(2);
		
		List<AppAliveEntry> appAliveEntries = Arrays.asList(
			new AppAliveEntry("app-name", aliveStart, aliveEnd, aliveEnd)
		);
		
		// when
		List<AlertDto> alerts = AlertingService.generateAlerts(
			"app-name", appAliveEntries, Duration.standardMinutes(30), aliveEnd.plusMinutes(31), DateTimeZone.UTC, Locale.ROOT);
		
		// then
		Assert.assertNotNull(alerts);
		Assert.assertEquals(2, alerts.size());
		
		AlertDto alert = alerts.get(0);
		Assert.assertNotNull(alert);
		Assert.assertEquals(aliveEnd.plus(Duration.standardMinutes(30)), alert.getEventTime());
	}
}
