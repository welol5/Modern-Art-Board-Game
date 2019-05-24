package core;

import java.util.ArrayList;

public abstract class Player {
	public final String name;
	protected int money;
	protected ArrayList<Card> hand;
	protected ArrayList<Card> winnings;
	
	public Player(String name) {
		this.name = name;
		money = 50;
		hand = new ArrayList<Card>();
		winnings = new ArrayList<Card>();
	}
	
	/**
	 * Give the player the painting they won in the auction
	 * @param card
	 */
	public void givePainting(Card card) {
		winnings.add(card);
	}
	
	/**
	 * 
	 * @return the list of paintings won by the player
	 */
	public ArrayList<Card> getWinnings(){
		return winnings;
	}
	
	/**
	 * Removes all paintings that the player has won
	 */
	public void clearWinnings() {
		winnings.clear();
	}
	
	/**
	 * Deal a painting card to the player
	 * @param card that the player will receive
	 */
	public void deal(Card card) {
		hand.add(card);
	}
	
	/**
	 * The player will lose an amount of money
	 * @param amount that will be lost
	 */
	public void pay(int amount) {
		money -= amount;
	}
	
	/**
	 * The player will be paid an amount of money
	 * @param amount to be paid
	 */
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
	
	/**
	 * Gets the price that will be used to sell the card
	 * @return the price
	 */
	public abstract int getFixedPrice(Card card);
	
	/**
	 * Asks the player if they will buy the card
	 * @param card the player might buy
	 * @param price the player would buy the card at
	 * @return the players answer
	 */
	public abstract boolean buy(Card card, int price);
}
