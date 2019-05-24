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
	public abstract void startSeason(int s);
	public abstract void showHand(Player player, ArrayList<Card> hand);
	public abstract void showHand(Player player, ArrayList<Card> hand, Artist artist);
	public abstract int getHandIndex(int maxVal);
	public abstract int getHandIndex(ArrayList<Card> hand, Artist artist);
	public abstract void end();
	public abstract int getBid(Player player, int highestSoFar);
	public abstract int getFixedPrice(Card card);
	public abstract boolean askPlayertoBuy(Card card, int price);
	public abstract void auctionWinner(Player player);
	public abstract void announceAuctionType(AuctionType type);
	public abstract void announceCard(Card card);
}
