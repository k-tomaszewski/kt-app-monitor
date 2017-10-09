package kt.appmonitor.dto;

import java.util.Collections;
import java.util.List;


/**
 * Represents details about error condition returned back to client.
 */
public class ErrorDetailsDto {
	
	private String message;
	private List<String> logMessages;

	public ErrorDetailsDto(String message, List<String> logMessages) {
		this.message = message;
		this.logMessages = Collections.unmodifiableList(logMessages);
	}

	public String getMessage() {
		return message;
	}

	public List<String> getLogMessages() {
		return logMessages;
	}
}
