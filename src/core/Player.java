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
	
	public void pay(int amount) {
		money -= amount;
	}
	
	public void recive(int amount) {
		money += amount;
	}
	
	/**
	 * Have the player choose a card they would like to bid on.
	 * @return the card to bid on
	 */
	public abstract Card chooseCard();
	
	/**
	 * Used to get the price the player would like to bid
	 * @param card being bid on
	 * @return the price the player is willing to pay
	 */
	public abstract int getBid(Card card);
}
