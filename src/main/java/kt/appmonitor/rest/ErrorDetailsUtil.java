package kt.appmonitor.rest;

import kt.appmonitor.dto.ErrorDetailsDto;
import kt.common.logback.InMemoryCollectingAppender;


public class ErrorDetailsUtil {
	
	public static ErrorDetailsDto createDetailsFor(Throwable t) {
		String message = t.getMessage();
		if (message == null) {
			message = t.getClass().getName();
		}
		return new ErrorDetailsDto(message, InMemoryCollectingAppender.getLogMessages(System.currentTimeMillis() - 2000));
	}
}
