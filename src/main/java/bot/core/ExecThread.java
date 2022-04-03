package bot.core;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import blockchain.core.Blockchain;
import blockchain.core.Wallet;
import blockchain.utils.StringUtils;
import bot.api.Command;

public class ExecThread extends Thread implements Command {

	private ConcurrentHashMap<Long, Wallet> _wallets = new ConcurrentHashMap<>();
	private static Wallet wallet;
	private WalletBot bot;
	private long l_chatId;
	public String chatId;
	private String messageText = "";
	private Keyboard keyboard=null;
	private List<Object[]> transactions ;

	public ExecThread(WalletBot bot,Update update) throws TelegramApiRequestException {
		this.bot = bot;
		messageText = update.getMessage().getText();
		l_chatId = update.getMessage().getChatId();
		chatId = String.valueOf(l_chatId);
		_wallets = WalletBot.getWallets();
		wallet = _wallets.get(l_chatId);
		if(keyboard==null)keyboard = new Keyboard();
		transactions=WalletBot.getTransactions();
	}

	public void run() {
		try {
		if(!wallet.flag) {
			if (!messageText.equals("") ) 
				chose(messageText);
			else unknown();
			}
		else {
			SendMessage message = SendMessage.builder().text("errore di digitazione").chatId(chatId).build();
			PublicKey publicKey;
			String [] parameters;
			Float value;
			try {
			parameters = messageText.split(" ");
			publicKey=StringUtils.loadPublicKey(parameters[0]);
			value=Float.valueOf(parameters[1]);
			}
			catch(Exception e) {
				bot.execute(message);
				unknown();
				set();
				wallet.flag=false;
				return;
			}
			message.setText("denaro inviato");
			Wallet wallet1=null;
			for(Entry<Long,Wallet> e : _wallets.entrySet() ) {
				if(e.getValue().getPublicKey().equals(publicKey)) {
					wallet1=e.getValue();
				}
			}
			transactions.add(new Object[]{wallet,wallet1.getPublicKey(),value});
			wallet.flag=false;
			bot.execute(message);
			
			
		}
		} catch (ClassNotFoundException | TelegramApiException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
		set();
	}

	@Override
	public void commands() throws TelegramApiException {
		String lista = "la lista dei comandi è: \n/command \n/help \n/play \n\n "
				+ "Puoi premere sui bottoni o usare i comandi corrispondenti scrivendoli in chat";
		SendMessage message = SendMessage.builder().text(lista).chatId(chatId).build();
		buttonsBegin(message);
		bot.execute(message);

	}
	public void play() throws TelegramApiException {
		String lista = "Digita uno dei comandi o clicca sul bottone corrispondente.";
		SendMessage message = SendMessage.builder().text(lista).chatId(chatId).build();
		buttonsMenu(message);
		bot.execute(message);
	}
	@Override
	public void help() throws TelegramApiException {
		String spiegazione = "Salve, il bot funziona così:\n\n"
				+ "-Per creare un nuovo portafoglio clicca \"Crea portafoglio\".\n"
				+ "-Per visualizzare il tuo saldo clicca \"Guarda saldo\".\n"
				+ "-Per inviare una nuova transazione digita clicca \"Invia una transazione\".";
		SendMessage message = SendMessage.builder().text(spiegazione).chatId(chatId).build();
		bot.execute(message);
	}

	@Override
	public void unknown() throws TelegramApiException {
		String messageText = "Comando sconosciuto, riprova e controlla che tutti i caratteri siano scritti correttamente!";
		SendMessage message = SendMessage.builder().text(messageText).chatId(chatId).build();
		buttonsBegin(message);
		bot.execute(message);
	}
	
	@Override
	public void see() throws TelegramApiException {
		SendMessage message = SendMessage.builder().text("il tuo saldo è: " + wallet.getBalance()+" DavideCoin").chatId(chatId).build();
		bot.execute(message);
	}

	@Override
	public void send() throws TelegramApiException {
		SendMessage message = SendMessage.builder().text("digita l'indirizzo del wallet a cui inviare i soldi"
				+ " e poi il valore da mandare divisi da uno spazio. [<indirizzo> <valore>]").chatId(chatId).build();
		wallet.flag=true;
		bot.execute(message);
	}

	@Override
	public void create() throws TelegramApiException {
		SendMessage message = SendMessage.builder().text("portafoglio creato ;).").chatId(chatId).build();
		wallet = new Wallet();
		transactions.add(new Object[]{Blockchain.walletA,wallet.getPublicKey(),50f});
		set();
		bot.execute(message);
	}
	
	@Override
	public void seeAddress() throws TelegramApiException {
		SendMessage message = SendMessage.builder().text(StringUtils.getStringFromKey(wallet.getPublicKey())).
				chatId(chatId).build();
		bot.execute(message);
	}

	private void buttonsMenu(SendMessage message) {
		keyboard.buildK_menu();
		message.setReplyMarkup(keyboard);
	}
	private void buttonsBegin(SendMessage message) {
		keyboard.buildK_beginButtons();;
		message.setReplyMarkup(keyboard);
	}
	//take from internet
    public static Key loadPublicKey(String stored) throws GeneralSecurityException, IOException 
    {
    	byte[] data = Base64.getDecoder().decode((stored.getBytes()));
    	X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
    	KeyFactory fact = KeyFactory.getInstance("RSA");
    	return fact.generatePublic(spec);
    }

	private void set() {
		// setting the new data
		_wallets.put(l_chatId, wallet);
		WalletBot.setWallets(_wallets);
		WalletBot.setTransactions(transactions);
	}

}