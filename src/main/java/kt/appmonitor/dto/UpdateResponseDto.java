package kt.appmonitor.dto;

/**
 * Data structure returned by {@link kt.appmonitor.rest.MainResource#updateAppAlive(java.lang.String, kt.appmonitor.dto.AppHeartBeatDto) }
 */
public class UpdateResponseDto {
	
	/**
	 * Information for the client: how many minutes should elapse before the next heart-beat should be sent.
	 */
	private int nextUpdateDelayMinutes;

	
	public UpdateResponseDto(long nextUpdateDelayMinutes) {
		this.nextUpdateDelayMinutes = (int)nextUpdateDelayMinutes;
	}

	public int getNextUpdateDelayMinutes() {
		return nextUpdateDelayMinutes;
	}
}
