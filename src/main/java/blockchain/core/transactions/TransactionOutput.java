package blockchain.core.transactions;

import java.security.PublicKey;

import blockchain.utils.StringUtils;

public class TransactionOutput  {
	
	private String id;
	private PublicKey reciepient; 
	private float value; //the amount of coins that transaction own
	private String parentTransactionId; //the id of the transaction this output was created in
	
	//Constructor
	public TransactionOutput(PublicKey reciepient, float value,String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtils.calculateHash(StringUtils.getStringFromKey(reciepient)+
				Float.toString(value)+parentTransactionId);
	}
	
	//Check if coin belongs to the correct wallet
	public boolean isOwned(PublicKey publicKey) {
		return (publicKey == reciepient);
	}

	public String getId() {
		return id;
	}

	public PublicKey getReciepient() {
		return reciepient;
	}

	public float getValue() {
		return value;
	}

	public String getParentTransactionId() {
		return parentTransactionId;
	}

	@Override
	public String toString() {
		return "TransactionOutput [id=" + id + ", reciepient=" + reciepient + ", value=" + value
				+ ", parentTransactionId=" + parentTransactionId + "]";
	}

}
