package core;

public class Card {
	private Artist artist;//artist of the painting
	private AuctionType auctionType;//the type of auction
	
	public Card(Artist a, AuctionType t) {
		artist = a;
		auctionType = t;
	}
	
	public Artist getArtist() {
		return artist;
	}
	
	public AuctionType getAuctionType() {
		return auctionType;
	}
}
