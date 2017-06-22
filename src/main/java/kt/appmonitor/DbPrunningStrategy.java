package kt.appmonitor;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class DbPrunningStrategy {
	
	private static final Logger LOG = LoggerFactory.getLogger(DbPrunningStrategy.class);
	
	private DateTime lastPrunningTime;
	
	@Async
	public void run() {
		final DateTime now = DateTime.now();
		if (lastPrunningTime != null && lastPrunningTime.plusDays(1).isAfter(now)) {
			LOG.info("DB prunning was executed at {} so it's not needed now.", lastPrunningTime);
			return;
		}
		LOG.info("Starting DB prunning... (TODO)");
		
		DateTime thresholdTime = now.minusMonths(2);
		
		// TODO
		
		lastPrunningTime = now;
	}
}
