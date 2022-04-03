package blockchain.core.transactions;

public class TransactionInput  {

	private String transactionOutputId; //transactionId
	private TransactionOutput unspentOutput; //Unspent transaction
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

	public String getTransactionOutputId() {
		return transactionOutputId;
	}
	public TransactionOutput getUnspentOutput() {
		return unspentOutput;
	}

	public void setTransactionOutputId(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

	public void setUnspentOutput(TransactionOutput unspentOutput) {
		this.unspentOutput = unspentOutput;
	}

	@Override
	public String toString() {
		return "TransactionInput [transactionOutputId=" + transactionOutputId + ", unspentOutput=" + unspentOutput
				+ "]";
	}



}
