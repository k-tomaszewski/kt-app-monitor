package kt.common.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import java.io.IOException;
import net.iharder.Base64;


public class ByteArrayBase64Deserializer extends StdScalarDeserializer<byte[]> {
	
	public ByteArrayBase64Deserializer() {
		super(byte[].class);
	}

	@Override
	public byte[] deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
		JsonToken currentToken = jsonParser.getCurrentToken();
		if (currentToken == JsonToken.VALUE_STRING) {
			return Base64.decode(jsonParser.getText());
		} else if (currentToken == JsonToken.VALUE_NULL) {
			return null;
		}
		throw deserializationContext.wrongTokenException(jsonParser, currentToken, "Invalid byte array value");		
	}
	
}
