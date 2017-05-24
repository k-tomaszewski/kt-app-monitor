package kt.appmonitor;


public interface SignedData {
	
	byte[] getDataBytes();
	byte[] getSignature();
}
