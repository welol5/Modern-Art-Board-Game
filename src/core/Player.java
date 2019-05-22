package core;

import java.util.ArrayList;

public abstract class Player {
	private String name;
	private int money;
	private ArrayList<Card> hand;
	
	public Player(String name) {
		this.name = name;
		money = 50;
		hand = new ArrayList<Card>();
	}
	
	public abstract Card chooseCard();
}
