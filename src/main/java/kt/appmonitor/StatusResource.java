package kt.appmonitor;

import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("status")
@Produces(MediaType.APPLICATION_JSON)
public class StatusResource extends RestResourceBase {
	
    @GET
    public Map<String, Object> getStatus() {
		HealthMonitorService service = getHealthMonitorService();
		return service.getStatusVariables();
    }	
}
