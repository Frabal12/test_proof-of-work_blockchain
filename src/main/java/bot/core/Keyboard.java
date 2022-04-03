package bot.core;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;



public class Keyboard extends ReplyKeyboardMarkup {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1973671381309951961L;

	private List<KeyboardRow> keyboardL = new ArrayList<>();

	
	public Keyboard() {
		keyboardL.clear();
		KeyboardRow firstKey = new KeyboardRow();
		KeyboardRow secondKey = new KeyboardRow();
		KeyboardRow thirdKey = new KeyboardRow();

		// Create a keyboard
		this.setSelective(true);
		this.setResizeKeyboard(true);
		this.setOneTimeKeyboard(false);

		// keys
		firstKey.add(new KeyboardButton("/start"));
		secondKey.add(new KeyboardButton("/help"));
		thirdKey.add(new KeyboardButton("/commands"));
	
		keyboardL.add(firstKey);
		keyboardL.add(secondKey);
		keyboardL.add(thirdKey);
		this.setKeyboard(keyboardL);
	}
	public void buildK_beginButtons() {
		keyboardL.clear();
		KeyboardRow firstKey = new KeyboardRow();
		KeyboardRow secondKey = new KeyboardRow();
		KeyboardRow thirdKey = new KeyboardRow();

		// Create a keyboard
		this.setSelective(true);
		this.setResizeKeyboard(true);
		this.setOneTimeKeyboard(false);

		// keys
		firstKey.add(new KeyboardButton("/start"));
		secondKey.add(new KeyboardButton("/help"));
		thirdKey.add(new KeyboardButton("/commands"));
	
		keyboardL.add(firstKey);
		keyboardL.add(secondKey);
		keyboardL.add(thirdKey);
		this.setKeyboard(keyboardL);
	}


	
	public void buildK_menu() {

		keyboardL.clear();
		KeyboardRow firstKey = new KeyboardRow();
		KeyboardRow secondKey = new KeyboardRow();
		KeyboardRow thirdKey = new KeyboardRow();
		KeyboardRow fourthKey = new KeyboardRow();
		
		// Create a keyboard
		this.setSelective(true);
		this.setResizeKeyboard(true);
		this.setOneTimeKeyboard(false);

		// keys
		firstKey.add(new KeyboardButton("Crea portafoglio"));
		secondKey.add(new KeyboardButton("Invia una transazione"));
		thirdKey.add(new KeyboardButton("Guarda saldo"));
		fourthKey.add(new KeyboardButton("Guarda indirizzo"));
		

		keyboardL.add(firstKey);
		keyboardL.add(secondKey);
		keyboardL.add(thirdKey);
		keyboardL.add(fourthKey);
		this.setKeyboard(keyboardL);
	}

	public void buildK_numberChose(int i) {
		keyboardL.clear();
		KeyboardRow firstKey = new KeyboardRow();
		KeyboardRow secondKey = new KeyboardRow();
		KeyboardRow thirdKey = new KeyboardRow();

		// Create a keyboard

		this.setSelective(true);
		this.setResizeKeyboard(false);
		this.setOneTimeKeyboard(false);

		// keys
		firstKey.add(new KeyboardButton(i/i+""));
		secondKey.add(new KeyboardButton(i/2+""));
		thirdKey.add(new KeyboardButton(i+""));

		keyboardL.add(firstKey);
		keyboardL.add(secondKey);
		keyboardL.add(thirdKey);
		this.setKeyboard(keyboardL);
	}
	

	
	//set the replyMarkup every time it's maked the build, very important !!
	public boolean verify(String buttonValue) {
		KeyboardRow rowToTest = new KeyboardRow();
		rowToTest.add(new KeyboardButton(buttonValue));
		return keyboardL.contains(rowToTest);
	}

}
