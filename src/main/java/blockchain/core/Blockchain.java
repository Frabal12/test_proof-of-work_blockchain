package blockchain.core;

import java.security.PublicKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import blockchain.core.transactions.Transaction;
import blockchain.core.transactions.TransactionInput;
import blockchain.core.transactions.TransactionOutput;
import blockchain.utils.StringUtils;
import bot.core.WalletBot;

public class Blockchain {
	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static HashMap<String,TransactionOutput> totalUnspent = new HashMap<String,TransactionOutput>();
	private static ConcurrentHashMap<Long, Wallet> _wallets = WalletBot.getWallets();
	
	public static int difficulty = 3;
	public static float minimumTransaction = 0.1f;
	public static Wallet walletA;
	private static Transaction genesisTransaction;
	
	public static void main(String...Args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //Setup Bouncey castle as a Security Provider
		walletA = new Wallet();
		
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			WalletBot bot = new WalletBot();
			botsApi.registerBot(bot);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
		
		//Create wallets:	
		String previousHash = "0";
		int count=0;
		//testing
		while(true) {
			_wallets=WalletBot.getWallets();
			Block block=new Block(previousHash);
			if(previousHash.equals("0")) {
				//Create wallets:	
				Wallet coinbase = new Wallet();
				
				//create genesis transaction, which sends 100 NoobCoin to walletA: 
				genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 1000000f, null);
				genesisTransaction.generateSignature(coinbase.getPrivateKey());	 //manually sign the genesis transaction	
				genesisTransaction.setTransactionId("0"); //manually set the transaction id
				genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.getReciepient(),
						genesisTransaction.getQuantity(), genesisTransaction.getTransactionId())); //manually add the Transactions Output
				Blockchain.totalUnspent.put(genesisTransaction.outputs.get(0).getId(),
						genesisTransaction.outputs.get(0)); //its important to store our first transaction in the UTXOs list.
			}
			

			count++;
			addBlock(block);
			previousHash=block.getHash();
			for(Object[] o : WalletBot.getTransactions()) {
				Transaction t = ((Wallet)o[0]).sendFunds((PublicKey)o[1],(Float)o[2]);
				block.addTransaction(t);
			}
			WalletBot.clearTransactions();
			isChainValid();
			WalletBot.setWallets(_wallets);
			System.out.println(count);
		}	
		}
	
	public static boolean verifyIntegrity() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0'); 
		for(int i=1;i<blockchain.size();i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			//if(!(currentBlock.getHash().equals(currentBlock.calculateHash("")))) return false;
			if(!(previousBlock.getHash().equals(currentBlock.getPreviousHash()))) return false;
			if(!(currentBlock.getMinedHash().substring(0, difficulty).equals(hashTarget))) return false;
		}
		return true;
	}
	
	public static Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		HashMap<String,TransactionOutput> tempUTXOs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
		tempUTXOs.put(genesisTransaction.outputs.get(0).getId(), genesisTransaction.outputs.get(0));
		
		//loop through blockchain to check hashes:
		for(int i=1; i < blockchain.size(); i++) {
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
		
			//compare registered hash and calculated hash:
			if(!currentBlock.getHash().equals(StringUtils.calculateHash(currentBlock.getDataForHash()))){
				System.out.println("#Current Hashes not equal");
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.getHash().equals(currentBlock.getPreviousHash()) ) {
				System.out.println("#Previous Hashes not equal");
				return false;
			}
			//check if hash is solved
			if(!currentBlock.getMinedHash().substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("#This block hasn't been mined");
				return false;
			}
			
			//loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for(int t=0; t <currentBlock.getData().size(); t++) {
				Transaction currentTransaction = currentBlock.getData().get(t);
				
				if(!currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false; 
				}
				
				for(TransactionInput input: currentTransaction.inputs) {	
					tempOutput = tempUTXOs.get(input.getTransactionOutputId());
					
					if(tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}
					
					if(input.getUnspentOutput().getValue() != tempOutput.getValue()) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}
					
					tempUTXOs.remove(input.getTransactionOutputId());
				}
				
				for(TransactionOutput output: currentTransaction.outputs) {
					tempUTXOs.put(output.getId(), output);
				}
				
				if( currentTransaction.outputs.get(0).getReciepient() != currentTransaction.getReciepient()) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					return false;
				}
				if( currentTransaction.outputs.get(1).getReciepient() != currentTransaction.getSender()) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					return false;
				}
				
			}
			
		}
		System.out.println("Blockchain is valid");
		return true;
	}
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockchain.add(newBlock);
	}


}
