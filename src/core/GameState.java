package core;

import java.util.ArrayList;

/**
 * 
 * @author William Elliman
 * 
 * This class is used to hold the higher level data about the game, as well as data that no players know.
 *
 */
public class GameState {
	private Player[] players;
	private ArrayList<Card> deck;
	
	/**
	 * This is used to setup a new game.
	 * @param players
	 */
	public GameState(String[] players) {
		//first all the new players need to be created
		this.players = new Player[players.length];
		for(int i = 0; i < players.length; i++) {
			this.players[i] = new Player(players[i]);
		}
	}
}
