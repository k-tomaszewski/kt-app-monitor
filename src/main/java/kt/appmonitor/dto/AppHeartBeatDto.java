package kt.appmonitor.dto;

import java.util.Map;
import org.joda.time.DateTime;


public class AppHeartBeatDto {

	private DateTime timestamp;
	private Map<String, Object> metrics;
	private byte[] signature;

	
	public DateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(DateTime timestamp) {
		this.timestamp = timestamp;
	}

	public Map<String, Object> getMetrics() {
		return metrics;
	}

	public void setMetrics(Map<String, Object> metrics) {
		this.metrics = metrics;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
}
