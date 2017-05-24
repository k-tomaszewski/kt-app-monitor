package kt.appmonitor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import kt.common.crypto.KeyUtil;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.signers.RSADigestSigner;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RsaSigningIT {
	
	private static final Logger LOG = LoggerFactory.getLogger(RsaSigningIT.class);
	private static final String PRIV_KEY_FILE_PATH = "target/test_priv_key.pem";
	private static final String PUB_KEY_FILE_PATH = "target/test_publ_key.pem";
	
	@Test
	public void shouldVerifyRsaSignature() throws IOException, InterruptedException, FileNotFoundException, CryptoException {
		// 1. generate RSA key pair
		Process process = Runtime.getRuntime().exec("openssl genrsa -out " + PRIV_KEY_FILE_PATH);
		int returnValue = process.waitFor();
		logInfo(process.getInputStream());
		logInfo(process.getErrorStream());
		LOG.info("openssl return code: {}", returnValue);
		Assert.assertEquals("RSA private key file not generated", 0, returnValue);
		
		process = Runtime.getRuntime().exec(
				String.format("openssl rsa -in %s -pubout -outform PEM -out %s", PRIV_KEY_FILE_PATH, PUB_KEY_FILE_PATH));
		returnValue = process.waitFor();
		logInfo(process.getInputStream());
		logInfo(process.getErrorStream());
		LOG.info("openssl return code: {}", returnValue);
		Assert.assertEquals("RSA public key file not created", 0, returnValue);
		
		File privKeyFile = new File(PRIV_KEY_FILE_PATH);
		Assert.assertTrue(privKeyFile.exists());
		
		File publKeyFile = new File(PUB_KEY_FILE_PATH);
		Assert.assertTrue(publKeyFile.exists());
		
		// 2. create signature
		final byte[] dataToSign = "This is a test message to be signed.".getBytes("UTF-8");
		
		final byte[] signature = createRsaSignature(dataToSign, privKeyFile);
		Assert.assertNotNull(signature);
		LOG.info("Signature size (bytes): {}", signature.length);
		Assert.assertTrue(signature.length > 0);
		
		// 3. test signature verification
		final SignatureVerifier signatureVerifier = new RSASignatureVerifier(publKeyFile);
		boolean validSignature = signatureVerifier.hasValidSignature(new SignedData() {
			@Override
			public byte[] getDataBytes() {
				return dataToSign;
			}

			@Override
			public byte[] getSignature() {
				return signature;
			}
		});
		Assert.assertTrue(validSignature);
	}
	
	static void logInfo(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line;
		while ((line = reader.readLine()) != null) {
			LOG.info(line);
		}
	}
	
	static byte[] createRsaSignature(byte[] dataToSign, File privKeyFile) throws FileNotFoundException, CryptoException {
		AsymmetricKeyParameter privKey = KeyUtil.loadPrivateKey(new FileInputStream(privKeyFile));
		
		RSADigestSigner signer = new RSADigestSigner(new SHA512Digest());
		signer.init(true, privKey);
		signer.update(dataToSign, 0, dataToSign.length);
		return signer.generateSignature();
	}
}
