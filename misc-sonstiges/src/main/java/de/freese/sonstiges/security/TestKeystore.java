/**
 * Created: 03.04.2012
 */

package de.freese.sonstiges.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.Base64;
import java.util.Collections;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

/**
 * keytool -genkey -storetype PKCS12 -keystore server_keystore.p12 -alias localhost -keyalg rsa -keysize 2048 -validity 36500 -storepass storepass -dname
 * "CN=Thomas Freese, OU=Application Development, O=Privat, L=Braunschweig, ST=Niedersachsen, C=DE";<br>
 *
 * @author Thomas Freese see base-security/keystore.txt
 */
public class TestKeystore
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        // final CertificateFactory cf = CertificateFactory.getInstance( "X.509" );
        // return cf.generateCertificate( inputStream ).getPublicKey();

        char[] keystorePSW = "storepass".toCharArray();
        String alias = "localhost";
        char[] aliasPSW = "storepass".toCharArray();

        // File keystoreFile = new File("src/main/resources/serverKeyStore");
        // File keystoreFile = new File("/tmp/server_keystore.p12");
        File keystoreFile = new File("src/main/resources/server_keystore.p12");
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        if (keystoreFile.exists())
        {
            try (InputStream in = new FileInputStream(keystoreFile))
            {
                keyStore.load(in, keystorePSW);
            }
        }
        else
        {
            keyStore.load(null, keystorePSW);
        }

        Collections.list(keyStore.aliases()).forEach(a -> System.out.println("Alias: " + a));

        Certificate cert = keyStore.getCertificate(alias);
        PublicKey publicKey = cert.getPublicKey();
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, aliasPSW);
        // new KeyPair(publicKey, (privateKey);

        // String algorythm = "AES/CBC/PKCS5Padding";
        String algorythm = "RSA/ECB/PKCS1Padding";
        String PROVIDER = "SunJCE";// "SUN";

        Cipher encodeCipher = Cipher.getInstance(algorythm, PROVIDER);
        Cipher decodeCipher = Cipher.getInstance(algorythm, PROVIDER);

        encodeCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        decodeCipher.init(Cipher.DECRYPT_MODE, privateKey);

        // String
        byte[] bytes = encodeCipher.doFinal("abcABC123".getBytes());
        System.out.println("Encrypted Bytes: " + new String(bytes));
        System.out.println("Encrypted Base64: " + Base64.getEncoder().encodeToString(bytes));

        bytes = decodeCipher.doFinal(bytes);
        System.out.println(new String(bytes));

        // Stream
        try (InputStream in = new FileInputStream("pom.xml");
             CipherOutputStream outputStream = new CipherOutputStream(new FileOutputStream("/tmp/pom-crypt.dat"), encodeCipher))
        // try (InputStream in = new FileInputStream("pom.xml");
        // FileOutputStream outputStream = new FileOutputStream("/tmp/pom-crypt.dat"))
        {
            // OutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4 * 1024];
            int numRead = 0;

            while ((numRead = in.read(buffer)) >= 0)
            {
                // byte[] updates = encodeCipher.update(buffer, 0, numRead);
                // System.out.println(Arrays.toString(updates));

                outputStream.write(buffer, 0, numRead);
            }

            outputStream.flush();
        }
    }
}
