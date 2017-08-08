package kt.appmonitor.rest;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import kt.appmonitor.HealthMonitorService;
import kt.appmonitor.data.AppAliveEntry;
import kt.appmonitor.dto.AppHeartBeatDto;


@Path("v1")
@Produces(MediaType.APPLICATION_JSON)
public class MainResource extends RestResourceBase {
	
	private HealthMonitorService healthMonitorService;
	
	@PUT
	@Path("{appName}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateAppAlive(@PathParam("appName") String appName, AppHeartBeatDto appHeartBeat) {
		getHealthMonitorService().updateAppAliveEntry(appName, appHeartBeat);
	}
	
	@GET
	@Path("{appName}")
	public List<AppAliveEntry> getAppAliveEntries(@PathParam("appName") String appName) {
		return getHealthMonitorService().getAppAliveEntries(appName);
	}
	
	private HealthMonitorService getHealthMonitorService() {
		if (healthMonitorService == null) {
			healthMonitorService = getBean(HealthMonitorService.class);
		}
		return healthMonitorService;
	}
}
