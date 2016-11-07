package kt.appmonitor;

import javax.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;


public class MainResourceTest extends JerseyTest {

    @Override
    protected Application configure() {
        return new ResourceConfig(MainResource.class);
    }

    /**
     * Test to see that the message "Got it!" is sent in the response.
     */
	@Ignore
    @Test
    public void testGetIt() {
        final String responseMsg = target().path("v1").request().get(String.class);

        assertEquals("Hello, Heroku!", responseMsg);
    }
}
