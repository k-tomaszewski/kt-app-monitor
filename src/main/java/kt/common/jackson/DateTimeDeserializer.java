package kt.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import java.io.IOException;
import org.joda.time.DateTime;


public class DateTimeDeserializer extends StdScalarDeserializer<DateTime> {

    public DateTimeDeserializer() {
        super(DateTime.class);
    }

    @Override
    public DateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) 
			throws IOException, JsonProcessingException {
		JsonToken currentToken = jsonParser.getCurrentToken();
		if (currentToken == JsonToken.VALUE_NUMBER_INT) {
			return new DateTime(jsonParser.getLongValue());
		} else if (currentToken == JsonToken.VALUE_NULL) {
			return null;
		}
		throw deserializationContext.wrongTokenException(jsonParser, currentToken, "Invalid date & time value (it should be a number)");
    }
}