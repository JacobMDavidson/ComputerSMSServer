package com.jacobmdavison.computersmsserver;

import java.security.*;
import java.util.Base64;

import javax.crypto.*;

public class RSA {
		public static void main (String[] args) throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {

		    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		    kpg.initialize(2048);   // 1024 or 2048
		    KeyPair kp = kpg.generateKeyPair();
		    Key publicKey = kp.getPublic();
		    Key privateKey = kp.getPrivate();
		    
		    String data = "THis is a test";
		    System.out.println("Plaintext: " + data);
		    String ciphertext = rsaEncrypt(data, publicKey);
		    System.out.println("Ciphertext: " + ciphertext);
		    
		    
		    String plaintext = rsaDecrypt(ciphertext, privateKey);
		    System.out.println("Decrypted Plaintext: " + plaintext);
		}


		public static String rsaEncrypt(String data, Key publicKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		      byte[] cipherData = cipher.doFinal(data.getBytes());
		      String ciphertext = new String(Base64.getEncoder().encode(cipherData));
	            System.out.println("encrypted (chipertext) = " + ciphertext);
		      return ciphertext;
		}

		public static String rsaDecrypt(String data, Key privateKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
			Cipher cipher = Cipher.getInstance("RSA");  
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			
			byte[] ciphertextBytes = Base64.getDecoder().decode(data.getBytes());
            byte[] decryptedBytes = cipher.doFinal(ciphertextBytes);
            String decryptedString = new String(decryptedBytes);
			
			
		 
		      return decryptedString;
		}
	}

