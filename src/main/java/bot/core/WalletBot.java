package bot.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import blockchain.core.Wallet;



public class WalletBot extends TelegramLongPollingBot {


	private ExecutorService exec = Executors.newCachedThreadPool();
	private static ConcurrentHashMap<Long, Wallet> _wallets = new ConcurrentHashMap<>();
	private static List<Object[]> transactions = 
			Collections.synchronizedList(new ArrayList<Object[]>());
	


	// per test, da sistemare
	int count = 4;
	boolean flag;
	

	public WalletBot() {
		// I initialize statesif nothing is entered
		_wallets.put(0l, new Wallet());
	}

	@Override
	public String getBotUsername() {
		return "toobig_bot";
	}

	@Override
	public String getBotToken() {
		return "insert bot token";
	}

	@Override
	public void onUpdateReceived(Update update) {
		try {
			switchUpdate(update);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	// in costruction
	public boolean[] buildFlags() {
		var temp = new boolean[2];
		temp[0] = false;
		temp[1] = false;
		return temp;
	}

	private void switchUpdate(Update update) throws TelegramApiException {
		if (update.hasMessage() && update.getMessage().hasText()) {
			_wallets.computeIfAbsent(update.getMessage().getChatId(), key -> new Wallet());
			exec.submit(new ExecThread(this, update));
		}
	}

	public static ConcurrentHashMap<Long, Wallet> getWallets() {
		return _wallets;
	}

	public static void setWallets(ConcurrentHashMap<Long, Wallet> _wall) {
		_wallets = _wall;
	}

	
	public static void clearTransactions() {
		transactions.clear();
	}

	public static List<Object[]> getTransactions() {
		return transactions;
	}

	public static void setTransactions(List<Object[]> transactions) {
		WalletBot.transactions = transactions;
	}
	public static void removeTransactions(Object[] transaction) {
		WalletBot.transactions.remove(transaction);
	}


}