package core;

import java.util.ArrayList;

public abstract class Player {
	protected String name;
	protected int money;
	protected ArrayList<Card> hand;
	
	public Player(String name) {
		this.name = name;
		money = 50;
		hand = new ArrayList<Card>();
	}
	
	public abstract Card chooseCard();
}
