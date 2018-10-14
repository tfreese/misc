/**
 * Created: 03.04.2012
 */

package de.freese.sonstiges.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * Beispeile für JCE-API.
 *
 * @author Thomas Freese see base-security/keystore.txt
 */
public class TestJCE
{
    /**
     * @param args String[]
     * @throws Exception Falls was schief geht.
     */
    public static void main(final String[] args) throws Exception
    {
        String TRANSFORM_TYPE = "AES/CBC/PKCS5Padding"; // CBC,ECB
        String ENCRYPTION_ALGO = "AES";
        int KEY_STRENGTH = 128;
        String KEYSTORE_TYPE = "jceks";// KeyStore.getDefaultType();
        String PROVIDER = "SunJCE";// "SUN";

        char[] keystorePSW = "gehaim".toCharArray();

        File keystoreFile = new File("/tmp/keystore.jks");
        KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE, PROVIDER);

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

        SecureRandom secureRandom = new SecureRandom();
        SecretKey secretKey = null;

        // Symetrischen Key erzeugen, wenn nicht vorhanden.
        String alias = "test-" + ENCRYPTION_ALGO + "-" + KEY_STRENGTH;
        char[] aliasPSW = alias.toCharArray();

        if (!keyStore.containsAlias(alias))
        {
            KeyGenerator kg = KeyGenerator.getInstance(ENCRYPTION_ALGO, PROVIDER);
            kg.init(KEY_STRENGTH, secureRandom);
            secretKey = kg.generateKey();

            keyStore.setKeyEntry(alias, secretKey, aliasPSW, null);

            try (OutputStream outputStream = new FileOutputStream(keystoreFile))
            {
                keyStore.store(outputStream, keystorePSW);
            }
        }

        secretKey = (SecretKey) keyStore.getKey(alias, aliasPSW);

        Cipher encryptCipher = Cipher.getInstance(TRANSFORM_TYPE, PROVIDER);
        Cipher decryptCipher = Cipher.getInstance(TRANSFORM_TYPE, PROVIDER);

        // byte iv[] = new byte[16];
        // secureRandom.nextBytes(iv);
        byte[] iv = secureRandom.generateSeed(16);

        encryptCipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
        // byte[] iv = encryptCipher.getIV();
        byte[] encryptedBytes = encryptCipher.doFinal("abcABC123".getBytes());
        System.out.println("Displaying Encrypted message....");
        System.out.println("Encrypted Bytes: " + new String(encryptedBytes));
        System.out.println("Encrypted Base64: " + Base64.getEncoder().encodeToString(encryptedBytes));

        System.out.println("");

        decryptCipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        byte[] decryptedBytes = decryptCipher.doFinal(encryptedBytes);
        System.out.println("Displaying Decrypted message....");
        System.out.println(new String(decryptedBytes));

        // Stream
        try (InputStream in = new FileInputStream("pom.xml");
             CipherOutputStream cipherOutputStream = new CipherOutputStream(new FileOutputStream("/tmp/pom-crypt.dat"), encryptCipher))
        {
            // OutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1 * 1024];
            int numRead = 0;

            while ((numRead = in.read(buffer)) >= 0)
            {
                // byte[] updates = encodeCipher.update(buffer, 0, numRead);
                // System.out.println(Arrays.toString(updates));

                cipherOutputStream.write(buffer, 0, numRead);
            }

            cipherOutputStream.flush();
        }
    }
}
