package kr.co.example.firebaseregister;

import android.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class HashUtils {

    private static final int SALT_LENGTH = 16; // salt 길이
    private static final int HASH_LENGTH = 32; // 해시 길이
    private static final int ITERATIONS = 10000; // 해시 반복 횟수

    // 비밀번호 해시 생성
    public static String hashPassword(String password) {
        try {
            // Salt 생성
            byte[] salt = generateSalt();

            // 비밀번호 + salt로 해시 생성
            byte[] hash = pbkdf2(password, salt);

            // Salt와 해시를 Base64로 결합하여 반환
            return Base64.encodeToString(concatenate(salt, hash), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 비밀번호 검증
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            byte[] storedData = Base64.decode(storedHash, Base64.DEFAULT);

            // 저장된 salt와 해시 분리
            byte[] salt = Arrays.copyOfRange(storedData, 0, SALT_LENGTH);
            byte[] storedHashBytes = Arrays.copyOfRange(storedData, SALT_LENGTH, storedData.length);

            // 새로운 해시 계산
            byte[] hash = pbkdf2(password, salt);

            // 저장된 해시와 비교
            return Arrays.equals(storedHashBytes, hash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // PBKDF2로 해시 생성
    private static byte[] pbkdf2(String password, byte[] salt) throws NoSuchAlgorithmException {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(keySpec);

            byte[] result = mac.doFinal(salt);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Salt 생성
    private static byte[] generateSalt() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        secureRandom.nextBytes(salt);
        return salt;
    }

    // Salt와 해시 결합
    private static byte[] concatenate(byte[] salt, byte[] hash) {
        byte[] result = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, result, 0, salt.length);
        System.arraycopy(hash, 0, result, salt.length, hash.length);
        return result;
    }
}
