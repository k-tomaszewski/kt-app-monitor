package kt.appmonitor;


public interface SignatureVerifier {
	
	boolean hasValidSignature(SignedData signedData);
}
