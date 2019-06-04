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
	public final boolean[] stillBidding;
	
	private final GameState state;
	
	public ObservableGameState(int playerCount, Card card, int highestBid, int bidder, GameState state, boolean isDouble) {
		this.playerCount = playerCount;
		this.card = card;
		this.highestBid = highestBid;
		this.state = state;
		this.bidder = bidder;
		this.isDouble = isDouble;
		stillBidding = null;
	}
	
	public ObservableGameState(int playerCount, Card card, int highestBid, int bidder, GameState state, boolean isDouble, boolean[] stillBidding) {
		this.playerCount = playerCount;
		this.card = card;
		this.highestBid = highestBid;
		this.state = state;
		this.bidder = bidder;
		this.isDouble = isDouble;
		this.stillBidding = stillBidding;
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
