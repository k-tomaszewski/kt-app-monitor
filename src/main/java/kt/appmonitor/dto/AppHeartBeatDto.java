package kt.appmonitor.dto;

import org.joda.time.DateTime;


public class AppHeartBeatDto {

	private DateTime timestamp;
	
	private byte[] signature;

	
	public DateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(DateTime timestamp) {
		this.timestamp = timestamp;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
}
