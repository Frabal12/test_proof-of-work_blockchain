package blockchain.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;

import blockchain.core.transactions.Transaction;

public class StringUtils {
	
	//privateKey, sender + receiver + quantity
	public static byte[] makeSignature(PrivateKey privateKey, String input) {
		Signature signature;
		byte [] output = null;
		try {
			signature = Signature.getInstance("ECDSA","BC");
		    signature.initSign(privateKey);
		    signature.update(input.getBytes());
		    output = signature.sign();
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
			throw new RuntimeException(e);
		}
		return output;
	}
	//publicKey, sender + receiver + quantity, signature
	public static boolean verifySignature(PublicKey publicKey, String input, byte[] signature) {
			Signature signatureVerify;
			try {
				signatureVerify = Signature.getInstance("ECDSA", "BC");
			    signatureVerify.initVerify(publicKey);
			    signatureVerify.update(input.getBytes());
			    return signatureVerify.verify(signature);
			} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException e) {
				throw new RuntimeException(e);
			}
	}
	
	public static String getStringFromKey(Key key) {
		//base64 translates binary data into strings
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	public static String getMerkleRoot(ArrayList<Transaction> transactions) {
		int count = transactions.size();
		ArrayList<String> previousTreeLayer = new ArrayList<String>();
		for(Transaction t : transactions) 
			previousTreeLayer.add(t.getTransactionId());
		ArrayList<String> treeLayer = previousTreeLayer;
		while(count > 1) {
			treeLayer = new ArrayList<String>();
			for(int i=1; i < previousTreeLayer.size(); i++) {
				treeLayer.add(calculateHash(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot;
		if(treeLayer.size() == 1) merkleRoot = treeLayer.get(0);
		else merkleRoot = "";
		return merkleRoot;
	}
	public static String calculateHash(String data)  {
		byte[] byteHash=null;	   
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			byteHash = digest.digest(data.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		StringBuffer hash = new StringBuffer();
		for (byte b : byteHash)
			//when the byte has a single digit, before the digit is added 0
			hash.append(String.format("%02x", b));
		return hash.toString();
	}
	
	//take from internet
    public static PublicKey loadPublicKey(String stored) throws GeneralSecurityException, IOException 
    {
    	byte[] data = Base64.getDecoder().decode((stored.getBytes()));
    	X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
    	KeyFactory fact = KeyFactory.getInstance("ECDSA");
    	return fact.generatePublic(spec);
    }
	
}
