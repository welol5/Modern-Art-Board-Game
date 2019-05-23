package io;

import java.util.ArrayList;

import core.Card;

public abstract class BasicIO {
	/**
	 * This method should be used to get all of the names of the players in the game.
	 * The useful part about this is the count of how many players.
	 * @return the names of all of the players
	 */
	public abstract String[] getPlayers();
	public abstract void startSeason(int s);
	public abstract void showHand(ArrayList<Card> hand);
	public abstract int getHandIndex(int maxVal);
	public abstract void end();
}
