package kt.common.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.joda.time.DateTime;


public class DateTimeModule extends SimpleModule {

    public DateTimeModule() {
        addSerializer(DateTime.class, new DateTimeSerializer());
        addDeserializer(DateTime.class, new DateTimeDeserializer());
    }
}
