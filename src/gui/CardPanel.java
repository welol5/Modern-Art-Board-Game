package gui;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.Card;
import javafx.scene.layout.HBox;

public class CardPanel extends HBox{
	
	private ArrayList<Card> hand;
	
	public CardPanel(ArrayList<Card> hand) {
		this.hand = hand;
	}

	public void update() {
		this.getChildren().clear();//clear the list
		
		//rebuild the list from hand
		for(Card c : hand) {
			this.getChildren().add(new GUICard(c));
		}
		
	}

}
