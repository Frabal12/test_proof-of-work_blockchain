package blockchain.core;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

import blockchain.core.transactions.Transaction;
import blockchain.core.transactions.TransactionInput;
import blockchain.core.transactions.TransactionOutput;

public class Wallet {
	public boolean flag;
	private PrivateKey privateKey; // sign the transaction, signature = (privateKey , sender + receiver + quantity)
	private PublicKey publicKey; // works like address, can verify the signature using
									// (signature, publicKey, sender + receiver + quantity)
	// list of all unspent transactions. to speed up the control of the money owned
	// in the wallets.
	public HashMap<String, TransactionOutput> unspentTransactions = new HashMap<String, TransactionOutput>();

	public Wallet() {
		generateKeyPair();
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void generateKeyPair() {
		try {
			// ECDSA - Elliptic Curve Digital Signature Algorithm, BC - Bouncy Castle
			// provider
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			// the set of parameters used to generate the elliptic curve
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			// create a keyPair with the key generator
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			// set the keys from keyPait
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// returns balance and stores the UTXO's owned by this wallet in this.UTXOs
	public float getBalance() {
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : Blockchain.totalUnspent.entrySet()) {
			TransactionOutput transactionUnspent = item.getValue();// -------------
			if (transactionUnspent.isOwned(publicKey)) { // if output belongs to me ( if coins belong to me )
				unspentTransactions.put(transactionUnspent.getId(), transactionUnspent);
				total += transactionUnspent.getValue();
			}
		}
		return total;
	}

	public Transaction sendFunds(PublicKey _recipient, float value) {
		if (getBalance() < value) { // gather balance and check funds.
			System.out.println("non ci sono abbastanza soldi sul portfoglio.");
			return null;
		}

		ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();

		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : unspentTransactions.entrySet()) {
			TransactionOutput transactionOut = item.getValue();// ------
			total += transactionOut.getValue();
			inputs.add(new TransactionInput(transactionOut.getId()));
			if (total > value)
				break;
		}

		Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
		newTransaction.generateSignature(privateKey);

		for (TransactionInput input : inputs) {
			unspentTransactions.remove(input.getTransactionOutputId());
		}
		return newTransaction;
	}

	@Override
	public String toString() {
		return "Wallet value: " + getBalance();
	}
}
