package com.example.msc.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import com.sun.mail.util.BASE64DecoderStream;
import com.sun.mail.util.BASE64EncoderStream;

public final class Encryptor {
    private static Cipher ecipher, dcipher;
    private static SecretKey key;

    static {
        try {
            ecipher = Cipher.getInstance("DES");
            dcipher = Cipher.getInstance("DES");
            key = new SecretKeySpec(new byte[]{110, 56, -51, 28, -2, -128, 110, -95}, "DES");

            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(final String str) {
        try {
            return new String(BASE64EncoderStream.encode(ecipher.doFinal(str.getBytes("UTF8"))));
        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println("Encryption failed, " + e.getMessage());
            System.exit(-1);
        }

        return "";
    }

    public static String decrypt(final String str) {
        try {
            return new String(dcipher.doFinal(BASE64DecoderStream.decode(str.getBytes())), "UTF8");
        } catch (final Exception e) {
            e.printStackTrace();
            System.out.println("Decryption failed, " + e.getMessage());
            System.exit(-1);
        }

        return "";
    }
}