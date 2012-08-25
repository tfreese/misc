/**
 * Created: 03.04.2012
 */

package de.freese.sonstiges.security;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

import org.apache.commons.codec.binary.Base64;

/**
 * @author Thomas Freese
 * @see SSL.txt
 */
@SuppressWarnings("javadoc")
public class TestKeystore
{
	/**
	 * @param args String[]
	 */
	public static void main(final String[] args)
	{
		// final CertificateFactory cf = CertificateFactory.getInstance( "X.509" );
		// return cf.generateCertificate( inputStream ).getPublicKey();

		char[] keystorePSW = "gehaim".toCharArray();
		String alias = "test-rsa";
		char[] aliasPSW = "test-rsa".toCharArray();

		try
		{
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());

			FileInputStream fis = new FileInputStream("C:/Users/tommy/.keystore");
			keystore.load(fis, keystorePSW);
			fis.close();

			// Enumeration<String> aliases = keystore.aliases();

			Certificate cert = keystore.getCertificate(alias);
			PublicKey publicKey = cert.getPublicKey();
			PrivateKey privateKey = (PrivateKey) keystore.getKey(alias, aliasPSW);
			// new KeyPair(publicKey, (privateKey);

			// String algorythm = "AES/CBC/PKCS5Padding";
			String algorythm = "RSA/ECB/PKCS1Padding";

			Cipher encodeCipher = Cipher.getInstance(algorythm);
			Cipher decodeCipher = Cipher.getInstance(algorythm);

			encodeCipher.init(Cipher.ENCRYPT_MODE, publicKey);
			decodeCipher.init(Cipher.DECRYPT_MODE, privateKey);

			// String
			byte[] bytes = encodeCipher.doFinal("abcABC123".getBytes());
			System.out.println(new String(bytes));
			System.out.println(Base64.encodeBase64String(bytes));

			bytes = decodeCipher.doFinal(bytes);
			System.out.println(new String(bytes));

			// Stream
			InputStream in = new FileInputStream("pom.xml");
			// OutputStream out = new ByteArrayOutputStream();
			OutputStream out = new FileOutputStream("pom-crypt.dat");
			CipherOutputStream cipherOutputStream = new CipherOutputStream(out, encodeCipher);

			byte[] buffer = new byte[1 * 1024];
			int numRead = 0;

			while ((numRead = in.read(buffer)) >= 0)
			{
				// byte[] updates = encodeCipher.update(buffer, 0, numRead);
				// System.out.println(Arrays.toString(updates));

				cipherOutputStream.write(buffer, 0, numRead);
			}

			cipherOutputStream.close();
			in.close();
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
