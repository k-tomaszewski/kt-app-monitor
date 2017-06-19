package kt.appmonitor.testing;

import javax.annotation.PostConstruct;
import kt.appmonitor.SignatureVerifier;
import kt.appmonitor.SignedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@Profile("dev")
@Component
public class DummySignatureVerifier implements SignatureVerifier {
	
	private static final Logger LOG = LoggerFactory.getLogger(DummySignatureVerifier.class);
	
	@PostConstruct
	public void postConstruct() {
		LOG.warn("Dummy signature verifier instanciated. THIS SHOULD NOT BE USED ON PROD.");
	}

	@Override
	public boolean hasValidSignature(SignedData signedData) {
		return true;
	}
	
}
