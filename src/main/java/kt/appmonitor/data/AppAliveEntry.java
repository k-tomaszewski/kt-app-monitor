package kt.appmonitor.data;

import org.joda.time.DateTime;


public class AppAliveEntry {
	
	private Integer id;
	private String appName;
	private DateTime aliveFromTime;
	private DateTime aliveToTime;
	private DateTime lastModifiedTime;
}
