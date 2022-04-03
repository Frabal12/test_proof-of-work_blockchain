package bot.api;

import java.io.IOException;

import org.telegram.telegrambots.meta.exceptions.TelegramApiException;



public interface Command {
	
	void unknown()throws TelegramApiException;
	void see()throws TelegramApiException;
	void send()throws TelegramApiException;
	void create()throws TelegramApiException;
	void play()throws TelegramApiException;
	void commands()throws TelegramApiException;
	void help()throws TelegramApiException;
	void seeAddress()throws TelegramApiException;

	default void chose(String messageText)throws TelegramApiException, IOException, ClassNotFoundException, InterruptedException{
		switch(messageText) {
		case "/help":help();break;
		case "/commands":commands();break;
		case "/start","Menù":play();break;
		case "Crea portafoglio":create();break;
		case "Invia una transazione":send();break;
		case "Guarda saldo":see();break;
		case "Guarda indirizzo":seeAddress();break;
		default: unknown();break;
		}
	}

}
