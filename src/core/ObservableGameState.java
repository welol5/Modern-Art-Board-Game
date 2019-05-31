package core;

/**
 * This class acts as a buffer between GameState and Players. It allows players to see information but not change it.
 * Because this is a buffer class, see GameStates JavaDoc for descriptions of methods
 * @author William Elliman
 *
 */
public class ObservableGameState {
	public final int playerCount;
	public final Card card;
	public final int highestBid;
	public final int bidder;
	public final boolean isDouble;
	
	private final GameState state;
	
	public ObservableGameState(int playerCount, Card card, int highestBid, int bidder, GameState state, boolean isDouble) {
		this.playerCount = playerCount;
		this.card = card;
		this.highestBid = highestBid;
		this.state = state;
		this.bidder = bidder;
		this.isDouble = isDouble;
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
		return state.getSeasonValues();
	}
}
