package kt.common.jackson;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import java.io.IOException;
import org.joda.time.DateTime;


public class DateTimeSerializer extends StdScalarSerializer<DateTime> {

    public DateTimeSerializer() {
        super(DateTime.class);
    }

    @Override
    public void serialize(DateTime dateTime, JsonGenerator jsonGenerator, SerializerProvider provider)
			throws IOException, JsonGenerationException {
		if (dateTime != null) {
			jsonGenerator.writeNumber(dateTime.getMillis());
		} else {
			jsonGenerator.writeNull();
		}
    }
}