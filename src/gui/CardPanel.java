package gui;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.Card;
import javafx.scene.layout.HBox;

public class CardPanel extends HBox implements Observer {
	
	private ArrayList<Card> hand;
	
	public CardPanel(ArrayList<Card> hand) {
		this.hand = hand;
		
	}

	@Override
	public void update(Observable o, Object arg) {
		
	}

}
