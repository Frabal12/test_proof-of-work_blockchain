package blockchain.core.transactions;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;

import blockchain.core.Blockchain;
import blockchain.utils.StringUtils;


public class Transaction {
	
	private String transactionId;//hash of the transaction
	private PublicKey sender;
	private PublicKey reciepient;
	private float quantity;
	private byte[] signature;//prevent that other people spend money from our wallet
	private Long timeStamp;
	
	//cryptocurrencies is owned in the wallet like a sum of all unspent transaction
	//a TransactionInput represent an unspent output transaction
	public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();
	
	private int sequence = 0; // how many transaction we make, he take part in the creation of hash to avoid
	                                 // the possibility to have the same hash for 2 transaction
	
	public Transaction(PublicKey sender, PublicKey reciepient, float quantity ,ArrayList<TransactionInput> inputs ) {
		this.sender = sender;
		this.reciepient = reciepient;
		this.quantity = quantity;
		sequence++;
		this.transactionId=StringUtils.calculateHash(StringUtils.getStringFromKey(sender)  + 
				StringUtils.getStringFromKey(reciepient) + Float.toString(quantity) + sequence);
		this.inputs=inputs;
		timeStamp = new Date().getTime();
	}

	
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtils.getStringFromKey(sender) + StringUtils.getStringFromKey(reciepient) + Float.valueOf(quantity) ;
		signature=StringUtils.makeSignature(privateKey, data);
	}
	public boolean verifySignature() {
		String data = StringUtils.getStringFromKey(sender) + StringUtils.getStringFromKey(reciepient) + Float.valueOf(quantity) ;
		return StringUtils.verifySignature(sender ,data, signature);
	}
	public PublicKey getSender() {
		return sender;
	}

	public PublicKey getReciepient() {
		return reciepient;
	}
	
	public float getQuantity() {
		return quantity;
	}
	public String getTransactionId() {
		return transactionId;
	}
	public byte[] getSignature() {
		return signature;
	}
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}
	
	//Returns true if new transaction could be created.	
	public boolean processTransaction() {
			
			if(!verifySignature()) {
				System.out.println("fallimento nella verifica della firma");
				return false;
			}
					
			//make sure transaction is unspent, adding them from total unspent to local list
			for(TransactionInput i : inputs) {
				i.setUnspentOutput(Blockchain.totalUnspent.get(i.getTransactionOutputId()));
			}

			//check if transaction value is valid:
			if(getInputsValue() < Blockchain.minimumTransaction) {
				System.out.println("quantità troppo piccola: " + getInputsValue());
				return false;
			}
			
			//generate new transaction outputs:
			float rest = getInputsValue() - quantity; //get value of inputs then the rest:
			sequence++;
			transactionId = StringUtils.calculateHash(StringUtils.getStringFromKey(sender)  + 
					StringUtils.getStringFromKey(reciepient) + Float.toString(quantity) + Long.toString(timeStamp)
					+ sequence);
			outputs.add(new TransactionOutput( reciepient, quantity , transactionId)); //send value 
			outputs.add(new TransactionOutput( sender, rest , transactionId)); //send back to sender the rest		
					
			//add outputs to Unspent list
			for(TransactionOutput o : outputs) {
				Blockchain.totalUnspent.put(o.getId() , o);
			}
			
			//remove transaction inputs from UTXO lists as spent:
			for(TransactionInput i : inputs) {
				if(i.getUnspentOutput() == null) continue; //if Transaction can't be found skip it 
				Blockchain.totalUnspent.remove(i.getUnspentOutput().getId());
			}
			
			return true;
		}
		
	//returns sum of input(unspent outputs) values
		public float getInputsValue() {
			float total = 0;
			for(TransactionInput i : inputs) {
				if(i.getUnspentOutput() == null) continue; 
				total += i.getUnspentOutput().getValue();
			}
			return total;
		}

	//returns sum of outputs:
		public float getOutputsValue() {
			float total = 0;
			for(TransactionOutput o : outputs) {
				total += o.getValue();
			}
			return total;
		}
		@Override
		public String toString() {
			return "Transaction [transactionId=" + transactionId +"         "+ inputs + "]";
		}
}
