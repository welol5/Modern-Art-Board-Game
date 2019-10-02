package core;

import java.util.Observable;

import javafx.beans.property.ReadOnlyStringProperty;

/**
 * This class acts as a buffer between GameState and Players. It allows players to see information but not change it.
 * Because this is a buffer class, see GameStates JavaDoc for descriptions of methods
 * @author William Elliman
 *
 */
public class ObservableGameState{

	/**
	 * The array that keeps track of the players still bidding.
	 */
	public boolean[] stillBidding;

	/**
	 * The actual state of the game.
	 */
	private final GameState state;

	/**
	 * @param state of the game
	 */
	public ObservableGameState(GameState state) {
		this.state = state;
		stillBidding = null;
	}

	/**
	 * Gets the season value for a specific artist
	 * @param artist
	 * @return the int version of the season value.
	 */
	public int getSeasonValue(Artist artist) {
		return state.getSeasonValue(artist);
	}

	/**
	 * @return the current top 3 artists.
	 */
	public Artist[] getTopSeasonValues() {
		return state.getTopThree();
	}

	/**
	 * @param artist
	 * @return the current value of an artists paintings.
	 */
	public int getArtistValue(Artist artist) {
		return state.getArtistValue(artist);
	}

	/**
	 * returns a copy of the season values array. This is an array that holds
	 * instances of {@link ArtistCount} that have the different artists and the
	 * value is the amount of paintings of that artist that have been played
	 * so far in the season. The array is sorted from most played at index 0 to
	 * least.
	 * @return a copy of the seasonValues array.
	 */
	public ArtistCount[] getSeasonValues() {
		ArtistCount[] copyArray = new ArtistCount[state.getSeasonValues().length];
		for(int i = 0; i < copyArray.length; i++) {
			copyArray[i] = state.getSeasonValues()[i].copy();
		}
		return copyArray;
	}

	/**
	 * This is here jsut to pass the ReadOnlyStringProperty through to the object that wants them.
	 * See {@link GameState}
	 * @param artist
	 * @return
	 */
	public ReadOnlyStringProperty getArtistCountString(Artist artist) {
		return state.getArtistCountString(artist);
	}
	
	/**
	 * This is here jsut to pass the ReadOnlyStringProperty through to the object that wants them.
	 * See {@link GameState}
	 * @param artist
	 * @return
	 */
	public ReadOnlyStringProperty getArtistValueString(Artist artist) {
		return state.getArtistValueString(artist);
	}
}
