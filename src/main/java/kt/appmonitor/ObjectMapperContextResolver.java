package kt.appmonitor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * http://stackoverflow.com/questions/29523169/register-jodamodule-in-jax-rs-application
 */
@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    final ObjectMapper mapper = new ObjectMapper();

    public ObjectMapperContextResolver() {
        mapper.registerModule(new JodaModule());
    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }  
}