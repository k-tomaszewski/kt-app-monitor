package kt.appmonitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.function.Supplier;
import kt.common.crypto.KeyUtil;
import net.iharder.Base64;
import org.apache.commons.lang3.Validate;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class RSASignatureVerifier implements SignatureVerifier {
	
	private static final Logger LOG = LoggerFactory.getLogger(RSASignatureVerifier.class);
	private static final String PUBL_KEY_RES = "public_key.pem";
	
	private final Supplier<InputStream> publicKeyStreamSupplier;
	
	
	public RSASignatureVerifier() {
		publicKeyStreamSupplier = () -> {
			InputStream is = RSASignatureVerifier.class.getClassLoader().getResourceAsStream(PUBL_KEY_RES);
			Validate.notNull(is, "Resource with public key not found");
			return is;
		};
		LOG.info("Default RSASignatureVerifier instance created.");
	}
	
	// for testing
	RSASignatureVerifier(File publicKeyFile) {
		publicKeyStreamSupplier = () -> {
			try {
				return new FileInputStream(publicKeyFile);
			} catch (FileNotFoundException ex) {
				throw new RuntimeException("Public key file not found: " + publicKeyFile.getAbsolutePath(), ex);
			}
		};
		LOG.info("Custom RSASignatureVerifier instance created. Key source: {}", publicKeyFile.getAbsolutePath());
	}

	@Override
	public boolean hasValidSignature(SignedData signedData) {
		RSADigestSigner signer = new RSADigestSigner(new SHA512Digest());
		signer.init(false, getRSAPublicKey());
		initSignerWithData(signer, signedData.getDataBytes());
		return signer.verifySignature(signedData.getSignature());
	}
	
	static void initSignerWithData(RSADigestSigner signer, byte[] data) {
		LOG.info("Signed data: {}", Base64.encodeBytes(data));
		signer.update(data, 0, data.length);
	}
	
	private RSAKeyParameters getRSAPublicKey() {
		try {
			InputStream publKeyInpStream = publicKeyStreamSupplier.get();
			return (RSAKeyParameters) KeyUtil.loadPublicKey(publKeyInpStream);
		} catch (Throwable t) {
			throw new RuntimeException("Cannot load RSA public key. " + t.getMessage(), t);
		}
	}
}
