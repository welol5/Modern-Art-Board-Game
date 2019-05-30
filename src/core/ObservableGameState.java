package core;

/**
 * This class acts as a buffer between GameState and Players. It allows players to see information but not change it.
 * @author William Elliman
 *
 */
public class ObservableGameState {
	public final int playerCount;
	public final Card card;
	public final int highestBid;
	
	private final GameState state;
	
	public ObservableGameState(int playerCount, Card card, int highestBid, GameState state) {
		this.playerCount = playerCount;
		this.card = card;
		this.highestBid = highestBid;
		this.state = state;
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
}
