package kt.appmonitor.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.xml.bind.DatatypeConverter;
import kt.appmonitor.SignedData;
import kt.common.jackson.ByteArrayBase64Deserializer;
import org.joda.time.DateTime;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class AppHeartBeatDto implements SignedData {

	private DateTime timestamp;
	private SortedMap<String, Object> metrics;
	
	@JsonDeserialize(using = ByteArrayBase64Deserializer.class)
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
	@JsonIgnore
	@Override
	public byte[] getDataBytes() {
		try {
			LinkedHashMap<String, Object> root = new LinkedHashMap<>();
			root.put("timestamp", timestamp.getMillis());
			if (metrics != null) {
				root.put("metrics", metrics);
			}
			final String data = (new ObjectMapper()).writeValueAsString(root);
			return data.getBytes("UTF-8");
			
		} catch (Exception ex) {
			throw new RuntimeException("Formating of data bytes to sign failed.", ex);
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
			txt.append("0x").append(DatatypeConverter.printHexBinary(signature));
		} else {
			txt.append("n/a");
		}
		txt.append("}");
		return txt.toString();
	}
}
