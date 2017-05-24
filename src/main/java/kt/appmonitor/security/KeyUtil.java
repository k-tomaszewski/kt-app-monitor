package kt.appmonitor.security;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.lang3.Validate;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KeyUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(KeyUtil.class);
	
	
	public static AsymmetricKeyParameter loadPublicKey(InputStream is) {
		SubjectPublicKeyInfo spki = (SubjectPublicKeyInfo) readPemObject(is);
		try {
			return PublicKeyFactory.createKey(spki);
		} catch (IOException ex) {
			throw new RuntimeException("Cannot create public key object based on input data", ex);
		}
	}
	
	public static AsymmetricKeyParameter loadPrivateKey(InputStream is) {
		PEMKeyPair keyPair = (PEMKeyPair) readPemObject(is);
		PrivateKeyInfo pki = keyPair.getPrivateKeyInfo();
		try {
			return PrivateKeyFactory.createKey(pki);
		} catch (IOException ex) {
			throw new RuntimeException("Cannot create private key object based on input data", ex);
		}
	}

	private static Object readPemObject(InputStream is) {
		try {
			Validate.notNull(is, "Input data stream cannot be null");
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			PEMParser pemParser = new PEMParser(isr);
			
			Object obj = pemParser.readObject();
			if (obj == null) {
				throw new Exception("No PEM object found");
			}
			
			LOG.debug("PEM object class: {}", obj.getClass().getName());
			return obj;
			
		} catch (Throwable ex) {
			throw new RuntimeException("Cannot read PEM object from input data", ex);
		}
	}
}
