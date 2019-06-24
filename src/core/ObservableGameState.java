package core;

import java.util.Observable;

/**
 * This class acts as a buffer between GameState and Players. It allows players to see information but not change it.
 * Because this is a buffer class, see GameStates JavaDoc for descriptions of methods
 * @author William Elliman
 *
 */
public class ObservableGameState extends Observable{
	
	public boolean[] stillBidding;
	
	private final GameState state;
	
	public ObservableGameState(GameState state) {
		this.state = state;
		stillBidding = null;
	}
	
	public int getSeasonValue(Artist artist) {
		return state.getSeasonValue(artist);
	}
	
	public Artist[] getTopSeasonValues() {
		return state.getTopThree();
	}
	
	public int getArtistValue(Artist artist) {
		return state.getArtistValue(artist);
	}
	
	public ArtistCount[] getSeasonValues() {
		ArtistCount[] copyArray = new ArtistCount[state.getSeasonValues().length];
		for(int i = 0; i < copyArray.length; i++) {
			copyArray[i] = state.getSeasonValues()[i].copy();
		}
		return copyArray;
	}
}
