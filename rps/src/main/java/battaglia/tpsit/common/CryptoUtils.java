package battaglia.tpsit.common;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Classe di utilità per la gestione della crittografia.
 * Fornisce metodi per generare chiavi RSA e AES, criptare e decriptare dati,
 * e convertire chiavi in formato Base64.
 */
public class CryptoUtils {

    private static final String RSA_ALGORITHM = "RSA";
    private static final String AES_ALGORITHM = "AES";
    private static final int RSA_KEY_SIZE = 2048;
    private static final int AES_KEY_SIZE = 256;

    /**
     * Genera una coppia di chiavi RSA.
     *
     * @return KeyPair contenente la chiave pubblica e privata RSA
     * @throws Exception Se si verifica un errore nella generazione delle chiavi
     */
    public static KeyPair generateRSAKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(RSA_KEY_SIZE);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * Genera una chiave AES casuale.
     *
     * @return SecretKey chiave AES generata
     * @throws Exception Se si verifica un errore nella generazione della chiave
     */
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        keyGenerator.init(AES_KEY_SIZE);
        return keyGenerator.generateKey();
    }

    /**
     * Converte una chiave pubblica RSA in formato Base64.
     *
     * @param publicKey La chiave pubblica RSA
     * @return Stringa Base64 rappresentante la chiave
     */
    public static String encodePublicKey(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * Converte una stringa Base64 in una chiave pubblica RSA.
     *
     * @param publicKeyBase64 Stringa Base64 della chiave pubblica
     * @return PublicKey La chiave pubblica RSA
     * @throws Exception Se si verifica un errore nella decodifica
     */
    public static PublicKey decodePublicKey(String publicKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * Converte una chiave privata RSA in formato Base64.
     *
     * @param privateKey La chiave privata RSA
     * @return Stringa Base64 rappresentante la chiave
     */
    public static String encodePrivateKey(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * Converte una stringa Base64 in una chiave privata RSA.
     *
     * @param privateKeyBase64 Stringa Base64 della chiave privata
     * @return PrivateKey La chiave privata RSA
     * @throws Exception Se si verifica un errore nella decodifica
     */
    public static PrivateKey decodePrivateKey(String privateKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyBase64);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * Converte una chiave AES in formato Base64.
     *
     * @param secretKey La chiave AES
     * @return Stringa Base64 rappresentante la chiave
     */
    public static String encodeAESKey(SecretKey secretKey) {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * Converte una stringa Base64 in una chiave AES.
     *
     * @param aesKeyBase64 Stringa Base64 della chiave AES
     * @return SecretKey La chiave AES
     * @throws Exception Se si verifica un errore nella decodifica
     */
    public static SecretKey decodeAESKey(String aesKeyBase64) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(aesKeyBase64);
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    /**
     * Cripta una chiave AES con una chiave pubblica RSA.
     *
     * @param aesKey La chiave AES da criptare
     * @param publicKey La chiave pubblica RSA
     * @return Stringa Base64 della chiave AES criptata
     * @throws Exception Se si verifica un errore nella crittografia
     */
    public static String encryptAESKeyWithRSA(SecretKey aesKey, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedKey = cipher.doFinal(aesKey.getEncoded());
        return Base64.getEncoder().encodeToString(encryptedKey);
    }

    /**
     * Decripta una chiave AES con una chiave privata RSA.
     *
     * @param encryptedAESKeyBase64 Stringa Base64 della chiave AES criptata
     * @param privateKey La chiave privata RSA
     * @return SecretKey La chiave AES decriptata
     * @throws Exception Se si verifica un errore nella decrittografia
     */
    public static SecretKey decryptAESKeyWithRSA(String encryptedAESKeyBase64, PrivateKey privateKey) throws Exception {
        byte[] encryptedKey = Base64.getDecoder().decode(encryptedAESKeyBase64);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedKey = cipher.doFinal(encryptedKey);
        return new SecretKeySpec(decryptedKey, AES_ALGORITHM);
    }

    /**
     * Cripta un testo con una chiave AES.
     *
     * @param plainText Il testo da criptare
     * @param secretKey La chiave AES
     * @return Stringa Base64 del testo criptato
     * @throws Exception Se si verifica un errore nella crittografia
     */
    public static String encryptWithAES(String plainText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Decripta un testo con una chiave AES.
     *
     * @param encryptedBase64 Stringa Base64 del testo criptato
     * @param secretKey La chiave AES
     * @return Il testo decriptato
     * @throws Exception Se si verifica un errore nella decrittografia
     */
    public static String decryptWithAES(String encryptedBase64, SecretKey secretKey) throws Exception {
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, "UTF-8");
    }
}
