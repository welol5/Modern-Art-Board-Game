package player;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import core.Artist;
import core.Card;
import core.ObservableGameState;
import fxmlgui.PlayerView;
import io.BasicIO;

/**
 * Abstract class that has all methods snd properties that apply to all players
 * @author William Elliman
 *
 */
public abstract class Player extends Observable{
	public final String name;
	protected int money;
	protected ArrayList<Card> hand;
	protected ArrayList<Card> winnings;
	
	protected BasicIO io;
	
	//vars to use as retVals
	protected Card chosenCard = null;
	protected int bid = -1;
	protected boolean buy = false;
	
	public Player(String name) {
		this.name = name;
		money = 100;
		hand = new ArrayList<Card>();
		winnings = new ArrayList<Card>();
		setChanged();
	}
	
	public void setGUI(PlayerView playerView) {
		io = playerView;
		setChanged();
	}
	
	/**
	 * Give the player the painting they won in the auction
	 * @param card
	 */
	public void givePainting(Card card) {
		winnings.add(card);
		setChanged();
	}
	
	public ArrayList<Card> getHand() {
		return hand;
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
		setChanged();
	}
	
	/**
	 * Deal a painting card to the player
	 * @param card that the player will receive
	 */
	public void deal(Card card) {
		hand.add(card);
		setChanged();
	}
	
	/**
	 * The player will lose an amount of money
	 * @param amount that will be lost
	 */
	public void pay(int amount) {
		money -= amount;
		setChanged();
	}
	
	/**
	 * The player will be paid an amount of money
	 * @param amount to be paid
	 */
	public void recive(int amount) {
		money += amount;
		setChanged();
	}
	
	public Card getChosenCard() {
		return chosenCard;
	}

	public int getBid() {
		return bid;
	}

	public boolean isBuy() {
		return buy;
	}
	
	/**
	 * Have the player choose a card they would like to bid on.
	 * @param state of the game
	 * @return the card to bid on
	 */
	public abstract void chooseCard();
	
	/**
	 * When the first card chosen is double auction, a second is needed by the same artist
	 * @return the second card
	 */
	public abstract void chooseSecondCard(Artist artist);
	
	/**
	 * Used to get the price the player would like to bid
	 * @param card being bid on
	 * @return the price the player is willing to pay
	 */
	public abstract void getBid(int highestBid);
	
	/**
	 * Gets the price that will be used to sell the card
	 * @return the price
	 */
	public abstract void getFixedPrice();
	
	/**
	 * Asks the player if they will buy the card
	 * @param card the player might buy
	 * @param price the player would buy the card at
	 * @return the players answer
	 */
	public abstract void buy(int price);
	
	/**
	 * This is here for debugging
	 * @return
	 */
	public int getMoney() {
		return money;
	}
	
	/**
	 * Allows the driver to tell all the players what card will be auctioned off without asking for a bid
	 * @param card
	 */
	public abstract void announceCard(Card card, boolean isDouble);
	
	/**
	 * Announces to the player that a season is ending
	 * @param season
	 */
	public abstract void announceSeasonEnd(int season);
	
	/**
	 * Announces to the player that another layer has won the auction
	 * @param turn of the winner
	 * @param name of the winner
	 * @param price that the winner paid
	 */
	public abstract void announceAuctionWinner(int turn, String name, int price);
}
