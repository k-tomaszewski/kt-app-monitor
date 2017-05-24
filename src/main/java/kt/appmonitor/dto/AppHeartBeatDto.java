package kt.appmonitor.dto;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import kt.appmonitor.SignedData;
import org.bouncycastle.util.encoders.Hex;
import org.joda.time.DateTime;


public class AppHeartBeatDto implements SignedData {

	private DateTime timestamp;
	private SortedMap<String, Object> metrics;
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

	public void setMetrics(SortedMap<String, Object> metrics) {
		this.metrics = new TreeMap(metrics);
	}
	
	/**
	 * Wszystkie dane oprocz pola {@link #signature}
	 */
	@Override
	public byte[] getDataBytes() {
		try {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			try (DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream)) {
				
				// 1. Long, timestamp in millis
				dataOutputStream.writeLong(timestamp.getMillis());
				
				// 2. Int, number of name-value pairs
				if (metrics != null) {
					dataOutputStream.writeInt(metrics.size());
					
					// for each name-value pair, sorted by names:
					for (Map.Entry<String, Object> entry : metrics.entrySet()) {
						
						// 3. name as UTF
						dataOutputStream.writeUTF(entry.getKey());
						
						// 4. value
						final Object value = entry.getValue();
						if (value instanceof Integer) {
							dataOutputStream.writeInt((Integer)value);
						} else if (value instanceof Long) {
							dataOutputStream.writeLong((Long)value);
						} else {
							dataOutputStream.writeUTF(value.toString());
						}
					}
				}
			}
			return byteArrayOutputStream.toByteArray();
		} catch (Throwable ex) {
			throw new RuntimeException("Cannot create byte representation of data for signature", ex);
		}
	}	

	@Override
	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
	
	@Override
	public String toString() {
		final StringBuilder txt = new StringBuilder();
		txt.append("{timestamp: ").append(timestamp).append(", metrics: ");
		if (metrics != null) {
			txt.append("{").append(metrics.entrySet().stream()
					.map((entry) -> entry.getKey() + ": " + entry.getValue() + "/" + entry.getValue().getClass().getSimpleName())
					.collect(Collectors.joining(", ")))
					.append("}");
		} else {
			txt.append("null");
		}
		txt.append(", signature: ");
		if (signature != null) {
			txt.append("0x").append(Hex.toHexString(signature));
		} else {
			txt.append("n/a");
		}
		txt.append("}");
		return txt.toString();
	}
}
