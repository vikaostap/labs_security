package labs567;

import common.Utils;
import org.bouncycastle.crypto.generators.Argon2BytesGenerator;
import org.bouncycastle.crypto.params.Argon2Parameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

public class CryptoFunctions {

    public static byte[] getArgon2iHash(String data, byte[] salt, byte[] pepper) {
        Security.addProvider(new BouncyCastleProvider());
        Argon2BytesGenerator generator = new Argon2BytesGenerator();
        Argon2Parameters.Builder builder = new Argon2Parameters.Builder();
        builder.withVersion(Argon2Parameters.ARGON2_id);
        /*
         * From https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html#argon2id
         *
         * Argon2id should use one of the following configuration settings as a base minimum which includes the minimum
         * memory size (m), the minimum number of iterations (t) and the degree of parallelism (p).
         * m=37 MiB, t=1, p=1
         * m=15 MiB, t=2, p=1
         */

        // 1 MiB = 1048.576 KB
        builder.withMemoryAsKB(37 * 1049);
        builder.withIterations(1);
        builder.withParallelism(1);

        // Set salt and pepper
        builder.withSalt(salt);
        builder.withSecret(pepper);

        byte[] hash = new byte[128];
        generator.init(builder.build());
        generator.generateBytes(data.toCharArray(), hash);
        return hash;
    }


    // AES
    private static final int AESKeySize = 256; // in bits
    private static final int AES_GCM_IV_Size = 12; // in bytes
    private static final int AES_GCM_Tag_Size = 128; // in bits
    private static final SecureRandom secureRandom = new SecureRandom();

    public static byte[] generateAESKey() {
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not get AES key generator");
        }
        keyGenerator.init(AESKeySize);
        SecretKey key = keyGenerator.generateKey();
        return key.getEncoded();
    }

    public static byte[] encryptAES(byte[] plaintext, byte[] privateKey) {
        try {
            byte[] iv = new byte[AES_GCM_IV_Size];
            secureRandom.nextBytes(iv);
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(AES_GCM_Tag_Size, iv);
            SecretKey secretKey = new SecretKeySpec(privateKey, "AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            byte[] cipherText = cipher.doFinal(plaintext);

            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);
            return byteBuffer.array();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not decrypt");
        }
    }

    public static byte[] decryptAES(byte[] cipherMessage, byte[] privateKey) {
        try {
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            // Use first 12 bytes for iv
            AlgorithmParameterSpec gcmIv = new GCMParameterSpec(AES_GCM_Tag_Size, cipherMessage, 0, AES_GCM_IV_Size);
            SecretKey secretKey = new SecretKeySpec(privateKey, "AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmIv);

            // Use everything from 12 bytes on as ciphertext
            return cipher.doFinal(cipherMessage, AES_GCM_IV_Size, cipherMessage.length - AES_GCM_IV_Size);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Could not decrypt");
        }
    }
}
