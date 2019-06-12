package io;

import java.util.ArrayList;

import core.Artist;
import core.AuctionType;
import core.Card;
import player.Player;

public abstract class BasicIO {
	
	/**
	 * This method should be used to get all of the names of the players in the game.
	 * The useful part about this is the count of how many players.
	 * @return the names of all of the players
	 */
	public abstract String[] getPlayers();
	
	/**
	 * Tells the players that a new season is starting.
	 * @param s the season number
	 */
	public abstract void startSeason(int s);
	
	/**
	 * Shows the player the cards in their hand.
	 * @param player whose hand will be shown
	 * @param hand the list of the players cards
	 */
	public abstract void showHand(Player player, ArrayList<Card> hand);
	
	/**
	 * Shows the player the cards in their hand. This one also limits to showing paintings from a specific artist
	 * @param player whose hand will be shown
	 * @param hand the list of the players cards
	 * @param artist 
	 */
	public abstract void showHand(Player player, ArrayList<Card> hand, Artist artist);
	
	/**
	 * gets the index of the card the player wants to choose
	 * @param maxVal the size of the list of cards
	 * @return the index of the card
	 */
	public abstract int getHandIndex(int maxVal);
	
	/**
	 * gets the index of the card the player would like to choose
	 * @param hand the list of the players cards
	 * @param artist that the paintings must be from
	 * @return the index of the card
	 */
	public abstract int getHandIndex(ArrayList<Card> hand, Artist artist);
	
	/**
	 * runs anything needed to end the IO and close the program
	 */
	public abstract void end();
	
	/**
	 * Gets the bid a player is willing to pay
	 * @param player bidding
	 * @param money that the player has
	 * @param highestSoFar highest bid so far (so the player can raise it or drop out)
	 * @return the price the player is willing to pay
	 */
	public abstract int getBid(String player, int money, int highestSoFar);
	
	/**
	 * Gets the price the player is selling the painting for
	 * @param card the painting being sold
	 * @return the price the player wants
	 */
	public abstract int getFixedPrice(Card card);
	
	/**
	 * Asks the player if they would like to buy the card
	 * @param card that the player will purchase
	 * @param price of the painting
	 * @return the players answer
	 */
	public abstract boolean askPlayertoBuy(Card card, int price);
	
	/**
	 * Informs the players who won the auction
	 * @param player that won
	 * @param price they paid
	 */
	public abstract void auctionWinner(String name, int price);
	
	/**
	 * Announces the type of auction
	 * @param type of the auction
	 */
	public abstract void announceAuctionType(AuctionType type);
	
	/**
	 * Announce the card players will be bidding on
	 * @param card the painting
	 */
	public abstract void announceCard(Card card);
	
	/**
	 * Announces the winner of the game
	 * @param player
	 */
	public abstract void announceWinner(Player player);
}
