package player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import core.Artist;
import core.Card;
import core.ObservableGameState;
import fxmlgui.PlayerView;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.HBox;

/**
 * Abstract class that has all methods and properties that apply to all players.
 * It contains things like the name and money values, but it also contains some of the objects
 * needed to interact with a GUI if it is being used. 
 * @author William Elliman
 *
 */
public abstract class Player{
	
	/**
	 * The name of the player.
	 */
	public final String name;
	
	/**
	 * The amount of money a player has.
	 */
	protected int money;//TODO this should be private so players cannot change it.
	
	/**
	 * The cards a player can play when it is their turn.
	 */
	protected ArrayList<Card> hand;
	
	/**
	 * The cards the player has won in the auctions of a given season.
	 */
	protected ArrayList<Card> winnings;
	
	/**
	 * The observable game state.
	 */
	protected ObservableGameState OGS;

	/**
	 * The playerView is the main panel this player will send its info to
	 * so that it can be displayed on the GUI.
	 */
	protected PlayerView playerView;
	
	/**
	 * moneyText is a string version of the money value, however, it is not just a string
	 * because the SimpleStringProperty is {@link ObservableValue} so that it can be bound to a
	 * Text object in the GUI.
	 */
	private SimpleStringProperty moneyText;
	
	/**
	 * winningStrings works similarly to moneyText. It is string values for the set of cards in
	 * winnings, although it only shows counts of how many cards are from each artist.
	 */
	private HashMap<Artist, SimpleStringProperty> winningsStrings;

	/**
	 * This initializes the most basic needs of a player.
	 * @param name of the player
	 * @param playerView GUI of the player. Should be null for no GUI for this specific player.
	 * @param OGS observable game state
	 */
	public Player(String name, PlayerView playerView, ObservableGameState OGS) {
		this.name = name;
		money = 100;
		hand = new ArrayList<Card>();
		winnings = new ArrayList<Card>();
		this.playerView = playerView;
		this.OGS = OGS;
		moneyText = new SimpleStringProperty();
		moneyText.set("" + money);
		winningsStrings = new HashMap<Artist, SimpleStringProperty>();

		for(Artist a : Artist.values()) {
			winningsStrings.put(a, new SimpleStringProperty());
			winningsStrings.get(a).set("" + 0);
		}
	}

	/**
	 * This constructor should only be used for players that do not care about anything.
	 * It is here for the RandomPlayer.
	 * @param name of the player
	 */
	public Player(String name) {
		this.name = name;
		money = 100;
		hand = new ArrayList<Card>();
		winnings = new ArrayList<Card>();

		playerView = null;
		moneyText = null;
		winningsStrings = null;
	}

	/**
	 * This should be called after the player makes any actions. It goes through
	 * the players hand and updated what is shown on the GUI if this player has a GUI.
	 */
	protected void updateGUI() {
		//if there is no GUI dont do anything
		if(playerView == null) {
			return;
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				playerView.updateGUI();
			}
		});
	}

	/**
	 * Give the player the painting they won in the auction.
	 * @param card
	 */
	public void givePainting(Card card) {
		winnings.add(card);
		if(winningsStrings != null) {
			winningsStrings.get(card.getArtist()).set("" + (Integer.parseInt(winningsStrings.get(card.getArtist()).get())+1));
		}
	}

	public ArrayList<Card> getHand() {
		return hand;
	}

	/**
	 * 
	 * @return the list of paintings won by the player.
	 */
	public ArrayList<Card> getWinnings(){
		return winnings;
	}

	/**
	 * Removes all paintings that the player has won.
	 */
	public void clearWinnings() {
		winnings.clear();

		if(winningsStrings != null) {
			for(Artist a : Artist.values()) {
				winningsStrings.get(a).set("0");
			}
		}
	}

	/**
	 * Deal a painting card to the player.
	 * @param card that the player will receive.
	 */
	public void deal(Card card) {
		hand.add(card);
		updateGUI();
	}

	/**
	 * The player will lose an amount of money.
	 * @param amount that will be lost.
	 */
	public void pay(int amount) {
		money -= amount;
		if(moneyText != null) {
			moneyText.set("" + money);
		}
	}

	/**
	 * The player will be paid an amount of money.
	 * @param amount to be paid.
	 */
	public void recive(int amount) {
		money += amount;
		if(moneyText != null) {
			moneyText.set("" + money);
		}
	}

	/**
	 * Have the player choose a card they would like to bid on.
	 * @return the card to bid on.
	 */
	public abstract Card chooseCard();

	/**
	 * When the first card chosen is double auction, a second is needed by the same artist.
	 * @param artist the artist the second card should be from.
	 * @return the second card
	 */
	public abstract Card chooseSecondCard(Artist artist);

	/**
	 * Used to get the price the player would like to bid
	 * @return the price the player is willing to pay
	 */
	public abstract int getBid(int highestBid);

	/**
	 * Gets the price that will be used to sell the card
	 * @return the price
	 */
	public abstract int getFixedPrice();

	/**
	 * Asks the player if they will buy the card
	 * @param price the player would buy the card at
	 * @return the players answer. True means the player would like to buy.
	 */
	public abstract boolean buy(int price);

	/**
	 * This is here for debugging
	 * @return
	 */
	public int getMoney() {
		return money;
	}

	/**
	 * Allows the driver to tell all the players what card will be auctioned off without asking for a bid
	 * @param card the card players will bid on.
	 * @param isDouble tells players if the current auction is actually a double auction.
	 */
	public abstract void announceCard(Card card, boolean isDouble);

	/**
	 * Announces to the player that a season is ending.
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

	/**
	 * @return the StringProperty version of the money value.
	 */
	public StringProperty getMoneyProperty() {
		return moneyText;
	}

	/**
	 * @param a the artist
	 * @return the StringProperty of the winnings of a certain artist.
	 */
	public StringProperty getWinningProperty(Artist a) {
		return winningsStrings.get(a);
	}
}
