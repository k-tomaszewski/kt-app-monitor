package kt.appmonitor.rest;

import kt.appmonitor.rest.RestResourceBase;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import kt.appmonitor.HealthMonitorService;


@Path("status")
@Produces(MediaType.APPLICATION_JSON)
public class StatusResource extends RestResourceBase {
	
    @GET
    public Map<String, Object> getStatus() {
		return getBean(HealthMonitorService.class).getStatusVariables();
    }	
}
