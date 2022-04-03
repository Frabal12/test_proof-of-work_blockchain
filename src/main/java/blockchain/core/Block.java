package blockchain.core;

import java.util.ArrayList;
import java.util.Date;

import blockchain.core.transactions.Transaction;
import blockchain.utils.StringUtils;

public class Block {
	private String dataForHash;
	private String hash;
	private String minedHash;
    private String previousHash;
    private String merkleRoot;
    private ArrayList<Transaction>data= new ArrayList<Transaction>();;
	private Long timeStamp;
	private Date date;
	private int nonce;
	
	public Block(String previousHash){
		this.previousHash=previousHash;
		timeStamp = new Date().getTime();
		date = new Date(timeStamp);
		if(data.size()==0)
			merkleRoot=StringUtils.getMerkleRoot(data);
		dataForHash=previousHash + Long.toString(timeStamp) + merkleRoot;
		hash=StringUtils.calculateHash(dataForHash);
		minedHash=hash;
	}
	
	
	public void mineBlock(int difficulty) {
		
		//\0 is NUL character, example "12\034" if i print string i only see 12
		//create a string like 0 * difficulty, example 00000 (difficulty 5)
		String target = new String(new char[difficulty]).replace('\0', '0'); 
		merkleRoot = StringUtils.getMerkleRoot(data);
		while(!minedHash.substring(0, difficulty).equals(target)) {
			nonce ++;
			minedHash = StringUtils.calculateHash(dataForHash + Integer.toString(nonce));
		}
		System.out.println("Blocco minato!!! : " + minedHash);
	}
	//Add transactions to this block
	public boolean addTransaction(Transaction transaction) {
		//process transaction and check if valid, unless block is genesis block then ignore.
		if(transaction == null) return false;		
		if((previousHash != "0")) {
			if((!transaction.processTransaction())) {
				System.out.println("Transaction failed to process. Discarded.");
				return false;
			}
		}
		data.add(transaction);
		merkleRoot = StringUtils.getMerkleRoot(data);
		hash=StringUtils.calculateHash(dataForHash);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}

	public String getHash() {
		return hash;
	}
	
	public String getMinedHash() {
		return minedHash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public ArrayList<Transaction> getData() {
		return data;
	}

	public Long getTimeStamp() {
		return timeStamp;
	}
	public String getDataForHash() {
		return dataForHash;
	}

	@Override
	public String toString() {
		return "Block [hash=" + hash + ",previousHash=" + previousHash + "ndata=" + data
				+ "date=" + date + "]";
	}

}
