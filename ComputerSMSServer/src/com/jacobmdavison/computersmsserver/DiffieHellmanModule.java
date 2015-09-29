package com.jacobmdavison.computersmsserver;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Uses Diffie Hellman to generate shared AES key, then encrypts/decrypts strings
 * Created by jacobdavidson on 8/27/15.
 */
public class DiffieHellmanModule {

    private static final int AES_KEY_SIZE = 128;
    private KeyPairGenerator keyPairGenerator;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private SecretKey secretKey;
    private boolean isConnected = false;

    public DiffieHellmanModule() {
        // Init the KeyPairGenerator
        try {
        	//int bitLength = 1024;
            //SecureRandom rnd = new SecureRandom();
            //BigInteger p = BigInteger.probablePrime(bitLength, rnd);
            //BigInteger g = BigInteger.probablePrime(bitLength, rnd);
            DHParameterSpec dhParameterSpec = new DHParameterSpec(skip1024Modulus, skip1024Base);
            keyPairGenerator = KeyPairGenerator.getInstance("DH");
            //keyPairGenerator.initialize(1024);
            keyPairGenerator.initialize(dhParameterSpec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } 
        
        
        catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        // Generate the Key Pairs
        KeyPair keyPair = keyPairGenerator.genKeyPair();

        publicKey = keyPair.getPublic();
        privateKey = keyPair.getPrivate();


    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
    
    public SecretKey getSecretKey() {
    	return secretKey;
    }

    // Set the public key and generate the shared AES key
    public void generateSecretKey(PublicKey receivedPublicKey, boolean lastPhase) {

        try {
            KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(privateKey);
            keyAgreement.doPhase(receivedPublicKey, lastPhase);

            // Generates the shared secret
            byte[] secret = keyAgreement.generateSecret();

            // Generate an AES key
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] bkey = Arrays.copyOf(sha256.digest(secret), AES_KEY_SIZE / Byte.SIZE);

            secretKey = new SecretKeySpec(bkey, "AES");
            isConnected = true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public String encryptString( String plainText ) {

        // @ TODO should I really init Cipher each time??
        try {
            // Instantiate the cipher
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] cipherText = cipher.doFinal(plainText.getBytes());
            
            // @ TODO fix
            return new String(Base64.getEncoder().encode(cipherText));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String decryptString( String cipherText ) {
        // @ TODO should I really init Cipher each time??
        try {
            // Instantiate the cipher
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] cipherTextBytes = Base64.getDecoder().decode(cipherText);
            byte[] plainText = cipher.doFinal(cipherTextBytes);
            return new String(plainText, "utf-8");

        } catch (BadPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }


    public boolean isConnected() {
        return isConnected;
    }

 // The 1024 bit Diffie-Hellman modulus values used by SKIP
    private static final byte skip1024ModulusBytes[] = {
            (byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
            (byte)0x4E, (byte)0x49, (byte)0xDB, (byte)0xCD,
            (byte)0x20, (byte)0xB4, (byte)0x9D, (byte)0xE4,
            (byte)0x91, (byte)0x07, (byte)0x36, (byte)0x6B,
            (byte)0x33, (byte)0x6C, (byte)0x38, (byte)0x0D,
            (byte)0x45, (byte)0x1D, (byte)0x0F, (byte)0x7C,
            (byte)0x88, (byte)0xB3, (byte)0x1C, (byte)0x7C,
            (byte)0x5B, (byte)0x2D, (byte)0x8E, (byte)0xF6,
            (byte)0xF3, (byte)0xC9, (byte)0x23, (byte)0xC0,
            (byte)0x43, (byte)0xF0, (byte)0xA5, (byte)0x5B,
            (byte)0x18, (byte)0x8D, (byte)0x8E, (byte)0xBB,
            (byte)0x55, (byte)0x8C, (byte)0xB8, (byte)0x5D,
            (byte)0x38, (byte)0xD3, (byte)0x34, (byte)0xFD,
            (byte)0x7C, (byte)0x17, (byte)0x57, (byte)0x43,
            (byte)0xA3, (byte)0x1D, (byte)0x18, (byte)0x6C,
            (byte)0xDE, (byte)0x33, (byte)0x21, (byte)0x2C,
            (byte)0xB5, (byte)0x2A, (byte)0xFF, (byte)0x3C,
            (byte)0xE1, (byte)0xB1, (byte)0x29, (byte)0x40,
            (byte)0x18, (byte)0x11, (byte)0x8D, (byte)0x7C,
            (byte)0x84, (byte)0xA7, (byte)0x0A, (byte)0x72,
            (byte)0xD6, (byte)0x86, (byte)0xC4, (byte)0x03,
            (byte)0x19, (byte)0xC8, (byte)0x07, (byte)0x29,
            (byte)0x7A, (byte)0xCA, (byte)0x95, (byte)0x0C,
            (byte)0xD9, (byte)0x96, (byte)0x9F, (byte)0xAB,
            (byte)0xD0, (byte)0x0A, (byte)0x50, (byte)0x9B,
            (byte)0x02, (byte)0x46, (byte)0xD3, (byte)0x08,
            (byte)0x3D, (byte)0x66, (byte)0xA4, (byte)0x5D,
            (byte)0x41, (byte)0x9F, (byte)0x9C, (byte)0x7C,
            (byte)0xBD, (byte)0x89, (byte)0x4B, (byte)0x22,
            (byte)0x19, (byte)0x26, (byte)0xBA, (byte)0xAB,
            (byte)0xA2, (byte)0x5E, (byte)0xC3, (byte)0x55,
            (byte)0xE9, (byte)0x2F, (byte)0x78, (byte)0xC7
    };

    // The SKIP 1024 bit modulus
    private static final BigInteger skip1024Modulus
            = new BigInteger(1, skip1024ModulusBytes);

    // The base used with the SKIP 1024 bit modulus
    private static final BigInteger skip1024Base = BigInteger.valueOf(2);
}

/*
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DiffieHellmanModule {
	private static final int AES_KEY_SIZE = 128;
	private static KeyPairGenerator kpg;
	
	static {
		try {
			// === Generates and inits a KeyPairGenerator ===

            // changed this to use default parameters, generating your
            // own takes a lot of time and should be avoided
            // use ECDH or a newer Java (8) to support key generation with
            // higher strength
			kpg = KeyPairGenerator.getInstance("DH");
			kpg.initialize(1024);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
	}
	
	public static void main( String[] args ) {
		// Generate the keypairs for alice and bob
		KeyPair kp1 = DiffieHellmanModule.genDHKeyPair();
		KeyPair kp2 = DiffieHellmanModule.genDHKeyPair();
		
		// Get the public key of Alice and Bob
		PublicKey pbk1 = kp1.getPublic();
		PublicKey pbk2 = kp2.getPublic();
		
		// Get the private key of Alice and Bob
		PrivateKey prk1 = kp1.getPrivate();
		PrivateKey prk2 = kp2.getPrivate();
		
		try {
			
			// Compute secret key for alice and bob
			SecretKey key1 = DiffieHellmanModule.agreeSecretKey(prk1, pbk2, true);
			SecretKey key2 = DiffieHellmanModule.agreeSecretKey(prk2, pbk1, true);
			
			// Instantiate the cipher
			Cipher c = Cipher.getInstance("AES/ECB/PKCS5Padding");
			
			// Init the cipher with Alices key
			c.init(Cipher.ENCRYPT_MODE, key1);
			
			// Compute the cipher text
			byte[] cipherText = c.doFinal("This is a test".getBytes());
			
			// Print the ciphertext
			System.out.println("Encrypted" + new String(cipherText, "utf-8"));
			
			// Init the decryption mode
			c.init(Cipher.DECRYPT_MODE, key2);
			
			// Decrypt and print the text
			System.out.println("Decrypted: " + new String(c.doFinal(cipherText), "utf-8"));
		
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	// @ TODO put in both client and server
	public static SecretKey agreeSecretKey( PrivateKey prk_self, 
			PublicKey pbk_peer, boolean lastPhase) throws Exception {
		
		KeyAgreement ka = KeyAgreement.getInstance("DH");
		ka.init(prk_self);
		ka.doPhase(pbk_peer, lastPhase);
		
		// Generates the shared secret
		byte[] secret = ka.generateSecret();
		
		// Generate an AES key
		
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		byte[] bkey = Arrays.copyOf(sha256.digest(secret), AES_KEY_SIZE / Byte.SIZE);
		SecretKey desSpec = new SecretKeySpec(bkey, "AES");
		
		return desSpec;
		
	}
	
	
	public static KeyPair genDHKeyPair() {
		return kpg.genKeyPair();
	}

}
*/
