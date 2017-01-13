package kt.appmonitor.dto;

import java.util.Map;
import java.util.stream.Collectors;
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
	
	public String toString() {
		final StringBuilder txt = new StringBuilder();
		txt.append("{timestamp: ").append(timestamp).append(", metrics: ");
		if (metrics != null) {
			txt.append("{").append(metrics.entrySet().stream()
					.map((entry) -> entry.getKey() + ": " + entry.getValue()).collect(Collectors.joining(", ")))
					.append("}");
		} else {
			txt.append("null");
		}
		txt.append(", signature: ");
		if (signature != null) {
			txt.append("<").append(signature.length).append("B>");
		} else {
			txt.append("null");
		}
		txt.append("}");
		return txt.toString();
	}
}
