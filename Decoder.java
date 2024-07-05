import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Decoder {

	private static String SECRET_KEY;
	private static String SALT;
	private static String HASH;

	public static String decrypt(final String encryptedPassword, final String SALT) {
		try {
			final byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
			final IvParameterSpec ivspec = new IvParameterSpec(iv);
			final SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			final KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), SALT.getBytes(), 65536, 256);
			final SecretKey tmp = factory.generateSecret(spec);
			final SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
			final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword));
			return new String(original, StandardCharsets.UTF_8);
		} catch (Exception exception) {
			System.out.println("Error while decrypting: " + exception);
			return null;
		}
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Введи хеш: ");
		HASH = scanner.nextLine();

		System.out.print("Введи соль/UUID игрока: ");
		SALT = scanner.nextLine();

		System.out.print("Введи ключ из кфг плагина: ");
		SECRET_KEY = scanner.nextLine();
		
		String decryptedPassword = decrypt(HASH, SALT);
		System.out.println("pwd: " + decryptedPassword);
	}
}
