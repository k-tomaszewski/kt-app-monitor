package kt.appmonitor;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

	@Override
	public Response toResponse(IllegalArgumentException e) {
		return Response.status(Response.Status.BAD_REQUEST)
			.entity(e.getMessage())
			.type(MediaType.TEXT_HTML_TYPE)
			.build();
	}
}
